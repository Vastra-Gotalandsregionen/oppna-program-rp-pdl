<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://portalen.vgregion.se/pdl" prefix="pdl" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/pdl.css" />

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="pdl clearfix">
    <%@ include file="progress.jsp" %>

    <%@ include file="choose/searchOutcomeInfo.jsp" %>
    <div class="info">
        <c:set var="patientInfoFor" value="Patientinformation" scope="request"/>
        <%@ include file="patientInformationFor.jsp" %>
        <c:choose>
            <c:when test="${state.patientInformationExist && state.pdlReport.hasRelationship.value}">
                <%@ include file="choose/hasInformation.jsp" %>
            </c:when>
            <c:when test="${state.patientInformationExist && !state.pdlReport.hasRelationship.value}">
                <%@ include file="choose/establishRelation.jsp" %>
            </c:when>
            <c:otherwise>
                <div class="clearfix callout callout-info">
                    Patientinformation saknas
                </div>

                <portlet:renderURL var="startUrl">
                    <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
                </portlet:renderURL>
                <a href="${startUrl}" class="link-button-mod">&laquo; Ny sökning</a>
            </c:otherwise>
        </c:choose>

        <!-- state.pdlReport.consent.value.hasConsent = ${state.pdlReport.consent.value.hasConsent} -->
        <!-- state.pdlReport.hasRelationship.value = ${state.pdlReport.hasRelationship.value} -->
        <!-- state.ctx.value.currentAssignment.otherProviders = ${state.ctx.value.currentAssignment.otherProviders} -->
        <!-- state.searchSession = ${state.searchSession} -->
        <!-- state.shouldBeVisible = ${state.shouldBeVisible} -->
        <!-- state.currentVisibility = ${state.currentVisibility} -->
    </div>
</div>
