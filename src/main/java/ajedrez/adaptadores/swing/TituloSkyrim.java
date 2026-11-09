package ajedrez.adaptadores.swing;

import javax.swing.*;
import java.awt.*;

public class TituloSkyrim extends JLabel {
    public TituloSkyrim(String texto, float tamanoFuente) {
        super(texto, SwingConstants.CENTER);
        setFont(FuentesSwing.cargarFuente("/fuentes/CinzelDecorative-Bold.ttf", tamanoFuente));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String texto = getText();
        Font fuente = getFont();
        FontMetrics medidas = g2.getFontMetrics(fuente);

        int anchoDisponible = Math.max(1, getWidth() - 30);
        int anchoTexto = medidas.stringWidth(texto);

        // si el titulo no entra, se reduce la fuente
        if (anchoTexto > anchoDisponible) {
            float escala = (float) anchoDisponible / anchoTexto;
            fuente = fuente.deriveFont(fuente.getSize2D() * escala);
        }
        g2.setFont(fuente);
        medidas = g2.getFontMetrics();
        anchoTexto = medidas.stringWidth(texto);

        int x = (getWidth() - anchoTexto) / 2;
        int y = (getHeight() - medidas.getHeight()) / 2 + medidas.getAscent();

        // sombra
        g2.setColor(new Color(30, 20, 10, 210));
        g2.drawString(texto, x + 4, y + 4);

        // contorno oscuro
        g2.setColor(new Color(95, 65, 25));
        g2.drawString(texto, x - 1, y);
        g2.drawString(texto, x + 1, y);
        g2.drawString(texto, x, y - 1);
        g2.drawString(texto, x, y + 1);

        // dorado metalico
        g2.setColor(new Color(185, 145, 75));
        g2.drawString(texto, x, y);

        // brillo superior
        g2.setColor(new Color(240, 225, 185));
        g2.drawString(texto, x, y - 1);

        g2.dispose();
    }
}