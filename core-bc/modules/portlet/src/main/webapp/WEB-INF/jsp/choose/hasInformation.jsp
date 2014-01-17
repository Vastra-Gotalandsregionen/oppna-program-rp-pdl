<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
            <ul class="infotypes">
                <c:forEach items="${state.csReport.aggregatedSystems.value}" var="infoSelection">
                    <li class="active">${infoSelection.key.value.desc}</li>
                    <portlet:actionURL name="selectInfoResource" var="selectInfoResourceUrl">
                        <portlet:param name="id" value="${infoSelection.key.id}" />
                    </portlet:actionURL>
                    <c:choose>
                        <c:when test="${pdl:expandInfoType(infoSelection.key, state)}">
                            <li class="sublist">
                                <ul>
                                    <c:forEach var="system" items="${infoSelection.value}">
                                        <%@ include file="careUnitEntry.jsp" %>
                                    </c:forEach>
                                    <%@ include file="blockedEntries.jsp" %>
                                    <c:if test="${!infoSelection.key.selected}">
                                        <%@ include file="otherCareUnitProviders.jsp" %>
                                    </c:if>
                                </ul>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="sublist">
                                <ul>
                                    <%@ include file="otherCareUnitProviders.jsp" %>
                                </ul>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </ul>
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
