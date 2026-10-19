package ajedrez.dominio.reglas.jaque;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

import java.util.Objects;

public class DetectorJaque {

    public boolean estaEnJaque(Tablero tablero, ColorPieza colorRey) {
        Objects.requireNonNull(tablero);
        Objects.requireNonNull(colorRey);
        Posicion posicionRey = buscarRey(tablero, colorRey);
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion origen = new Posicion(fila, columna);
                Pieza pieza = tablero.obtenerPieza(origen);
                if (pieza == null || pieza.getColor() == colorRey) {
                    continue;
                }
                if (pieza.getReglaMovimiento().amenaza(tablero, origen, posicionRey, pieza.getColor())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Posicion buscarRey(Tablero tablero, ColorPieza colorRey) {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                Pieza pieza = tablero.obtenerPieza(posicion);
                if (pieza != null && pieza.getTipo() == TipoPieza.REY && pieza.getColor() == colorRey) {
                    return posicion;
                }
            }
        }
        throw new IllegalStateException("No se encontro el rey del color " + colorRey);
    }
}
