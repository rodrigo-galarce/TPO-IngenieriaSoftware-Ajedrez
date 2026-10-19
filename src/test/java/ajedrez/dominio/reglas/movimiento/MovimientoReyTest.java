package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MovimientoReyTest {
    private Tablero tablero;
    private MovimientoRey movimientoRey;
    private Posicion origen;

    @BeforeEach
    void prepararEscenario() {
        tablero = new Tablero(8, 8);
        movimientoRey = new MovimientoRey();
        origen = new Posicion(4, 4);
        Pieza rey = new Pieza(TipoPieza.REY, ColorPieza.BLANCO, movimientoRey);
        tablero.colocarPieza(rey, origen);
    }

    @Test
    void permiteMovimientoHorizontal() {
        assertTrue(movimientoRey.esMovimientoValido(tablero, origen, new Posicion(4, 5), ColorPieza.BLANCO));
    }

    @Test
    void permiteMovimientoVertical() {
        assertTrue(movimientoRey.esMovimientoValido(tablero, origen, new Posicion(3, 4), ColorPieza.BLANCO));
    }

    @Test
    void permiteMovimientoDiagonal() {
        assertTrue(movimientoRey.esMovimientoValido(tablero, origen, new Posicion(3, 3), ColorPieza.BLANCO));
    }

    @Test
    void rechazaMovimientoDeDosCasillas() {
        assertFalse(movimientoRey.esMovimientoValido(tablero, origen, new Posicion(4, 6), ColorPieza.BLANCO));
    }

    @Test
    void rechazaMismaCasilla() {
        assertFalse(movimientoRey.esMovimientoValido(tablero, origen, origen, ColorPieza.BLANCO));
    }

    @Test
    void permiteCapturarPiezaEnemiga() {
        Pieza enemigo = new Pieza(TipoPieza.PEON, ColorPieza.NEGRO);
        Posicion destino = new Posicion(3, 4);
        tablero.colocarPieza(enemigo, destino);
        assertTrue(movimientoRey.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void noPermiteCapturarPiezaAliada() {
        Pieza aliada = new Pieza(TipoPieza.PEON, ColorPieza.BLANCO);
        Posicion destino = new Posicion(3, 4);
        tablero.colocarPieza(aliada, destino);
        assertFalse(movimientoRey.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void detectaCasillaAmenazada() {
        assertTrue(movimientoRey.amenaza(tablero, origen, new Posicion(5, 5), ColorPieza.BLANCO));
    }

    @Test
    void rechazaDestinoFueraDelTablero() {
        assertFalse(movimientoRey.esMovimientoValido(tablero, origen, new Posicion(4, 8), ColorPieza.BLANCO));
    }
}
