<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
