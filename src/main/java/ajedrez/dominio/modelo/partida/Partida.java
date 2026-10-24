package ajedrez.dominio.modelo.partida;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;
import ajedrez.dominio.puertos.IPartida;
import ajedrez.dominio.reglas.jaque.DetectorJaque;
import java.util.Objects;

public class Partida implements IPartida {
    private final Tablero tablero;
    private ColorPieza turnoActual;
    private final DetectorJaque detectorJaque;
    private final RelojPartida reloj;
    private ColorPieza ganador;

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
        this.ganador = null;
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
        boolean movimientoValido = pieza.getReglaMovimiento().esMovimientoValido(tablero, origen, destino, pieza.getColor());
        if (!movimientoValido) {
            throw new IllegalArgumentException("El movimiento no es valido");
        }
        tablero.moverPieza(origen, destino);
        if (turnoActual == ColorPieza.BLANCO) {
            turnoActual = ColorPieza.NEGRO;
        } else {
            turnoActual = ColorPieza.BLANCO;
        }
    }

    @Override
    public boolean estaEnJaque(ColorPieza color) {
        return detectorJaque.estaEnJaque(tablero, color);
    }

    @Override
    public int getSegundosRestantes(ColorPieza color) {
        return reloj.getSegundosRestantes(color);
    }

    @Override
    public boolean estaFinalizada() {
        return ganador != null;
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