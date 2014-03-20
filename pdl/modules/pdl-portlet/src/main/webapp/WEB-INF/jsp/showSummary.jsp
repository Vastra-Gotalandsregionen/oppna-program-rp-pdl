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
    <%@ include file="progress.jsp" %>

    <div class="info">
        <c:set var="patientInfoFor" value="System med patientinformation" scope="request"/>
        <%@ include file="patientInformationFor.jsp" %>
    </div>
    <div class="clearfix">
        <portlet:renderURL var="startUrl">
            <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
        </portlet:renderURL>
        <ul class="section-navigation-list clearfix">
            <c:forEach items="${state.sumReport.careSystems}" var="systemEntry">
                <li>
                    <a href="${careSystemUrls[systemEntry.key.systemKey]}">
                        <span class="inner">
                            <span class="title">${systemEntry.key.displayName}</span>
                            <span class="description">
                                <div class="clearfix">
                                    <br/>
                                    <c:forEach items="${systemEntry.value}" var="withInfoType">
                                        <b>${withInfoType.informationType.desc}</b><br/>
                                        <c:forEach items="${withInfoType.value}" var="system">
                                            ${system.careProviderDisplayName} - ${system.careUnitDisplayName}<br/>
                                        </c:forEach>
                                        <br/>
                                    </c:forEach>
                                </div>
                            </span>
                        </span>
                    </a>
                </li>
            </c:forEach>
        <ul>
    </div>
    <div class="clearfix">
        <portlet:actionURL name="searchPatient" var="searchPatientUrl">
            <portlet:param name="patientId" value="${state.patient.patientId}" />
            <portlet:param name="patientIdType" value="" />
            <portlet:param name="currentAssignment" value="${state.ctx.value.currentAssignment.assignmentHsaId}" />
            <portlet:param name="reset" value="false" />
        </portlet:actionURL>
        <a href="${startUrl}" class="link-button-mod">&laquo; Ny sökning</a>
        <a href="${searchPatientUrl}" class="link-button-mod">&laquo; Förändra dina val</a>
    </div>
</div>
