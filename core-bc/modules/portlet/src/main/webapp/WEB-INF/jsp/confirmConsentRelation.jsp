<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<portlet:actionURL name="cancelConfirmation" var="cancelConfirmationUrl" />

<div class="clearfix">
    <c:choose>
        <c:when test="${state.confirmRelation && !state.confirmConsent}">
            <div class="clearfix">
                <portlet:actionURL name="establishRelation" var="establishRelationUrl">
                    <portlet:param name="confirmed" value="true" />
                </portlet:actionURL>
                <portlet:renderURL var="startUrl">
                    <portlet:param name="jspPage" value="/WEB-INF/jsp/view.jsp" />
                </portlet:renderURL>
                <div class="clearfix confirm">
                    <p class="heading">Intyga aktiv patientrelation för ${state.patient.patientDisplayName} (${state.patient.patientIdFormatted})</p>
                    <p>Det finns patientinformation för ${state.patient.patientDisplayName}. För att få tillgång till informationen måste du intyga att du har en aktiv patientrelation.
                    Här finner du <a href="http://www.vgregion.se/sv/Vastra-Gotalandsregionen/startsida/Vard-och-halsa/Sa-styrs-varden/Halso--och-sjukvardsavdelningen/Patientdatalagen/">information om patientdatalagen</a></p>
                    <p class="affirm">Jag är delaktig i vården av denna patient och behöver ta del av patientens vårdinformation.</p>
                    <a href="${establishRelationUrl}" class="link-button-mod link-button-mod-warn">Jag intygar patientrelation</a>
                    <a href="${startUrl}" class="link-button-mod">Sök igen</a>
                </div>
            <div>
        </c:when>
        <c:when test="${state.confirmRelation && state.confirmConsent}">
           <c:choose>
               <c:when test="${state.confirmEmergency}">
                    <portlet:actionURL name="establishRelationConsent" var="relationConsentUrl">
                        <portlet:param name="emergency" value="true" />
                        <portlet:param name="confirmed" value="true" />
                    </portlet:actionURL>
                    <div class="confirm">
                        <a href="${relationConsentUrl}">Nödöppna sammanhållen journalföring. Ditt val loggförs.</a>
                        <a href="${relationConsentUrl}">
                            <i class="icon arrow_right_warn"></i>
                            <span class="continue">Gå vidare</span>
                        </a>
                        <a href="${cancelConfirmationUrl}">
                            <span class="continue cancel">Avbryt</span>
                        </a>
                    </div>
               </c:when>
               <c:otherwise>
                    <portlet:actionURL name="establishRelationConsent" var="relationConsentUrl">
                        <portlet:param name="emergency" value="false" />
                        <portlet:param name="confirmed" value="true" />
                    </portlet:actionURL>
                    <div class="confirm">
                        <a href="${relationConsentUrl}">Intyga patientrelation och patientens samtycke till sammanhållen journalföring. Ditt val loggförs.</a>
                        <a href="${relationConsentUrl}">
                            <i class="icon arrow_right_warn"></i>
                            <span class="continue">Gå vidare</span>
                        </a>
                        <a href="${cancelConfirmationUrl}">
                            <span class="continue cancel">Avbryt</span>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${!state.confirmRelation && state.confirmConsent}">
            <div class="clearfix">
                <c:choose>
                    <c:when test="${state.confirmEmergency}">
                        <portlet:actionURL name="establishConsent" var="establishConsentUrl">
                             <portlet:param name="emergency" value="true" />
                             <portlet:param name="confirmed" value="true" />
                        </portlet:actionURL>
                        <div class="confirm">
                            <a href="${establishConsentUrl}">Nödöppna sammanhållen journalföring. Ditt val loggförs.</a>
                            <a href="${establishConsentUrl}">
                                <i class="icon arrow_right_warn"></i>
                                <span class="continue">Gå vidare</span>
                            </a>
                            <a href="${cancelConfirmationUrl}">
                                <span class="continue cancel">Avbryt</span>
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <portlet:actionURL name="establishConsent" var="establishConsentUrl" >
                            <portlet:param name="emergency" value="false" />
                            <portlet:param name="confirmed" value="true" />
                        </portlet:actionURL>
                        <div class="confirm">
                            <a href="${establishConsentUrl}">Intyga patientens samtycke till sammanhållen journalföring. Ditt val loggförs.</a>
                            <a href="${establishConsentUrl}">
                                <i class="icon arrow_right_warn"></i>
                                <span class="continue">Gå vidare</span>
                            </a>
                            <a href="${cancelConfirmationUrl}">
                                <span class="continue cancel">Avbryt</span>
                            </a>
                        </div>
                     </c:otherwise>
                 </c:choose>
            <div>
        </c:when>
  </c:choose>
</div>

