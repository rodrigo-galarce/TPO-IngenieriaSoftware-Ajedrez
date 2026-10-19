package ajedrez.dominio.reglas.movimiento;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

public class MovimientoReina implements IReglaMovimiento {
    private final IReglaMovimiento movimientoTorre;
    private final IReglaMovimiento movimientoAlfil;

    public MovimientoReina() {
        this.movimientoTorre = new MovimientoTorre();
        this.movimientoAlfil = new MovimientoAlfil();
    }

    @Override
    public boolean esMovimientoValido(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        return movimientoTorre.esMovimientoValido(tablero, origen, destino, color) || movimientoAlfil.esMovimientoValido(tablero, origen, destino, color);
    }

    @Override
    public boolean amenaza(Tablero tablero, Posicion origen, Posicion destino, ColorPieza color) {
        return movimientoTorre.amenaza(tablero, origen, destino, color) || movimientoAlfil.amenaza(tablero, origen, destino, color);
    }
}