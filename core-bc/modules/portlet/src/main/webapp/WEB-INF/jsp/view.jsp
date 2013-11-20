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

<portlet:actionURL name="searchPatient" var="searchPatientURL" />

<div>
    <aui:form action="${searchPatientURL}" name="searchPatientForm" cssClass="pdl-patient-form" method="post">
        <aui:fieldset label="Sök patientinformation">
            <spring:bind path="patientId">
              <c:set var="elementWrapCssClass" scope="page" value="element-wrap" />
              <c:if test="${status.error}">
                  <c:set var="elementWrapCssClass" scope="page" value="element-wrap element-has-errors" />
              </c:if>
              <div class="${elementWrapCssClass}">
                  <aui:field-wrapper cssClass="element-field-wrap">
                      <label for="<portlet:namespace />patientId">
                          <span>Patient-ID</span>
                      </label>
                      <aui:input name="patientId" cssClass="element-field" type="text" />
                  </aui:field-wrapper>
              </div>
            </spring:bind>

            <div class="${elementWrapCssClass}">
                <aui:field-wrapper cssClass="element-field-wrap">
                    <label for="<portlet:namespace />assignment">
                        <span>Uppdrag</span>
                    </label>
                    ${state.ctx.assignmentDisplayName}
                </aui:field-wrapper>
            </div>
        </aui:fieldset>

        <aui:button-row>
            <aui:button type="submit" value="Sök patientinformation" cssClass="rp-button" />
        </aui:button-row>
    </aui:form>
</div>
