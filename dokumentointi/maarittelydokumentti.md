# Määrittelydokumentti


## Sovelluksen tarkoitus

Fortress Duel on peli joka on tarkoitettu 2 pelaajan pelattavaksi toisiaan vastaan samalla tietokoneella. Pelissä pelaajat yrittävät tuhota toistensa linnoitukset ampumalla niitä vuorotellen oman linnoituksensa tykillä.


## Käyttäjät

Sovelluksessa on vain 1 käyttäjärooli, pelaaja. Pelaajia on aina kaksi pelaamassa toisiansa vastaan.


## Sovelluksen toiminta

### Aloitusnäkymä

- Aloitusnäkymässä näkyy TOP 5 eniten voittoja keränneet pelaajat.

- Aloitusnäkymästä voi valita kentän. Valittavana on 3 kenttää; Mountains, Vacuum chamber ja Meadow.

### Nimien syöttö -näkymä

- Kun kenttä on valittu niin päästään nimien syöttä -näkymään. 

- Kun nimet on syötetty niin jatketaan varsinaisen pelin pelaamiseen. 

### Pelin kulku

- Pelin alkaessa molempien pelaajien linnoitukset ovat täysin ehjiä.
- Vuorotellen pelaajat ampuvat yhden laukauksen oman linnoituksensa tykillä yrittäen osua toisen pelaajan linnoitukseen.
- Kun pelaajan vuoro tulee niin pelaaja määrittää tykkinsä suuntauksen sekä ammuksen alkunopeuden hiirellä. Pelaajan oman linnoituksen tykin kohdalta osoittaa viiva hiireen. Tämän viivan suunta kertoo tykin suuntauksen sekä viivan pituus on suoraan verrannollinen ammuksen alkunopeuteen.
- Pelaajan tykin suuntaus asteina sekä ammuksen alkunopeuden määrä näytetään pelaajan nimen alla. Myös pelaajan linnoituksen terveys näytetään pelaajan nimen alla.
- Kun pelaaja on mielestään löytänyt oikean suuntauksen ja alkunopeuden suuruuden niin pelaaja klikkaa hiirellä ruutua ja tällöin ammus lähtee matkaan.
- Jos ammus osuu maahan tai linnoitukseen niin ammus räjähtää. Jos räjähdysalueella on linnoitusta niin tämä osa linnoituksesta tuhoutuu. Myös maa tuhoutuu räjähdysalueelta. 
- Vuoro vaihtuu kun ammus räjähtää tai lentää peli-ikkunan vasemman rajan vasemmalle puolelle tai oikean rajan oikealle puolelle.
- Peli loppuu kun jomman kumman pelaajan linnoituksesta ei ole enää mitään jäljellä. Voittaneen pelaajan tilastoihin lisätään merkintä voitetusta pelistä. Pelin voi myös lopettaa koska tahansa siihen tarkoitetulla painikkeella. Tällöin tulosta ei tallenneta mitenkään.
- Kun peli on loppunut niin päädytään takaisin aloitusnäkymään.

### Vacuum chamber kentän erikoisuudet

- Vacuum chamber kentässä tyhjiökammio ei tuhoudu räjähdysalueelta toisin kuin normaalissa kentässä maa tuhoutuu.
- Vacuum chamber kentässä voi imeä ilman pois tyhjiökammiosta siihen tarkoitetulla painikkeella ennen ammuksen ampumista, tällöin ammukseen ei vaikuta ilmanvastus. Vastaavasti ilman voi päästää takaisin kammioon siihen tarkoitetulla painikkeella ennen ammuksen ampumista.
- Vacuum chamber kentässä on painike jolla tykkiä voi ampua samoilla asetuksilla kuin mitä käytti aiemmin ampuessa omalla tykillään.
- Vacuum chamber kentässä ammuksista jää vana pelikenttään.

### Ammuksen lentorata

Ammuksen lentorata mallinnetaan fysikaalisesti mahdollisimman todenmukaisesti dynamiikan sääntöjen mukaan. Ammuksen lentorataa laskettaessa otetaan huomioon ammuksen lähtöpaikka, alkunopeus, alkusuunta, painovoima sekä ilmanvastus.

