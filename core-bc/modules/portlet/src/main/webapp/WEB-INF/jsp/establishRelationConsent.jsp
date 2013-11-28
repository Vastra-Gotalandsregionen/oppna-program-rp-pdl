<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<h2>För ${state.patient.patientDisplayName} (${state.patient.patientId}) finns varken samtycke för sammanhållen journalföring eller patientrelation registrerat</h2>
<br/>
<h3 class="legend">Att intyga patientrelation eller samtycke för sammanhållen journalföring</h3>
<p>
    För att få se patientinformation måste det finnas en registrerad patientrelation. Du kan själv intyga och registrera en patientrelation nedan.
</p>
<p>
    Vill du se patientinformation från andra vårdgivare än din egen måste du även intyga att patienten har gett sitt Samtycke för sammanhållen journalföring.
</p>
<p>
    Vid nödsituation så kan ett akut Samtycke för sammanhållen journalföring upprättas.
<p/>
<jsp:include page="pdlInfoCallout.jsp" />
<div>
    <portlet:actionURL name="establishRelation" var="establishRelationUrl" />

    <portlet:actionURL name="establishRelationConsent" var="relationConsentUrl">
        <portlet:param name="emergency" value="false" />
    </portlet:actionURL>

    <portlet:actionURL name="establishRelationConsent" var="relationConsentEmergencyUrl">
        <portlet:param name="emergency" value="true" />
    </portlet:actionURL>

    <a href="${establishRelationUrl}" class="link-button-mod">Intyga patientrelation</a>
    <a href="${relationConsentUrl}" class="link-button-mod">Intyga patientrelation och samtycke</a>
    <a href="${relationConsentEmergencyUrl}" class="link-button-mod link-button-mod-danger">Nödöppning av sammanhållen journalföring</a>
</div>

