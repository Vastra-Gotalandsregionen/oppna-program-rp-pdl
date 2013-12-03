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
    <portlet:actionURL name="establishRelation" var="establishRelationUrl" />

    <portlet:actionURL name="establishRelationConsent" var="relationConsentUrl">
        <portlet:param name="emergency" value="false" />
    </portlet:actionURL>

    <portlet:actionURL name="establishRelationConsent" var="relationConsentEmergencyUrl">
        <portlet:param name="emergency" value="true" />
    </portlet:actionURL>

    <a href="${establishRelationUrl}" class="link-button-mod">Intyga patientrelation</a>
    <a href="${relationConsentUrl}" class="link-button-mod">Intyga patientrelation och samtycke</a>
    <a href="${relationConsentEmergencyUrl}" class="link-button-mod link-button-mod-danger">Nödöppning av sammanhållen journalföring</a>
</div>

