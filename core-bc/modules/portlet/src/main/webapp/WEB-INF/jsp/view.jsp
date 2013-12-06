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

<jsp:include page="common.jsp" />

<portlet:actionURL name="searchPatient" var="searchPatientUrl" />
<div class="pdl clearfix">

    <jsp:include page="progress.jsp" />

    <aui:form action="${searchPatientUrl}" name="searchPatientForm" cssClass="pdl-form" method="post">
        <aui:fieldset label="S&ouml;k patientinformation">
                <c:set var="elementWrapCssClass" scope="page" value="element-wrap" />
                <c:if test="${status.error}">
                    <c:set var="elementWrapCssClass" scope="page" value="element-wrap element-has-errors" />
                </c:if>
            <div class="${elementWrapCssClass}">
                <aui:field-wrapper cssClass="element-field-wrap">
                    <label for="<portlet:namespace />title">
                        <span>Patient-ID</span>
                    </label>
                    <aui:input name="patientId" cssClass="element-field" type="text" label="" />
                </aui:field-wrapper>
                <span class="element-field-help">
                    Patient-ID ska anges på formatet ÅÅÅÅMMDDXXXX.
                </span>
            </div>
       </aui:fieldset>
        <aui:button-row>
            <aui:button type="submit" value="S&ouml;k" cssClass="rp-button" />
        </aui:button-row>
    </aui:form>
</div>
