package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;
import ajedrez.dominio.modelo.creacion.CreacionDePiezas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovimientoAlfilTest {
    private Tablero tablero;
    private MovimientoAlfil movimientoAlfil;
    private Posicion origen;
    private CreacionDePiezas creador;

    @BeforeEach
    void prepararEscenario() {
        tablero = new Tablero(8, 8);
        movimientoAlfil = new MovimientoAlfil();
        creador = new CreacionDePiezas();
        origen = new Posicion(3, 3);
        Pieza alfil = creador.crear(TipoPieza.ALFIL, ColorPieza.BLANCO);
        tablero.colocarPieza(alfil, origen);
    }

    @Test
    void permiteMovimientoDiagonal() {
        boolean resultado = movimientoAlfil.esMovimientoValido(tablero, origen, new Posicion(6, 6), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void permiteMovimientoDiagonalHaciaArriba() {
        boolean resultado = movimientoAlfil.esMovimientoValido(tablero, origen, new Posicion(0, 0), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void rechazaMovimientoHorizontal() {
        boolean resultado = movimientoAlfil.esMovimientoValido(tablero, origen, new Posicion(3, 7), ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void rechazaMovimientoVertical() {
        boolean resultado = movimientoAlfil.esMovimientoValido(tablero, origen, new Posicion(7, 3), ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void noPermiteAtravesarPiezas() {
        Pieza obstaculo = creador.crear(TipoPieza.PEON, ColorPieza.NEGRO);
        tablero.colocarPieza(obstaculo, new Posicion(4, 4));
        boolean resultado = movimientoAlfil.esMovimientoValido(tablero, origen, new Posicion(6, 6), ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void permiteCapturarPiezaEnemiga() {
        Pieza enemigo = creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO);
        Posicion destino = new Posicion(5, 5);
        tablero.colocarPieza(enemigo, destino);
        assertTrue(movimientoAlfil.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void noPermiteCapturarPiezaAliada() {
        Pieza aliada = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        Posicion destino = new Posicion(5, 5);
        tablero.colocarPieza(aliada, destino);
        assertFalse(movimientoAlfil.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void rechazaMovimientoFueraDelTablero() {
        assertFalse(movimientoAlfil.esMovimientoValido(tablero, origen, new Posicion(9, 9), ColorPieza.BLANCO));
    }
}