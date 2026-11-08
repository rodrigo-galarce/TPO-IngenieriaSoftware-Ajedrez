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

    private void colocarReyes(Tablero tablero) {
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 4));
    }

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
        colocarReyes(tablero);
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
        colocarReyes(tablero);
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
        assertEquals(599, partida.getSegundosRestantes(ColorPieza.BLANCO));
        assertEquals(600, partida.getSegundosRestantes(ColorPieza.NEGRO));
    }

    @Test
    void descuentaTiempoDelNuevoTurno() {
        Tablero tablero = new Tablero(8, 8);
        colocarReyes(tablero);
        Partida partida = new Partida(tablero);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(6, 0);
        Posicion destino = new Posicion(5, 0);

        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), origen);
        partida.moverPieza(origen, destino);
        partida.avanzarUnSegundo();
        assertEquals(600, partida.getSegundosRestantes(ColorPieza.BLANCO));
        assertEquals(599, partida.getSegundosRestantes(ColorPieza.NEGRO));
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

    @Test
    void reyNoPuedeMoverseACasillaAmenazada() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        Partida partida = new Partida(tablero);
        Posicion origen = new Posicion(7, 3);
        Posicion destino = new Posicion(7, 4);
        Pieza reyBlanco = creador.crear(TipoPieza.REY, ColorPieza.BLANCO);
        tablero.colocarPieza(reyBlanco, origen);
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 0));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 4));
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(reyBlanco, tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void noPermiteMoverPiezaQueDejaAlReyEnJaque() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        Partida partida = new Partida(tablero);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 5);
        Pieza torreBlanca = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 0));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 4));
        tablero.colocarPieza(torreBlanca, origen);
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(torreBlanca, tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void detectaJaqueMate() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(5, 5));
        tablero.colocarPieza(creador.crear(TipoPieza.REINA, ColorPieza.NEGRO), new Posicion(6, 6));
        Partida partida = new Partida(tablero);
        assertTrue(partida.estaEnJaqueMate(ColorPieza.BLANCO));
    }

    @Test
    void estarEnJaqueNoSiempreEsJaqueMate() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 0));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 7));
        Partida partida = new Partida(tablero);
        assertTrue(partida.estaEnJaque(ColorPieza.BLANCO));
        assertFalse(partida.estaEnJaqueMate(ColorPieza.BLANCO));
    }

    @Test
    void finalizaPartidaCuandoHayJaqueMate() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(2, 5));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REINA, ColorPieza.BLANCO), new Posicion(2, 6));
        Partida partida = new Partida(tablero);
        partida.moverPieza(new Posicion(2, 6), new Posicion(1, 6));
        assertTrue(partida.estaFinalizada());
        assertEquals(ColorPieza.BLANCO, partida.getGanador());
        assertTrue(partida.estaEnJaqueMate(ColorPieza.NEGRO));
    }

    @Test
    void detectaAhogado() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(6, 5));
        tablero.colocarPieza(creador.crear(TipoPieza.REINA, ColorPieza.NEGRO), new Posicion(5, 6));
        Partida partida = new Partida(tablero);
        assertFalse(partida.estaEnJaque(ColorPieza.BLANCO));
        assertTrue(partida.estaAhogado(ColorPieza.BLANCO));
    }

    @Test
    void noHayAhogadoSiExistenMovimientosLegales() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 0));
        Partida partida = new Partida(tablero);
        assertFalse(partida.estaAhogado(ColorPieza.BLANCO));
    }

    @Test
    void finalizaPartidaPorAhogado() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(2, 2));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 0));
        tablero.colocarPieza(creador.crear(TipoPieza.REINA, ColorPieza.BLANCO), new Posicion(2, 1));
        Partida partida = new Partida(tablero);
        partida.moverPieza(new Posicion(2, 1), new Posicion(1, 2));
        assertTrue(partida.estaFinalizada());
        assertTrue(partida.terminoEnTablas());
        assertNull(partida.getGanador());
        assertTrue(partida.estaAhogado(ColorPieza.NEGRO));
    }

    @Test
    void promocionaPeonBlancoAReina() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(1, 0);
        Posicion destino = new Posicion(0, 0);
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), origen);
        Partida partida = new Partida(tablero);
        partida.moverPieza(origen, destino, TipoPieza.REINA);
        Pieza piezaPromocionada = tablero.obtenerPieza(destino);
        assertNull(tablero.obtenerPieza(origen));
        assertEquals(TipoPieza.REINA, piezaPromocionada.getTipo());
        assertEquals(ColorPieza.BLANCO, piezaPromocionada.getColor());
        assertEquals(ColorPieza.NEGRO, partida.getTurnoActual());
    }

    @Test
    void noPermitePromocionarAReyNiOmitirEleccion() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion origen = new Posicion(1, 0);
        Posicion destino = new Posicion(0, 0);
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(peon, origen);
        Partida partida = new Partida(tablero);
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino, TipoPieza.REY));
        assertThrows(IllegalArgumentException.class, () -> partida.moverPieza(origen, destino));
        assertSame(peon, tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void promocionaPeonNegroACaballo() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(6, 0));
        Partida partida = new Partida(tablero);
        // primero juegan las blancas
        partida.moverPieza(new Posicion(7, 7), new Posicion(6, 7));
        // el peon negro llega a la ultima fila
        partida.moverPieza(new Posicion(6, 0), new Posicion(7, 0), TipoPieza.CABALLO);
        Pieza promocionada = tablero.obtenerPieza(new Posicion(7, 0));
        assertEquals(TipoPieza.CABALLO, promocionada.getTipo());
        assertEquals(ColorPieza.NEGRO, promocionada.getColor());
        assertNull(tablero.obtenerPieza(new Posicion(6, 0)));
        assertEquals(ColorPieza.BLANCO, partida.getTurnoActual());
    }

    @Test
    void permitePromocionarCapturandoUnaPieza() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), new Posicion(1, 0));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 1));
        Partida partida = new Partida(tablero);
        partida.moverPieza(new Posicion(1, 0), new Posicion(0, 1), TipoPieza.ALFIL);
        Pieza promocionada = tablero.obtenerPieza(new Posicion(0, 1));
        assertEquals(TipoPieza.ALFIL, promocionada.getTipo());
        assertEquals(ColorPieza.BLANCO, promocionada.getColor());
        assertNull(tablero.obtenerPieza(new Posicion(1, 0)));
        assertEquals(ColorPieza.NEGRO, partida.getTurnoActual());
    }

    @Test
    void registraCapturaDeUnaPieza() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        Pieza torreBlanca = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        tablero.colocarPieza(torreBlanca, new Posicion(4, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(4, 6));
        Partida partida = new Partida(tablero);
        assertEquals(0, partida.getCapturas(torreBlanca));
        partida.moverPieza(new Posicion(4, 4), new Posicion(4, 6));
        assertEquals(1, partida.getCapturas(torreBlanca));
        assertSame(torreBlanca, tablero.obtenerPieza(new Posicion(4, 6)));
    }

    @Test
    void conservaCapturasAlPromocionarPeon() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 7));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        Pieza peonBlanco = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(peonBlanco, new Posicion(2, 2));
        tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(1, 1));
        tablero.colocarPieza(creador.crear(TipoPieza.TORRE, ColorPieza.NEGRO), new Posicion(0, 0));
        Partida partida = new Partida(tablero);
        // Primera captura: el peon blanco captura en b7
        partida.moverPieza(new Posicion(2, 2), new Posicion(1, 1));
        assertEquals(1, partida.getCapturas(peonBlanco));
        partida.moverPieza(new Posicion(0, 7), new Posicion(1, 7));
        // Segunda captura: el peon captura en a8 y promociona
        partida.moverPieza(new Posicion(1, 1), new Posicion(0, 0), TipoPieza.REINA);
        Pieza reinaPromocionada = tablero.obtenerPieza(new Posicion(0, 0));
        assertEquals(TipoPieza.REINA, reinaPromocionada.getTipo());
        assertEquals(2, partida.getCapturas(reinaPromocionada));
        assertEquals(0, partida.getCapturas(peonBlanco));
    }

    @Test
    void activaKillstreakAlLlegarATresCapturas() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.BLANCO), new Posicion(7, 4));
        tablero.colocarPieza(creador.crear(TipoPieza.REY, ColorPieza.NEGRO), new Posicion(0, 7));
        Pieza torreBlanca = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        tablero.colocarPieza(torreBlanca, new Posicion(4, 3));

        // Tres piezas enemigas para capturar.
        for (int columna = 3; columna <= 5; columna++) {
            tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(3, columna));
        }
        Partida partida = new Partida(tablero);
        assertFalse(partida.tieneKillstreak(torreBlanca));

        // 1er captura
        partida.moverPieza(new Posicion(4, 3), new Posicion(3, 3));
        assertEquals(1, partida.getCapturas(torreBlanca));
        assertFalse(partida.tieneKillstreak(torreBlanca));
        partida.moverPieza(new Posicion(0, 7), new Posicion(0, 6));

        // 2da captura
        partida.moverPieza(new Posicion(3, 3), new Posicion(3, 4));
        assertEquals(2, partida.getCapturas(torreBlanca));
        assertFalse(partida.tieneKillstreak(torreBlanca));
        partida.moverPieza(new Posicion(0, 6), new Posicion(0, 7));

        // 3er captura
        partida.moverPieza(new Posicion(3, 4), new Posicion(3, 5));
        assertEquals(3, partida.getCapturas(torreBlanca));
        assertTrue(partida.tieneKillstreak(torreBlanca));
    }
}