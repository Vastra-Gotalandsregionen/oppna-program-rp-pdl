<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<ul class="infotypes">
    <c:forEach items="${state.csReport.aggregatedSystems.value}" var="infoSelection">
        <li class="active">
            <c:if test="${pdl:expandInfoType(infoSelection.key, state)}">
                <portlet:actionURL name="toggleAllCheckboxes" var="toggleAllCheckboxesUrl">
                    <portlet:param name="id" value="${infoSelection.key.id}" />
                </portlet:actionURL>
                <a href="${toggleAllCheckboxesUrl}"><i class="icon check_all"></i></a>
            </c:if>
            ${infoSelection.key.value.desc}
        </li>
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
<div class="clearfix" style="margin-top: 1.2em;">
    <portlet:renderURL var="startUrl">
        <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
    </portlet:renderURL>
    <a href="${startUrl}" class="link-button-mod">&laquo; Ny sökning</a>
    <portlet:actionURL name="goToSummary" var="goToSummaryUrl" />
    <c:if test="${state.csReport.selectedInfoResource}">
        <a href="${goToSummaryUrl}" class="link-button-mod link-button-mod-proceed">Gå vidare &raquo;</a>
    </c:if>
</div>

