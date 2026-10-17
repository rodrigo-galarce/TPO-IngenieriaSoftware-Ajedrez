
package ajedrez.dominio.modelo.tablero;

import ajedrez.dominio.modelo.pieza.Pieza;
import java.util.Objects;

public class Tablero {
    private final int filas;
    private final int columnas;
    private final Pieza[][] casillas;

    public Tablero(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("Las dimensiones del tablero deben ser positivas");
        }
        this.filas = filas;
        this.columnas = columnas;
        this.casillas = new Pieza[filas][columnas];
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public boolean estaDentro(Posicion posicion) {
        Objects.requireNonNull(posicion);
        return posicion.fila() >= 0
                && posicion.fila() < filas
                && posicion.columna() >= 0
                && posicion.columna() < columnas;
    }

    public Pieza obtenerPieza(Posicion posicion) {
        validarPosicion(posicion);
        return casillas[posicion.fila()][posicion.columna()];
    }

    public void colocarPieza(Pieza pieza, Posicion posicion) {
        Objects.requireNonNull(pieza, "La pieza no puede ser null");
        validarPosicion(posicion);
        if (obtenerPieza(posicion) != null) {
            throw new IllegalStateException("La casilla está ocupada");
        }
        casillas[posicion.fila()][posicion.columna()] = pieza;
    }

    private void validarPosicion(Posicion posicion) {
        if (!estaDentro(posicion)) {
            throw new IllegalArgumentException("Error. La posición está fuera del tablero");
        }
    }
}