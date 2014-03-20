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
        <c:when test="${state.refs.success}">
            <!-- IE < 10 does not like giving a tbody a height.  The workaround here applies the scrolling to a wrapped <div>. -->
            <!--[if lte IE 9]>
            <div class="old_ie_wrapper">
            <!--<![endif]-->
            <table class="fixed_headers">
                <thead>
                    <tr>
                      <th class="text" scope="col">Datum</th>
                      <th class="text" scope="col">Bilder</th>
                      <th class="text" scope="col">Remitterande vårdenhet</th>
                      <th class="text" scope="col">Remitterande organisatorisk enhet</th>
                      <th class="text" scope="col">Undersökningar</th>
                      <th class="text" scope="col">Status</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="infoRow" items="${state.refs.value}" varStatus="loopStatus">
                    <portlet:actionURL name="showReferral" var="showReferralUrl">
                        <portlet:param name="requestId" value="${infoRow.id}" />
                    </portlet:actionURL>
                    <tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'} ${state.currentReferral.success && state.currentReferral.value.infoBrokerId == infoRow.infoBrokerId ? 'selected' : ''}">
                        <td><a href="${showReferralUrl}">${infoRow.requestDisplayDate}</a></td>
                        <td>${infoRow.numImages}</td>
                        <td>${infoRow.careUnitDisplayName}</td>
                        <td>${infoRow.orgUnitDisplayName}</td>
                        <td>${infoRow.examinations}</td>
                        <td>${infoRow.status}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <!--[if lte IE 9]>
            </div>
            <!--<![endif]-->
        </c:when>
        <c:otherwise>
            <div class="clearfix callout callout-info">
                Patientinformation saknas
            </div>
        </c:otherwise>
    </c:choose>

    <%@ include file="showReferral.jsp" %>

</div>
