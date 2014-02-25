<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<h3 class="legend">${patientInfoFor} för
    <c:choose>
        <c:when test="${state.patient.haveInformation}">${state.patient.patientDisplayName} (${state.patient.patientIdFormatted}, ${state.patient.age} år, ${state.patient.sexDisplayName})</c:when>
        <c:otherwise>${state.patient.patientIdFormatted}, ${state.patient.age} år</c:otherwise>
    </c:choose>
</h3>
