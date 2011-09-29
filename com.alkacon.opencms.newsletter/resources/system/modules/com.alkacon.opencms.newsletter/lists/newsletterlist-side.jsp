<%@ page taglibs="c,cms,fmt" %>

<fmt:setLocale value="${cms.locale}" />

<c:choose>
	<c:when test="${not empty param.folder}">
		<c:set var="folder" value="${param.folder}" />
	</c:when>
	<c:otherwise>
		<c:set var="folder" value="${cms.subSitePath}.content/newsletter/" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.count}">
		<c:set var="count" value="${param.count}" />
	</c:when>
	<c:otherwise>
		<c:set var="count" value="5" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${not empty param.sentonly}">
		<c:set var="sentonly" value="true" />
	</c:when>
	<c:otherwise>
		<c:set var="sentonly" value="false" />
	</c:otherwise>
</c:choose>

<c:set var="imgpos"><cms:elementsetting name="imgalign" default="none" /></c:set>
<c:set var="contentpath">${cms.element.sitePath}</c:set>

<cms:contentload collector="allNewslettersInFolder" param="%(pageContext.folder)|alkacon-newsletter|%(pageContext.count)|%(pageContext.sentonly)|false" preload="true" >		
	<cms:contentinfo var="info" />			
	<c:if test="${info.resultSize > 0}">
		<cms:contentload editable="true">
			<cms:contentaccess var="content" />
			
			<div class="boxbody_listentry">
				<h5><a href="<cms:link baseUri="${param.pageUri}">${content.filename}?uri=${content.filename}</cms:link>">${content.value.Title}</a></h5>
		
				<p>
					<a href="<cms:link baseUri="${param.pageUri}">${content.filename}?uri=${content.filename}</cms:link>"><b>${content.value.Subject}</b></a><br/>
					${cms:trimToSize(cms:stripHtml(content.value.Text), 50)}<br/>
					<small><a href="<cms:link baseUri="${param.pageUri}">${content.filename}?uri=${content.filename}</cms:link>">Mehr...</a></small>
				</p>
			</div>
		</cms:contentload>
	</c:if>
</cms:contentload>