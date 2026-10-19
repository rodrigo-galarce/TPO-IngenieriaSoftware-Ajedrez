package ajedrez.dominio.reglas.jaque;

import ajedrez.dominio.modelo.creacion.CreacionDePiezas;
import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DetectorJaqueTest {

    @Test
    void detectaJaqueDeTorreAlReyBlanco() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 4));
        assertTrue(detector.estaEnJaque(tablero, ColorPieza.BLANCO));
    }

    @Test
    void noDetectaJaqueSiHayUnObstaculo() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), new Posicion(4, 4));
        assertFalse(detector.estaEnJaque(tablero, ColorPieza.BLANCO));
    }

    @Test
    void detectaJaqueDePeonAlReyBlanco() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(4, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(3, 3));
        assertTrue(detector.estaEnJaque(tablero, ColorPieza.BLANCO));
    }

    @Test
    void peonNoDaJaqueHaciaAdelante() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(4, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(3, 4));
        assertFalse(detector.estaEnJaque(tablero, ColorPieza.BLANCO));
    }

    @Test
    void detectaJaqueDeCaballoAlReyBlanco() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(4, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO), new Posicion(2, 3));
        assertTrue(detector.estaEnJaque(tablero, ColorPieza.BLANCO));
    }

    @Test
    void detectaJaqueDeAlfilAlReyNegro() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(1, 1));
        tablero.colocarPieza(creador.crear(TipoPieza.ALFIL, ColorPieza.BLANCO), new Posicion(4, 4));
        assertTrue(detector.estaEnJaque(tablero, ColorPieza.NEGRO));
    }

    @Test
    void detectaJaqueDeReinaAlReyBlanco() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        DetectorJaque detector = new DetectorJaque();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(4, 6));
        tablero.colocarPieza(creador.crear(TipoPieza.REINA, ColorPieza.NEGRO), new Posicion(4, 2));
        assertTrue(detector.estaEnJaque(tablero, ColorPieza.BLANCO));
    }





}
