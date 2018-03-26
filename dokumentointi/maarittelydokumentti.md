# M‰‰rittelydokumentti


## Sovelluksen tarkoitus

Fortress Duel on peli joka on tarkoitettu 2 pelaajan pelattavaksi toisiaan vastaan samalla tietokoneella. Peliss‰ pelaajat yritt‰v‰t tuhota toistensa linnoitukset ampumalla niit‰ vuorotellen oman linnoituksensa tykill‰.


## K‰ytt‰j‰t

Sovelluksessa on vain 1 k‰ytt‰j‰rooli, pelaaja. Pelaajia on aina kaksi pelaamassa toisiansa vastaan.


## Perusversion toiminta

### Pelin kulku

- Pelin alkaessa molempien pelaajien linnoitukset ovat t‰ysin ehji‰.
- Vuorotellen pelaajat ampuvat yhden laukauksen oman linnoituksensa tykill‰ yritt‰en osua toisen pelaajan linnoitukseen.
  - Kun pelaajan vuoro tulee niin pelaaja m‰‰ritt‰‰ tykkins‰ suuntauksen hiirell‰. Tykki osoittaa linnoituksesta kohti pelaajan hiirt‰. Kun pelaaja on mielest‰‰n lˆyt‰nyt oikean suuntauksen niin pelaaja klikkaa hiirell‰ ruutua. (tykki‰ ei piirret‰)
  - Nyt kun suuntaus on lyˆty lukkoon niin pelaaja m‰‰ritt‰‰ ammuksen alkunopeuden prosentteina suurimmasta mahdollisesta alkunopeudesta. Ruudulla n‰kyv‰ mittari alkaa vaihdella 0% ja 100% prosentin v‰lill‰. Pelaaja painaa hiirell‰ ruutua kun mittari on pelaajan mielest‰ oikeassa kohtaa.
  - Kun pelaaja on m‰‰ritt‰nyt ammuksen alkunopeuden niin ammus l‰htee matkaan.
  - Jos ammus osuu maahan tai linnoitukseen niin se r‰j‰ht‰‰. Jos r‰j‰hdysalueella on linnoitusta niin t‰m‰ osa linnoituksesta tuhoutuu. 
  - Vuoro vaihtuu kun ammus r‰j‰ht‰‰ tai lent‰‰ peli-ikkunan vasemman rajan vasemmalle puolelle tai oikean rajan oikealle puolelle.
- Peli loppuu kun jomman kumman pelaajan linnoituksesta ei ole en‰‰ mit‰‰n j‰ljell‰.

### Ammuksen lentorata

Ammuksen lentorata mallinnetaan fysikaalisesti mahdollisimman todenmukaisesti dynamiikan s‰‰ntˆjen mukaan. Ammuksen lentorataa laskettaessa otetaan huomioon ammuksen alkunopeus ja suunta sek‰ painovoiman vaikutus ammukseen. Ilmanvastusta ei oteta huomioon pelin perusversiossa.
Peli-ikkunassa yksi pixeli vastaa 1 metri‰. Simulaatio on reaaliaikainen.


## Mahdolliset lis‰ominaisuudet

Peliin saatetaan mahdollisesti lis‰t‰ myˆs seuraavat toiminnallisuudet:
- Ilmanvastuksen huomioiminen laskettaessa ammuksen lentorataa.
- Erilaiset kent‰t joissa maan pinnan muodot vaihtelevat peruskent‰n tasaisesta pinnasta.
- Tilastojen pit‰minen pelatuista peleist‰ ja pelien tuloksista tallennettuna paikallisesti tietokoneelle.


