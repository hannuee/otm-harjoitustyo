# OTM-Harjoitusty�


## Fortress Duel

Fortress Duel on peli joka on tarkoitettu 2 pelaajan pelattavaksi toisiaan vastaan samalla tietokoneella. Peliss� pelaajat yritt�v�t tuhota toistensa linnoitukset ampumalla niit� vuorotellen oman linnoituksensa tykill�.


## Dokumentaatio

[M��rittelydokumentti](https://github.com/hannuee/otm-harjoitustyo/blob/master/dokumentointi/maarittelydokumentti.md)

[Arkkitehtuurikuvaus](https://github.com/hannuee/otm-harjoitustyo/blob/master/dokumentointi/arkkitehtuuri.md)

[Ty�aikakirjanpito](https://github.com/hannuee/otm-harjoitustyo/blob/master/dokumentointi/tyoaikakirjanpito.md)


## Releaset

[v1.0](https://github.com/hannuee/otm-harjoitustyo/releases/tag/v1.0)

[v0.9-beta](https://github.com/hannuee/otm-harjoitustyo/releases/tag/v0.9-beta)

[v0.8-beta](https://github.com/hannuee/otm-harjoitustyo/releases/tag/v0.8-beta)


## Komentorivitoiminnot

Kaikki allaolevat toiminnot voi my�s suorittaa NetBeansin painikkeilla.
Seuraavat komennot tulee suorittaa kansiossa _otm-harjoitustyo\FortressDuel_.

### Testaus

Testit suoritetaan komennolla

```
mvn test
```

Testikattavuusraportti luodaan komennolla

```
mvn jacoco:report
```

Kattavuusraporttia voi tarkastella avaamalla selaimella tiedosto _target/site/jacoco/index.html_

### Suoritettavan jarin generointi

Komento

```
mvn package
```

generoi hakemistoon _target_ suoritettavan jar-tiedoston _FortressDuel-1.0-SNAPSHOT.jar_

### Checkstyle

Tiedostoon [checkstyle.xml](https://github.com/hannuee/otm-harjoitustyo/blob/master/FortressDuel/checkstyle.xml) m��rittelem�t tarkistukset suoritetaan komennolla

```
 mvn jxr:jxr checkstyle:checkstyle
```

Mahdolliset virheilmoitukset selvi�v�t avaamalla selaimella tiedosto _target/site/checkstyle.html_