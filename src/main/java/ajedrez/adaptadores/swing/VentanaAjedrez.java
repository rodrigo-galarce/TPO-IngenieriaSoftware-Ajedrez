package ajedrez.adaptadores.swing;

import ajedrez.dominio.modelo.pieza.TipoPieza;
import ajedrez.dominio.puertos.IPartida;
import ajedrez.dominio.modelo.pieza.ColorPieza;
import ajedrez.dominio.modelo.pieza.Pieza;
import ajedrez.dominio.modelo.tablero.Posicion;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class VentanaAjedrez {
    private static final int FILAS = 8;
    private static final int COLUMNAS = 8;

    // tamaño visual del tablero en pantalla
    private static final int ANCHO_TABLERO_VISUAL = 820;
    private static final int ALTO_TABLERO_VISUAL = 820;

    // zona interna donde están las 64 casillas dentro del png
    private static final int ZONA_CASILLAS_X = 272;
    private static final int ZONA_CASILLAS_Y = 208;
    private static final int ZONA_CASILLAS_ANCHO = 1508;
    private static final int ZONA_CASILLAS_ALTO = 1495;

    private final JFrame ventana;
    private final IPartida partida;
    private final JLabel[][] casillas;
    private Posicion piezaSeleccionada;
    private final JLabel etiquetaEstado;
    private final JLabel relojBlancas;
    private final JLabel relojNegras;
    private final Timer temporizador;
    private final Map<String, ImageIcon> iconosPiezas = new HashMap<>();
    private List<Posicion> movimientosDisponibles = List.of();

    public VentanaAjedrez(IPartida partida) {
        this.partida = Objects.requireNonNull(partida);
        casillas = new JLabel[8][8];
        piezaSeleccionada = null;
        ventana = new JFrame("Ajedrez");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);

        Image imagenFondo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/fondo.png"), "No se encontro la imagen del fondo")).getImage();
        JPanel panelFondo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int anchoImagen = imagenFondo.getWidth(this);
                int altoImagen = imagenFondo.getHeight(this);
                double escala = Math.max((double) getWidth() / anchoImagen, (double) getHeight() / altoImagen);
                int ancho = (int) Math.ceil(anchoImagen * escala);
                int alto = (int) Math.ceil(altoImagen * escala);
                int x = (getWidth() - ancho) / 2;
                int y = (getHeight() - alto) / 2;
                g.drawImage(imagenFondo, x, y, ancho, alto, this);
            }
        };
        ventana.setContentPane(panelFondo);

        Image imagenTablero = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/tablero.png"),"No se encontro la imagen del tablero")).getImage();
        JPanel panelTablero = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);g.drawImage(imagenTablero, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelTablero.setOpaque(false);
        panelTablero.setPreferredSize(new Dimension(ANCHO_TABLERO_VISUAL, ALTO_TABLERO_VISUAL));

        // casillas
        double escalaX = (double) ANCHO_TABLERO_VISUAL / 2048;
        double escalaY = (double) ALTO_TABLERO_VISUAL / 2048;

        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                final int filaActual = fila;
                final int columnaActual = columna;
                JLabel casilla = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        Posicion posicion = new Posicion(filaActual, columnaActual);

                        // resaltar la pieza seleccionada en celeste
                        if (posicion.equals(piezaSeleccionada)) {
                            g2.setColor(new Color(100, 190, 255, 45));
                            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                            g2.setColor(new Color(130, 210, 255, 190));
                            g2.setStroke(new BasicStroke(2.5f));
                            g2.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 12, 12);
                        }
                        // dibujar los destinos legales
                        if (movimientosDisponibles.contains(posicion)) {
                            Pieza piezaDestino = partida.obtenerPieza(posicion);
                            if (piezaDestino == null) {
                                // casilla libre: circulo celeste en el centro
                                int diametro = Math.min(getWidth(), getHeight()) / 4;
                                int x = (getWidth() - diametro) / 2;
                                int y = (getHeight() - diametro) / 2;
                                g2.setColor(new Color(115, 205, 255, 170));
                                g2.fillOval(x, y, diametro, diametro);
                            } else {
                                // captura: anillo alrededor de la pieza enemiga
                                int margen = 6;
                                g2.setColor(new Color(115, 205, 255, 210));
                                g2.setStroke(new BasicStroke(4f));
                                g2.drawOval(margen, margen, getWidth() - margen * 2, getHeight() - margen * 2);
                            }
                        }
                        g2.dispose();
                    }
                };
                casilla.setHorizontalAlignment(SwingConstants.CENTER);
                casilla.setVerticalAlignment(SwingConstants.BOTTOM);
                casilla.setOpaque(false);
                casilla.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 44));

                int x = (int) Math.round((ZONA_CASILLAS_X + columna * ZONA_CASILLAS_ANCHO / 8.0) * escalaX);
                int y = (int) Math.round((ZONA_CASILLAS_Y + fila * ZONA_CASILLAS_ALTO / 8.0) * escalaY);
                int xFinal = (int) Math.round((ZONA_CASILLAS_X + (columna + 1) * ZONA_CASILLAS_ANCHO / 8.0) * escalaX);
                int yFinal = (int) Math.round((ZONA_CASILLAS_Y + (fila + 1) * ZONA_CASILLAS_ALTO / 8.0) * escalaY);
                casilla.setBounds(x, y, xFinal - x, yFinal - y);

                Pieza pieza = partida.obtenerPieza(new Posicion(fila, columna));
                casilla.setIcon(obtenerIcono(pieza));

                casillas[fila][columna] = casilla;

                casilla.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        seleccionarCasilla(new Posicion(filaActual, columnaActual));
                    }
                });
                panelTablero.add(casilla);
            }
        }

        JPanel contenedorTablero = new JPanel(new GridBagLayout());
        contenedorTablero.setOpaque(false);
        contenedorTablero.add(panelTablero);
        ventana.add(contenedorTablero, BorderLayout.CENTER);

        // se crean los dos relojes
        relojBlancas = new JLabel();
        relojNegras = new JLabel();
        relojBlancas.setHorizontalAlignment(SwingConstants.CENTER);
        relojNegras.setHorizontalAlignment(SwingConstants.CENTER);
        Font fuenteReloj = new Font("Arial", Font.BOLD, 22);
        relojBlancas.setFont(fuenteReloj);
        relojNegras.setFont(fuenteReloj);

        // panel para poner los relojes uno al lado del otro
        JPanel panelRelojes = new JPanel(new GridLayout(1, 2));
        panelRelojes.add(relojBlancas);
        panelRelojes.add(relojNegras);

        // etiqueta para el turno y el jaque
        etiquetaEstado = new JLabel();
        etiquetaEstado.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaEstado.setFont(new Font("Arial", Font.BOLD, 16));
        etiquetaEstado.setPreferredSize(new Dimension(560, 40));

        // panel inferior con relojes y estado
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelRelojes, BorderLayout.NORTH);
        panelInferior.add(etiquetaEstado, BorderLayout.SOUTH);
        ventana.add(panelInferior, BorderLayout.SOUTH);

        // cada segundo le avisamos a Partida que transcurrio tiempo
        temporizador = new Timer(1000, e -> {
            partida.avanzarUnSegundo();
            actualizarRelojes();
            actualizarEstado();
            if (partida.estaFinalizada()) {
                ((Timer) e.getSource()).stop();;
            }
        });
        actualizarRelojes();
        actualizarEstado();
        ventana.pack();
        ventana.setLocationRelativeTo(null);
    }

    private ImageIcon obtenerIcono(Pieza pieza) {
        if (pieza == null) {
            return null;
        }
        String color;
        String carpeta;
        if (pieza.getColor() == ColorPieza.BLANCO) {
            color = "blanco";
            carpeta = "blancas";
        } else {
            color = "negro";
            carpeta = "negras";
        }
        String tipo = pieza.getTipo().name().toLowerCase();
        String nombreArchivo = color + "_" + tipo;
        // primero buscamos si ya tenemos la imagen cargada
        if (iconosPiezas.containsKey(nombreArchivo)) {
            return iconosPiezas.get(nombreArchivo);
        }
        String ruta = "/imagenes/piezas/" + carpeta + "/" + nombreArchivo + ".png";
        URL recurso = getClass().getResource(ruta);
        if (recurso == null) {
            throw new IllegalStateException("No se encontro la imagen: " + ruta);
        }
        ImageIcon original = new ImageIcon(recurso);
        int anchoOriginal = original.getIconWidth();
        int altoOriginal = original.getIconHeight();
        if (anchoOriginal <= 0 || altoOriginal <= 0) {
            throw new IllegalStateException("No se pudo cargar la imagen: " + ruta);
        }
        // tamaño maximo de las piezas dentro de la casilla
        int tamanoMaximo = 68;
        double escala = Math.min((double) tamanoMaximo / anchoOriginal, (double) tamanoMaximo / altoOriginal);
        int ancho = Math.max(1, (int) Math.round(anchoOriginal * escala));
        int alto = Math.max(1, (int) Math.round(altoOriginal * escala));
        Image imagenEscalada = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        ImageIcon icono = new ImageIcon(imagenEscalada);
        iconosPiezas.put(nombreArchivo, icono);
        return icono;
    }

    public void mostrar() {
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setVisible(true);
        temporizador.start();
    }

    private void seleccionarCasilla(Posicion posicion) {
        if (partida.estaFinalizada()) {
            return;
        }
        Pieza pieza = partida.obtenerPieza(posicion);
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
            Pieza piezaOrigen = partida.obtenerPieza(piezaSeleccionada);
            boolean promociona = piezaOrigen.getTipo() == TipoPieza.PEON && ((piezaOrigen.getColor() == ColorPieza.BLANCO && posicion.fila() == 0)
                    || (piezaOrigen.getColor() == ColorPieza.NEGRO && posicion.fila() == 7));
            if (promociona) {
                TipoPieza tipoElegido = elegirPromocion();
                if (tipoElegido == null) {
                    return;
                }
                partida.moverPieza(piezaSeleccionada, posicion, tipoElegido);
            } else {
                partida.moverPieza(piezaSeleccionada, posicion);
            }
            piezaSeleccionada = null;
            actualizarPiezas();
            actualizarSeleccion();
            actualizarEstado();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(ventana, e.getMessage(), "Movimiento invalido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private TipoPieza elegirPromocion() {
        String[] opciones = {"QUEEN", "ROOK", "BISHOP", "KNIGHT"};
        int eleccion = JOptionPane.showOptionDialog(ventana, "Choose your promotion piece:", "PAWN PROMOTION", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,null, opciones,opciones[0]);
        if (eleccion == 0) {
            return TipoPieza.REINA;
        } else if (eleccion == 1) {
            return TipoPieza.TORRE;
        } else if (eleccion == 2) {
            return TipoPieza.ALFIL;
        } else if (eleccion == 3) {
            return TipoPieza.CABALLO;
        }
        return null;
    }

    private void actualizarSeleccion() {
        if (piezaSeleccionada == null) {
            movimientosDisponibles = List.of();
        } else {
            movimientosDisponibles = partida.obtenerMovimientosLegales(piezaSeleccionada);
        }
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                casillas[fila][columna].repaint();
            }
        }
    }

    private void actualizarPiezas() {
        for (int fila = 0; fila < 8; fila++) {
            for (int columna = 0; columna < 8; columna++) {
                Posicion posicion = new Posicion(fila, columna);
                Pieza pieza = partida.obtenerPieza(posicion);
                casillas[fila][columna].setIcon(obtenerIcono(pieza));
            }
        }
        ventana.setTitle("Ajedrez - Turno: " + partida.getTurnoActual());
    }

    private void actualizarEstado() {
        ColorPieza turno = partida.getTurnoActual();
        if (partida.estaFinalizada()) {
            if (partida.terminoEnTablas()) {
                etiquetaEstado.setText("STALEMATE");
                return;
            }
            ColorPieza ganador = partida.getGanador();
            if (partida.estaEnJaqueMate(turno)) {
                etiquetaEstado.setText("CHECKMATE - Ganador: " + ganador);
            } else {
                etiquetaEstado.setText("TIEMPO AGOTADO - Ganador: " + ganador);
            }
            return;
        }
        if (partida.estaEnJaque(turno)) {
            etiquetaEstado.setText("Turno: " + turno + " - CHECK");
        } else {
            etiquetaEstado.setText("Turno: " + turno);
        }
    }

    private void actualizarRelojes() {
        int segundosBlancas = partida.getSegundosRestantes(ColorPieza.BLANCO);
        int segundosNegras = partida.getSegundosRestantes(ColorPieza.NEGRO);
        relojBlancas.setText("Blancas: " + formatearTiempo(segundosBlancas));
        relojNegras.setText("Negras: " + formatearTiempo(segundosNegras));
    }

    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}