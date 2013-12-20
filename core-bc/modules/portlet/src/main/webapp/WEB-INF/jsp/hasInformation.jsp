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

<c:choose>
    <c:when test="${!state.pdlReport.hasRelationship.value && !state.confirmRelation}">
        <jsp:include page="establishRelation.jsp" />
        <jsp:include page="newSearch.jsp" />
    </c:when>
    <c:when test="${(state.confirmRelation && !state.pdlReport.hasRelationship.value)}">
        <jsp:include page="confirmConsentRelation.jsp" />
    </c:when>
    <c:when test="${state.pdlReport.hasRelationship.value}">
        <h3 class="legend">Patientinformation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</h3>
        <c:if test="${state.csReport.containsSameCareUnit || state.currentVisibility != 'SAME_CARE_UNIT'}">
            <ul class="infotypes">
                <c:forEach items="${state.csReport.aggregatedSystems.value}" var="infoSelection">
                    <c:if test="${state.shouldBeVisible[infoSelection.key.lowestVisibility]}">
                        <c:choose>
                            <c:when test="${infoSelection.key.selected}">
                                <li class="active">${infoSelection.key.value.desc}</li>
                                <li class="sublist">
                                    <ul>
                                        <c:forEach var="system" items="${infoSelection.value}">
                                            <portlet:actionURL name="toggleInformation" var="toggleInformationUrl">
                                                <portlet:param name="id" value="${system.id}" />
                                                <portlet:param name="confirmed" value="false" />
                                                <portlet:param name="revokeEmergency" value="false" />
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
                                                    <c:if test="${system.blocked && system.needConfirmation}">
                                                        <portlet:actionURL name="toggleInformation" var="toggleInformationEmergencyUrl">
                                                            <portlet:param name="id" value="${system.id}" />
                                                            <portlet:param name="confirmed" value="true" />
                                                            <portlet:param name="revokeEmergency" value="true" />
                                                        </portlet:actionURL>
                                                        <portlet:actionURL name="cancelRevokeConfirmation" var="cancelRevokeConfirmationUrl">
                                                            <portlet:param name="id" value="${system.id}" />
                                                        </portlet:actionURL>
                                                        <portlet:actionURL name="toggleInformation" var="toggleInformationConsentUrl">
                                                            <portlet:param name="id" value="${system.id}" />
                                                            <portlet:param name="confirmed" value="true" />
                                                            <portlet:param name="revokeEmergency" value="false" />
                                                        </portlet:actionURL>
                                                        <div class="unlock">
                                                            <b>Passera spärr</b> för ${system.value.careProviderDisplayName} - ${system.value.careUnitDisplayName}
                                                            <div class="clearfix">
                                                                <a href="${toggleInformationConsentUrl}" class="link-button-mod link-button-mod-warn">Passera spärr med medgivande</a>
                                                                <a href="${cancelRevokeConfirmationUrl}" class="link-button-mod">Avbryt</a>
                                                                <a href="${toggleInformationEmergencyUrl}" class="link-button-mod link-button-mod-danger" style="float:right">Nödöppna information</a>
                                                            </div>
                                                        </div>
                                                    </c:if>
                                                </li>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${infoSelection.key.containsBlocked[state.currentVisibility] && !infoSelection.key.viewBlocked }">
                                            <li>
                                                <div class="clearfix">
                                                    <div class="callout callout-danger callout-action">
                                                        <portlet:actionURL name="showBlockedInformation" var="showBlockedInformationUrl">
                                                            <portlet:param name="id" value="${infoSelection.key.id}" />
                                                        </portlet:actionURL>
                                                        <a href="${showBlockedInformationUrl}">Visa v&aring;rdenheter med sp&auml;rrad information</a>
                                                    <div>
                                                </div>
                                            </li>
                                        </c:if>
                                    </ul>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <portlet:actionURL name="selectInfoResource" var="selectInfoResourceUrl">
                                    <portlet:param name="id" value="${infoSelection.key.id}" />
                                </portlet:actionURL>
                                <li class="active">
                                    ${infoSelection.key.value.desc}
                                </li>
                                <li class="sublist">
                                    <ul>
                                        <li>
                                            <div class="clearfix">
                                                <div class="callout callout-info callout-action">
                                                    <a href="${selectInfoResourceUrl}">
                                                        Visa vårdenheter
                                                    </a>
                                                </div>
                                            </div>
                                        </li>
                                    </ul>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:forEach>
            </ul>
        </c:if>
        <jsp:include page="pdlScopeControls.jsp"/>
        <div class="clearfix" style="margin-top: 1.2em;">
            <portlet:renderURL var="startUrl">
                <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
            </portlet:renderURL>
            <a href="${startUrl}" class="link-button-mod">&laquo; Ny sökning</a>
            <portlet:actionURL name="goToSummary" var="goToSummaryUrl" />
            <a href="${goToSummaryUrl}" class="link-button-mod link-button-mod-proceed">Gå vidare &raquo;</a>
        </div>
    </c:when>
</c:choose>
