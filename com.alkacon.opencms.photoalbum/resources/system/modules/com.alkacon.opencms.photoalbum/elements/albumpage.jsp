<%@ page session="false" import="com.alkacon.opencms.photoalbum.CmsPhotoAlbumBean" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%
	CmsPhotoAlbumBean cms = new CmsPhotoAlbumBean(pageContext, request, response);
	pageContext.setAttribute("cms", cms);
%>

<c:set var="count" value="0" />
<c:set var="start" value="${((param.page-1) * param.itemsPerPage) + 1}" />
<c:set var="end" value="${param.page * param.itemsPerPage}" />

<c:forEach items="${cms.readImages[param.vfsFolder]}" var="photo">
	<c:set var="count" value="${count+1}" />

	<c:set var="imagePath" value="${fn:substringAfter(photo.rootPath, cms:vfs(pageContext).requestContext.siteRoot)}" />

	<c:set var="imageTitle" value="${cms:vfs(pageContext).property[imagePath]['Title']}" />
	<c:if test="${empty imageTitle && param.showResourceNameAsTitle == 'true'}">
		<c:set var="imageTitle" value="${photo.name}" />
	</c:if>

	<c:choose>
		<c:when test="${start le count && (end ge count || param.itemsPerPage == -1)}">
			<div style="background-color: ${param.background}; width: ${fn:substringBefore(param.size, 'x')}px; " class="album_box">
				<div>
					<a href="<cms:link>${imagePath}</cms:link>" title="${imageTitle}" class="thickbox" rel="page${param.page}" >
						<cms:img src="${imagePath}" alt="${imageTitle}" width="${fn:substringBefore(param.size, 'x')}" height="${fn:substringAfter(param.size, 'x')}" scaleColor="${param.background}" scaleQuality="${param.quality}" scaleFilter="${fn:replace(param.filter, '.', ':')}" >
							<cms:param name="class">gallery_thumb</cms:param>
						</cms:img>
					</a>
				</div>
				
				<%-- Title of the image --%>
				<div style="text-align: ${param.alignTitle}">
					<c:if test="${param.showTitle == 'true'}">
						<c:if test="${empty imageTitle}">
							<c:set var="imageTitle" value="&nbsp;" />
						</c:if>
						<c:out value="${imageTitle}" escapeXml="false" />
					</c:if>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<a href="<cms:link>${imagePath}</cms:link>" title="${imageTitle}" class="thickbox" rel="page${param.page}" style="display: none;" ></a>
		</c:otherwise>
	</c:choose>
</c:forEach>