package ajedrez.adaptadores.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BotonSkyrim extends JButton {
    private static final Color FONDO_NORMAL = new Color(37, 43, 50);
    private static final Color FONDO_RESALTADO = new Color(65, 76, 87);
    private static final Color BORDE_NORMAL = new Color(178, 148, 91);
    private static final Color BORDE_RESALTADO = new Color(238, 204, 137);

    public BotonSkyrim (String texto) {
        super(texto);
        setFont(FuentesSwing.cargarFuente("/fuentes/Cinzel-SemiBold.ttf", 18f));
        setForeground(new Color(240, 218, 174));
        setBackground(FONDO_NORMAL);
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cambiarBorde(BORDE_NORMAL);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(FONDO_RESALTADO);
                cambiarBorde(BORDE_RESALTADO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(FONDO_NORMAL);
                cambiarBorde(BORDE_NORMAL);
            }
        });
    }

    private void cambiarBorde(Color color) {
        Border bordeExterior = BorderFactory.createLineBorder(color, 2);
        Border espacioInterior = BorderFactory.createEmptyBorder(10, 22, 10, 22);
        setBorder(BorderFactory.createCompoundBorder(bordeExterior, espacioInterior));
    }
}