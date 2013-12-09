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
        <portlet:param name="confirmed" value="false" />
    </portlet:actionURL>

    <portlet:actionURL name="establishConsent" var="establishConsentEmergencyUrl">
         <portlet:param name="emergency" value="true" />
         <portlet:param name="confirmed" value="false" />
    </portlet:actionURL>

    <a href="${establishConsentUrl}" class="link-button-mod button-simple">Intyga samtycke</a>
    <a href="${establishConsentEmergencyUrl}" class="link-button-mod button-simple-danger">Nödöppning sammanhållen journalföring</i></a>
</div>
