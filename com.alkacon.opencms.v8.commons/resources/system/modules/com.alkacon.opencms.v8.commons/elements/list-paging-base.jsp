<%@page session="false" taglibs="c,cms,fmt" %>

<c:set var="singlepagepath" value="${param.singlepagepath}" />
<c:set var="folder" value="${param.itemfolder}" />
<c:set var="count" value="${param.itemcount}" />
<c:set var="collectorname" value="${param.collectorname}" />
<c:set var="collectorparam" value="${param.collectorparam}" />

<c:choose>
	<c:when test="${empty singlepagepath || empty folder || empty count || empty collectorname || empty collectorparam}">
		<div style="border: 2px solid red; padding: 10px;">
			One of the required parameters to build the list is missing
			<ul>
				<li>singlepagepath: ${singlepagepath}</li>
				<li>folder: ${folder}</li>
				<li>count: ${count}</li>
				<li>collectorname: ${collectorname}</li>
				<li>collectorparam: ${collectorparam}</li>
			</ul>
		</div>
	</c:when>
	<c:otherwise>

		<c:set var="imgpos"><cms:elementsetting name="imgalign" default="none" /></c:set>
		
		<fmt:setLocale value="${cms.locale}" />
		<fmt:bundle basename="com.alkacon.opencms.v8.commons.workplace">
		<script type="text/javascript">
			var currentPage = 1;
			var lastPage = 1;
			var itemsPerPage = 0;
			var pageIndex = 1;
			var itemCount = 0;
			var itemLocale = "${cms.locale}";
			var listCenterPath = "<cms:link>${singlepagepath}</cms:link>";
			var collectorName = "${collectorname}";
			var collectorParam = "${collectorparam}";
			var pageUri = "${cms.requestContext.uri}";
			var imgPos = "${imgpos}";
			var imgWidth = 0;
			var fmtPaginationPrev = "<fmt:message key="v8.list.pagination.previous" />";
			var fmtPaginationNext = "<fmt:message key="v8.list.pagination.next" />";
		</script>
		
		<%-- Set the image width --%>
		<c:set var="imgwidth">0</c:set>
		<c:if test="${imgpos == 'imageleft' || imgpos == 'imageright'}">
			<c:set var="imgwidth">${(cms.container.width - 20) / 3}</c:set>
		</c:if>
		
		<cms:contentload collector="%(pageContext.collectorname)" param="%(pageContext.collectorparam)" preload="true" >
		
			<cms:contentinfo var="info" />			
			<c:if test="${info.resultSize > 0}">
				<cms:contentload editable="false" pageSize="%(pageContext.count)" pageIndex="%(param.pageIndex)" pageNavLength="5">
					<cms:contentinfo var="innerInfo" scope="request" />
				</cms:contentload>
		
					<c:if test="${!cms.edited && innerInfo.resultSize > innerInfo.pageSize}">
						<script type="text/javascript">
							$(document).ready(function() {
								itemsPerPage = ${count};
								itemCount = ${innerInfo.resultSize};
								imgPos = "${imgpos}";
								imgWidth = ${imgwidth};
								initPagination();
							});
						</script>
					</c:if>
		
			</c:if>
		</cms:contentload>
		
		<c:if test="${info.resultSize > 0}">			
			<div id="list_center_pages">
				<c:choose>
				<c:when test="${!cms.requestContext.currentProject.onlineProject && (innerInfo.resultSize > innerInfo.pageSize)}">
					<c:set var="pages" value="${innerInfo.resultSize / count}" />
					<c:if test="${(innerInfo.resultSize % count) > 0}">
						<c:set var="pages" value="${pages + 1}" />
					</c:if>
					<c:forEach begin="1" end="${pages}" varStatus="status">
					<div id="list_center_page_${status.count}"<c:if test="${status.count > 1}"> style="display: none;"</c:if>>
						<%-- Show the links in the given path --%>
						<cms:include file="${singlepagepath}">
							<cms:param name="pageUri" value="${cms.requestContext.uri}" />
							<cms:param name="__locale" value="${cms.locale}" />
							<cms:param name="imgPos" value="${imgpos}" />
							<cms:param name="imgWidth" value="${imgwidth}" />
							<cms:param name="itemsPerPage" value="${count}" />
							<cms:param name="collectorName" value="${collectorname}" />
							<cms:param name="collectorParam" value="${collectorparam}" />
							<cms:param name="pageIndex" value="${status.count}" />
						</cms:include>
					</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div id="list_center_page_1">
						<%-- Show the links in the given path --%>
						<cms:include file="${singlepagepath}">
							<cms:param name="pageUri" value="${cms.requestContext.uri}" />
							<cms:param name="__locale" value="${cms.locale}" />
							<cms:param name="imgPos" value="${imgpos}" />
							<cms:param name="imgWidth" value="${imgwidth}" />
							<cms:param name="itemsPerPage" value="${count}" />
							<cms:param name="collectorName" value="${collectorname}" />
							<cms:param name="collectorParam" value="${collectorparam}" />
							<cms:param name="pageIndex" value="${param.pageIndex}" />
						</cms:include>
					</div>
				</c:otherwise>
				</c:choose>
			</div>
		
			<c:if test="${innerInfo.resultSize > innerInfo.pageSize}">
				<c:choose>
					<c:when test="${cms.edited}" >
						<div class="boxbody_listentry"><fmt:message key="v8.list.pagination.reload" /></div>
					</c:when>
					<c:otherwise>
						<div class="boxbody_listentry"><div id="pagination" class="pagination"></div></div>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:if>
		
		</fmt:bundle>

	</c:otherwise>
</c:choose>