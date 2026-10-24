package ajedrez.dominio.modelo.creacion;

import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import ajedrez.dominio.modelo.tablero.Tablero;
import java.util.Objects;

public class ConfiguracionInicial {
    private final CreacionDePiezas creador;

    public ConfiguracionInicial(CreacionDePiezas creador) {
        this.creador = Objects.requireNonNull(creador);
    }

    public void colocarPiezas(Tablero tablero) {
        Objects.requireNonNull(tablero);
        if (tablero.getFilas() != 8 || tablero.getColumnas() != 8) {
            throw new IllegalArgumentException("La configuracion inicial requiere un tablero de 8x8");
        }
        colocarPeones(tablero);
        colocarPiezasPrincipales(tablero);
    }

    private void colocarPeones(Tablero tablero) {
        for (int columna = 0; columna < 8; columna++) {
            tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.NEGRO), new Posicion(1, columna));
            tablero.colocarPieza(creador.crear(TipoPieza.PEON, ColorPieza.BLANCO), new Posicion(6, columna));
        }
    }

    private void colocarPiezasPrincipales(Tablero tablero) {
        TipoPieza[] orden = {
                TipoPieza.TORRE,
                TipoPieza.CABALLO,
                TipoPieza.ALFIL,
                TipoPieza.REINA,
                TipoPieza.REY,
                TipoPieza.ALFIL,
                TipoPieza.CABALLO,
                TipoPieza.TORRE
        };
        for (int columna = 0; columna < orden.length; columna++) {
            tablero.colocarPieza(creador.crear(orden[columna], ColorPieza.NEGRO), new Posicion(0, columna));
            tablero.colocarPieza(creador.crear(orden[columna], ColorPieza.BLANCO), new Posicion(7, columna));
        }
    }
}