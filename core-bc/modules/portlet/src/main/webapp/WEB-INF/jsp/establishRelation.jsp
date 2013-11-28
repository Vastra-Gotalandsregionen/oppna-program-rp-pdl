<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<h2>Du saknar patientrelation med ${state.patient.patientDisplayName} (${state.patient.patientId})</h2>
<br/>
<h3 class="legend">Att intyga patientrelation</h3>
<p>
    För att få se patientinformation måste det finnas en registrerad patientrelation. Du kan själv intyga och registrera en patientrelation nedan.
</p>
<jsp:include page="pdlInfoCallout.jsp" />
<div>
    <portlet:actionURL name="establishRelation" var="establishRelationUrl" />

    <a href="${establishRelationUrl}" class="link-button-mod">Intyga patientrelation</a>
</div>
