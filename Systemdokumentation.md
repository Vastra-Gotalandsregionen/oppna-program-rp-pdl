# Systemdokumentation PDL-tjänsten i Regionportalen #
**Innehåll**


## Bakgrund och ändamål ##
Patientdatalagen syftar till att reglera hur vårdpersonal tar del av patientinformation.

Applikationen "Sök patientinformation med stöd för PDL" innehåller ett gränssnitt som guidar användaren att göra de val som krävs för att PDL ska uppfyllas. När information valts ut så visas den i en visare som ej ska göra annan information tillgänglig än den som användaren aktivt har valt.

Ändamålet med applikationen är främst att skapa en central ingång för att visa patientinformation och samtidigt förenkla för webbsystem att stödja PDL.

**Externa resurser**

  * [Socialstyrelsens handbok för PDL](http://www.socialstyrelsen.se/regelverk/handbocker/handbokominformationshanteringochjournalforing).

## Funktionsbeskrivning ##
I korthet så kräver PDL att ett antal krav uppfylls innan patientinformation visas.

  1. Vårdpersonal ska identifieras med stark autentisering (exempelvis SITHS e-legitimation)
  1. Vårdpersonal ska ha en aktiv patientrelation med patienten
  1. Vårdpersonal arbetar med ett aktuellt vårduppdrag som beskriver vad han eller hon får tillgång att se
  1. Vårdpersonal kan direkt välja bland information som tillhör den vårdenhet som uppdraget ligger på
  1. Vårdpersonal måste aktivt välja att få se information från andra vårdenheter eller vårdgivare (om uppdraget tillåter det
  1. Vårdpersonal måste aktivt välja vilken information som är relevant för det aktuella tillfället
  1. Alla val som gör loggas i en systemlogg och ligger till grund för uppföljning för att stävja missbruk
  1. Vid sammanhållen journalföring (direktåtkomst av information från annan vårdgivare) så krävs att patienten har gett sitt samtycke till sammanhållen journalföring
  1. Vårdpersonal som sökt på en patient med spärrad information på annan vårdenhet behöver göra ett aktivt val att få se på vilken vårdenhet den spärrade informationen finns
  1. Vårdpersonal som vill få tillgång till spärrad information på annan vårdenhet behöver inhämta samtycke från patient eller intyga att en nödsituation råder
  1. Vårdpersonal kan ej komma åt spärrad information hos andra vårdgivare utan att först kontakta denna vårdgivare och be dem att öppna spärren för dem

## Användargränssnitt ##
Flöde vår Vårdpersonal som vill söka på patientinformation. Detta flödet visar alla situationer som kan uppstå inom PDL. I normalfallet så räcker det med tre musklick.

**Inre sekretess**
  1. Ett tillgängligt medarbetaruppdrag väljs och en sökning görs via gränssnittet ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_1.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_1.png)
  1. Användaren behöver intyga patientrelation om en sådan inte redan finns ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_2.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_2.png)
  1. Användaren får se information som tillhör vårdenheten för det valda medarbetaruppdraget (i detta fallet tomt). Användaren väljer istället att få se information från en annan vårdenhet ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_3.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_3.png)
  1. Då det finns spärrade vårdenheter kan användaren avgöra om dessa är relevant och välja att få se vart informationen finns ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_4.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_4.png)
  1. Efter ett aktivt val får användaren se ytterligare tillgänglig information (uppmärkt med hänglås)  ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_5.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_5.png)
  1. I det fall att användaren beslutar att den spärrade informationen är relevant så kan denne välja att passera spärren ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_6.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_6.png)
  1. Slutgiltligen väljer användaren att gå vidare och visa vårdsystem ![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_8.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_8.png)

**Sammanhållen Journalföring**
  1. Ett tillgängligt uppdrag väljs och en sökning görs via gränssnittet
  1. Användaren behöver intyga patientrelation om en sådan inte redan finns
  1. Användaren behöver intyga att samtyckte från patient finns om en sådan inte redan finns
  1. Användaren får se information som tillhör vårdenheten för det valda medarbetaruppdraget
  1. Användaren väljer att få se information från en annan vårdgivare
  1. Då det finns spärrade vårdenheter kan användaren avgöra om dessa är relevant och välja att få se vart informationen finns
  1. Eftersom uppdraget är Sammanhållen Journalföring och informationen finns hos en annan vårgivare så kan inte användaren välja att passera spärren.
  1. Slutgiltligen väljer användaren att gå vidare och visa vårdsystem

## Översiktlig systembeskrivning ##
Systemet består av ett Portlet gränssnitt, samt ett antal interna tjänster som kommunicerar med tredjepartssystem.

Systemet har även en PostgreSQL-databas i vilken logginformation sparas. Denna kan komma att ersättas med en loggtjänst i framtiden.

Information hämtas in från flera tjänster och ger i slutändan användaren möjlighet att se patientinformation.

