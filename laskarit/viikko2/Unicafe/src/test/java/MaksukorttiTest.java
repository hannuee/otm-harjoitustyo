
package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(10);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti!=null);      
    }
    
    
    // Viikko 2 tehtävä 2:
    
    @Test
    public void luodunKortinSaldoOikein() {
        assertEquals(10, kortti.saldo());
    }
    
    @Test
    public void kortilleVoiLadataRahaa80senttia() {
        kortti.lataaRahaa(80);
        // ^ Tämän jälkeen kortilla pitäisi olla: 10 + 80 = 90
        
        assertEquals(90, kortti.saldo());
    }
    
    @Test
    public void kortilleVoiLadataRahaa500senttia() {
        kortti.lataaRahaa(500);
        // ^ Tämän jälkeen kortilla pitäisi olla: 10 + 500 = 510
        
        assertEquals(510, kortti.saldo());
    }
    
    @Test
    public void kortiltaVoiOttaaRahaaJotaSiellaOn() {
        kortti.lataaRahaa(500);
        kortti.otaRahaa(505);
        // ^ Näiden jälkeen kortilla pitäisi olla: 10 + 500 - 505 = 5
        
        assertEquals(5, kortti.saldo());
    }
    
    @Test
    public void kortiltaVoiOttaaKaikkiRahat() {
        kortti.lataaRahaa(500);
        kortti.otaRahaa(510);
        // ^ Näiden jälkeen kortilla pitäisi olla: 10 + 500 - 510 = 0
        
        assertEquals(0, kortti.saldo());
    }
    
    @Test
    public void kortiltaEiOtetaRahaaJosYritetaanOttaaLiikaa() {
        kortti.otaRahaa(20);
        
        assertEquals(10, kortti.saldo());
    }
    
    @Test
    public void otaRahaaMetodiPalauttaaTrueKunRahaRiittaa() {
        kortti.lataaRahaa(500);
        // ^ Tämän jälkeen kortilla pitäisi olla: 10 + 500 = 510
        
        assertTrue(kortti.otaRahaa(505));
    }
    
    @Test
    public void otaRahaaMetodiPalauttaaFalseKunRahaEiRiita() {
        kortti.lataaRahaa(500);
        // ^ Tämän jälkeen kortilla pitäisi olla: 10 + 500 = 510
        
        assertFalse(kortti.otaRahaa(511));
    }
    
    @Test
    public void kortinTiedotTulostuvatOikein() {
        kortti.lataaRahaa(500);
        // ^ Tämän jälkeen kortilla pitäisi olla: 10 + 500 = 510, eli 5.10
        
        assertEquals("saldo: 5.10", kortti.toString());
    }
}
