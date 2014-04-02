<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/rp-progressbar.css" />
<portlet:defineObjects />
<liferay-theme:defineObjects />

<div>
    <ul class="rp-progress-bar clearfix">
        <li class="first <c:choose><c:when test="${state.currentProgress == 'SEARCH'}">current-unstarted</c:when><c:when test="${state.currentProgress != 'SEARCH'}">done-current</c:when></c:choose>">
            <portlet:renderURL var="startUrl">
                <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
            </portlet:renderURL>
            <a href="${startUrl}">S&ouml;k patientinformation</a>
        </li>
        <li class="<c:choose><c:when test="${state.currentProgress == 'CHOOSE'}">current-unstarted</c:when><c:when test="${state.currentProgress == 'SYSTEMS'}">done-current</c:when></c:choose>">
            <c:choose>
                <c:when test="${state.currentProgress == 'CHOOSE' || state.currentProgress == 'SYSTEMS'}">
                    <portlet:actionURL name="searchPatient" var="searchPatientUrl">
                        <portlet:param name="patientId" value="${state.patient.patientId}" />
                        <portlet:param name="patientIdType" value="" />
                        <portlet:param name="currentAssignment" value="${state.ctx.value.currentAssignment}" />
                        <portlet:param name="reset" value="false" />
                    </portlet:actionURL>
                    <a href="${searchPatientUrl}">V&auml;lj vårdenheter</a>
                </c:when>
                <c:otherwise>
                    V&auml;lj vårdenheter
                </c:otherwise>
            </c:choose>
        </li>
        <li class="last <c:if test="${state.currentProgress == 'SYSTEMS'}">done-current</c:if>">
            <c:choose>
                <c:when test="${state.currentProgress == 'SYSTEMS'}">
                    <portlet:actionURL name="showSummary" var="showSummaryUrl" />
                    <a href="${showSummaryUrl}">Visa system</a>
                </c:when>
                <c:otherwise>
                    Visa system
                </c:otherwise>
            </c:choose>
        </li>
    </ul>
</div>