[![](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/PDL%20Informationso%CC%88versikt.png)](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/pdf/PDL%20Informationso%CC%88versikt%20v01.pdf)
_[Diagram för informationsöversikt](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/pdf/PDL%20Informationso%CC%88versikt%20v01.pdf)_

## Informations- och verksamhetslogik ##
Diagrammet beskriver standardflödet i verksamhetslogiken tillsammans med en beskrivning av hur information transformeras för varje steg.

[![](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/PDL%20Verksamhetslogik.png)](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/pdf/PDL%20Verksamhetslogik%20v02.pdf)
_[Diagram för PDL Verksamhetslogik](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/pdf/PDL%20Verksamhetslogik%20v02.pdf)_

## HSA Medarbetaruppdrag ##
Diagrammet beskriver logiken för att hantera HSA medarbetaruppdrag.

[![](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/HSA%20Medarbetaruppdrag.png)](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/pdf/HSA%20Medarbetaruppdrag%20v01.pdf)
_[Diagram för HSA medarbetaruppdrag](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/pdf/HSA%20Medarbetaruppdrag%20v01.pdf)_

## Datalagring ##
Datalagring för loggdata sker i tabellen RP\_PDL\_EVENT\_LOG i ett utpekat schema i en SQL-databas (ex PostgreSQL). Se rubriken loggning för mer information.

## Installation ##
PDL-tjänsten är en Portlet och har beroende mot ett antal andra tjänster för att kunna fungera. Portleten är testad i Liferay men har få direkta beroenden mot Liferay.

Dessa tjänster krävs för version 1.0 av PDL-tjänsten.

| **Beroende** | **Roll** |
|:-------------|:---------|
| Siteminder   | Stark Autentisiering med SITHS Smart card |
| HSA Orgmaster | Katalogtjänst för att göra uppslag för Medarbetaruppdrag, Vårdgivare och Vårdenhet |
| Patientrelation (SÄK) | En patientrelation mellan användaren och patienten är obligatorisk |
| Samtycke till Sammanhållen Journalföring (SÄK) | För SJF så krävs att patienten gett sitt samtycke  |
| Spärrar (SÄK) | Patienten kan hindra vårdpersonal att ta del av information om inte en nödsituation råder |
| Källdata för patientinformation | Olika källsystem tillhandahåller sökning för patientinformation (se separat tabell) |
| Visare       | Patientinformation kan ha olika visare (see separate table) |
| Lagring av logginformation | PostgreSQL databas där data är persisterad. Kan ändras till loggtjänst i framtiden |

| **Source** | **Type of information** | **Viewers** |
|:-----------|:------------------------|:------------|
| Mawell Info Broker | Radiology requests and studies | GE Zero Footprint |

## Konfiguration ##
Konfigurationsfiler läggs i hemmakatalogen under `~/.rp/pdl`. Exempel på konfigurationslayout finns i källkoden.

Dessa element hanteras i konfigurationsfilerna:
  1. Certifikat
  1. Webservice endpoints
  1. Lösenord
  1. Anslutning till datakälla

## Felhantering ##
Eftersom PDL-tjänsten används för både att söka upp patientinformation och reglera åtkomst så är det viktigt att gränssnittet hanterar en situation där en eller flera underliggande tjänster inte är stabila eller inte svarar.

Patientsäkerhet är högsta prioritet. Därför förutsätter PDL-tjänsten att när fel upptår i underliggande tjänster så ska användaren ändå i möjligaste mån få fortsatt åtkomst till den information som finns tillgänglig.

Nedan följer ett par exempel på felhantering i praktiken i PDL-tjänsten.

### En källa med patientinformation svarar med poster otillräcklig data ###
Källdata måste vara uppmärkt med HSA-ID för vårdenhet, om detta ej sker så visas den information som finns tillgänglig upp för användaren tillsammans med en information om att ytterligare information som ej visas finns.

### En tjänst med patientinformation är ej åtkomlig ###
Om det finns patientinformation tillgänglig från andra källor så visas denna informationen tillsammans med ett varning om att information ej kunde hämtas från alla underliggande källor.

### En eller flera tjänster i Säkerhetstjänsterna svarar ej ###
Om en eller flera tjänster i säkerhetstjänsterna ej svarar så passeras dessa. En varning ges om att mer information än vanligt kan vara tillgängligt

Detta är ett exceptionellt läge som måste åtgärdas omgående, men informationen är fortfarande tillgänglig:
  1. Fel i spärrtjänsten - Ingen information är blockeras
  1. Fel i patientrelationstjänsten - Alla patienter har en giltlig relation
  1. Fel i samtyckestjänsten - Alla patienter samtycker till att dela med sig av sin information

### Tjänst som ger HSA-medarbetaruppdrag finns ej att tillgå ###
Stark autentisering är viktigt, därför får användaren inte fortsätta i detta fallet.

## Loggning ##

Loggning är en viktig komponent i PDL-tjänsten för att kunna i efterhand granska de val som en användare gjort i applikationen.

