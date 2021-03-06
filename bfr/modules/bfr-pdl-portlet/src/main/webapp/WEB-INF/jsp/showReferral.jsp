<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:if test="${state.ticket.success && state.currentReferral.success}">
<div id="showReferral">
    <div id="referralContainer">
    <ul class="clearfix">
        <li><span class="key">Remiss-ID</span><span class="value">${state.currentReferral.value.risId}</span></li>
        <li><span class="key">Prioritet</span><span class="value">${state.currentReferral.value.priority}</span></li>
        <li><span class="key">Antal bilder</span><span class="value">${state.currentReferral.value.imageCount}</span></li>
        <li><span class="key">Utförande enhet</span><span class="value">${state.currentReferral.value.fillerLocation}</span></li>
        <li><span class="key">Remitterande enhet</span><span class="value">${state.currentReferral.value.placerLocation}</span></li>
        <li><span class="key">Remitterande läkare</span><span class="value">${state.currentReferral.value.referringPhysicianName}</span></li>
        <li><span class="key">Frågeställning</span><span class="value">${state.currentReferral.value.question}</span></li>
        <li><span class="key">Anamnes</span><span class="value">${state.currentReferral.value.anamnesis}</span></li>
    </ul>
    </div>
    <div id="studiesContainer">
        <c:forEach var="study" items="${state.currentReferral.value.studies}" varStatus="studiesStatus">
            <ul class="clearfix">
                <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}"><span class="key">Undersökningsdatum</span><span class="value">${study.displayDate}</span></li>
                <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}"><span class="key">Beskrivning</span><span class="value">${study.code} - ${study.description}</span></li>
                <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}">
                    <span class="key">Bilder (${study.noOfImages})</span>
                    <span class="value">
                        <c:forEach var="url" items="${study.studyUrls}" varStatus="urlStatus">
                            <a href="${url}" target="zfp_window">Bildserie ${urlStatus.index+1}</a><i class="icon link_external"></i><c:if test="${!urlStatus.last}">, </c:if>
                        </c:forEach>
                    </span>
                </li>
                <c:forEach var="report" items="${study.studyReports}" varStatus="reportStatus">
                    <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}"><span class="key">Undersökningssvar</span><span class="value">${report.status}</span></li>
                    <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}"><span class="key">Svarsdatum</span><span class="value">${report.displayDate}</span></li>
                    <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}"><span class="key">Svarande läkare</span><span class="value">${report.signer}</span></li>
                    <li class="${studiesStatus.index % 2 == 0 ? 'even' : 'odd'}"><span class="key">Svarstext</span><span class="value">${report.text}</span></li>
                </c:forEach>
            </ul>
        </c:forEach>
    </div>
</div>
</c:if>
