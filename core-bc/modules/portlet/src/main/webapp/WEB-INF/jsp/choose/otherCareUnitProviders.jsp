<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:if test="${pdl:displayShowMore(infoSelection.key, state)}">
    <li>
        <div class="clearfix">
            <div class="callout callout-info callout-action">
                <a href="${selectInfoResourceUrl}">
                    <c:choose>
                        <c:when test="${pdl:displayOtherUnits(infoSelection.key, state)}">
                            Visa andra vårdenheter
                        </c:when>
                        <c:when test="${pdl:displayOtherProviders(infoSelection.key, state)}">
                            Visa vårdenheter för andra vårdgivare
                        </c:when>
                    </c:choose>
                </a>
            </div>
        </div>
    </li>
</c:if>