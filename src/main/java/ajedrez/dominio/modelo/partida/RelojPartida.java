package ajedrez.dominio.modelo.partida;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import java.util.Objects;

public class RelojPartida {
    private int segundosBlancas;
    private int segundosNegras;

    public RelojPartida() {
        this(180);
    }

    public RelojPartida(int segundosIniciales) {
        if (segundosIniciales <= 0) {
            throw new IllegalArgumentException("El tiempo inicial debe ser mayor a cero");
        }
        segundosBlancas = segundosIniciales;
        segundosNegras = segundosIniciales;
    }

    public int getSegundosRestantes(ColorPieza color) {
        Objects.requireNonNull(color);
        if (color == ColorPieza.BLANCO) {
            return segundosBlancas;
        } else {
            return segundosNegras;
        }
    }

    public void descontarSegundo(ColorPieza color) {
        Objects.requireNonNull(color);
        if (color == ColorPieza.BLANCO) {
            if (segundosBlancas > 0) {
                segundosBlancas--;
            }
        } else {
            if (segundosNegras > 0) {
                segundosNegras--;
            }
        }
    }

    public boolean tiempoAgotado(ColorPieza color) {
        return getSegundosRestantes(color) == 0;
    }
}
