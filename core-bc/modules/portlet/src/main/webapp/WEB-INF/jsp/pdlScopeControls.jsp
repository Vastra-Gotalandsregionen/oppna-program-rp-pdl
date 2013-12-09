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

<div class="clearfix">
    <c:choose>
       <c:when test="${!state.showOtherCareUnits}">
            <p>
                <portlet:actionURL name="showOtherCareUnits" var="showOtherCareUnitsUrl" />
                <a href="${showOtherCareUnitsUrl}" class="link-button-mod button-simple">Visa information för andra vårdenheter</a>
            </p>
       </c:when>
       <c:when test="${state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
           <p>
               <c:if test="${!state.pdlReport.consent.value.hasConsent}">
                   <jsp:include page="establishConsent.jsp" />
               </c:if>
               <c:if test="${!state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
                   <portlet:actionURL name="showOtherCareProviders" var="showOtherCareProvidersUrl" />
                   <a href="${showOtherCareProvidersUrl}" class="link-button-mod button-simple">Visa information för andra vårdgivare</a>
               </c:if>
           </p>
       </c:when>
       <c:when test="${state.showOtherCareUnits}">
            <p>
                <c:choose>
                    <c:when test="${state.confirmConsent}">
                        <jsp:include page="confirmConsentRelation.jsp" />
                    </c:when>
                    <c:when test="${!state.pdlReport.consent.value.hasConsent}">
                        <jsp:include page="establishConsent.jsp" />
                    </c:when>
                    <c:when test="${!state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
                        <portlet:actionURL name="showOtherCareProviders" var="showOtherCareProvidersUrl" />
                        <a href="${showOtherCareProvidersUrl}" class="link-button-mod button-simple">Visa information för andra vårdgivare</a>
                    </c:when>
                </c:choose>
            </p>
       </c:when>
    </c:choose>
</div>
