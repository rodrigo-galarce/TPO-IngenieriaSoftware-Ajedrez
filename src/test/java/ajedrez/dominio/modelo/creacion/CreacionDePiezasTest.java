package ajedrez.dominio.modelo.creacion;

import ajedrez.dominio.modelo.pieza.*;
import ajedrez.dominio.reglas.movimiento.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreacionDePiezasTest {
    @Test
    void creaTorreConEstrategiaCorrecta() {
        // arrange
        CreacionDePiezas fabrica = new CreacionDePiezas();
        // act
        Pieza torre = fabrica.crear(TipoPieza.TORRE, ColorPieza.BLANCO);
        // assert
        assertEquals(TipoPieza.TORRE, torre.getTipo());
        assertEquals(ColorPieza.BLANCO, torre.getColor());
        assertInstanceOf(MovimientoTorre.class, torre.getReglaMovimiento());
    }

    @Test
    void creaTodasLasPiezasConEstrategia() {
        CreacionDePiezas fabrica = new CreacionDePiezas();
        for (TipoPieza tipo : TipoPieza.values()) {
            Pieza pieza = fabrica.crear(tipo, ColorPieza.NEGRO);
            assertNotNull(pieza.getReglaMovimiento());
            assertEquals(tipo, pieza.getTipo());
        }
    }

    @Test
    void rechazaTiposNulos() {
        CreacionDePiezas fabrica = new CreacionDePiezas();
        assertThrows(NullPointerException.class, () -> fabrica.crear(null, ColorPieza.BLANCO));
    }

    @Test
    void noPermiteRegistrarEstrategiaDuplicada() {
        CreacionDePiezas fabrica = new CreacionDePiezas();
        assertThrows(IllegalArgumentException.class, () -> fabrica.registrar(TipoPieza.TORRE, MovimientoCaballo::new));
    }

    @Test
    void asignaLaEstrategiaCorrectaACadaTipo() {
        CreacionDePiezas creador = new CreacionDePiezas();
        assertInstanceOf(MovimientoPeon.class, creador.crear(TipoPieza.PEON, ColorPieza.BLANCO).getReglaMovimiento());
        assertInstanceOf(MovimientoTorre.class, creador.crear(TipoPieza.TORRE, ColorPieza.BLANCO).getReglaMovimiento());
        assertInstanceOf(MovimientoCaballo.class, creador.crear(TipoPieza.CABALLO, ColorPieza.BLANCO).getReglaMovimiento());
        assertInstanceOf(MovimientoAlfil.class, creador.crear(TipoPieza.ALFIL, ColorPieza.BLANCO).getReglaMovimiento());
        assertInstanceOf(MovimientoReina.class, creador.crear(TipoPieza.REINA, ColorPieza.BLANCO).getReglaMovimiento());
        assertInstanceOf(MovimientoRey.class, creador.crear(TipoPieza.REY, ColorPieza.BLANCO).getReglaMovimiento());
    }
}