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
    <jsp:include page="progress.jsp" />

    <jsp:include page="outcomeInfo.jsp" />
    <div class="info">
        <c:choose>
            <c:when test="${state.pdlReport.hasPatientInformation}">
                <jsp:include page="hasInformation.jsp" />
            </c:when>
            <c:otherwise>
                <h2>Det finns ingen patientinformation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h2>
            </c:otherwise>
        </c:choose>

        <!-- state.pdlReport.consent.value.hasConsent = ${state.pdlReport.consent.value.hasConsent} -->
        <!-- state.pdlReport.hasRelationship.value = ${state.pdlReport.hasRelationship.value} -->
        <!-- state.ctx.otherProviders = ${state.ctx.otherProviders} -->
        <!-- state.showOtherCareUnits = ${state.showOtherCareUnits} -->
        <!-- state.showOtherCareProviders = ${state.showOtherCareProviders} -->
        <!-- state.searchSession = ${state.searchSession} -->
        <!-- state.shouldBeVisible = ${state.shouldBeVisible} -->
        <!-- state.currentVisibility = ${state.currentVisibility} -->
    </div>
</div>
