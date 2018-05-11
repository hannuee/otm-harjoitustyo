# M��rittelydokumentti


## Sovelluksen tarkoitus

Fortress Duel on peli joka on tarkoitettu 2 pelaajan pelattavaksi toisiaan vastaan samalla tietokoneella. Peliss� pelaajat yritt�v�t tuhota toistensa linnoitukset ampumalla niit� vuorotellen oman linnoituksensa tykill�.


## K�ytt�j�t

Sovelluksessa on vain 1 k�ytt�j�rooli, pelaaja. Pelaajia on aina kaksi pelaamassa toisiansa vastaan.


## Sovelluksen toiminta

### Aloitusn�kym�

- Aloitusn�kym�ss� n�kyy TOP 5 eniten voittoja ker�nneet pelaajat.

- Aloitusn�kym�st� voi valita kent�n. Valittavana on 3 kentt��; Mountains, Vacuum chamber ja Meadow.

### Nimien sy�tt� -n�kym�

- Kun kentt� on valittu niin p��st��n nimien sy�tt� -n�kym��n. 

- Kun nimet on sy�tetty niin jatketaan varsinaisen pelin pelaamiseen. 

### Pelin kulku

- Pelin alkaessa molempien pelaajien linnoitukset ovat t�ysin ehji�.
- Vuorotellen pelaajat ampuvat yhden laukauksen oman linnoituksensa tykill� yritt�en osua toisen pelaajan linnoitukseen.
- Kun pelaajan vuoro tulee niin pelaaja m��ritt�� tykkins� suuntauksen sek� ammuksen alkunopeuden hiirell�. Pelaajan oman linnoituksen tykin kohdalta osoittaa viiva hiireen. T�m�n viivan suunta kertoo tykin suuntauksen sek� viivan pituus on suoraan verrannollinen ammuksen alkunopeuteen.
- Pelaajan tykin suuntaus asteina sek� ammuksen alkunopeuden m��r� n�ytet��n pelaajan nimen alla. My�s pelaajan linnoituksen terveys n�ytet��n pelaajan nimen alla.
- Kun pelaaja on mielest��n l�yt�nyt oikean suuntauksen ja alkunopeuden suuruuden niin pelaaja klikkaa hiirell� ruutua ja t�ll�in ammus l�htee matkaan.
- Jos ammus osuu maahan tai linnoitukseen niin ammus r�j�ht��. Jos r�j�hdysalueella on linnoitusta niin t�m� osa linnoituksesta tuhoutuu. My�s maa tuhoutuu r�j�hdysalueelta. 
- Vuoro vaihtuu kun ammus r�j�ht�� tai lent�� peli-ikkunan vasemman rajan vasemmalle puolelle tai oikean rajan oikealle puolelle.
- Peli loppuu kun jomman kumman pelaajan linnoituksesta ei ole en�� mit��n j�ljell�. Voittaneen pelaajan tilastoihin lis�t��n merkint� voitetusta pelist�. Pelin voi my�s lopettaa koska tahansa siihen tarkoitetulla painikkeella. T�ll�in tulosta ei tallenneta mitenk��n.
- Kun peli on loppunut niin p��dyt��n takaisin aloitusn�kym��n.

### Vacuum chamber kent�n erikoisuudet

- Vacuum chamber kent�ss� tyhji�kammio ei tuhoudu r�j�hdysalueelta toisin kuin normaalissa kent�ss� maa tuhoutuu.
- Vacuum chamber kent�ss� voi ime� ilman pois tyhji�kammiosta siihen tarkoitetulla painikkeella ennen ammuksen ampumista, t�ll�in ammukseen ei vaikuta ilmanvastus. Vastaavasti ilman voi p��st�� takaisin kammioon siihen tarkoitetulla painikkeella ennen ammuksen ampumista.
- Vacuum chamber kent�ss� on painike jolla tykki� voi ampua samoilla asetuksilla kuin mit� k�ytti aiemmin ampuessa omalla tykill��n.
- Vacuum chamber kent�ss� ammuksista j�� vana pelikentt��n.

### Ammuksen lentorata

Ammuksen lentorata mallinnetaan fysikaalisesti mahdollisimman todenmukaisesti dynamiikan s��nt�jen mukaan. Ammuksen lentorataa laskettaessa otetaan huomioon ammuksen l�ht�paikka, alkunopeus, alkusuunta, painovoima sek� ilmanvastus.

