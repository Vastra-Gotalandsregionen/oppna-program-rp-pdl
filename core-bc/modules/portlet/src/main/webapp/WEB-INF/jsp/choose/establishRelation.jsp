<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="clearfix">
    <portlet:actionURL name="establishRelation" var="establishRelationUrl">
        <portlet:param name="confirmed" value="false" />
    </portlet:actionURL>

    <a href="${establishRelationUrl}" class="link-button-mod button-simple">Intyga patientrelation</a>
</div>
