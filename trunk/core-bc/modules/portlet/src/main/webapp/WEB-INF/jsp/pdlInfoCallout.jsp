<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<div class="clearfix callout callout-info">
	<%-- Alt 1 --%>

	<%-- 
	<p class="title">
		Du är inloggad som ${state.ctx.employeeDisplayName} (${state.ctx.careProviderDisplayName} - ${state.ctx.careUnitDisplayName})
	</p>
	<p>
		Alla dina val i systemet loggförs
	</p>
	<p>
		Dessa val kommer att i efterhand <a href="http://www.vgregion.se/sv/Vastra-Gotalandsregionen/startsida/Vard-och-halsa/Sa-styrs-varden/Halso--och-sjukvardsavdelningen/Patientdatalagen/">granskas enligt patientdatalagen.</a>	
	</p>
	
	--%>
	
	<%-- Alt 2 --%>
	
	<p class="label">Alla dina val i systemet loggförs</p>
	<p>Dessa val kommer att i efterhand <a href="http://www.vgregion.se/sv/Vastra-Gotalandsregionen/startsida/Vard-och-halsa/Sa-styrs-varden/Halso--och-sjukvardsavdelningen/Patientdatalagen/">granskas enligt patientdatalagen.</a>	</p>
</div>
