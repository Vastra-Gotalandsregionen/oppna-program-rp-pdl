# Loggning #

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