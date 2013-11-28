<%@page contentType="text/html" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<jsp:include page="common.jsp" />

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="pdl">

    <jsp:include page="outcomeInfo.jsp" />
    <div class="info">
        <c:choose>
            <c:when test="${!state.pdlReport.hasPatientInformation}">
                <h2>Det saknas patientinformation i källsystem för ${state.patient.patientDisplayName} (${state.patient.patientId})</h2>
            </c:when>
            <c:when test="${state.pdlReport.missingBothRelationConsent}">
                <h2>För ${state.patient.patientDisplayName} (${state.patient.patientId}) finns varken samtycke för sammanhållen journalföring eller patientrelation tillgängligt</h2>
            </c:when>
            <c:when test="${!state.pdlReport.consent.value.hasConsent}">
                <h2>${state.patient.patientDisplayName} (${state.patient.patientId}) har ej medgivit samtycke för sammanhållen journalföring</h2>
            </c:when>
            <c:when test="${!state.pdlReport.hasRelationship.value}">
                <h2>Du saknar patientrelation med ${state.patient.patientDisplayName} (${state.patient.patientId})</h2>
                <portlet:actionURL name="establishRelationship" var="relationshipURL" />
            </c:when>
        </c:choose>
    </div>
</div>
