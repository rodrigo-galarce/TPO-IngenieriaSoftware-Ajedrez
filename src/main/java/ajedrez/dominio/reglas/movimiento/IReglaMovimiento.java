
package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.tablero.Tablero;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.pieza.ColorPieza;

public interface IReglaMovimiento {
    boolean esMovimientoValido(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color);
    boolean amenaza(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color);
}
