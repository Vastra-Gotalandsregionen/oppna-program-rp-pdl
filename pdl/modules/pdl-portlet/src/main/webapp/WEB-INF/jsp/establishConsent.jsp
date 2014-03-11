<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://portalen.vgregion.se/pdl" prefix="pdl" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/pdl.css" />

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="pdl clearfix">
    <%@ include file="progress.jsp" %>


    <c:set var="patientInfoFor" value="Patientinformation" scope="request"/>
    <%@ include file="patientInformationFor.jsp" %>

    <portlet:actionURL name="establishConsent" var="establishConsentUrl" >
        <portlet:param name="emergency" value="false" />
        <portlet:param name="confirmed" value="true" />
    </portlet:actionURL>

    <portlet:actionURL name="establishConsent" var="establishConsentEmergencyUrl">
         <portlet:param name="emergency" value="true" />
         <portlet:param name="confirmed" value="true" />
    </portlet:actionURL>

    <div class="clearfix callout callout-info">
        <p class="label">Du måste inhämta patientens samtycke</p>
        <p>Det finns patientinformation hos annan vårdgivare för ${state.patient.patientDisplayName}. För att få information från andra vårdgivare behöver du inhämta patientens samtycke.</p>
        <p>Råder en nödsituation kan detta steget passeras. Ditt val loggförs.<p>
        <p><a href="http://www.vgregion.se/sv/Vastra-Gotalandsregionen/startsida/Vard-och-halsa/Sa-styrs-varden/Halso--och-sjukvardsavdelningen/Patientdatalagen/" target="_blank">Visa information om Patientdatalagen</a><i class="icon link_external"></i></p>
    </div>

   <a href="${searchPatientUrl}" class="link-button-mod">&laquo; Avbryt</a>
   <a href="${establishConsentUrl}" class="link-button-mod">Jag har inhämtat patientens samtycke &raquo;</a>
   <a href="${establishConsentEmergencyUrl}" class="link-button-mod link-button-mod-danger">Nödsituation &raquo;</i></a>
</div>

