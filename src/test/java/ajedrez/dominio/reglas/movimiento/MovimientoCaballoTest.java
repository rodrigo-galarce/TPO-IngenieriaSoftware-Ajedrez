package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoCaballoTest {
    private Tablero tablero;
    private MovimientoCaballo movimientoCaballo;
    private Posicion origen;

    @BeforeEach
    void prepararEscenario() {
        tablero = new Tablero(8, 8);
        movimientoCaballo = new MovimientoCaballo();
        origen = new Posicion(4, 4);
        Pieza caballo = new Pieza(TipoPieza.CABALLO, ColorPieza.BLANCO, movimientoCaballo);
        tablero.colocarPieza(caballo, origen);
    }

    @Test
    void permiteMovimientoEnL() {
        boolean resultado = movimientoCaballo.esMovimientoValido(tablero, origen, new Posicion(2, 5), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void permiteMovimientoEnLOtraDireccion() {
        boolean resultado = movimientoCaballo.esMovimientoValido(tablero, origen, new Posicion(5, 2), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void rechazaMovimientoHorizontal() {
        boolean resultado = movimientoCaballo.esMovimientoValido(tablero, origen, new Posicion(4, 6), ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void permiteSaltarSobreOtrasPiezas() {
        Pieza obstaculo = new Pieza(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(obstaculo, new Posicion(3, 4));
        boolean resultado = movimientoCaballo.esMovimientoValido(tablero, origen, new Posicion(2, 5), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void permiteCapturarPiezaEnemiga() {
        Pieza enemigo = new Pieza(TipoPieza.PEON, ColorPieza.NEGRO);
        Posicion destino = new Posicion(2, 5);
        tablero.colocarPieza(enemigo, destino);
        assertTrue(movimientoCaballo.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void noPermiteCapturarPiezaAliada() {
        Pieza aliada = new Pieza(TipoPieza.PEON, ColorPieza.BLANCO);
        Posicion destino = new Posicion(2, 5);
        tablero.colocarPieza(aliada, destino);
        assertFalse(movimientoCaballo.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void detectaCasillaAmenazadaConAliada() {
        Pieza aliada = new Pieza(TipoPieza.PEON, ColorPieza.BLANCO);
        Posicion destino = new Posicion(2, 5);
        tablero.colocarPieza(aliada, destino);
        assertTrue(movimientoCaballo.amenaza(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void rechazaDestinoFueraDelTablero() {
        boolean resultado = movimientoCaballo.esMovimientoValido(tablero, origen, new Posicion(6, 9), ColorPieza.BLANCO);
        assertFalse(resultado);
    }
}