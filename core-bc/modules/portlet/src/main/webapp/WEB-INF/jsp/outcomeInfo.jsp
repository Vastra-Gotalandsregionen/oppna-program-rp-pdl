<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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

<c:if test="${state.pdlReport.hasNonSuccessOutcome}">
    <div class="callout callout-danger">
    	  
        <p class="label">En eller flera frågor mot de bakomliggade säkerhetstjänsterna misslyckades.</p>
        <!--
        <p>Följande av säkerhetstjänsterna sattes ur spel och har passerats.</p>
        <ul>
            <c:if test="${state.pdlReport.systems.outcome != 'SUCCESS'}">
                <li>Tjänst för spärrar
                    <c:choose>
                        <c:when test="${state.pdlReport.systems.outcome == 'CLIENT_FAILURE'}">misslyckades på grund av ett klientfel.</c:when>
                        <c:when test="${state.pdlReport.systems.outcome == 'COMMUNICATION_FAILURE'}">misslyckades på grund av ett kommunikationsfel.</c:when>
                        <c:when test="${state.pdlReport.systems.outcome == 'REMOTE_FAILURE'}">misslyckades på grund av ett fel i säkerhetstjänsterna.</c:when>
                    </c:choose></li>
            </c:if>
            <c:if test="${state.pdlReport.hasRelationship.outcome != 'SUCCESS'}">
                <li>Tjänst för patientrelation
                        <c:choose>
                            <c:when test="${state.pdlReport.hasRelationship.outcome == 'CLIENT_FAILURE'}">misslyckades på grund av ett klientfel.</c:when>
                            <c:when test="${state.pdlReport.hasRelationship.outcome == 'COMMUNICATION_FAILURE'}">misslyckades på grund av ett kommunikationsfel.</c:when>
                            <c:when test="${state.pdlReport.hasRelationship.outcome == 'REMOTE_FAILURE'}">misslyckades på grund av ett fel i säkerhetstjänsterna.</c:when>
                        </c:choose></li>
                </c:if>
            <c:if test="${state.pdlReport.consent.outcome != 'SUCCESS'}">
                    <li>Tjänst för samtycke till sammanhållen journalföring
                    <c:choose>
                        <c:when test="${state.pdlReport.consent.outcome == 'CLIENT_FAILURE'}">misslyckades på grund av ett klientfel.</c:when>
                        <c:when test="${state.pdlReport.consent.outcome == 'COMMUNICATION_FAILURE'}">misslyckades på grund av ett kommunikationsfel.</c:when>
                        <c:when test="${state.pdlReport.consent.outcome == 'REMOTE_FAILURE'}">misslyckades på grund av ett fel i säkerhetstjänsterna.</c:when>
                </c:choose></li>
            </c:if>
        </ul>
        -->
        <p>Var noggrann med dina val eftersom du kan komma att se mer information än vanligt. Alla dina val loggförs.</p>
    </div>
</c:if>
