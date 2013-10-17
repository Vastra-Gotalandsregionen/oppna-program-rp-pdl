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
     <c:if test="${!state.report.hasRelationship.value}">
         <portlet:actionURL name="establishRelationship" var="relationshipURL" />
        Du saknar patientrelation med ${state.pwe.patientDisplayName}. <a href="${relationshipURL}">Skapa relation med patient.</a>
     </c:if>
    <c:if test="${state.report.hasRelationship.fallback}">
        Ett problem uppstod vid verifiering av patientrelation. Patientrelation ignoreras.<br/>
    </c:if>
    <c:if test="${state.report.hasRelationship.value}">
        Välj i vilka system som du vill finna information.
    </c:if>
</div>
<liferay-util:html-bottom>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/pdl-async.js"></script>
</liferay-util:html-bottom>
