package ajedrez.adaptadores.swing;

import ajedrez.dominio.modelo.partida.Partida;
import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaAjedrez {
    private final JFrame ventana;
    private final Partida partida;
    private final JLabel[][] casillas;
    private Posicion piezaSeleccionada;
    private final JLabel etiquetaEstado;

    public VentanaAjedrez(Partida partida) {
        this.partida = Objects.requireNonNull(partida);
        casillas = new JLabel[8][8];
        piezaSeleccionada = null;
        ventana = new JFrame("Ajedrez");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);

        JPanel panelTablero = new JPanel(new GridLayout(8, 8));
        Color colorClaro = new Color(240, 217, 181);
        Color colorOscuro = new Color(181, 136, 99);

        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                JLabel casilla = new JLabel();
                casilla.setPreferredSize(new Dimension(70, 70));
                casilla.setHorizontalAlignment(SwingConstants.CENTER);
                casilla.setVerticalAlignment(SwingConstants.CENTER);
                casilla.setOpaque(true);
                casilla.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 44));

                if ((fila + columna) % 2 == 0) {
                    casilla.setBackground(colorClaro);
                } else {
                    casilla.setBackground(colorOscuro);
                }

                Pieza pieza = partida.getTablero().obtenerPieza(new Posicion(fila, columna));
                casilla.setText(obtenerSimbolo(pieza));
                casillas[fila][columna] = casilla;
                // se guardan las coordenadas de la casilla
                final int filaActual = fila;
                final int columnaActual = columna;
                // se detecta cuando el usuario hace clic
                casilla.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        seleccionarCasilla(new Posicion(filaActual, columnaActual));
                    }
                });
                panelTablero.add(casilla);
            }
        }
        ventana.add(panelTablero);
        etiquetaEstado = new JLabel();
        etiquetaEstado.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaEstado.setFont(new Font("Arial", Font.BOLD, 16));
        etiquetaEstado.setPreferredSize(new Dimension(560, 40));
        ventana.add(etiquetaEstado, BorderLayout.SOUTH);
        actualizarEstado();
        ventana.pack();
        ventana.setLocationRelativeTo(null);
    }

    private String obtenerSimbolo(Pieza pieza) {
        if (pieza == null) {
            return "";
        }
        if (pieza.getColor() == ColorPieza.BLANCO) {
            return switch (pieza.getTipo()) {
                case PEON -> "♙";
                case TORRE -> "♖";
                case CABALLO -> "♘";
                case ALFIL -> "♗";
                case REINA -> "♕";
                case REY -> "♔";
            };
        } else {
            return switch (pieza.getTipo()) {
                case PEON -> "♟";
                case TORRE -> "♜";
                case CABALLO -> "♞";
                case ALFIL -> "♝";
                case REINA -> "♛";
                case REY -> "♚";
            };
        }
    }

    public void mostrar() {
        ventana.setVisible(true);
    }

    private void seleccionarCasilla(Posicion posicion) {
        Pieza pieza = partida.getTablero().obtenerPieza(posicion);
        // 1er clic - selecciona una pieza propia
        if (piezaSeleccionada == null) {
            if (pieza == null) {
                return;
            }
            if (pieza.getColor() != partida.getTurnoActual()) {
                return;
            }
            piezaSeleccionada = posicion;
            actualizarSeleccion();
            return;
        }
        // Clic sobre la misma pieza - cancela la seleccion
        if (posicion.equals(piezaSeleccionada)) {
            piezaSeleccionada = null;
            actualizarSeleccion();
            return;
        }

        // Clic sobre otra pieza propia - cambia la seleccion
        if (pieza != null && pieza.getColor() == partida.getTurnoActual()) {
            piezaSeleccionada = posicion;
            actualizarSeleccion();
            return;
        }

        // 2do clic - intenta realizar el movimiento
        try {
            partida.moverPieza(piezaSeleccionada, posicion);
            piezaSeleccionada = null;
            actualizarPiezas();
            actualizarSeleccion();
            actualizarEstado();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(ventana, e.getMessage(), "Movimiento invalido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarSeleccion() {
        Color colorClaro = new Color(240, 217, 181);
        Color colorOscuro = new Color(181, 136, 99);
        Color colorSeleccionado = new Color(130, 190, 120);

        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Posicion posicion = new Posicion(fila, columna);
                if (posicion.equals(piezaSeleccionada)) {
                    casillas[fila][columna].setBackground(colorSeleccionado);
                } else if ((fila + columna) % 2 == 0) {
                    casillas[fila][columna].setBackground(colorClaro);
                } else {
                    casillas[fila][columna].setBackground(colorOscuro);
                }
            }
        }
    }

    private void actualizarPiezas() {
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Posicion posicion = new Posicion(fila, columna);
                Pieza pieza = partida.getTablero().obtenerPieza(posicion);
                casillas[fila][columna].setText(obtenerSimbolo(pieza));
            }
        }
        ventana.setTitle("Ajedrez - Turno: " + partida.getTurnoActual());
    }

    private void actualizarEstado() {
        ColorPieza turno = partida.getTurnoActual();
        if (partida.estaEnJaque(turno)) {
            etiquetaEstado.setText("Turno: " + turno + " - JAQUE");
        } else {
            etiquetaEstado.setText("Turno: " + turno);
        }
    }
}