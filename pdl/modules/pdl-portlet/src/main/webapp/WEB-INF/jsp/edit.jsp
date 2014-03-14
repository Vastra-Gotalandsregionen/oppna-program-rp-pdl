<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/pdl.css" />

<portlet:defineObjects />
<liferay-theme:defineObjects />

<portlet:actionURL name="save" var="saveURL">
    <portlet:param name="action" value="save"/>
</portlet:actionURL>

<div class="pdl clearfix">
    <aui:form action="${saveURL}" method="post"
        name="savePdlPreferences" cssClass="pdl-preferences-form">

        <aui:fieldset>

          <aui:field-wrapper cssClass="element-field-wrap">
            <label for="<portlet:namespace />establishRelationDuration">
                <span><h4>Nyskapad patientrelation är aktiv i</h4></span>
            </label>
            <aui:input label="" name="establishRelationDuration" type="text"  value="${establishRelationDuration}" />

            <aui:select name="establishRelationTimeUnit" cssClass="element-field" label="">
                <c:forEach items="${roundedTimeUnitList}" var="timeUnit">
                    <aui:option value="${timeUnit}" label="${timeUnit.description}" selected="${establishRelationTimeUnit == timeUnit}"/>
                </c:forEach>
            </aui:select>

          </aui:field-wrapper>

          <aui:field-wrapper cssClass="element-field-wrap">
            <label for="<portlet:namespace />establishConsentDuration">
                <span><h4>Nyskapade Samtycken till Sammanhållen journalföring är aktiva i</h4></span>
            </label>
            <aui:input label="" name="establishConsentDuration" type="text" value="${establishConsentDuration}" />

            <aui:select name="establishConsentTimeUnit" cssClass="element-field" label="">
                <c:forEach items="${roundedTimeUnitList}" var="timeUnit">
                    <aui:option value="${timeUnit}" label="${timeUnit.description}" selected="${establishConsentTimeUnit == timeUnit}"/>
                </c:forEach>
            </aui:select>
          </aui:field-wrapper>

        </aui:fieldset>

        <aui:button-row>
            <aui:button type="submit" value="save" />
        </aui:button-row>
    </aui:form>
</div>
