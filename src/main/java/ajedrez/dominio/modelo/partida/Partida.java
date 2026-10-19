package ajedrez.dominio.modelo.partida;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;

import java.util.Objects;

public class Partida {
    private final Tablero tablero;
    private ColorPieza turnoActual;

    public Partida(Tablero tablero) {
        this.tablero = Objects.requireNonNull(tablero);
        this.turnoActual = ColorPieza.BLANCO;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public ColorPieza getTurnoActual() {
        return turnoActual;
    }

    public void moverPieza(Posicion origen, Posicion destino) {
        if (!tablero.estaDentro(origen) || !tablero.estaDentro(destino)) {
            throw new IllegalArgumentException("La posicion esta fuera del tablero");
        }
        Pieza pieza = tablero.obtenerPieza(origen);
        if (pieza == null) {
            throw new IllegalArgumentException("No hay una pieza en el origen");
        }
        if (pieza.getColor() != turnoActual) {
            throw new IllegalArgumentException("No es el turno de esa pieza");
        }
        boolean movimientoValido = pieza.getReglaMovimiento().esMovimientoValido(tablero, origen, destino, pieza.getColor());
        if (!movimientoValido) {
            throw new IllegalArgumentException("El movimiento no es valido");
        }
        tablero.moverPieza(origen, destino);
        turnoActual = (turnoActual == ColorPieza.BLANCO) ? ColorPieza.NEGRO : ColorPieza.BLANCO;
    }
}