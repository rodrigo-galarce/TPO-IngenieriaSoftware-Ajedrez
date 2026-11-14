package ajedrez.adaptadores.swing;

public class BotonSonido extends BotonSkyrim {
    public BotonSonido() {
        super("");
        actualizarTexto();
        addActionListener(e -> {
            SonidoSkyrim.alternarSilencio();
            actualizarTexto();
        });
    }

    private void actualizarTexto() {
        if (SonidoSkyrim.estaSilenciado()) {
            setText("SOUND OFF");
        } else {
            setText("SOUND ON");
        }
    }
}