package ajedrez.adaptadores.swing;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public final class FuentesSkyrim {
    private FuentesSkyrim() {
    }

    public static Font cargarFuente(String ruta, float tamano) {
        try (InputStream archivo = FuentesSkyrim.class.getResourceAsStream(ruta)) {
            if (archivo == null) {
                throw new IllegalStateException("No se encontro la fuente: " + ruta);
            }
            Font fuente = Font.createFont(Font.TRUETYPE_FONT, archivo);
            return fuente.deriveFont(tamano);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Error al cargar la fuente: " + ruta, e);
        }
    }
}