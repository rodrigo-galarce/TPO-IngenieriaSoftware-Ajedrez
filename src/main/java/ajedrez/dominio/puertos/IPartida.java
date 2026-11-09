package ajedrez.dominio.puertos;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import java.util.List;

public interface IPartida {
    Pieza obtenerPieza(Posicion posicion);

    List<Posicion> obtenerMovimientosLegales(Posicion origen);;

    ColorPieza getTurnoActual();

    void moverPieza(Posicion origen, Posicion destino);

    void moverPieza(Posicion origen, Posicion destino, TipoPieza tipoPromocion);

    boolean estaEnJaque(ColorPieza color);

    boolean estaEnJaqueMate(ColorPieza color);

    boolean tieneKillstreak(Pieza pieza);

    int getSegundosRestantes(ColorPieza color);

    boolean estaFinalizada();

    boolean terminoEnTablas();

    ColorPieza getGanador();

    void avanzarUnSegundo();
}