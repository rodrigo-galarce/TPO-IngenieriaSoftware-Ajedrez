package ajedrez.dominio.modelo.partida;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;
import ajedrez.dominio.puertos.IPartida;
import ajedrez.dominio.reglas.jaque.DetectorJaque;
import ajedrez.dominio.modelo.creacion.CreacionDePiezas;
import java.util.Objects;

public class Partida implements IPartida {
    private final Tablero tablero;
    private ColorPieza turnoActual;
    private final DetectorJaque detectorJaque;
    private final RelojPartida reloj;
    private final CreacionDePiezas creadorPiezas;
    private ColorPieza ganador;
    private boolean tablasPorAhogado;

    public Partida(Tablero tablero) {
        this(tablero, new DetectorJaque(), new RelojPartida());
    }

    public Partida(Tablero tablero, DetectorJaque detectorJaque) {
        this(tablero, detectorJaque, new RelojPartida());
    }

    public Partida(Tablero tablero, DetectorJaque detectorJaque, RelojPartida reloj) {
        this.tablero = Objects.requireNonNull(tablero);
        this.detectorJaque = Objects.requireNonNull(detectorJaque);
        this.reloj = Objects.requireNonNull(reloj);
        this.turnoActual = ColorPieza.BLANCO;
        this.creadorPiezas = new CreacionDePiezas();
        this.ganador = null;
        this.tablasPorAhogado = false;
    }

    public Tablero getTablero() {
        return tablero;
    }

    @Override
    public Pieza obtenerPieza(Posicion posicion) {
        return tablero.obtenerPieza(posicion);
    }

    @Override
    public ColorPieza getTurnoActual() {
        return turnoActual;
    }

    @Override
    public void moverPieza(Posicion origen, Posicion destino) {
        moverPieza(origen, destino, null);
    }

    @Override
    public void moverPieza(Posicion origen, Posicion destino, TipoPieza tipoPromocion) {
        if (estaFinalizada()) {
            throw new IllegalStateException("La partida ya finalizo");
        }
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
        Pieza piezaDestino = tablero.obtenerPieza(destino);
        if (piezaDestino != null && piezaDestino.getTipo() == TipoPieza.REY) {
            throw new IllegalArgumentException("No se puede capturar al rey");
        }
        if (!esMovimientoLegal(tablero, origen, destino, turnoActual)) {
            throw new IllegalArgumentException("El movimiento no es valido o deja al rey en jaque");
        }
        // se comprueba si el peon llega a la ultima fila
        boolean promociona = pieza.getTipo() == TipoPieza.PEON && ((pieza.getColor() == ColorPieza.BLANCO && destino.fila() == 0)
                || (pieza.getColor() == ColorPieza.NEGRO && destino.fila() == tablero.getFilas() - 1));
        if (promociona) {
            if (tipoPromocion != TipoPieza.REINA && tipoPromocion != TipoPieza.TORRE && tipoPromocion != TipoPieza.ALFIL && tipoPromocion != TipoPieza.CABALLO) {
                throw new IllegalArgumentException("Debe seleccionar reina, torre, alfil o caballo");
            }
        } else if (tipoPromocion != null) {
            throw new IllegalArgumentException("Este movimiento no permite promocion");
        }
        ColorPieza jugadorQueMovio = turnoActual;
        tablero.moverPieza(origen, destino);
        // la promocion forma parte del mismo movimiento
        if (promociona) {
            Pieza nuevaPieza = creadorPiezas.crear(tipoPromocion, pieza.getColor());
            tablero.reemplazarPieza(nuevaPieza, destino);
        }
        if (turnoActual == ColorPieza.BLANCO) {
            turnoActual = ColorPieza.NEGRO;
        } else {
            turnoActual = ColorPieza.BLANCO;
        }
        if (estaEnJaqueMate(turnoActual)) {
            ganador = jugadorQueMovio;
        } else if (estaAhogado(turnoActual)) {
            tablasPorAhogado = true;
        }
    }

    private boolean esMovimientoLegal(Tablero tableroEvaluado, Posicion origen, Posicion destino, ColorPieza color) {
        if (!tableroEvaluado.estaDentro(origen) || !tableroEvaluado.estaDentro(destino)) {
            return false;
        }
        Pieza pieza = tableroEvaluado.obtenerPieza(origen);
        if (pieza == null || pieza.getColor() != color) {
            return false;
        }
        Pieza piezaDestino = tableroEvaluado.obtenerPieza(destino);
        if (piezaDestino != null) {
            if (piezaDestino.getColor() == color || piezaDestino.getTipo() == TipoPieza.REY) {
                return false;
            }
        }
        boolean movimientoValido = pieza.getReglaMovimiento().esMovimientoValido(tableroEvaluado, origen, destino, color);
        if (!movimientoValido) {
            return false;
        }
        Tablero tableroPrueba = tableroEvaluado.copiar();
        tableroPrueba.moverPieza(origen, destino);
        return !detectorJaque.estaEnJaque(tableroPrueba, color);
    }

    private boolean tieneMovimientosLegales(ColorPieza color) {
        for (int fila = 0; tablero.estaDentro(new Posicion(fila, 0)); fila++) {
            for (int columna = 0; tablero.estaDentro(new Posicion(0, columna)); columna++) {
                Posicion origen = new Posicion(fila, columna);
                Pieza pieza = tablero.obtenerPieza(origen);
                if (pieza == null || pieza.getColor() != color) {
                    continue;
                }
                for (int filaDestino = 0; tablero.estaDentro(new Posicion(filaDestino, 0)); filaDestino++) {
                    for (int columnaDestino = 0; tablero.estaDentro(new Posicion(0, columnaDestino)); columnaDestino++) {
                        Posicion destino = new Posicion(filaDestino, columnaDestino);
                        if (esMovimientoLegal(tablero, origen, destino, color)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean estaEnJaque(ColorPieza color) {
        return detectorJaque.estaEnJaque(tablero, color);
    }

    @Override
    public boolean estaEnJaqueMate(ColorPieza color) {
        if (!detectorJaque.estaEnJaque(tablero, color)) {
            return false;
        }
        return !tieneMovimientosLegales(color);
    }

    public boolean estaAhogado(ColorPieza color) {
        if (detectorJaque.estaEnJaque(tablero, color)) {
            return false;
        }
        return !tieneMovimientosLegales(color);
    }

    @Override
    public int getSegundosRestantes(ColorPieza color) {
        return reloj.getSegundosRestantes(color);
    }

    @Override
    public boolean estaFinalizada() {
        return ganador != null || tablasPorAhogado;
    }

    @Override
    public boolean terminoEnTablas() {
        return tablasPorAhogado;
    }

    @Override
    public ColorPieza getGanador() {
        return ganador;
    }

    @Override
    public void avanzarUnSegundo() {
        if (estaFinalizada()) {
            return;
        }
        reloj.descontarSegundo(turnoActual);
        if (reloj.tiempoAgotado(turnoActual)) {
            if (turnoActual == ColorPieza.BLANCO) {
                ganador = ColorPieza.NEGRO;
            } else {
                ganador = ColorPieza.BLANCO;
            }
        }
    }
}