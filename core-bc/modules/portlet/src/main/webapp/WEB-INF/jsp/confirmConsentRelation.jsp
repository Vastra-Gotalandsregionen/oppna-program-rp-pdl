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
                <div class="confirm">
                    <a href="${establishRelationUrl}">Intyga patientrelation. Ditt val loggförs</a>
                    <a href="${establishRelationUrl}">
                        <i class="icon arrow_right_warn"></i>
                        <span class="continue">Gå vidare</span>
                    </a>
                    <a href="${cancelConfirmationUrl}">
                        <span class="continue cancel">Avbryt</span>
                    </a>
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

