<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="clearfix">
    <portlet:actionURL name="establishRelation" var="establishRelationUrl">
        <portlet:param name="confirmed" value="true" />
    </portlet:actionURL>

    <h3 class="legend">Intyga patientrelation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h3>

    <div class="clearfix callout callout-info">
        <p class="label">Du måste intyga patientrelation för att få ta del av patientens vårdinformation.</p>

        <p>Det finns patientinformation för ${state.patient.patientDisplayName}. För att få tillgång till informationen måste du intyga att du har en patientrelation.
        Här finner du <a href="http://www.vgregion.se/sv/Vastra-Gotalandsregionen/startsida/Vard-och-halsa/Sa-styrs-varden/Halso--och-sjukvardsavdelningen/Patientdatalagen/">information om patientdatalagen</a></p>
    </div>
    <a href="${startUrl}" class="link-button-mod">&laquo; Sök igen</a>
    <a href="${establishRelationUrl}" class="link-button-mod link-button-mod-danger">Jag intygar patientrelation &raquo;</a>
<div>
