<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:choose>
    <c:when test="${state.ticket.success}">
        <h3 class="legend">${patientInfoFor} för
            <c:choose>
                <c:when test="${state.ticket.value.patient.haveInformation}">${state.ticket.value.patient.patientDisplayName} (${state.ticket.value.patient.patientIdFormatted}, ${state.ticket.value.patient.age} år, ${state.ticket.value.patient.sexDisplayName})</c:when>
                <c:otherwise>${state.ticket.value.patient.patientIdFormatted}, ${state.ticket.value.patient.age} år</c:otherwise>
            </c:choose>
        </h3>
    </c:when>
    <c:otherwise>
        <h3 class="legend">Ingen patientinformation tillgänglig</h3>
    </c:otherwise>
</c:choose>
