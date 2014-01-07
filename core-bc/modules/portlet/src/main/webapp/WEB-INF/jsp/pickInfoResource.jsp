<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/pdl.css" />

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="pdl clearfix">
    <jsp:include page="progress.jsp" />

    <jsp:include page="outcomeInfo.jsp" />
    <div class="info">
        <c:choose>
            <c:when test="${state.pdlReport.hasPatientInformation}">
                <jsp:include page="hasInformation.jsp" />
            </c:when>
            <c:otherwise>
                <h2>Det finns ingen patientinformation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h2>
                <portlet:renderURL var="startUrl">
                    <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
                </portlet:renderURL>
                <a href="${startUrl}" class="link-button-mod">Ny sökning</a>
            </c:otherwise>
        </c:choose>

        <!-- state.pdlReport.consent.value.hasConsent = ${state.pdlReport.consent.value.hasConsent} -->
        <!-- state.pdlReport.hasRelationship.value = ${state.pdlReport.hasRelationship.value} -->
        <!-- state.ctx.value.currentAssignment.otherProviders = ${state.ctx.value.currentAssignment.otherProviders} -->
        <!-- state.showOtherCareUnits = ${state.showOtherCareUnits} -->
        <!-- state.showOtherCareProviders = ${state.showOtherCareProviders} -->
        <!-- state.searchSession = ${state.searchSession} -->
        <!-- state.shouldBeVisible = ${state.shouldBeVisible} -->
        <!-- state.currentVisibility = ${state.currentVisibility} -->
        <!-- state.confirmRelation = ${state.confirmRelation} -->
        <!-- state.confirmConsent = ${state.confirmConsent} -->
    </div>
</div>