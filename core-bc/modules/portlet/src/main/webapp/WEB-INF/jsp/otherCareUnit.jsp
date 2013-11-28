<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:forEach var="infotype" items="${state.csReport.includeOtherCareUnit}">
   <h3 class="legend">${infotype.desc}</h3>
   <c:forEach var="system" items="${state.csReport.systems.value[infotype]}">
       <ul>
           <c:if test="${system.visibility == 'SAME_CARE_UNIT' || system.visibility == 'OTHER_CARE_UNIT'}">
               <li>${system.value.value.displayName}</li>
           </c:if>
       </ul>
   </c:forEach>
</c:forEach>

<c:if test="${state.ctx.otherProviders}">
    <portlet:actionURL name="showOtherCareProviders" var="showOtherCareProvidersUrl" />
    <a href="${showOtherCareProvidersUrl}" class="link-button-mod">Visa information för andra vårdgivare</a>
</c:if>
