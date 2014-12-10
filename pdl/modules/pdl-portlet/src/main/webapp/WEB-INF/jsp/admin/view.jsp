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

<portlet:actionURL name="searchPatient" var="searchPatientUrl" />
<div class="pdl clearfix">

    <%@ include file="../progress.jsp" %>
    <%@ include file="../search/hsaOutcomeInfo.jsp" %>
    <c:if test="${state.ctx.success}">

        <%@ include file="../search/pdlInfoCallout.jsp" %>
        <aui:form id="pdlSearchForm" action="${searchPatientUrl}" name="searchPatientForm" cssClass="pdl-form" method="post">
            <aui:fieldset>
                <c:set var="elementWrapCssClass" scope="page" value="element-wrap" />
                <c:if test="${status.error}">
                    <c:set var="elementWrapCssClass" scope="page" value="element-wrap element-has-errors" />
                </c:if>
                <div class="${elementWrapCssClass}">
                    <aui:field-wrapper cssClass="element-field-wrap">
                        <label for="<portlet:namespace />patientId">
                            <span>Patient-ID</span>
                        </label>
                        <aui:input name="patientId" autocomplete="off" cssClass="element-field" type="text" label="" autoFocus="true"/>
                    </aui:field-wrapper>
                    <span class="element-field-help">
                        Patient-ID. Personnummer och Samordningsnummer anges på formatet YYYYMMDD-NNNN
                    </span>
                </div>
                <div class="${elementWrapCssClass}">
                    <aui:field-wrapper cssClass="element-field-wrap">
                        <label for="<portlet:namespace />patientIdType">
                            <span>Typ av Patient ID</span>
                        </label>
                        <aui:select name="patientIdType" cssClass="element-field" label="">
                            <c:forEach items="${infobrokerPersonIdTypeList}" var="pidtype">
                                <aui:option value="${pidtype}" label="${pidtype.description}" selected="${'PAT_PERS_NR' == pidtype.infoBrokerTypeCode}"/>
                            </c:forEach>
                        </aui:select>
                    </aui:field-wrapper>
                    <span class="element-field-help">
                        Typ av Patient-ID.
                    </span>
                </div>
                <div class="${elementWrapCssClass}">
                    <aui:field-wrapper cssClass="element-field-wrap">
                        <label for="<portlet:namespace />currentAssignment">
                            <span>Aktuellt uppdrag</span>
                        </label>
                        <aui:select name="currentAssignment" cssClass="element-field" label="">
                            <c:forEach items="${state.ctx.value.assignments}" var="as">
                                <aui:option value="${as.key}" label="${as.value.assignmentDisplayName}" selected="${state.ctx.value.currentAssignment.assignmentHsaId == as.key}"/>
                            </c:forEach>
                        </aui:select>
                    </aui:field-wrapper>
                    <span class="element-field-help">
                        Uppdraget avgör vilken information du får se.
                    </span>
                </div>
                <aui:input type="hidden" name="reset" value="true" />
            </aui:fieldset>
            <aui:button-row>
                <aui:button type="submit" value="S&ouml;k &raquo;" cssClass="rp-button rp-button-proceed" />
            </aui:button-row>
        </aui:form>
    </c:if>
</div>
