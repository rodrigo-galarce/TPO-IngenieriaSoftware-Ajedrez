package ajedrez.adaptadores.swing;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import javax.swing.SwingUtilities;

public final class SonidoSkyrim {
    private static boolean silenciado = false;
    private static boolean menuActivo = false;
    private static Clip musicaMenu;
    private static Clip efectoActual;

    private SonidoSkyrim() {
    }

    public static boolean estaSilenciado() {
        return silenciado;
    }

    public static void alternarSilencio() {
        silenciado = !silenciado;
        if (silenciado) {
            if (musicaMenu != null) {
                musicaMenu.stop();
                detenerEfectoActual();
            }
        } else {
            if (menuActivo) {
                iniciarMusicaMenu();
            }
        }
    }

    public static void iniciarMusicaMenu() {
        menuActivo = true;
        if (silenciado) {
            return;
        }
        try {
            if (musicaMenu == null) {
                URL recurso = SonidoSkyrim.class.getResource("/sonidos/musica_menu.wav");
                if (recurso == null) {
                    throw new IllegalStateException("No se encontro la musica del menu");
                }
                try (AudioInputStream audio = AudioSystem.getAudioInputStream(recurso)) {
                    Clip nuevoClip = AudioSystem.getClip();
                    try {
                        nuevoClip.open(audio);
                        musicaMenu = nuevoClip;
                    } catch (IOException | LineUnavailableException e) {
                        nuevoClip.close();
                        throw e;
                    }
                }
            }
            if (!musicaMenu.isRunning()) {
                musicaMenu.setFramePosition(0);
                musicaMenu.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException |
                 IOException |
                 LineUnavailableException |
                 IllegalArgumentException e) {
            System.err.println("No se pudo reproducir la musica: " + e.getMessage());
        }
    }

    public static void detenerMusicaMenu() {
        menuActivo = false;
        if (musicaMenu != null) {
            musicaMenu.stop();
            musicaMenu.close();
            musicaMenu = null;
        }
    }

    public static void reproducirEfecto(String archivo) {
        if (silenciado) {
            return;
        }
        // se evita que dos efectos se mezclen
        detenerEfectoActual();
        URL recurso = SonidoSkyrim.class.getResource("/sonidos/" + archivo);
        if (recurso == null) {
            throw new IllegalStateException("No se encontro el sonido: " + archivo);
        }
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(recurso)) {
            Clip clip = AudioSystem.getClip();
            try {
                clip.open(audio);
                efectoActual = clip;
                // se liberamos el sonido al terminar
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        SwingUtilities.invokeLater(() -> {
                            clip.close();
                            if (efectoActual == clip) {
                                efectoActual = null;
                            }
                        });
                    }
                });
                clip.start();
            } catch (IOException | LineUnavailableException e) {
                clip.close();
                throw e;
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("No se pudo reproducir " + archivo + ": " + e.getMessage());
        }
    }

    private static void detenerEfectoActual() {
        if (efectoActual != null) {
            Clip anterior = efectoActual;
            efectoActual = null;
            anterior.stop();
            anterior.close();
        }
    }
}