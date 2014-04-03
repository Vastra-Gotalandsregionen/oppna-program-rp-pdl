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
    <c:if test="${state.currentReferral.failure && state.currentReferral.outcome != 'UNFULFILLED_FAILURE'}">
       <div class="callout callout-danger">
           Tjänst för att hämta remiss
           <c:choose>
               <c:when test="${state.currentReferral.outcome == 'CLIENT_FAILURE'}">misslyckades på grund av ett klientfel.</c:when>
               <c:when test="${state.currentReferral.outcome == 'COMMUNICATION_FAILURE'}">misslyckades på grund av ett kommunikationsfel.</c:when>
               <c:when test="${state.currentReferral.outcome == 'REMOTE_FAILURE'}">misslyckades på grund av ett fel i säkerhetstjänsterna.</c:when>
           </c:choose>
       </div>
    </c:if>

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

                    <c:set var="expand" value="${state.ticket.success && state.currentReferral.success && infoRow.infoBrokerId eq state.currentReferral.value.infoBrokerId}" />

                    <portlet:actionURL name="showReferral" var="showReferralUrl">
                        <portlet:param name="requestId" value="${infoRow.id}" />
                        <portlet:param name="expand" value="${expand ? false : true}" />
                    </portlet:actionURL>
                    <c:set var="rowClass" value="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}"/>
                    <tr class="${rowClass}">
                        <td><a href="${showReferralUrl}">${infoRow.requestDisplayDate}</a></td>
                        <td>${infoRow.numImages}</td>
                        <td>${infoRow.careUnitDisplayName}</td>
                        <td>${infoRow.orgUnitDisplayName}</td>
                        <td>${infoRow.examinations}</td>
                        <td>${infoRow.status}</td>
                    </tr>
                        <c:if test="${expand && state.currentReferral.success}">
                            <tr class="${rowClass}">
                                <td colspan="6">
                                    <%@ include file="showReferral.jsp" %>
                                </td>
                            </tr>
                        </c:if>
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

</div>
