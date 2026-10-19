package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

public class MovimientoRey implements ReglaMovimiento {
    @Override
    public boolean esMovimientoValido(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        if (!amenaza(tablero, origen, destino, color)) {
            return false;
        }
        Pieza piezaDestino = tablero.obtenerPieza(destino);
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
        int diferenciaFila = Math.abs(destino.fila() - origen.fila());
        int diferenciaColumna = Math.abs(destino.columna() - origen.columna());
        if (diferenciaFila == 0 && diferenciaColumna == 0) {
            return false;
        }
        // maximo una casilla en cada direccion
        return diferenciaFila <= 1 && diferenciaColumna <= 1;
    }
}