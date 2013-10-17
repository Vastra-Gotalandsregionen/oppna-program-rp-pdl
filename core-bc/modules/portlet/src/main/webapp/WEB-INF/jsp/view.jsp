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
            <spring:bind path="ssn">
              <c:set var="elementWrapCssClass" scope="page" value="element-wrap" />
              <c:if test="${status.error}">
                  <c:set var="elementWrapCssClass" scope="page" value="element-wrap element-has-errors" />
              </c:if>
              <div class="${elementWrapCssClass}">
                  <aui:field-wrapper cssClass="element-field-wrap">
                      <label for="<portlet:namespace />ssn">
                          <span>Person-/samordningsnummer</span>
                          <span class="element-mandatory">*<span> Obligatorisk</span></span>
                      </label>
                      <aui:input name="ssn" cssClass="element-field" type="text" label="" />
                  </aui:field-wrapper>
                  <span class="element-field-help">
                      Skriv in personnummer eller samordningsnummer för den patient du vill ha information kring. Format som stöds ååmmdd-nnnn, ååmmdd+nnnn.
                  </span>
              </div>
            </spring:bind>

            <div class="${elementWrapCssClass}">
                <aui:field-wrapper cssClass="element-field-wrap">
                    <label for="<portlet:namespace />ssn">
                        <span>Uppdrag</span>
                    </label>
                    ${assignment}
                </aui:field-wrapper>
                <span class="element-field-help">
                    Ditt nuvarande uppdrag.
                </span>
            </div>
        </aui:fieldset>

        <aui:button-row>
            <aui:button type="submit" value="Sök patientinformation" cssClass="rp-button" />
        </aui:button-row>
    </aui:form>
</div>
