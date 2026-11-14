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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class VentanaAjedrez {
    private static final int FILAS = 8;
    private static final int COLUMNAS = 8;

    // zona interna donde están las 64 casillas dentro del png
    private static final int ZONA_CASILLAS_X = 272;
    private static final int ZONA_CASILLAS_Y = 208;
    private static final int ZONA_CASILLAS_ANCHO = 1504;
    private static final int ZONA_CASILLAS_ALTO = 1504;
    private static final int OFFSET_SELECCION_Y = 1;

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
    private int tamanoTableroVisual;
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
        ventana.setResizable(true);
        ventana.setMinimumSize(new Dimension(650, 550));
        auraNorte = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/efectos/aura_blanco.gif"), "No se encontro el aura del Norte"));
        auraSombras = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/efectos/aura_negro.gif"), "No se encontro el aura de las Sombras"));

        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        // espacio disponible para el tablero
        int espacioHorizontal = pantalla.width - 80;
        // reservamos espacio para el titulo, los botones, el indicador de turno y los relojes
        int espacioVertical = pantalla.height - 260;
        // mantenemos el tablero cuadrado y evitamos que supere los 820 pixeles
        tamanoTableroVisual = Math.min(820, Math.min(espacioHorizontal, espacioVertical));

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
                            g2.fillRoundRect(0, OFFSET_SELECCION_Y, getWidth(), getHeight() - OFFSET_SELECCION_Y, 6, 6);
                            g2.setColor(new Color(130, 210, 255, 190));
                            g2.setStroke(new BasicStroke(2f));
                            g2.drawRoundRect(1, 1 + OFFSET_SELECCION_Y, getWidth() - 3, getHeight() - 3 - OFFSET_SELECCION_Y, 6, 6);
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

                ubicarCasilla(casilla, fila, columna, tamanoTableroVisual);

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

        // boton menu y new game
        JButton botonMenu = new BotonSkyrim("MAIN MENU");
        botonMenu.addActionListener(e -> confirmarVolverAlMenu());
        JButton botonNuevaPartida = new BotonSkyrim("NEW GAME");
        botonNuevaPartida.addActionListener(e -> confirmarNuevaPartida());
        JButton botonSonido = new BotonSonido();

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
        etiquetaEstado.setFont(FuentesSkyrim.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 22f)); etiquetaEstado.setPreferredSize(new Dimension(560, 50));
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
        contenedorTablero.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // se calcula el espacio disponible para el tablero
                int anchoDisponible = contenedorTablero.getWidth() - 20;
                int altoDisponible = contenedorTablero.getHeight() - etiquetaEstado.getPreferredSize().height - 10;
                // tablero siempre cuadrado
                int nuevoTamano = Math.min(820, Math.min(anchoDisponible, altoDisponible));
                // evita tamaños invalidos
                if (nuevoTamano < 200 || nuevoTamano == tamanoTableroVisual) {
                    return;
                }
                tamanoTableroVisual = nuevoTamano;
                // se ajusta el tamaño de la imagen del tablero
                panelTablero.setPreferredSize(new Dimension(nuevoTamano, nuevoTamano));
                // se reubican las 64 casillas
                for (int fila = 0; fila < FILAS; fila++) {
                    for (int columna = 0; columna < COLUMNAS; columna++) {
                        ubicarCasilla(casillas[fila][columna], fila, columna, nuevoTamano);
                    }
                }
                // se vuelven a escalar los png de las piezas
                iconosPiezas.clear();
                actualizarPiezas();
                // se actualiza la distribucion y el dibujo
                panelTablero.revalidate();
                contenedorTablero.revalidate();
                contenedorTablero.repaint();
            }
        });
        // los relojes quedan separados debajo.
        ventana.add(panelRelojes, BorderLayout.SOUTH);

        // cada segundo le avisamos a Partida que transcurrio tiempo
        temporizador = new Timer(1000, e -> {
            partida.avanzarUnSegundo();
            actualizarRelojes();
            actualizarEstado();
            if (partida.estaFinalizada()) {
                ((Timer) e.getSource()).stop();
            }
        });

        temporizadorAuras = new Timer(100, e -> {
            panelTablero.repaint();
        });

        actualizarRelojes();
        actualizarEstado();

        JLabel tituloJuego = new TituloSkyrim("THE ELDER CHESS: SKYRIM", 30f);
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        Dimension tamanoBoton = new Dimension(180, 52);
        botonNuevaPartida.setPreferredSize(tamanoBoton);
        botonMenu.setPreferredSize(tamanoBoton);
        botonSonido.setPreferredSize(tamanoBoton);

        // panel izquierdo: NEW GAME y un espacio vacio
        JPanel panelIzquierdo = new JPanel(new GridLayout(2, 1, 0, 8));
        panelIzquierdo.setOpaque(false);
        JPanel espacioVacio = new JPanel();
        espacioVacio.setOpaque(false);
        panelIzquierdo.add(botonNuevaPartida);
        panelIzquierdo.add(espacioVacio);
        // panel derecho: MAIN MENU y SOUND ON/OFF
        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 0, 8));
        panelDerecho.setOpaque(false);
        panelDerecho.add(botonMenu);
        panelDerecho.add(botonSonido);
        // ambos lados tienen el mismo tamaño
        panelSuperior.add(panelIzquierdo, BorderLayout.WEST);
        panelSuperior.add(tituloJuego, BorderLayout.CENTER);
        panelSuperior.add(panelDerecho, BorderLayout.EAST);

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
        nombre.setFont(FuentesSkyrim.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 14f));
        reloj.setHorizontalAlignment(SwingConstants.CENTER);
        reloj.setForeground(Color.WHITE);
        reloj.setFont(FuentesSkyrim.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 26f));
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
            Pieza piezaDestinoAntes = partida.obtenerPieza(posicion);
            boolean huboCaptura = piezaDestinoAntes != null;
            if (piezaOrigen.getTipo() == TipoPieza.PEON && piezaSeleccionada.columna() != posicion.columna()) {
                huboCaptura = true;
            }
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
            boolean hayJaque = !partida.estaFinalizada() && partida.estaEnJaque(partida.getTurnoActual());
            Pieza piezaMovida = partida.obtenerPieza(posicion);
            boolean capturaConKillstreak = huboCaptura && partida.tieneKillstreak(piezaMovida);
            if (capturaConKillstreak) {
                SonidoSkyrim.reproducirEfecto("captura_killstreak.wav");
            } else if (hayJaque) {
                SonidoSkyrim.reproducirEfecto("jaque.wav");
            } else if (huboCaptura) {
                SonidoSkyrim.reproducirEfecto("captura.wav");
            } else {
                SonidoSkyrim.reproducirEfecto("movimiento.wav");
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
                if (titulo.equals("CHECKMATE")) {
                    SonidoSkyrim.reproducirEfecto("jaque_mate.wav");
                }
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

    private void ubicarCasilla(JLabel casilla, int fila, int columna, int tamanoTablero) {
        double escala = (double) tamanoTablero / 2048.0;
        int x = (int) Math.round((ZONA_CASILLAS_X + columna * ZONA_CASILLAS_ANCHO / 8.0) * escala);
        int y = (int) Math.round((ZONA_CASILLAS_Y + fila * ZONA_CASILLAS_ALTO / 8.0) * escala);
        int xFinal = (int) Math.round((ZONA_CASILLAS_X + (columna + 1) * ZONA_CASILLAS_ANCHO / 8.0) * escala);
        int yFinal = (int) Math.round((ZONA_CASILLAS_Y + (fila + 1) * ZONA_CASILLAS_ALTO / 8.0) * escala);
        casilla.setBounds(x, y, xFinal - x, yFinal - y);
    }
}