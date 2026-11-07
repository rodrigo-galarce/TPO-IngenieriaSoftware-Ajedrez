package ajedrez.dominio.modelo.tablero;

import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.creacion.CreacionDePiezas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableroTest {
    private CreacionDePiezas creador;

    @BeforeEach
    void prepararEscenario() {
        creador = new CreacionDePiezas();
    }

    @Test
    void tableroNuevoEstaVacio() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Posicion posicion = new Posicion(0, 0);
        // act
        Pieza pieza = tablero.obtenerPieza(posicion);
        // assert
        assertNull(pieza);
    }

    @Test
    void permiteDimensionesConfigurables() {
        // arrange
        Tablero tablero = new Tablero(10, 8);
        // act y assert
        assertEquals(10, tablero.getFilas());
        assertEquals(8, tablero.getColumnas());
    }

    @Test
    void permiteColocarUnaPieza() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        Posicion posicion = new Posicion(7, 0);
        // act
        tablero.colocarPieza(torre, posicion);
        // assert
        assertSame(torre, tablero.obtenerPieza(posicion));
    }

    @Test
    void rechazaPosicionesFueraDelTablero() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Posicion posicion = new Posicion(8, 0);
        // act y assert
        assertThrows(IllegalArgumentException.class,() -> tablero.obtenerPieza(posicion));
    }

    @Test
    void noPermiteColocarEnCasillaOcupada() {
        // arrange
        Tablero tablero = new Tablero(8, 8);
        Posicion posicion = new Posicion(0, 0);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        Pieza caballo = creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO);
        tablero.colocarPieza(torre, posicion);
        // act y assert
        assertThrows(IllegalStateException.class,() -> tablero.colocarPieza(caballo, posicion));
    }

    @Test
    void muevePiezaACasillaVacia() {
        Tablero tablero = new Tablero(8, 8);
        Posicion origen = new Posicion(6, 0);
        Posicion destino = new Posicion(5, 0);
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        tablero.colocarPieza(peon, origen);
        tablero.moverPieza(origen, destino);
        assertNull(tablero.obtenerPieza(origen));
        assertSame(peon, tablero.obtenerPieza(destino));
    }

    @Test
    void permiteCapturarPiezaEnemiga() {
        Tablero tablero = new Tablero(8, 8);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 5);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        Pieza enemigo = creador.crear(TipoPieza.CABALLO, ColorPieza.NEGRO);
        tablero.colocarPieza(torre, origen);
        tablero.colocarPieza(enemigo, destino);
        tablero.moverPieza(origen, destino);
        assertNull(tablero.obtenerPieza(origen));
        assertSame(torre, tablero.obtenerPieza(destino));
    }

    @Test
    void noPermiteCapturarPiezaAliada() {
        Tablero tablero = new Tablero(8, 8);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 5);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        Pieza aliada = creador.crear(TipoPieza.CABALLO, ColorPieza.BLANCO);
        tablero.colocarPieza(torre, origen);
        tablero.colocarPieza(aliada, destino);
        assertThrows(IllegalArgumentException.class, () -> tablero.moverPieza(origen, destino));
        assertSame(torre, tablero.obtenerPieza(origen));
        assertSame(aliada, tablero.obtenerPieza(destino));
    }

    @Test
    void noPermiteMoverDesdeCasillaVacia() {
        Tablero tablero = new Tablero(8, 8);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 5);
        assertThrows(IllegalArgumentException.class, () -> tablero.moverPieza(origen, destino));
        assertNull(tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
    }

    @Test
    void noPermiteMoverFueraDelTablero() {
        Tablero tablero = new Tablero(8, 8);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(8, 4);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        tablero.colocarPieza(torre, origen);
        assertThrows(IllegalArgumentException.class, () -> tablero.moverPieza(origen, destino));
        assertSame(torre, tablero.obtenerPieza(origen));
    }

    @Test
    void noPermiteMoverALaMismaCasilla() {
        Tablero tablero = new Tablero(8, 8);
        Posicion origen = new Posicion(4, 4);
        Pieza rey = creador.crear(TipoPieza.REY, ColorPieza.BLANCO);
        tablero.colocarPieza(rey, origen);
        assertThrows(IllegalArgumentException.class, () -> tablero.moverPieza(origen, origen));
        assertSame(rey, tablero.obtenerPieza(origen));
    }

    @Test
    void copiarTableroPermiteMoverSinModificarOriginal() {
        Tablero tablero = new Tablero(8, 8);
        Pieza torre = creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        Posicion origen = new Posicion(4, 4);
        Posicion destino = new Posicion(4, 5);
        tablero.colocarPieza(torre, origen);
        Tablero copia = tablero.copiar();
        copia.moverPieza(origen, destino);
        assertSame(torre, tablero.obtenerPieza(origen));
        assertNull(tablero.obtenerPieza(destino));
        assertNull(copia.obtenerPieza(origen));
        assertSame(torre, copia.obtenerPieza(destino));
    }

    @Test
    void reemplazarPiezaConservaPosicionYColor() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        Posicion posicion = new Posicion(0, 0);
        Pieza peon = creador.crear(TipoPieza.PEON, ColorPieza.BLANCO);
        Pieza reina = creador.crear(TipoPieza.REINA, ColorPieza.BLANCO);
        tablero.colocarPieza(peon, posicion);
        tablero.reemplazarPieza(reina, posicion);
        assertSame(reina, tablero.obtenerPieza(posicion));
        assertEquals(TipoPieza.REINA, tablero.obtenerPieza(posicion).getTipo());
        assertEquals(ColorPieza.BLANCO, tablero.obtenerPieza(posicion).getColor());
    }

    @Test
    void noPermiteReemplazarUnaCasillaVacia() {
        Tablero tablero = new Tablero(8, 8);
        CreacionDePiezas creador = new CreacionDePiezas();
        Pieza reina = creador.crear(TipoPieza.REINA, ColorPieza.BLANCO);
        Posicion posicion = new Posicion(0, 0);
        assertThrows(IllegalStateException.class, () -> tablero.reemplazarPieza(reina, posicion));
    }
}