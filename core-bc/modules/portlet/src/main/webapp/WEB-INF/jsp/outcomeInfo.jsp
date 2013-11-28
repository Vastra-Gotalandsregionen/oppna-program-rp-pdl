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

<c:if test="${state.pdlReport.hasNonSuccessOutcome}">
    <div class="portlet-msg-error">
        <p>En eller flera frågor mot de bakomliggade säkerhetstjänsterna misslyckades. Var noggran när du väljer ut vilken information du tar del av eftersom du kan få tillgång till mer information än normalt.</p>
        <p>De tjänster som påverkades är följande system:</p>
        <ul>
            <c:if test="${state.pdlReport.systems.outcome != Outcome.SUCCESS}">
                <li>Tjänst för spärrar
                    <c:choose>
                        <c:when test="state.pdlReport.systems.outcome == Outcome.CLIENT_FAILURE">misslyckades på grund av ett klientfel.</c:when>
                        <c:when test="state.pdlReport.systems.outcome == Outcome.COMMUNICATION_FAILURE">misslyckades på grund av ett kommunikationsfel.</c:when>
                        <c:when test="state.pdlReport.systems.outcome == Outcome.REMOTE_FAILURE">misslyckades på grund av ett fel i tjänsten.</c:when>
                    </c:choose></li>
            </c:if>
            <c:if test="${state.pdlReport.hasRelationship.outcome != Outcome.SUCCESS}">
                <li>Tjänst för patientrelation
                        <c:choose>
                            <c:when test="state.pdlReport.hasRelationship.outcome == Outcome.CLIENT_FAILURE">misslyckades på grund av ett klientfel.</c:when>
                            <c:when test="state.pdlReport.hasRelationship.outcome == Outcome.COMMUNICATION_FAILURE">misslyckades på grund av ett kommunikationsfel.</c:when>
                            <c:when test="state.pdlReport.hasRelationship.outcome == Outcome.REMOTE_FAILURE">misslyckades på grund av ett fel i tjänsten.</c:when>
                        </c:choose></li>
                </c:if>
            <c:if test="${state.pdlReport.consent.outcome != Outcome.SUCCESS}">
            <li>Tjänst för samtycke till sammanhållen journalföring
                    <c:choose>
                        <c:when test="state.pdlReport.hasRelationship.outcome == Outcome.CLIENT_FAILURE">misslyckades på grund av ett klientfel.</c:when>
                        <c:when test="state.pdlReport.hasRelationship.outcome == Outcome.COMMUNICATION_FAILURE">misslyckades på grund av ett kommunikationsfel.</c:when>
                        <c:when test="state.pdlReport.hasRelationship.outcome == Outcome.REMOTE_FAILURE">misslyckades på grund av ett fel i tjänsten.</c:when>
                </c:choose></li>
            </c:if>
        </ul>
    </div>
</c:if>
