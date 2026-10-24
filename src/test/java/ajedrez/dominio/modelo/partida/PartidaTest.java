package ajedrez.dominio.modelo.partida;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.tablero.Tablero;
import ajedrez.dominio.modelo.creacion.CreacionDePiezas;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.creacion.ConfiguracionInicial;
import ajedrez.dominio.reglas.jaque.DetectorJaque;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {
    @Test
    void comienzaConTurnoBlanco() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void utilizaElTableroRecibido() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        assertSame(tablero, partida.getTablero());
    }

    @Test
    void noPermiteCrearPartidaSinTablero() {
        assertThrows(NullPointerException.class, () -> new Partida(null));
    }

    @Test
    void movimientoValidoCambiaElTurno() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(6, 0);
        Posicion destino = new Posicion(5, 0);
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(peon, origen);
        partida.moverPieza(origen, destino);
        assertNull(tablero.obtenerPieza(origen));
        assertSame(peon, tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.NEGRO, partida.getTurnoActual());
    }

    @Test
    void noPermiteMoverPiezaFueraDeTurno() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(1, 0);
        Posicion destino = new Posicion(2, 0);
        Pieza peonNegro = creador.crear(TipoPieza.PEON, ColorPieza.NEGRO);
        tablero.colocarPieza(peonNegro, origen);
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(peonNegro, tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void movimientoInvalidoNoCambiaElTurno() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(5, 5);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        tablero.colocarPieza(torre, origen);
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(torre, tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void alternaTurnosEntreBlancasYNegras() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Pieza peonBlanco = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        Pieza peonNegro = creador.crear(TipoPieza.PEON, ColorPieza.NEGRO);

        Posicion origenBlanco = new Posicion(6, 0);
        Posicion destinoBlanco = new Posicion(5, 0);

        Posicion origenNegro = new Posicion(1, 0);
        Posicion destinoNegro = new Posicion(2, 0);

        tablero.colocarPieza(peonBlanco, origenBlanco);
        tablero.colocarPieza(peonNegro, origenNegro);

        // act y assert
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
        partida.moverPieza(origenBlanco, destinoBlanco);
        assertSame(peonBlanco, tablero.obtenerPieza(destinoBlanco));
        assertEquals(ColorPieza.NEGRO, partida.getTurnoActual());
        partida.moverPieza(origenNegro, destinoNegro);
        assertSame(peonNegro, tablero.obtenerPieza(destinoNegro));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void noPermiteMoverDesdeCasillaVacia() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 5);
        // act y assert
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertNull(tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void noPermiteMoverFueraDelTablero() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(6, 0);
        Posicion destino = new Posicion(8, 0);
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(peon, origen);
        // act y assert
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(peon, tablero.obtenerPieza(origen));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void iniciaPartidaConPiezasEnPosicionInicial() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        ConfiguracionInicial configuracion = new ConfiguracionInicial(creador);
        configuracion.colocarPiezas(tablero);
        // act
        Partida partida = new Partida(tablero);
        // assert
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
        assertEquals(TipoPieza.REY, partida.getTablero().obtenerPieza(new Posicion(7, 4)).getTipo());
        assertEquals(TipoPieza.REY, partida.getTablero().obtenerPieza(new Posicion(0, 4)).getTipo());
        assertEquals(TipoPieza.PEON, partida.getTablero().obtenerPieza(new Posicion(6, 0)).getTipo()
        );
    }

    @Test
    void partidaDetectaJaqueAlReyBlanco() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 0));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 4));
        Partida partida = new Partida(tablero);
        assertTrue(partida.estaEnJaque(ColorPieza.BLANCO));
        assertFalse(partida.estaEnJaque(ColorPieza.NEGRO));
    }

    @Test
    void partidaNoDetectaJaqueSiHayObstaculo() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), new Posicion(4, 4));
        Partida partida = new Partida(tablero);
        assertFalse(partida.estaEnJaque(ColorPieza.BLANCO));
    }

    @Test
    void descuentaTiempoDelJugadorActual() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        partida.avanzarUnSegundo();
        assertEquals(179, partida.getSegundosRestantes(ColorPieza.BLANCO));
        assertEquals(180, partida.getSegundosRestantes(ColorPieza.NEGRO));
    }

    @Test
    void descuentaTiempoDelNuevoTurno() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(6, 0);
        Posicion destino = new Posicion(5, 0);

        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), origen);
        partida.moverPieza(origen, destino);
        partida.avanzarUnSegundo();
        assertEquals(180, partida.getSegundosRestantes(ColorPieza.BLANCO));
        assertEquals(179, partida.getSegundosRestantes(ColorPieza.NEGRO));
    }

    @Test
    void finalizaPartidaCuandoSeAgotaElTiempo() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero, new DetectorJaque(), new RelojPartida(2));
        partida.avanzarUnSegundo();
        assertFalse(partida.estaFinalizada());
        partida.avanzarUnSegundo();
        assertTrue(partida.estaFinalizada());
        assertEquals(ColorPieza.NEGRO, partida.getGanador());
        assertEquals(0, partida.getSegundosRestantes(ColorPieza.BLANCO));
        assertThrows(IllegalStateException.class, () -> partida.moverPieza(new Posicion(6, 0), new Posicion(5, 0)));
    }

    @Test
    void noPermiteCapturarAlReyEnemigo() {
        Tablero tablero = new Tablero(8, 8);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 6);
        Pieza torreBlanca = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        Pieza reyNegro = creador.crear(TipoPieza.REY, ColorPieza.NEGRO);
        tablero.colocarPieza(torreBlanca, origen);
        tablero.colocarPieza(reyNegro, destino);
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(torreBlanca, tablero.obtenerPieza(origen));
        assertSame(reyNegro, tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }
}