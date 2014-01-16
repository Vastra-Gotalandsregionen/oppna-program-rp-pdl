<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<portlet:actionURL name="cancelConfirmation" var="cancelConfirmationUrl" />

<div class="clearfix">
    <c:choose>
        <c:when test="${state.confirmRelation && !state.confirmConsent}">
            <div class="clearfix">
                <portlet:actionURL name="establishRelation" var="establishRelationUrl">
                    <portlet:param name="confirmed" value="true" />
                </portlet:actionURL>
                <portlet:renderURL var="startUrl">
                    <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
                </portlet:renderURL>
                
                <h3 class="legend">Intyga patientrelation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h3>
                
                <div class="clearfix callout callout-info">
                	<p class="label">Du måste intyga patientrelation för att få ta del av patientens vårdinformation.</p>
                	
                    <p>Det finns patientinformation för ${state.patient.patientDisplayName}. För att få tillgång till informationen måste du intyga att du har en patientrelation.
                    Här finner du <a href="http://www.vgregion.se/sv/Vastra-Gotalandsregionen/startsida/Vard-och-halsa/Sa-styrs-varden/Halso--och-sjukvardsavdelningen/Patientdatalagen/">information om patientdatalagen</a></p>
                </div>
                
                <a href="${startUrl}" class="link-button-mod">&laquo; Sök igen</a>
                <a href="${establishRelationUrl}" class="link-button-mod link-button-mod-danger">Jag intygar patientrelation &raquo;</a>
                
            <div>
        </c:when>
        <c:when test="${!state.confirmRelation && state.confirmConsent}">
                <portlet:actionURL name="establishConsent" var="establishConsentEmergencyUrl">
                     <portlet:param name="emergency" value="true" />
                     <portlet:param name="confirmed" value="true" />
                </portlet:actionURL>
                <portlet:actionURL name="establishConsent" var="establishConsentUrl" >
                    <portlet:param name="emergency" value="false" />
                    <portlet:param name="confirmed" value="true" />
                </portlet:actionURL>

                <div class="clearfix confirm">
                    <p class="heading">För att få tillgång till information från andra vårdgivare så måste ${state.patient.patientDisplayName} medge sitt samtycke till sammanhållen journalföring</p>

                    <div class="clearfix">
                        <a href="${establishConsentUrl}" class="link-button-mod button-simple">Intyga samtycke</a>
                        <a href="${establishConsentEmergencyUrl}" class="link-button-mod button-simple-danger">Nödsituation</i></a>
                        <a href="${cancelConfirmationUrl}" class="link-button-mod button-simple">Avbryt</a>
                    </div>
                </div>
        </c:when>
  </c:choose>
</div>

