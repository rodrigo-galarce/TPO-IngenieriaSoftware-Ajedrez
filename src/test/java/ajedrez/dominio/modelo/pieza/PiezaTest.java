package ajedrez.dominio.modelo.pieza;

import ajedrez.dominio.reglas.movimiento.MovimientoTorre;
import ajedrez.dominio.reglas.movimiento.IReglaMovimiento;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PiezaTest {
    @Test
    void piezaRecibeSuEstrategiaPorConstructor() {
        // arrange
        IReglaMovimiento estrategia = new MovimientoTorre();
        // act
        Pieza torre = new Pieza(TipoPieza.TORRE, ColorPieza.BLANCO, estrategia);
        // assert
        assertSame(estrategia, torre.getReglaMovimiento());
        assertEquals(TipoPieza.TORRE, torre.getTipo());
        assertEquals(ColorPieza.BLANCO, torre.getColor());
    }
}