<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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

<div class="pdl clearfix">
    <jsp:include page="outcomeInfo.jsp" />
    <div class="info">
        <c:choose>
            <c:when test="${!state.pdlReport.hasPatientInformation}">
                <h2>Det saknas patientinformation i källsystem för ${state.patient.patientDisplayName} (${state.patient.patientId})</h2>
            </c:when>
            <c:when test="${state.pdlReport.missingBothRelationConsent}">
                <c:choose>
                    <c:when test="${state.ctx.otherProviders}">
                        <jsp:include page="establishRelationConsent.jsp" />
                    </c:when>
                    <c:otherwise>
                        <jsp:include page="establishRelation.jsp" />
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:when test="${!state.pdlReport.hasRelationship.value}">
                <jsp:include page="establishRelation.jsp" />
            </c:when>
            <c:when test="${state.pdlReport.hasRelationship.value}">
                <c:choose>
                    <c:when test="${!state.showOtherCareUnits}">
                         <jsp:include page="sameCareUnit.jsp" />
                    </c:when>
                    <c:when test="${state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
                        <jsp:include page="otherCareProvider.jsp" />
                    </c:when>
                    <c:when test="${state.showOtherCareUnits}">
                         <jsp:include page="otherCareUnit.jsp" />
                    </c:when>
                </c:choose>
            </c:when>
            <c:otherwise>
                <div class="callout callout-warning">
                    Sökningen har hamnat i ett okänt läge. Var vänlig och försök att söka igen. Om problemet kvarstår kontakta support.
                </div>
            </c:otherwise>
        </c:choose>
        <!--
        <p>state.pdlReport.consent.value.hasConsent = ${state.pdlReport.consent.value.hasConsent}</p>
        <p>state.pdlReport.hasRelationship.value = ${state.pdlReport.hasRelationship.value}</p>
        <p>state.ctx.otherProviders = ${state.ctx.otherProviders}</p>
        <p>state.showOtherCareUnits = ${state.showOtherCareUnits}</p>
        <p>state.showOtherCareProviders = ${state.showOtherCareProviders}</p>
        -->
    </div>
</div>
