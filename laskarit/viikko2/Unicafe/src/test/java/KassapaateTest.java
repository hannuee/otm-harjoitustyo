/**
 * @author Hannu Erälaukko
 */

// Viikko 2 tehtävä 5:

package com.mycompany.unicafe;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class KassapaateTest {
    
    Kassapaate kassa;
    
    @Before
    public void setUp() {
        kassa = new Kassapaate();
    }
    
    
    // Kassapäätteen luominen:
    
    @Test
    public void luotuKassaOlemassa() {
        assertTrue(kassa != null);      
    }
    
    @Test
    public void luodussaKassassaOikeaAlkusumma() {
        assertEquals(100000, kassa.kassassaRahaa());
    }
    
    @Test
    public void luodussaKassassaEiMyytyjaEdullisiaLounaita() {
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }
    
    @Test
    public void luodussaKassassaEiMyytyjaMaukkaitaLounaita() {
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }
    
    
    // Edullisen lounaan käteisosto:
    
    @Test
    public void syoEdullisestiToimiiRiittavallaKateissummalla() {
        int vaihtoraha = kassa.syoEdullisesti(300);
        
        assertEquals("Vaihtorahaa annettiin takaisin väärä määrä.", 60, vaihtoraha);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.edullisiaLounaitaMyyty());
        assertEquals("Saatu maksu ei päätynyt kassan rahavarantoon oikein.", 100240, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoEdullisestiToimiiJuuriOikeallaKateissummalla() {
        int vaihtoraha = kassa.syoEdullisesti(240);
        
        assertEquals("Vaihtorahaa annettiin takaisin väärä määrä.", 0, vaihtoraha);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.edullisiaLounaitaMyyty());
        assertEquals("Saatu maksu ei päätynyt kassan rahavarantoon oikein.", 100240, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoEdullisestiEiToimiRiittamattomallaKateissummalla() {
        int vaihtoraha = kassa.syoEdullisesti(100);
        
        assertEquals("Rahaa annettiin takaisin väärä määrä.", 100, vaihtoraha);
        assertEquals("Kassa ei kirjannut myynnin perumista oikein.", 0, kassa.edullisiaLounaitaMyyty());
        assertEquals("Kassan rahavaranto on virheellinen.", 100000, kassa.kassassaRahaa());
    }
    
    
    // Maukkaan lounaan käteisosto:
    
    @Test
    public void syoMaukkaastiToimiiRiittavallaKateissummalla() {
        int vaihtoraha = kassa.syoMaukkaasti(500);
        
        assertEquals("Vaihtorahaa annettiin takaisin väärä määrä.", 100, vaihtoraha);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.maukkaitaLounaitaMyyty());
        assertEquals("Saatu maksu ei päätynyt kassan rahavarantoon oikein.", 100400, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoMaukkaastiToimiiJuuriOikeallaKateissummalla() {
        int vaihtoraha = kassa.syoMaukkaasti(400);
        
        assertEquals("Vaihtorahaa annettiin takaisin väärä määrä.", 0, vaihtoraha);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.maukkaitaLounaitaMyyty());
        assertEquals("Saatu maksu ei päätynyt kassan rahavarantoon oikein.", 100400, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoMaukkaastiEiToimiRiittamattomallaKateissummalla() {
        int vaihtoraha = kassa.syoMaukkaasti(100);
        
        assertEquals("Rahaa annettiin takaisin väärä määrä.", 100, vaihtoraha);
        assertEquals("Kassa ei kirjannut myynnin perumista oikein.", 0, kassa.maukkaitaLounaitaMyyty());
        assertEquals("Kassan rahavaranto on virheellinen.", 100000, kassa.kassassaRahaa());
    }
    
    
    // Edullisen lounaan korttiosto:
    
    @Test
    public void syoEdullisestiToimiiKortillaJossaPaljonRahaa() {
        Maksukortti kortti = new Maksukortti(300);
        
        boolean onnistuiko = kassa.syoEdullisesti(kortti);
        
        assertEquals("Kassa ei veloittanut korttia oikein.", 60, kortti.saldo());
        assertTrue("Kassa ei onnistunut korttimaksun käsittelyssä oikein.", onnistuiko);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.edullisiaLounaitaMyyty());
        assertEquals("Kassan rahavarannon määrä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoEdullisestiToimiiKortillaJossaJuuriOikeaSumma() {
        Maksukortti kortti = new Maksukortti(240);
        
        boolean onnistuiko = kassa.syoEdullisesti(kortti);
        
        assertEquals("Kassa ei veloittanut korttia oikein.", 0, kortti.saldo());
        assertTrue("Kassa ei onnistunut korttimaksun käsittelyssä oikein.", onnistuiko);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.edullisiaLounaitaMyyty());
        assertEquals("Kassan rahavarannon määrä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoEdullisestiEiToimiKortillaJossaEiTarpeeksiRahaa() {
        Maksukortti kortti = new Maksukortti(100);
        
        boolean onnistuiko = kassa.syoEdullisesti(kortti);
        
        assertEquals("Kortin rahamäärä muuttui vaikkei pitänyt.", 100, kortti.saldo());
        assertFalse("Kassa ei onnistunut korttimaksun käsittelyssä oikein.", onnistuiko);
        assertEquals("Kassa ei kirjannut myynnin peruuntumista oikein.", 0, kassa.edullisiaLounaitaMyyty());
        assertEquals("Kassan rahavarannon määrä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
    
    // Maukkaan lounaan korttiosto:
    
    @Test
    public void syoMaukkaastiToimiiKortillaJossaPaljonRahaa() {
        Maksukortti kortti = new Maksukortti(500);
        
        boolean onnistuiko = kassa.syoMaukkaasti(kortti);
        
        assertEquals("Kassa ei veloittanut korttia oikein.", 100, kortti.saldo());
        assertTrue("Kassa ei onnistunut korttimaksun käsittelyssä oikein.", onnistuiko);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.maukkaitaLounaitaMyyty());
        assertEquals("Kassan rahavarannon määrä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoMaukkaastiToimiiKortillaJossaJuuriOikeaSumma() {
        Maksukortti kortti = new Maksukortti(400);
        
        boolean onnistuiko = kassa.syoMaukkaasti(kortti);
        
        assertEquals("Kassa ei veloittanut korttia oikein.", 0, kortti.saldo());
        assertTrue("Kassa ei onnistunut korttimaksun käsittelyssä oikein.", onnistuiko);
        assertEquals("Kassa ei kirjannut myyntiä oikein.", 1, kassa.maukkaitaLounaitaMyyty());
        assertEquals("Kassan rahavarannon määrä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
    @Test
    public void syoMaukkaastiEiToimiKortillaJossaEiTarpeeksiRahaa() {
        Maksukortti kortti = new Maksukortti(100);
        
        boolean onnistuiko = kassa.syoMaukkaasti(kortti);
        
        assertEquals("Kortin rahamäärä muuttui vaikkei pitänyt.", 100, kortti.saldo());
        assertFalse("Kassa ei onnistunut korttimaksun käsittelyssä oikein.", onnistuiko);
        assertEquals("Kassa ei kirjannut myynnin peruuntumista oikein.", 0, kassa.maukkaitaLounaitaMyyty());
        assertEquals("Kassan rahavarannon määrä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
    
    // Kortin lataaminen:
    
    @Test
    public void kassaLataaKorttiaOikeinKunLadattavaSummaPositiivinen(){
        Maksukortti kortti = new Maksukortti(100);
        
        kassa.lataaRahaaKortille(kortti, 100);
        
        assertEquals("Kortin rahamäärä ei muuttunut oikein.", 200, kortti.saldo());
        assertEquals("Kassan rahamäärä ei muuttunut oikein.", 100100, kassa.kassassaRahaa());
    }
    
    @Test
    public void kassaEiLataaKorttiaKunLadattavaSummaNegatiivinen(){
        Maksukortti kortti = new Maksukortti(100);
        
        kassa.lataaRahaaKortille(kortti, -100);
        
        assertEquals("Kortin rahamäärä muuttui vaikkei pitänyt.", 100, kortti.saldo());
        assertEquals("Kassan rahamäärä muuttui vaikkei pitänyt.", 100000, kassa.kassassaRahaa());
    }
    
}
