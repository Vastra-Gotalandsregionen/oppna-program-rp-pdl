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

    <div class="info">
        <h1>Summering av patientinformation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h1>

        Dina val.
    </div>
    <div class="clearfix">
        <portlet:renderURL var="startUrl">
            <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
        </portlet:renderURL>
        <a href="${startUrl}" class="link-button-mod">Ny sökning</a>
    </div>
</div>