## Fält som ingår i loggtjänsten ##

Varje loggpost innehåller följande fält.

| **Fältnamn** | **Beskrivning** | **Exempel** |
|:-------------|:----------------|:------------|
| id           | Unikt id för raden | 021b9675-5b12-4f36-81e5-ead005adfd9d |
| employee\_display\_name | Visningsnamn för anställde | Karin Mattsson |
| employee\_id | employee\_id    | SE2321000131-P000000000977 |
| patient\_display\_name | Visningsnamn för patient | Tian Testberg |
| patient\_id  | Patient ID (ex personnummer) | 201010101010 |
| assignment\_display\_name | Visningsnamn på medarbetaruppdrag | VoB SJF\_NU Akutklinik |
| assignment\_id | HSA-ID på medarbetaruppdrag | SE2321000131-S000000012310 |
| care\_provider\_display\_name | Visningsnamn för vårdgivare | Västra Götalandsregionen |
| care\_provider\_id | HSA-ID på vårdgivare | SE2321000131-E000000000001 |
| care\_unit\_display\_name | Visningsnamn för vårdenhet | Akutklinik  |
| care\_unit\_id | HSA-ID på vårdenhet | SE2321000131-E000000006834 |
| creation\_time | Tid när loggen skapades | 2014-03-03 15:11:11.133+01 |
| log\_text    | Text som beskriver vad användaren såg i gränssnittet | === UNDERSÖKNINGSRESULTAT === 

&lt;BR&gt;

 [ ] Västra Götalandsregionen - Verksamhet Medicin Geriatrik och Akutmottagning Östra |
| system\_id   | Identifiering på vilket system som loggat | Regionportalen - Sök Patient PDL |
| user\_action | Vilket aktiva val som användaren utfört | ATTEST\_RELATION |
| search\_session | Unikt id för att knyta ihop de aktiva val som gjorts under en sökning | "bba806a0-d16c-4d59-9668-cec16f89f7e9" |

Under en sökning skapas flera rader av logginformation i databasen. Det som är intressant är att se vilka aktiva val användaren gör. Detta skrivs in i fältet user\_action ovan.

| **Fältvärde** | **Loggningstillfälle** |
|:--------------|:-----------------------|
| SEARCH\_PATIENT | När patienten trycker på sökknappen |
| ATTEST\_RELATION | När en patientrelation behöver attesteras |
| ATTEST\_CONSENT | När ett patientsamtycke till sammanhållen jounralföring attesteras |
| EMERGENCY\_CONSENT | När en nödsituation råder och samtycke till sammanhållen journalföring behövs |
| REVEAL\_BLOCKED | När användaren anser sig behöva få reda på vilken information som är skyddad |
| PASS\_BLOCKED | Passera blockerad information med medgivande från patient |
| EMERGENCY\_PASS\_BLOCKED | Passera blockerad infomrmation vid nödsituation |
| REVEAL\_OTHER\_UNITS | Visar informationsresurstype från andra vårdgivare |
| REVEAL\_OTHER\_PROVIDER | Visar informationsresurstype från andra vårdenheter |
| SUMMARY\_CARE\_SYSTEMS | Visa summeringen för vårdapplikation |
| ENTER\_CARE\_SYSTEM | Vilken vårdapplikation som användaren öppnar |

## Loggtjänsten i Säkerhetstjänsterna ##
Det finns en loggningstjänst i säkerhetstjänsterna, men den var ej klar vi byggandet av PDL-tjänsten. Därför togs beslutet att bygga en lokal loggtjänst i PDL-tjänsterna. Men utgå ifrån Säkerhetstjänsternas loggtjänst för att senare kunna byta över.

## Skillnad mellan Loggtjänst i Säkerhetstjänsten och i PDL-tjänstens logg ##

| **Fältnamn PDL-logg** | **Fältnamn loggtjänst i Säkerhetstjänsterna**  |
|:----------------------|:-----------------------------------------------|
| id                    | Log > LogId                                    |
| employee\_display\_name | Log > User > Name                              |
| employee\_id          | Log > User > UserId                            |
| patient\_display\_name | Log > Resources > PatientName                  |
| patient\_id           | Log > Resources > PatientId                    |
| assignment\_display\_name |                                                |
| assignment\_id        | Log > User > Assignment                        |
| care\_provider\_display\_name | Log > User > CareProvider > CareProviderName   |
| care\_provider\_id    | Log > User > CareProvider > CareProviderId     |
| care\_unit\_display\_name | Log > User > CareUnit > CareUnitName           |
| care\_unit\_id        | Log > User > CareUnit > CareUnitId             |
| creation\_time        | Log > Activity > StartDate                     |
| log\_text             | Log > Resources > Resource                     |
| system\_id            | Log > System > SystemId                        |
| user\_action          |                                                |
| search\_session       |                                                |

## Lokala utvecklingsmiljö ##
## Säkerhet ##
## Kvarstående utvecklingspunkter ##