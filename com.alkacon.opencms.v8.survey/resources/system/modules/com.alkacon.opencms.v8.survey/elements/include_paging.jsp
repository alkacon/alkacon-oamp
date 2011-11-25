<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%-- 
 Can only be include if following parameters are set and initialize:
 	+ "cms" of the type CmsFormReportingBean
 	+ "workBean" of the type CmsFormWorkBean contains the current list
 	+ "curPage" of the type Integer --> only in the detail page, describes the current page
--%>

<div id="webformPaging">

	<%-- Previous page --%>
	<c:choose>
		<c:when test="${curPage <= 1}"><fmt:message key='report.prev_button.headline'/></c:when>
			<c:otherwise>
				<a href="<cms:link>${cms.requestContext.uri}?report=true&page=${curPage - 1}&amp;detail=true</cms:link>" title="<fmt:message key='report.prev_button.headline'/>"><fmt:message key="report.prev_button.headline"/></a>
			</c:otherwise>	
	</c:choose>

	&nbsp;
	<c:forEach var="pageitem" begin="1" end="${fn:length(workBean.list)}">
		<c:if test="${ (pageitem >= (curPage-2)) && (pageitem <= (curPage+2)) }">
			<c:if test="${pageitem!=curPage}"><a href="<cms:link>${cms.requestContext.uri}?report=true&page=${pageitem}&amp;detail=true</cms:link>"><c:out value="${pageitem}"/></a></c:if>
			<c:if test="${pageitem==curPage}"><c:out value="${curPage}"/></c:if>
		</c:if>
	</c:forEach>
	&nbsp;

	<%-- Next page --%>
	<c:choose>
		<c:when test="${curPage >= fn:length(workBean.list)}"><fmt:message key='report.next_button.headline'/></c:when>
		<c:otherwise>
			<a href="<cms:link>${cms.requestContext.uri}?report=true&page=${curPage + 1}&amp;detail=true</cms:link>" title="<fmt:message key='report.next_button.headline'/>"><fmt:message key="report.next_button.headline"/></a>
		</c:otherwise>	
	</c:choose>

</div>

