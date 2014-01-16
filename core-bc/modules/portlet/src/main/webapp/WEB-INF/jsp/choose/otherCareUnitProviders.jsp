<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:if test="${(state.ctx.value.currentAssignment.otherUnits && state.shouldBeVisible['OTHER_CARE_UNIT']) || (state.ctx.value.currentAssignment.otherProviders && state.shouldBeVisible['OTHER_CARE_PROVIDER'])}">
    <li>
        <div class="clearfix">
            <div class="callout callout-info callout-action">
                <a href="${selectInfoResourceUrl}">
                    <c:choose>
                        <c:when test="${state.ctx.value.currentAssignment.otherUnits }">
                            Visa andra vårdenheter
                        </c:when>
                        <c:when test="${state.ctx.value.currentAssignment.otherProviders}">
                            Visa vårdenheter för andra vårdgivare
                        </c:when>
                    </c:choose>
                </a>
            </div>
        </div>
    </li>
</c:if>
