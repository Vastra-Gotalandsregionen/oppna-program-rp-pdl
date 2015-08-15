# Anslutningsguide PDL-tjänsten i Regionportalen #
**Innehåll**


## Översikt ##
Anslutande vårdapplikationer behöver genomföra ett antal aktiviteter för att ansluta sig.

Följande stycken beskriver i detalj vad som krävs.

Av dessa så är uppmärkning med HSA-ID är en aktivitet som behöver göras vid en anpassning till Patientdatalagen, oavsett om Regionportalens PDL-tjänst används eller om en egen anpassning görs.

## Tillhandahålla en söktjänst för patientbunden vårdinformation ##
För att PDL-tjänsten ska kunna avgöra om en patient har vårdinformation knutet till sig så behövs en söktjänst för patientinformation och en klassificering av vilken typ av information som finns.

### Söktjänst ###
Söktjänsten är en web service som tar emot ett patient-ID, exempelvis ett personnummer.

Svaret från tjänsten innehåller patientbunden information, där viss information är obligatorisk.

| Information | Obligatorisk |
|:------------|:-------------|
| HSA-ID på enhet som äger informationen | JA`*`        |
| Resurs-ID för informationen | JA           |
| HSA-ID på _vårdenhet_ som äger informationen | NEJ          |
| HSA-ID på vårdgivare som äger informationen | NEJ          |

`*` = Om HSA-ID på nivåerna Vårdenhet och Vårdgivare finns så ersätter detta HSA-ID på enhet

Resurs-ID är de ID som vårdapplikationen får tillbaka. PDL-tjänsten skickar en biljett som innehåller ett urval av Resurs-ID:n, baserat på hur användaren valt information i PDL-tjänstens gränssnitt.

Formatet på Resurs-ID bestäms av vårdapplikationen.

### Klassificering av information ###

Patientinformationen behöver klassificeras enligt vilken informationsresurstyp den tillhör.

PDL-tjänsten behöver ett regelverk för att kunna avgöra vilken typ av information som svaret i söktjänsten innehåller. Ett exempel kan vara att göra skillnad på vad som är en Remiss (Vårdbegäran, VBE) och ett Undersökning (Undersökningsresultat, UND).

Ofta innehåller en vårdapplikation endast en typ av information.

**Informationsresurstyper som hanteras**

| Informationsresurstyp | Kod |
|:----------------------|:----|
| Diagnos               | DIA |
| Funktionsnedsättning  | FUN |
| Läkemedel Ordination/förskrivning | LAK |
| Läkemedel Utlämning   | LKM |
| PADL                  | PAD |
| Vård- och omsorgstagare | PAT |
| Undersökningsresultat | UND |
| Uppmärksamhetsinformation | UPP |
| Vårdbegäran           | VBE |
| Vård- och omsorgstjänst | VOT |
| Vård- och omsorgskontakt | VKO |
| Vård- och omsorgsdokument (ostrukturerad) | VOO |
| Vård- och omsorgsplan (ostrukturerad) | VPO |

## Märka upp vårdinformation med HSA-ID ##

Säkerhetstjänsterna kräver att information som kontrolleras är uppmärkt med HSA-ID:n från HSA-katalogens struktur. När spärrar, patientrelationer och samtycken skapas så märks de upp med HSA-ID:n som jämförs enligt ett regelverk mot patientinformation.

Uppmärkning av patientinformation med HSA-ID:n **är därför en förutsättning** vid en PDL-anpassning för att kunna följa Vård- och omsorgsbranschens framtagna kontrakt kring säkerhetstjänsterna.

Detta arbete kan vara både komplext och omfattande om HSA-ID:n inte finns sedan innan. Erfarenhet har visat att det är ytterst viktigt att i ett tidigt skede göra en analys av vilken information som saknar HSA-ID.

### Om HSA från Ineras hemsida ###
"HSA är en elektronisk katalog som innehåller kvalitetssäkrade uppgifter om personer, funktioner och enheter i Sveriges kommuner, landsting och privata vårdgivare."

