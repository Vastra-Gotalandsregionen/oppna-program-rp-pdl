<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<div class="clearfix">
    <portlet:actionURL name="establishConsent" var="establishConsentUrl" >
        <portlet:param name="emergency" value="false" />
        <portlet:param name="confirmed" value="true" />
    </portlet:actionURL>

    <portlet:actionURL name="establishConsent" var="establishConsentEmergencyUrl">
         <portlet:param name="emergency" value="true" />
         <portlet:param name="confirmed" value="true" />
    </portlet:actionURL>

    <div class="clearfix confirm">
        <p class="heading">${state.patient.patientDisplayName} har ej medgivit sitt samtycke till sammanhållen journalföring</p>

        <p class="affirm">För att få information från andra vårdgivare behöver du inhämta patientens samtycke. Råder en nödsituation kan detta steget passeras. Ditt val loggförs.<p>

        <div class="clearfix">
            <a href="${establishConsentUrl}" class="link-button-mod button-simple">Jag har inhämtat patientens samtycke</a>
            <a href="${establishConsentEmergencyUrl}" class="link-button-mod button-simple-danger">Nödsituation</i></a>
        </div>
    </div>
</div>