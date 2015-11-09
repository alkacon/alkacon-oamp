<%@ page session="false" import="com.alkacon.opencms.v8.photoalbum.CmsPhotoAlbumBean" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><%
	CmsPhotoAlbumBean cms = new CmsPhotoAlbumBean(pageContext, request, response);
	pageContext.setAttribute("cms", cms);
%>

<c:set var="start" value="${((param.page-1) * param.itemsPerPage) + 1}" />
<c:set var="end" value="${param.page * param.itemsPerPage}" />
<c:set var="templatevariant" value="${param.templateVariant}" />

<c:forEach items="${cms.readImages[param.vfsFolder]}" var="photo" varStatus="status"><%--

	--%><c:set var="imagePath" value="${fn:substringAfter(photo.rootPath, cms:vfs(pageContext).requestContext.siteRoot)}" /><%--
	--%><c:if test="${empty imagePath}" ><%--
	        --%><c:set var="imagePath" value="${photo.rootPath}" /><%--
	--%></c:if><%--
	--%><c:set var="imageTitle" value="${cms:vfs(pageContext).property[imagePath]['Title']}" /><%--
	--%><c:if test="${empty imageTitle && param.showResourceNameAsTitle == 'true'}"><%--
		--%><c:set var="imageTitle" value="${photo.name}" /><%--
	--%></c:if><%--

	--%><c:if test="${cms.isDownscaleRequired[photo]}"><%--
		--%><c:set var="imageParam" value="?__scale=t:3,q:${param.quality},w:${fn:substringBefore(param.maxImageSize, 'x')},h:${fn:substringAfter(param.maxImageSize, 'x')}" /><%--
	--%></c:if><%--

	--%><c:choose><%--
		--%><c:when test="${start le status.count && (end ge status.count || param.itemsPerPage == -1)}"><%--
			--%><div style="background-color: ${param.background}; width: ${fn:substringBefore(param.size, 'x')}px; " class="album_box">
				<div>
					<a href="<cms:link>${imagePath}${imageParam}</cms:link>" title="${imageTitle}" class="imagelink_${param.albumid}" rel="page${param.page}" >
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
			</div><%--
		--%></c:when><%--
		--%><c:otherwise><%--
			--%><a href="<cms:link>${imagePath}${imageParam}</cms:link>" title="${imageTitle}" class="imagelink_${param.albumid}" rel="page${param.page}" style="display: none;" ></a><%--
		--%></c:otherwise><%--
	--%></c:choose><%--
--%></c:forEach>