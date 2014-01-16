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
                                    <li>
                                        <ul>
                                            <jsp:include page="careUnitEntry.jsp" />
                                        </ul>
                                    </li>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <portlet:actionURL name="selectInfoResource" var="selectInfoResourceUrl">
                                    <portlet:param name="id" value="${infoSelection.key.id}" />
                                </portlet:actionURL>
                                <li class="active">
                                    ${infoSelection.key.value.desc}
                                </li>
                                <jsp:include page="careUnitEntry.jsp" />

                                <li class="sublist">
                                    <ul>
                                        <jsp:include page="careUnitEntry.jsp" />
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
