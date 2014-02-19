<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:if test="${state.missingResults}">
    <div class="callout callout-info">
        <p class="label">Ett eller flera vårdsystem svarade med ofullständig information.</p>
        <p>Ytterligare patientinformation kan finnas.</p>
    </div>
</c:if>
<c:if test="${state.sourcesNonSuccessOutcome}">
    <div class="callout callout-danger">
        <p class="label">En eller flera sökningar i vårdsystem misslyckades.</p>
        <p>Ytterligare patientinformation kan finnas.</p>
    </div>
</c:if>
<c:if test="${state.pdlReport.hasNonSuccessOutcome}">
    <div class="callout callout-danger">
        <p class="label">En eller flera frågor mot de bakomliggade säkerhetstjänsterna misslyckades.</p>
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
        <p>Var noggrann med dina val eftersom du kan komma att se mer information än vanligt. Alla dina val loggförs.</p>
    </div>
</c:if>
