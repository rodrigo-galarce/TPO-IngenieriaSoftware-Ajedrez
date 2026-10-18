package ajedrez.dominio.modelo.pieza;

import ajedrez.dominio.reglas.movimiento.ReglaMovimiento;
import java.util.Objects;

public class Pieza {
    private final TipoPieza tipo;
    private final ColorPieza color;
    private final ReglaMovimiento reglaMovimiento;

    // constructor temporal para mantener los tests existentes
    public Pieza(TipoPieza tipo, ColorPieza color) {
        this(tipo, color, null);
    }

    // constructor por inyeccion de dependencias
    public Pieza(TipoPieza tipo, ColorPieza color, ReglaMovimiento reglaMovimiento) {
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

    public ReglaMovimiento getReglaMovimiento() {
        return reglaMovimiento;
    }
}
