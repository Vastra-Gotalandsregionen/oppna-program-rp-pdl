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

<h2>Patientinformation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h2>
<c:choose>
    <c:when test="${state.pdlReport.missingBothRelationConsent}">
        <c:choose>
            <c:when test="${state.ctx.otherProviders}">
                <jsp:include page="establishRelationConsent.jsp" />
            </c:when>
            <c:otherwise>
                <jsp:include page="establishRelation.jsp" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${!state.pdlReport.hasRelationship.value}">
        <jsp:include page="establishRelation.jsp" />
    </c:when>
    <c:when test="${state.pdlReport.hasRelationship.value}">
    <ul class="infotypes">
        <c:forEach items="${state.csReport.aggregatedSystems.value}" var="infoSelection">
            <c:if test="${state.shouldBeVisible[infoSelection.key.lowestVisibility] && (infoSelection.key.containsOnlyBlocked[state.currentVisibility] && infoSelection.key.viewBlocked || !infoSelection.key.containsOnlyBlocked[state.currentVisibility]) }">
                <c:choose>
                    <c:when test="${infoSelection.key.selected}">
                        <li class="active">${infoSelection.key.value.desc}</li>
                        <li class="sublist">
                            <ul>
                                <c:forEach var="system" items="${infoSelection.value}">
                                    <portlet:actionURL name="toggleInformation" var="toggleInformationUrl">
                                        <portlet:param name="id" value="${system.id}" />
                                        <portlet:param name="blocked" value="${system.blocked}" />
                                    </portlet:actionURL>
                                    <c:if test="${state.shouldBeVisible[system.visibility] && ((system.blocked && infoSelection.key.viewBlocked) || !system.blocked)}">
                                        <li>
                                            <c:choose>
                                                <c:when test="${system.selected}">
                                                    <a href="${toggleInformationUrl}">
                                                        <i class="icon checked"></i>${system.value.careProviderDisplayName} - ${system.value.careUnitDisplayName}
                                                    </a>
                                                    <c:if test="${system.initiallyBlocked && !system.blocked}">
                                                        <i class="icon unlocked"></i>
                                                    </c:if>
                                                </c:when>
                                                <c:when test="${!system.selected}">
                                                    <a href="${toggleInformationUrl}">
                                                        <i class="icon unchecked"></i>${system.value.careProviderDisplayName} - ${system.value.careUnitDisplayName}
                                                    </a>
                                                    <c:if test="${system.blocked}">
                                                        <i class="icon locked"></i>
                                                    </c:if>
                                                    <c:if test="${system.initiallyBlocked && !system.blocked}">
                                                        <i class="icon unlocked"></i>
                                                    </c:if>
                                                </c:when>
                                            </c:choose>
                                        </li>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${infoSelection.key.containsBlocked && !infoSelection.key.viewBlocked}">
                                    <li>
                                        <div class="blocked blockedCareUnit">
                                            <portlet:actionURL name="showBlockedInformation" var="showBlockedInformationUrl">
                                                <portlet:param name="id" value="${infoSelection.key.id}" />
                                            </portlet:actionURL>
                                            <a href="${showBlockedInformationUrl}">Ytterligare vårdenheter med spärrad information finns</a>
                                            <a href="${showBlockedInformationUrl}">
                                                <i class="icon arrow_right_red"></i>
                                                <span class="showInformationType">Visa spärrade vårdenheter</span>
                                            </a>
                                        <div>
                                    </li>
                                </c:if>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <portlet:actionURL name="selectInfoResource" var="selectInfoResourceUrl">
                            <portlet:param name="id" value="${infoSelection.key.id}" />
                        </portlet:actionURL>
                        <li>
                            <a href="${selectInfoResourceUrl}">${infoSelection.key.value.desc}</a>
                            <a href="${selectInfoResourceUrl}">
                            <i class="icon arrow_right_blue"></i>
                            <span class="showCareUnits">Visa vårdenheter</span>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:forEach>
        </ul>
        <c:if test="${state.csReport.containsBlockedInfoTypes[state.currentVisibility]}">
            <portlet:actionURL name="showBlockedInformationTypes" var="showBlockedInformationTypesUrl">
                <portlet:param name="visibility" value="${state.currentVisibility}" />
            </portlet:actionURL>
            <div class="blocked blockedInfoTypes">
                <a href="${showBlockedInformationTypesUrl}">Ytterligare informationstyper med spärrad information finns</a>
                <a href="${showBlockedInformationTypesUrl}">
                    <i class="icon arrow_right_red"></i>
                    <span class="showInformationType">Visa spärrade informationstyper</span>
                </a>
            </div>
        </c:if>
        <div class="clearfix">
        <c:choose>
           <c:when test="${!state.showOtherCareUnits}">
                <p>
                    <portlet:actionURL name="showOtherCareUnits" var="showOtherCareUnitsUrl" />
                    <a href="${showOtherCareUnitsUrl}" class="link-button-mod">Visa information för andra vårdenheter</a>
                </p>
           </c:when>
           <c:when test="${state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
               <p>
                   <c:if test="${!state.pdlReport.consent.value.hasConsent}">
                       <jsp:include page="establishConsent.jsp" />
                   </c:if>
                   <c:if test="${!state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
                       <portlet:actionURL name="showOtherCareProviders" var="showOtherCareProvidersUrl" />
                       <a href="${showOtherCareProvidersUrl}" class="link-button-mod">Visa information för andra vårdgivare</a>
                   </c:if>
               </p>
           </c:when>
           <c:when test="${state.showOtherCareUnits}">
                <p>
                    <c:if test="${!state.pdlReport.consent.value.hasConsent}">
                        <jsp:include page="establishConsent.jsp" />
                    </c:if>
                    <c:if test="${!state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent}">
                        <portlet:actionURL name="showOtherCareProviders" var="showOtherCareProvidersUrl" />
                        <a href="${showOtherCareProvidersUrl}" class="link-button-mod">Visa information för andra vårdgivare</a>
                    </c:if>
                </p>
           </c:when>
        </c:choose>
        </div>
        <jsp:include page="pdlInfoCallout.jsp" />
    </c:when>
</c:choose>
