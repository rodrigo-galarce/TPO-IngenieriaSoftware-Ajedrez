package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

public class MovimientoTorre implements IReglaMovimiento {
    @Override
    public boolean esMovimientoValido(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        if (!amenaza(tablero, origen, destino, color)) {
            return false;
        }
        Pieza piezaDestino = tablero.obtenerPieza(destino);
        // no se puede capturar una pieza del mismo color
        return piezaDestino == null || piezaDestino.getColor() != color;
    }

    @Override
    public boolean amenaza(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        if (tablero == null || origen == null || destino == null || color == null) {
            return false;
        }
        if (!tablero.estaDentro(origen) || !tablero.estaDentro(destino)) {
            return false;
        }
        Pieza piezaOrigen = tablero.obtenerPieza(origen);
        if (piezaOrigen == null || piezaOrigen.getColor() != color) {
            return false;
        }
        int diferenciaFila = destino.fila() - origen.fila();
        int diferenciaColumna = destino.columna() - origen.columna();
        // no se puede quedar en la misma casilla
        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }
        // movimientos horizontales o verticales
        if (diferenciaFila != 0 && diferenciaColumna != 0) {
            return false;
        }
        int pasoFila = Integer.compare(diferenciaFila, 0);
        int pasoColumna = Integer.compare(diferenciaColumna, 0);
        int fila = origen.fila() + pasoFila;
        int columna = origen.columna() + pasoColumna;
        // revisa las casillas del medio
        while (fila != destino.fila() || columna != destino.columna()) {
            Posicion intermedia = new Posicion(fila, columna);
            if (tablero.obtenerPieza(intermedia) != null) {
                return false;
            }
            fila += pasoFila;
            columna += pasoColumna;
        }
        return true;
    }
}