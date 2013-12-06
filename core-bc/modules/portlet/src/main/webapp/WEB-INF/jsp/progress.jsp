<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/rp-progressbar.css" />
<portlet:defineObjects />
<liferay-theme:defineObjects />

<div>
    <ul class="rp-progress-bar clearfix">
        <li class="first <c:choose><c:when test="${state.currentProgress == 'SEARCH'}">current-unstarted</c:when><c:when test="${state.currentProgress == 'CHOOSE'}">done-current</c:when></c:choose>">
            S&ouml;k patientinformation
        </li>
        <li class="first <c:choose><c:when test="${state.currentProgress == 'CHOOSE'}">current-unstarted</c:when><c:when test="${state.currentProgress == 'SYSTEMS'}">done-current</c:when></c:choose>">
            V&auml;lj informationsresurs
        </li>
        <li class="first <c:choose><c:when test="${state.currentProgress == 'SYSTEMS'}">current-unstarted</c:when></c:choose>">
            V&auml;lj informationsk&auml;lla
        </li>
    </ul>
</div>
