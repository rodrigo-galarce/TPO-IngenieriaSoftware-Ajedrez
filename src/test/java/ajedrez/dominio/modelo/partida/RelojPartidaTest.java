package ajedrez.dominio.modelo.partida;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RelojPartidaTest {
    @Test
    void ambosJugadoresComienzanConTresMinutos() {
        RelojPartida reloj = new RelojPartida();
        assertEquals(180, reloj.getSegundosRestantes(ColorPieza.BLANCO));
        assertEquals(180, reloj.getSegundosRestantes(ColorPieza.NEGRO));
    }

    @Test
    void descuentaTiempoSoloAlJugadorIndicado() {
        RelojPartida reloj = new RelojPartida();
        reloj.descontarSegundo(ColorPieza.BLANCO);
        assertEquals(179, reloj.getSegundosRestantes(ColorPieza.BLANCO));
        assertEquals(180, reloj.getSegundosRestantes(ColorPieza.NEGRO));
    }

    @Test
    void noDescuentaTiempoPorDebajoDeCero() {
        RelojPartida reloj = new RelojPartida(2);
        reloj.descontarSegundo(ColorPieza.BLANCO);
        reloj.descontarSegundo(ColorPieza.BLANCO);
        reloj.descontarSegundo(ColorPieza.BLANCO);
        assertEquals(0, reloj.getSegundosRestantes(ColorPieza.BLANCO));
        assertTrue(reloj.tiempoAgotado(ColorPieza.BLANCO));
        assertFalse(reloj.tiempoAgotado(ColorPieza.NEGRO));
    }
}