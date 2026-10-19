package ajedrez.dominio.modelo.pieza;

import ajedrez.dominio.reglas.movimiento.IReglaMovimiento;
import java.util.Objects;

public class Pieza {
    private final TipoPieza tipo;
    private final ColorPieza color;
    private final IReglaMovimiento reglaMovimiento;

    // constructor temporal para mantener los tests existentes
    public Pieza(TipoPieza tipo, ColorPieza color) {
        this(tipo, color, null);
    }

    // constructor por inyeccion de dependencias
    public Pieza(TipoPieza tipo, ColorPieza color, IReglaMovimiento reglaMovimiento) {
        this.tipo = Objects.requireNonNull(tipo);
        this.color = Objects.requireNonNull(color);
        this.reglaMovimiento = reglaMovimiento;
    }

    public TipoPieza getTipo() {
        return tipo;
    }

    public ColorPieza getColor() {
        return color;
    }

    public IReglaMovimiento getReglaMovimiento() {
        return reglaMovimiento;
    }
}
