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

class MovimientoTorreTest {
    private Tablero tablero;
    private MovimientoTorre movimientoTorre;
    private Posicion origen;
    private CreacionDePiezas creador;

    @BeforeEach
    void prepararEscenario() {
        tablero = new Tablero(8, 8);
        movimientoTorre = new MovimientoTorre();
        creador = new CreacionDePiezas();
        origen = new Posicion(4, 3);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        tablero.colocarPieza(torre, origen);
    }

    @Test
    void permiteMovimientoVertical() {
        // act
        boolean resultado = movimientoTorre.esMovimientoValido(tablero,origen,new Posicion(1, 3),ColorPieza.BLANCO);
        // assert
        assertTrue(resultado);
    }

    @Test
    void permiteMovimientoHorizontal() {
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, new Posicion(4, 7), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void rechazaMovimientoDiagonal() {
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, new Posicion(6, 5), ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void rechazaMovimientoALaMismaCasilla() {
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, origen, ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void noPermiteAtravesarOtraPieza() {
        // arrange
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.NEGRO);
        tablero.colocarPieza(peon, new Posicion(4, 5));
        // act
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, new Posicion(4, 7), ColorPieza.BLANCO);
        // assert
        assertFalse(resultado);
    }

    @Test
    void permiteCapturarPiezaEnemiga() {
        Pieza enemigo = creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO);
        tablero.colocarPieza(enemigo, new Posicion(4, 6));
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, new Posicion(4, 6), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void noPermiteCapturarPiezaAliada() {
        Pieza aliada = creador.crear(TipoPieza.CABALLO, ColorPieza.BLANCO);
        tablero.colocarPieza(aliada, new Posicion(4, 6));
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, new Posicion(4, 6), ColorPieza.BLANCO);
        assertFalse(resultado);
    }

    @Test
    void detectaCasillaAmenazadaConPiezaAliada() {
        Pieza aliada = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(aliada, new Posicion(4, 6));
        boolean resultado = movimientoTorre.amenaza(tablero, origen, new Posicion(4, 6), ColorPieza.BLANCO);
        assertTrue(resultado);
    }

    @Test
    void rechazaDestinoFueraDelTablero() {
        boolean resultado = movimientoTorre.esMovimientoValido(tablero, origen, new Posicion(4, 8), ColorPieza.BLANCO);
        assertFalse(resultado);
    }
}
