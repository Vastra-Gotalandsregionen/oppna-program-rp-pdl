<%@page contentType="text/html" pageEncoding="UTF-8" %>

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

<div>
    <c:if test="${!state.pdlReport.hasRelationship.value}">
     <portlet:actionURL name="establishRelationship" var="relationshipURL" />
        Du saknar patientrelation med ${state.pwe.patientDisplayName}. <a href="${relationshipURL}">Skapa relation med patient.</a>
    </c:if>
    <c:if test="${state.pdlReport.hasRelationship.fallback}">
        Ett problem uppstod vid verifiering av patientrelation. Patientrelation undantas för denna sökningen.<br/>
    </c:if>
    <c:if test="${state.pdlReport.hasBlocks.fallback}">
        Ett problem uppstod vid verifiering av spärrar. Spärrar ignoreras för denna sökningen.<br/>
    </c:if>
    <c:if test="${state.pdlReport.hasConsent.fallback}">
        Ett problem uppstod vid verifiering av samtycke. Samtycke undantas för denna sökningen.<br/>
    </c:if>

    <c:if test="${state.pdlReport.hasRelationship.value}">
        <c:choose>
             <c:when test="${state.csReport.hasSameUnit}">
                Välj i vilka system som du vill finna information.
                <ul>
                    <c:forEach var="sys" items="${state.csReport.sameUnit}">
                        <li>${sys.value.displayName}</li>
                    </c:forEach>
                </ul>
             </c:when>
             <c:otherwise>
                Inga system med patientdata kunde hittas inom din vårdenhet.
             </c:otherwise>
        </c:choose>

        <c:if test="${state.csReport.hasSameCareProvider && !state.showSameCareProvider}">
            <portlet:actionURL name="sameCareProvider" var="sameCareProviderURL" />
            Det finns fler ytterligare system som innehåller patientinformation på en annan vårdenhet. ${state.pwe.patientDisplayName}. <a href="${sameCareProviderURL}">Relevant information finns i andra system hos inom vårdgivare.</a>
        </c:if>

        <c:if test="${state.showSameCareProvider}">
            System från andra vårdenheter.
            <ul>
                <c:forEach var="sys" items="${state.csReport.sameCareProvider}">
                    <li>${sys.value.displayName}</li>
                </c:forEach>
            </ul>
        </c:if>
    </c:if>
</div>
<liferay-util:html-bottom>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/pdl-async.js"></script>
</liferay-util:html-bottom>
