package ajedrez;

import ajedrez.adaptadores.swing.VentanaAjedrez;
import ajedrez.dominio.modelo.creacion.ConfiguracionInicial;
import ajedrez.dominio.modelo.creacion.CreacionDePiezas;
import ajedrez.dominio.modelo.partida.Partida;
import ajedrez.dominio.modelo.tablero.Tablero;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tablero tablero = new Tablero(8, 8);
            CreacionDePiezas creador = new CreacionDePiezas();
            ConfiguracionInicial configuracion = new ConfiguracionInicial(creador);
            configuracion.colocarPiezas(tablero);
            Partida partida = new Partida(tablero);
            VentanaAjedrez ventana = new VentanaAjedrez(partida);
            ventana.mostrar();
        });
    }
}