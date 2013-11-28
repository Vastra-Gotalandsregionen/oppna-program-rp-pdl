<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<h3 class="legend">Det finns ytterligare information hos andra vårdgivare</h3>
<p>
    Vill du se patientinformation från andra vårdgivare än din egen måste du intyga att patienten har gett sitt Samtycke för sammanhållen journalföring.
</p>
<p>
    Vid nödsituation så kan samtycket förbigås genom att nödöppna sammanhållen journalföring.
<p/>
<jsp:include page="pdlInfoCallout.jsp" />
<div>

    <portlet:actionURL name="establishConsent" var="establishConsentUrl" >
        <portlet:param name="emergency" value="false" />
    </portlet:actionURL>

    <portlet:actionURL name="establishConsent" var="establishConsentEmergencyUrl">
         <portlet:param name="emergency" value="true" />
     </portlet:actionURL>

    <a href="${establishConsentUrl}" class="link-button-mod">Intyga samtycke</a>
    <a href="${establishConsentEmergencyUrl}" class="link-button-mod link-button-mod-danger">Nödöppning sammanhållen jounrnalföring</i></a>
</div>
