<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:if test="${pdl:displayCareUnit(infoSelection.key, system, state)}">
    <li>
        <portlet:actionURL name="toggleInformation" var="toggleInformationUrl">
            <portlet:param name="id" value="${system.id}" />
            <portlet:param name="confirmed" value="false" />
            <portlet:param name="revokeEmergency" value="false" />
        </portlet:actionURL>
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
        <c:if test="${pdl:displayUnblockConfirmation(system)}">
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
                    <a href="${cancelRevokeConfirmationUrl}" class="link-button-mod button-simple">&laquo; Avbryt</a>
                    <a href="${toggleInformationConsentUrl}" class="link-button-mod button-simple">Passera spärr med medgivande &raquo;</a>
                    <a href="${toggleInformationEmergencyUrl}" class="link-button-mod button-simple-danger">Nödöppna information &raquo;</a>
                </div>
            </div>
        </c:if>
    </li>
</c:if>

