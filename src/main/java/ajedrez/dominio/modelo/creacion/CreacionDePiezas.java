package ajedrez.dominio.modelo.creacion;

import ajedrez.dominio.modelo.pieza.*;
import ajedrez.dominio.reglas.movimiento.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class CreacionDePiezas {
    private final Map<TipoPieza, Supplier<IReglaMovimiento>> estrategias;
    public CreacionDePiezas() {
        estrategias = new EnumMap<>(TipoPieza.class);
        registrar(TipoPieza.TORRE, MovimientoTorre::new);
        registrar(TipoPieza.ALFIL, MovimientoAlfil::new);
        registrar(TipoPieza.CABALLO, MovimientoCaballo::new);
        registrar(TipoPieza.REINA, MovimientoReina::new);
        registrar(TipoPieza.REY, MovimientoRey::new);
        registrar(TipoPieza.PEON, MovimientoPeon::new);
    }

    public void registrar(TipoPieza tipo, Supplier<IReglaMovimiento> creador) {
        Objects.requireNonNull(tipo);
        Objects.requireNonNull(creador);
        if (estrategias.containsKey(tipo)) {
            throw new IllegalArgumentException("Ya existe una estrategia para " + tipo);
        }
        estrategias.put(tipo, creador);
    }

    public Pieza crear(TipoPieza tipo, ColorPieza color) {
        Objects.requireNonNull(tipo);
        Objects.requireNonNull(color);
        Supplier<IReglaMovimiento> creador = estrategias.get(tipo);
        if (creador == null) {
            throw new IllegalArgumentException("No existe una estrategia para " + tipo);
        }
        IReglaMovimiento regla = Objects.requireNonNull(creador.get());
        return new Pieza(tipo, color, regla);
    }
}