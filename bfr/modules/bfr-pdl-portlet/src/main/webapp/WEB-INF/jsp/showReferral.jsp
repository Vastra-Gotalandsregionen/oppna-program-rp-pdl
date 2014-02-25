<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/bfr.css" />
<div class="clearfix">
    <c:set var="patientInfoFor" value="Patientinformation Radiologi" scope="request"/>
    <%@ include file="patientInformationFor.jsp" %>

    <c:choose>
        <c:when test="${state.ticket.success && state.currentReferral.success}">
            <ul>
                <li>RisId: ${state.currentReferral.value.risId}</li>
                <li>Prioritet: ${state.currentReferral.value.priority}</li>
                <li>Placeringsdatum: ${state.currentReferral.value.placingDate}</li>
                <li>Utförande enhet: ${state.currentReferral.value.fillerLocation}</li>
                <li>Remitterande enhet: ${state.currentReferral.value.placerLocation}</li>
                <li>Remitterande läkare: ${state.currentReferral.value.referringPhysicianName}</li>
                <li>Frågeställning: ${state.currentReferral.value.question}</li>
                <li>Anamnes: ${state.currentReferral.value.anamnesis}</li>
            </ul>
            <c:forEach var="study" items="${state.currentReferral.value.studies}" varStatus="studiesStatus">
                <ul>
                    <ul>
                        <li>Studier</li>
                        <c:forEach var="url" items="${study.studyUrls}" varStatus="urlStatus">
                            <li><a href="${url}" target="_blank">Bild ${urlStatus.index}</a></li>
                        </c:forEach>
                    </ul>
                    <li>Undersökning: ${study.risId}</li>
                    <li>Utförandedatum: ${study.date}</li>
                    <li>${study.description}</li>
                    <li>Serie: ${study.noOfImages}</li>
                    <li>
                        <c:forEach var="report" items="${study.studyReports}" varStatus="reportStatus">
                            <ul>
                                <li>Undersökningssvar - ${report.status}</li>
                                <li>Svarsdatum: ${report.date}</li>
                                <li>Svarande läkare: ${report.signer}</li>
                                <li>Svarstext: ${report.text}</li>
                            </ul>
                        </c:forEach>
                    </li>
                </ul>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="clearfix callout callout-info">
                Kan ej hitta information
            </div>
        </c:otherwise>
    </c:choose>
</div>


<!--
  public final String status;
    public final Date date;
    public final String signer;
    public final String text;
    -->