[Generell information om HSA och dess innebörd finns på Ineras hemsida](http://www.inera.se/TJANSTER--PROJEKT/HSA/).

## Webbaserad visare ##
Eftersom möjligheten finns att användaren väljer information från flera olika vårdapplikationer om denne söker från Regionportalen. Då presenteras denne med en vy där olika visare kan väljas.  I de fall det finns fler än en visare för vald information.

![https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_8.png](https://oppna-program-rp-pdl.googlecode.com/svn/wiki/images/pdl_inre_8.png)

Denna visare kan öppnas antingen som en iframe eller som en extern länk.

## Mottaga och tolka biljetter ##
För att förmedla vilka val som görs i PDL-tjänsten så skickas en PDL-biljett med till visaren. Visaren behöver då begränsa urvalet av information som användaren har tillgång till baserat på de Resurs-ID som finns i biljetten.

Förutom PDL-biljetten så finns även en SAML-biljett att tillgå som kommer från Ineras SITHS-federering. Inga val i PDL-tjänsten får göras utan en starkt användare med ett Vård och Behandling-uppdrag.

### PDL-Biljett ###
Biljetten är en digitalt signerad XML struktur som innehåller information om användaren, patienten och vilken information som är vald.

Vårdapplikationen kan välja vilken struktur som Resurs-ID:n ska presenteras som i biljetten.

Fält som ingår i PDL-biljetten
| Fältnamn | Beskrivning |
|:---------|:------------|
| UserContext.employeeDisplayName | Användarens visningsnamn (Förnamn Efternamn) |
| UserContext.employeeHsaId | Unikt HSA-ID för användaren |
| Patient.patientId | Patient-ID (20-101010-1010)|
| Patient.patientDisplayName | Patientens visningsnamn (Förnamn Efternamn) |
| Patient.sex | Kön på patienten |
| Patient.age | Patientens ålder |
| References | Strukturer som varierar mellan olika vårdapplikationer |

**När vårdapplikationen tar emot biljetten så behövs ett antal kriterier uppfyllas för att validera biljettens äkthet**

  1. Hämta ut signaturen
  1. Hämta ut certifikatet
  1. Verifiera certifikatets äkthet
    * [Verifiera att certifikatet är utfärdat av en betrodd SITHS CA-certifikat utfärdare](http://www.inera.se/TJANSTER--PROJEKT/SITHS/Dokument-for-siths/CA-certifikat-SITHS/)
    * Verifiera att certifikatet inte är tillbakadraget
    * Verifiera att HSA-ID (extrafältet: Serialnumber) i certifikatet överensstämmer med PDL-tjänstens serienummer
  1. Verifiera meddelandets äkthet genom att verifiera signaturen gentemot certifikatet
  1. Hämta ut strukturen som innehåller Resurs-ID:n och visa upp den informationen för användaren

Denna hantering skiljer sig inte mot annan certifikatshantering men är viktig för att vara säker på att biljetten inte har blivit påverkad på vägen mellan PDL-tjänsten och vårdapplikationens visare.

I grundutförandet så skickas XML-biljetten skickas över som ett POST-meddelande.

### SAML-biljett ###
Då PDL-biljetten endast verifierar att en viss användare har gjort ett antal val så kan visaren även behöva identifiera avändarens identitet i de fallen att användaren inte redan är inloggad i vårdapplikationens visare.

Om vårdapplikationen stödjer federering med SAML 2.0 och SAMBI-profilen så kan vårdapplikationen återanvända den biljett som finns tillgänglig från PDL-tjänsten eller hämta ut en ny mha HSA-ID på användaren.

I biljetten återfinns, förutom en stark identifiering av användaren, aktuellt medarbetaruppdrag.

**Resurser om Autentisiering och SAMBI**
  * Ytterligare [information om Autentisieringstjänsten finns på Ineras hemsida](http://www.inera.se/TJANSTER--PROJEKT/Sakerhetstjanster/Autentiseringstjanst/)
  * Information om SAMBI och det nationella federeringsinitiativet som SAMBI ingår i [återfinns på hemsidan för SAMBI](http://www.sambi.se/)