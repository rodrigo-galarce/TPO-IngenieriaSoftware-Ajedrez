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
    private final Timer temporizadorAuras;
    private final Map<String, ImageIcon> iconosPiezas = new HashMap<>();
    private List<Posicion> movimientosDisponibles = List.of();
    private final int tamanoTableroVisual;
    private final Runnable volverAlMenu;
    private final Runnable iniciarNuevaPartida;
    private boolean resultadoMostrado = false;
    private final JPanel panelNorte;
    private final JPanel panelSombras;
    private final ImageIcon auraNorte;
    private final ImageIcon auraSombras;

    public VentanaAjedrez(IPartida partida, Runnable volverAlMenu, Runnable iniciarNuevaPartida) {
        this.partida = Objects.requireNonNull(partida);
        this.volverAlMenu = Objects.requireNonNull(volverAlMenu);
        this.iniciarNuevaPartida = Objects.requireNonNull(iniciarNuevaPartida);
        casillas = new JLabel[8][8];
        piezaSeleccionada = null;
        ventana = new JFrame("The Elder Chess: Skyrim");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        auraNorte = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/efectos/aura_blanco.gif"), "No se encontro el aura del Norte"));
        auraSombras = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/efectos/aura_negro.gif"), "No se encontro el aura de las Sombras"));

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        tamanoTableroVisual = Math.min(820, Math.min(pantalla.width - 80, pantalla.height - 160));

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
                super.paintComponent(g);
                // primero se dibuja el tablero
                g.drawImage(imagenTablero, 0, 0, getWidth(), getHeight(), this);
                // luego se dibujan las auras detras de las piezas
                for (int fila = 0; fila < FILAS; fila++) {
                    for (int columna = 0; columna < COLUMNAS; columna++) {
                        Posicion posicion = new Posicion(fila, columna);
                        Pieza pieza = partida.obtenerPieza(posicion);
                        if (pieza == null || !partida.tieneKillstreak(pieza)) {
                            continue;
                        }
                        JLabel casilla = casillas[fila][columna];
                        if (casilla == null) {
                            continue;
                        }
                        ImageIcon aura;
                        if (pieza.getColor() == ColorPieza.BLANCO) {
                            aura = auraNorte;
                        } else {
                            aura = auraSombras;
                        }
                        // El aura es mas grande que la casilla
                        int tamanoAura = (int) Math.round(Math.min(casilla.getWidth(), casilla.getHeight()) * 1.5);
                        // se centra el aura sobre la pieza
                        int x = casilla.getX() + (casilla.getWidth() - tamanoAura) / 2;
                        int y = casilla.getY() + (casilla.getHeight() - tamanoAura) / 2;
                        g.drawImage(aura.getImage(), x, y, tamanoAura, tamanoAura, this);
                    }
                }
            }
        };
        panelTablero.setOpaque(false);
        panelTablero.setPreferredSize(new Dimension(tamanoTableroVisual, tamanoTableroVisual));

        // casillas
        double escalaX = (double) tamanoTableroVisual / 2048;
        double escalaY = (double) tamanoTableroVisual / 2048;

        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                final int filaActual = fila;
                final int columnaActual = columna;
                JLabel casilla = new JLabel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Posicion posicion = new Posicion(filaActual, columnaActual);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

        // se crean los dos relojes
        relojBlancas = new JLabel();
        relojNegras = new JLabel();
        relojBlancas.setHorizontalAlignment(SwingConstants.CENTER);
        relojNegras.setHorizontalAlignment(SwingConstants.CENTER);
        Font fuenteReloj = new Font("Arial", Font.BOLD, 22);
        relojBlancas.setFont(fuenteReloj);
        relojNegras.setFont(fuenteReloj);
        relojBlancas.setForeground(new Color(210, 230, 245));
        relojNegras.setForeground(new Color(240, 195, 185));

        // boton menu y new game
        JButton botonMenu = new BotonSkyrim("MAIN MENU");
        botonMenu.setFont(new Font("Serif", Font.BOLD, 16));
        botonMenu.setFocusPainted(false);
        botonMenu.addActionListener(e -> confirmarVolverAlMenu());
        JButton botonNuevaPartida = new BotonSkyrim("NEW GAME");
        botonNuevaPartida.setFont(new Font("Serif", Font.BOLD, 16));
        botonNuevaPartida.setFocusPainted(false);
        botonNuevaPartida.addActionListener(e -> confirmarNuevaPartida());

        // panel para poner los relojes uno al lado del otro
        JPanel panelRelojes = new JPanel(new GridLayout(1, 2, 20, 0));
        panelRelojes.setOpaque(false);
        panelRelojes.setBorder(BorderFactory.createEmptyBorder(5, 30, 12, 30));
        panelNorte = crearPanelReloj("ARMY OF THE NORTH", relojBlancas, new Color(38, 59, 77), new Color(120, 157, 183), new Color(190, 220, 240));
        panelSombras = crearPanelReloj("ARMY OF SHADOWS", relojNegras, new Color(56, 39, 45), new Color(164, 106, 99), new Color(235, 190, 180));
        panelRelojes.add(panelNorte);
        panelRelojes.add(panelSombras);

        // etiqueta para el turno y el jaque
        etiquetaEstado = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                String texto = getText();
                if (texto != null && !texto.isEmpty()) {
                    g2.setFont(getFont());
                    FontMetrics medidas = g2.getFontMetrics();
                    int anchoTexto = medidas.stringWidth(texto);
                    int x = (getWidth() - anchoTexto) / 2;
                    int y = (getHeight() - medidas.getHeight()) / 2 + medidas.getAscent();
                    // sombra oscura
                    g2.setColor(new Color(0, 0, 0, 220));
                    g2.drawString(texto, x + 2, y + 2);
                    // texto principal con el color del ejercito
                    g2.setColor(getForeground());
                    g2.drawString(texto, x, y);
                }
                g2.dispose();
            }
        };
        etiquetaEstado.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaEstado.setFont(FuentesSwing.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 22f)); etiquetaEstado.setPreferredSize(new Dimension(560, 50));
        etiquetaEstado.setForeground(new Color(235, 220, 190));
        etiquetaEstado.setOpaque(false);

        // El tablero y el indicador de turno forman un mismo conjunto.
        JPanel panelJuego = new JPanel(new BorderLayout());
        panelJuego.setOpaque(false);
        panelJuego.add(panelTablero, BorderLayout.CENTER);
        panelJuego.add(etiquetaEstado, BorderLayout.SOUTH);

        // Centramos ambos componentes sobre el fondo.
        contenedorTablero.add(panelJuego);
        ventana.add(contenedorTablero, BorderLayout.CENTER);

        // Los relojes quedan separados debajo.
        ventana.add(panelRelojes, BorderLayout.SOUTH);

        // cada segundo le avisamos a Partida que transcurrio tiempo
        temporizador = new Timer(1000, e -> {
            partida.avanzarUnSegundo();
            actualizarRelojes();
            actualizarEstado();
            if (partida.estaFinalizada()) {
                ((Timer) e.getSource()).stop();;
            }
        });

        temporizadorAuras = new Timer(100, e -> {
            panelTablero.repaint();
        });

        actualizarRelojes();
        actualizarEstado();

        JLabel tituloJuego = new JLabel("THE ELDER CHESS: SKYRIM", SwingConstants.CENTER);
        tituloJuego.setFont(FuentesSwing.cargarFuente("/fuentes/CinzelDecorative-Bold.ttf", 30f));
        tituloJuego.setForeground(new Color(235, 220, 190));
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // ambos botones tienen el mismo tamaño
        Dimension tamanoBoton = new Dimension(180, 52);
        botonNuevaPartida.setPreferredSize(tamanoBoton);
        botonMenu.setPreferredSize(tamanoBoton);

        // botones en los extremos y titulo centrado
        panelSuperior.add(botonNuevaPartida, BorderLayout.WEST);
        panelSuperior.add(tituloJuego, BorderLayout.CENTER);
        panelSuperior.add(botonMenu, BorderLayout.EAST);

        ventana.add(panelSuperior, BorderLayout.NORTH);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
    }

    private JPanel crearPanelReloj(String nombreEjercito, JLabel reloj, Color colorFondo, Color colorBorde, Color colorTexto) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(colorBorde, 2), BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        JLabel nombre = new JLabel(nombreEjercito);
        nombre.setHorizontalAlignment(SwingConstants.CENTER);
        nombre.setForeground(colorTexto);
        nombre.setFont(FuentesSwing.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 14f));
        reloj.setHorizontalAlignment(SwingConstants.CENTER);
        reloj.setForeground(Color.WHITE);
        reloj.setFont(FuentesSwing.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 26f));
        panel.add(nombre, BorderLayout.NORTH);
        panel.add(reloj, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarResaltadoRelojes() {
        ColorPieza turno = partida.getTurnoActual();
        boolean turnoNorte = !partida.estaFinalizada() && turno == ColorPieza.BLANCO;
        boolean turnoSombras = !partida.estaFinalizada() && turno == ColorPieza.NEGRO;
        actualizarBordeReloj(panelNorte, new Color(120, 157, 183), new Color(155, 225, 255), turnoNorte);
        actualizarBordeReloj(panelSombras, new Color(164, 106, 99), new Color(255, 155, 135), turnoSombras);
    }

    private void actualizarBordeReloj(JPanel panel, Color colorNormal, Color colorActivo, boolean activo) {
        Color colorBorde;
        int grosor;
        if (activo) {
            colorBorde = colorActivo;
            grosor = 4;
        } else {
            colorBorde = colorNormal;
            grosor = 2;
        }
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(colorBorde, grosor), BorderFactory.createEmptyBorder(8, 20, 8, 20)));
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
        int tamanoMaximo = (int) Math.round(tamanoTableroVisual * 68.0 / 820.0);
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
        temporizadorAuras.start();
    }

    private void confirmarVolverAlMenu() {
        boolean relojEstabaActivo = temporizador.isRunning();

        // pausar el reloj mientras el jugador decide
        temporizador.stop();

        int respuesta = JOptionPane.showConfirmDialog(ventana, "The current game will be lost. Return to main menu?", "RETURN TO MAIN MENU", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            temporizadorAuras.stop();
            ventana.dispose();
            volverAlMenu.run();
        } else {
            // si cancela, continua la partida
            if (relojEstabaActivo && !partida.estaFinalizada()) {
                temporizador.start();
            }
        }
    }

    private void confirmarNuevaPartida() {
        boolean relojEstabaActivo = temporizador.isRunning();
        temporizador.stop();
        int respuesta = JOptionPane.showConfirmDialog(ventana, "The current game will be lost. Start a new game?", "NEW GAME", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            temporizador.stop();
            temporizadorAuras.stop();
            ventana.dispose();
            // App se encarga de crear una partida nueva
            iniciarNuevaPartida.run();
        } else {
            if (relojEstabaActivo && !partida.estaFinalizada()) {
                temporizador.start();
            }
        }
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
    }

    private void actualizarEstado() {
        actualizarResaltadoRelojes();
        ColorPieza turno = partida.getTurnoActual();
        if (partida.estaFinalizada()) {
            String titulo;
            String mensaje;
            if (partida.terminoEnTablas()) {
                titulo = "STALEMATE";
                mensaje = "The game ends in a draw.";
                etiquetaEstado.setText("STALEMATE");
            } else {
                ColorPieza ganador = partida.getGanador();
                String ejercitoGanador;
                if (ganador == ColorPieza.BLANCO) {
                    ejercitoGanador = "ARMY OF THE NORTH";
                } else {
                    ejercitoGanador = "ARMY OF SHADOWS";
                }
                if (partida.estaEnJaqueMate(turno)) {
                    titulo = "CHECKMATE";
                } else {
                    titulo = "TIME OUT";
                }
                mensaje = ejercitoGanador + " WINS!";
                etiquetaEstado.setText(titulo + " - " + ejercitoGanador);
            }
            if (!resultadoMostrado) {
                resultadoMostrado = true;
                temporizador.stop();
                JOptionPane.showMessageDialog(ventana, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        }
        String nombreEjercito;
        if (turno == ColorPieza.BLANCO) {
            nombreEjercito = "ARMY OF THE NORTH";
            etiquetaEstado.setForeground(new Color(166, 221, 247));
        } else {
            nombreEjercito = "ARMY OF SHADOWS";
            etiquetaEstado.setForeground(new Color(231, 166, 151));
        }
        if (partida.estaEnJaque(turno)) {
            etiquetaEstado.setText("TURN: " + nombreEjercito + " - CHECK");
        } else {
            etiquetaEstado.setText("TURN: " + nombreEjercito);
        }
    }

    private void actualizarRelojes() {
        int segundosBlancas = partida.getSegundosRestantes(ColorPieza.BLANCO);
        int segundosNegras = partida.getSegundosRestantes(ColorPieza.NEGRO);
        relojBlancas.setText(formatearTiempo(segundosBlancas));
        relojNegras.setText(formatearTiempo(segundosNegras));
    }

    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
}