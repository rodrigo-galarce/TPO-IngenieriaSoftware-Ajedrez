package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

public class MovimientoPeon implements IReglaMovimiento {
    @Override
    public boolean esMovimientoValido(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        if (!datosValidos(tablero, origen, destino, color)) {
            return false;
        }
        int direccion = obtenerDireccion(color);
        int diferenciaFila = destino.fila() - origen.fila();
        int diferenciaColumna = destino.columna() - origen.columna();
        Pieza piezaDestino = tablero.obtenerPieza(destino);
        // captura diagonal: debe haber una pieza enemiga
        if (amenaza(tablero, origen, destino, color)) {
            return piezaDestino != null && piezaDestino.getColor() != color;
        }
        // el avance normal debe ser vertical
        if (diferenciaColumna != 0) {
            return false;
        }
        // avance de una casilla
        if (diferenciaFila == direccion) {
            return piezaDestino == null;
        }

        // avance de dos casillas desde la fila inicial
        int filaInicial = color == ColorPieza.BLANCO
                ? tablero.getFilas() - 2
                : 1;
        if (origen.fila() == filaInicial && diferenciaFila == 2 * direccion) {
            Posicion intermedia = new Posicion(origen.fila() + direccion, origen.columna());
            return tablero.obtenerPieza(intermedia) == null && piezaDestino == null;
        }
        return false;
    }

    @Override
    public boolean amenaza(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        if (!datosValidos(tablero, origen, destino, color)) {
            return false;
        }
        int direccion = obtenerDireccion(color);
        int diferenciaFila = destino.fila() - origen.fila();
        int diferenciaColumna = Math.abs(destino.columna() - origen.columna());
        return diferenciaFila == direccion && diferenciaColumna == 1;
    }

    private int obtenerDireccion(ColorPieza color) {
        return color == ColorPieza.BLANCO ? -1 : 1;
    }

    private boolean datosValidos(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        if (tablero == null || origen == null || destino == null || color == null) {
            return false;
        }
        if (!tablero.estaDentro(origen) || !tablero.estaDentro(destino)) {
            return false;
        }
        Pieza piezaOrigen = tablero.obtenerPieza(origen);
        return piezaOrigen != null && piezaOrigen.getColor() == color;
    }
}