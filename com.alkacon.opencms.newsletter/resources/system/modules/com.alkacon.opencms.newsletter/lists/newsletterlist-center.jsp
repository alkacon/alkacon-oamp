<%@page session="false" taglibs="c,cms" %><% 
String allcount = "1000";
pageContext.setAttribute("allcount", allcount);
%>

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

<cms:include file="%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/elements/list-paging-base.jsp)">
	<cms:param name="singlepagepath">%(link.weak:/system/modules/com.alkacon.opencms.newsletter/lists/newsletterlist-center-singlepage.jsp)</cms:param>
	<cms:param name="itemfolder">${folder}</cms:param>
	<cms:param name="itemcount">${count}</cms:param>
	<cms:param name="collectorname">allNewslettersInFolder</cms:param>
	<cms:param name="collectorparam">${folder}|alkacon-newsletter|${allcount}|${sentonly}|false</cms:param>
</cms:include>