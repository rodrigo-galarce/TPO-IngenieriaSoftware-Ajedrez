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

class MovimientoReinaTest {
    private Tablero tablero;
    private MovimientoReina movimientoReina;
    private Posicion origen;
    private CreacionDePiezas creador;

    @BeforeEach
    void prepararEscenario() {
        tablero = new Tablero(8, 8);
        movimientoReina = new MovimientoReina();
        creador = new CreacionDePiezas();
        origen = new Posicion(3, 3);
        Pieza reina = creador.crear(TipoPieza.REINA, ColorPieza.BLANCO);
        tablero.colocarPieza(reina, origen);
    }

    @Test
    void permiteMovimientoHorizontal() {
        assertTrue(movimientoReina.esMovimientoValido(tablero, origen, new Posicion(3, 7), ColorPieza.BLANCO));
    }

    @Test
    void permiteMovimientoVertical() {
        assertTrue(movimientoReina.esMovimientoValido(tablero, origen, new Posicion(0, 3), ColorPieza.BLANCO));
    }

    @Test
    void permiteMovimientoDiagonal() {
        assertTrue(movimientoReina.esMovimientoValido(tablero, origen, new Posicion(6, 6), ColorPieza.BLANCO));
    }

    @Test
    void rechazaMovimientoIrregular() {
        assertFalse(movimientoReina.esMovimientoValido(tablero, origen, new Posicion(5, 6), ColorPieza.BLANCO));
    }

    @Test
    void noPermiteAtravesarPiezas() {
        Pieza obstaculo = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(obstaculo, new Posicion(4, 4));
        assertFalse(movimientoReina.esMovimientoValido(tablero, origen, new Posicion(6, 6), ColorPieza.BLANCO));
    }

    @Test
    void permiteCapturarPiezaEnemiga() {
        Pieza enemigo = creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO);
        Posicion destino = new Posicion(3, 6);
        tablero.colocarPieza(enemigo, destino);
        assertTrue(movimientoReina.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void noPermiteCapturarPiezaAliada() {
        Pieza aliada = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        Posicion destino = new Posicion(3, 6);
        tablero.colocarPieza(aliada, destino);
        assertFalse(movimientoReina.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void detectaCasillaAmenazada() {
        assertTrue(movimientoReina.amenaza(tablero, origen, new Posicion(6, 6), ColorPieza.BLANCO));
    }
}