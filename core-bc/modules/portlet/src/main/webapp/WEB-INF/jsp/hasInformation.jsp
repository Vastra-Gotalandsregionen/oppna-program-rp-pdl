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
            <c:if test="${(infoSelection.key.lowestVisibility == 'SAME_CARE_UNIT') || (state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent && infoSelection.key.lowestVisibility == 'OTHER_CARE_PROVIDER') || (state.showOtherCareUnits && infoSelection.key.lowestVisibility == 'OTHER_CARE_UNIT') }">
                <c:choose>
                    <c:when test="${infoSelection.key.selected}">
                        <li class="active">${infoSelection.key.value.desc}</li>
                        <li class="sublist">
                            <ul>
                                <c:forEach var="system" items="${infoSelection.value}">
                                    <portlet:actionURL name="toggleInformation" var="toggleInformationUrl">
                                        <portlet:param name="id" value="${system.id}" />
                                    </portlet:actionURL>
                                    <c:if test="${(system.visibility == 'SAME_CARE_UNIT') || (state.showOtherCareProviders && state.pdlReport.consent.value.hasConsent && system.visibility == 'OTHER_CARE_PROVIDER') || (state.showOtherCareUnits && system.visibility == 'OTHER_CARE_UNIT') }">
                                        <c:if test="${(system.blocked && infoSelection.key.viewBlocked) || !system.blocked}">
                                            <li>
                                                <c:choose>
                                                    <c:when test="${system.selected}">
                                                        <i class="icon checked"></i><a href="${toggleInformationUrl}">${system.value.careProviderDisplayName} - ${system.value.careUnitDisplayName}</a>
                                                        <c:if test="${system.initiallyBlocked && !system.blocked}">
                                                            <i class="icon unlocked"></i>
                                                        </c:if>
                                                    </c:when>
                                                    <c:when test="${!system.selected}">
                                                        <i class="icon unchecked"></i><a href="${toggleInformationUrl}">${system.value.careProviderDisplayName} - ${system.value.careUnitDisplayName}</a>
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
                                    </c:if>
                                </c:forEach>
                                <c:if test="${infoSelection.key.containsBlocked && !infoSelection.key.viewBlocked}">
                                    <li>
                                        <portlet:actionURL name="showBlockedInformation" var="showBlockedInformationUrl">
                                            <portlet:param name="id" value="${infoSelection.key.id}" />
                                        </portlet:actionURL>
                                        <a href="${showBlockedInformationUrl}">Visa ytterligare spärrad information</a>
                                    </li>
                                </c:if>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <portlet:actionURL name="selectInfoResource" var="selectInfoResourceUrl">
                            <portlet:param name="id" value="${infoSelection.key.id}" />
                        </portlet:actionURL>
                        <li><a href="${selectInfoResourceUrl}">${infoSelection.key.value.desc}</a><i class="icon arrow_right"></i></li>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:forEach>
        </ul>
        <c:if test="${state.csReport.containsBlockedInfoTypes}">
            Contains blocked info types.
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
