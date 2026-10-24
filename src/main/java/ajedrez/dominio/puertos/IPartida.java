package ajedrez.dominio.puertos;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;

public interface IPartida {
    Pieza obtenerPieza(Posicion posicion);;

    ColorPieza getTurnoActual();

    void moverPieza(Posicion origen, Posicion destino);

    boolean estaEnJaque(ColorPieza color);

    int getSegundosRestantes(ColorPieza color);

    boolean estaFinalizada();

    ColorPieza getGanador();

    void avanzarUnSegundo();
}