package ajedrez.dominio.modelo.pieza;

import java.util.Objects;

public class Pieza {
    private final TipoPieza tipo;
    private final ColorPieza color;

    public Pieza(TipoPieza tipo, ColorPieza color) {
        this.tipo = Objects.requireNonNull(tipo);
        this.color = Objects.requireNonNull(color);
    }

    public TipoPieza getTipo() {
        return tipo;
    }

    public ColorPieza getColor() {
        return color;
    }
}