package ajedrez.dominio.modelo.creacion;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfiguracionInicialTest {
    @Test
    void colocaLas32PiezasEnElTablero() {
        Tablero tablero = new Tablero(8, 8);
        ConfiguracionInicial configuracion = new ConfiguracionInicial(new CreacionDePiezas());
        configuracion.colocarPiezas(tablero);
        int cantidadPiezas = 0;
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                if (tablero.obtenerPieza(new Posicion(fila, columna)) != null) {
                    cantidadPiezas++;
                }
            }
        }
        assertEquals(32, cantidadPiezas);
    }

    @Test
    void colocaLosReyesEnSusPosicionesIniciales() {
        Tablero tablero = new Tablero(8, 8);
        ConfiguracionInicial configuracion = new ConfiguracionInicial(new CreacionDePiezas());
        configuracion.colocarPiezas(tablero);
        Pieza reyNegro = tablero.obtenerPieza(new Posicion(0, 4));
        Pieza reyBlanco = tablero.obtenerPieza(new Posicion(7, 4));
        assertEquals(TipoPieza.REY, reyNegro.getTipo());
        assertEquals(ColorPieza.NEGRO, reyNegro.getColor());
        assertEquals(TipoPieza.REY, reyBlanco.getTipo());
        assertEquals(ColorPieza.BLANCO, reyBlanco.getColor());
    }

    @Test
    void colocaLosPeonesEnSusFilasIniciales() {
        Tablero tablero = new Tablero(8, 8);
        ConfiguracionInicial configuracion = new ConfiguracionInicial(new CreacionDePiezas());
        configuracion.colocarPiezas(tablero);
        for (int columna = 0; columna < 8; columna++) {
            Pieza peonNegro = tablero.obtenerPieza(new Posicion(1, columna));
            Pieza peonBlanco = tablero.obtenerPieza(new Posicion(6, columna));
            assertEquals(TipoPieza.PEON, peonNegro.getTipo());
            assertEquals(ColorPieza.NEGRO, peonNegro.getColor());
            assertEquals(TipoPieza.PEON, peonBlanco.getTipo());
            assertEquals(ColorPieza.BLANCO, peonBlanco.getColor());
        }
    }

    @Test
    void colocaLasPiezasPrincipalesEnElOrdenCorrecto() {
        Tablero tablero = new Tablero(8, 8);
        ConfiguracionInicial configuracion = new ConfiguracionInicial(new CreacionDePiezas());
        configuracion.colocarPiezas(tablero);
        TipoPieza[] ordenEsperado = {
                TipoPieza.TORRE,
                TipoPieza.CABALLO,
                TipoPieza.ALFIL,
                TipoPieza.REINA,
                TipoPieza.REY,
                TipoPieza.ALFIL,
                TipoPieza.CABALLO,
                TipoPieza.TORRE};
        for (int columna = 0; columna < 8; columna++) {
            Pieza piezaNegra = tablero.obtenerPieza(new Posicion(0, columna));
            Pieza piezaBlanca = tablero.obtenerPieza(new Posicion(7, columna));
            assertEquals(ordenEsperado[columna], piezaNegra.getTipo());
            assertEquals(ColorPieza.NEGRO, piezaNegra.getColor());
            assertEquals(ordenEsperado[columna], piezaBlanca.getTipo());
            assertEquals(ColorPieza.BLANCO, piezaBlanca.getColor());
        }
    }

    @Test
    void rechazaTableroQueNoSeaDeOchoPorOcho() {
        Tablero tablero = new Tablero(10, 8);
        ConfiguracionInicial configuracion = new ConfiguracionInicial(new CreacionDePiezas());
        assertThrows(IllegalArgumentException.class, () -> configuracion.colocarPiezas(tablero));
    }
}