
package ajedrez.dominio.modelo.tablero;

import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.pieza.ColorPieza;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableroTest {
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
        Pieza torre = new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO);
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
        Pieza torre = new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO);
        Pieza caballo = new Pieza(TipoPieza.CABALLO, ColorPieza.NEGRO);
        tablero.colocarPieza(torre, posicion);
        // act y assert
        assertThrows(IllegalStateException.class,() -> tablero.colocarPieza(caballo, posicion));
    }
}