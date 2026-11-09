package ajedrez.adaptadores.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class VentanaMenu {
    private final JFrame ventana;

    public VentanaMenu(Runnable iniciarPartida) {
        Objects.requireNonNull(iniciarPartida);
        ventana = new JFrame("The Elder Chess: Skyrim");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Image imagenFondo = new ImageIcon(Objects.requireNonNull(getClass().getResource("/imagenes/fondo_menu.png"), "No se encontro la imagen del fondo")).getImage();

        JPanel panelFondo = new JPanel(new GridBagLayout()) {
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

        JLabel titulo = new TituloSkyrim("THE ELDER CHESS: SKYRIM", 62f);
        titulo.setPreferredSize(new Dimension(1000, 120));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(FuentesSwing.cargarFuente("/fuentes/CinzelDecorative-Bold.ttf", 62f));
        titulo.setPreferredSize(new Dimension(1, 120));
        titulo.setOpaque(false);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonJugar = new BotonSkyrim("START");
        JButton botonSalir = new BotonSkyrim("EXIT");
        Font fuenteBotones = FuentesSwing.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 22f);

        botonJugar.setFont(fuenteBotones);
        botonSalir.setFont(fuenteBotones);

        botonJugar.setPreferredSize(new Dimension(280, 55));
        botonSalir.setPreferredSize(new Dimension(280, 55));

        botonJugar.setFocusPainted(false);
        botonSalir.setFocusPainted(false);

        // Al comenzar, cerramos el menu y creamos la partida.
        botonJugar.addActionListener(e -> {
            ventana.dispose();
            iniciarPartida.run();
        });

        botonSalir.addActionListener(e -> ventana.dispose());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // TITULO
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 15, 55, 15);
        panelFondo.add(titulo, gbc);

        // START
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 15, 12, 15);
        panelFondo.add(botonJugar, gbc);

        // EXIT
        gbc.gridy = 2;
        gbc.insets = new Insets(12, 15, 15, 15);
        panelFondo.add(botonSalir, gbc);
    }

    public void mostrar() {
        ventana.setResizable(true);
        ventana.setMinimumSize(new Dimension(700, 450));
        ventana.setSize(1100, 700);
        ventana.setLocationRelativeTo(null);
        // el juego comienza maximizado
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setVisible(true);
    }
}