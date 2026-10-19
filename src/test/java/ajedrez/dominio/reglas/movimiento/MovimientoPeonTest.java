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

class MovimientoPeonTest {
    private Tablero tablero;
    private MovimientoPeon movimientoPeon;
    private Posicion origen;
    private CreacionDePiezas creador;

    @BeforeEach
    void prepararEscenario() {
        tablero = new Tablero(8, 8);
        movimientoPeon = new MovimientoPeon();
        creador = new CreacionDePiezas();
        origen = new Posicion(6, 3);
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(peon, origen);
    }

    @Test
    void permiteAvanzarUnaCasilla() {
        assertTrue(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(5, 3), ColorPieza.BLANCO));
    }

    @Test
    void permiteAvanzarDosCasillasDesdeInicio() {
        assertTrue(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(4, 3), ColorPieza.BLANCO));
    }

    @Test
    void rechazaAvanzarTresCasillas() {
        assertFalse(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(3, 3), ColorPieza.BLANCO));
    }

    @Test
    void rechazaMovimientoHaciaAtras() {
        assertFalse(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(7, 3), ColorPieza.BLANCO));
    }

    @Test
    void noPermiteAvanzarSiHayObstaculo() {
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(5, 3));
        assertFalse(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(5, 3), ColorPieza.BLANCO));
    }

    @Test
    void noPermiteSaltarObstaculoEnAvanceDoble() {
        tablero.colocarPieza(creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO), new Posicion(5, 3));
        assertFalse(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(4, 3), ColorPieza.BLANCO));
    }

    @Test
    void permiteCapturaDiagonal() {
        Posicion destino = new Posicion(5, 4);
        tablero.colocarPieza(creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO), destino);
        assertTrue(movimientoPeon.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void rechazaDiagonalVacia() {
        assertFalse(movimientoPeon.esMovimientoValido(tablero, origen, new Posicion(5, 4), ColorPieza.BLANCO));
    }

    @Test
    void rechazaCapturarPiezaAliada() {
        Posicion destino = new Posicion(5, 4);
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO), destino);
        assertFalse(movimientoPeon.esMovimientoValido(tablero, origen, destino, ColorPieza.BLANCO));
    }

    @Test
    void amenazaDiagonalAunqueEsteVacia() {
        assertTrue(movimientoPeon.amenaza(tablero, origen, new Posicion(5, 4), ColorPieza.BLANCO));
    }

    @Test
    void noPermiteDobleAvanceFueraDeFilaInicial() {
        Tablero otroTablero = new Tablero(8, 8);
        Posicion otraPosicion = new Posicion(5, 3);
        otroTablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), otraPosicion);
        assertFalse(movimientoPeon.esMovimientoValido(otroTablero, otraPosicion, new Posicion(3, 3), ColorPieza.BLANCO));
    }

    @Test
    void peonNegroAvanzaHaciaAbajo() {
        Tablero otroTablero = new Tablero(8, 8);
        Posicion origenNegro = new Posicion(1, 3);
        otroTablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), origenNegro);
        assertTrue(movimientoPeon.esMovimientoValido(otroTablero, origenNegro, new Posicion(3, 3), ColorPieza.NEGRO));
    }
}