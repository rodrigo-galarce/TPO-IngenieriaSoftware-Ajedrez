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

        JLabel titulo = new JLabel("THE ELDER CHESS: SKYRIM");
        titulo.setFont(FuentesSwing.cargarFuente("/fuentes/CinzelDecorative-Bold.ttf", 42f));
        titulo.setForeground(new Color(235, 220, 190));

        JButton botonJugar = new JButton("NEW GAME");
        JButton botonSalir = new JButton("EXIT");
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
        gbc.insets = new Insets(15, 15, 15, 15);

        gbc.gridy = 0;
        panelFondo.add(titulo, gbc);

        gbc.gridy = 1;
        panelFondo.add(botonJugar, gbc);

        gbc.gridy = 2;
        panelFondo.add(botonSalir, gbc);
    }

    public void mostrar() {
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setVisible(true);
    }
}