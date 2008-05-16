<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%
	CmsCommentsAccess cmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("cmt", cmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="com.alkacon.opencms.comments.frontend" />
<p align="center">
<c:choose>
<c:when test="${cmt.userCanManage}">
	<c:choose>
	<c:when test="${cmt.config.moderated}">
		<a href="#" onclick="loadComments()" >
			<fmt:message key="header.user.manage.2" >
				<fmt:param value="${cmt.countApprovedComments}" />
				<fmt:param value="${cmt.countNewComments}" />
			</fmt:message>
		</a>
	</c:when>
	<c:otherwise>
		<a href="#" onclick="loadComments()" >
			<fmt:message key="header.user.manage.1" >
				<fmt:param value="${cmt.countComments}" />
			</fmt:message>
		</a>
	</c:otherwise>
	</c:choose>
</c:when>
<c:when test="${cmt.userCanPost}">
	<a href="#" onclick="loadComments()" >
		<fmt:message key="header.user.post.1" >
			<fmt:param value="${cmt.countComments}" />
		</fmt:message>
	</a>
</c:when>
<c:when test="${cmt.userCanView}">
	<a href="#" onclick="loadComments()" >
		<fmt:message key="header.user.read.1" >
			<fmt:param value="${cmt.countComments}" />
		</fmt:message>
	</a>
</c:when>
<c:otherwise>
	<c:if test="${cmt.guestUser}">
	        <a href="#" onclick="loginDialog()" >
			<fmt:message key="header.user.login.1" >
				<fmt:param value="${cmt.countComments}" />
			</fmt:message>
		</a>
		<cms:include file="%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/login.jsp:87972a79-12be-11dd-a2ad-111d34530985)">
			<cms:param name="requestedResource" value="${param.cmturi}?cmtshow=true" />
		</cms:include>
	</c:if>
</c:otherwise>
</c:choose>
</p>
<script type="text/javascript" >
  function loadComments() {
    $("#commentbox").html("<center><img src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/load.gif:d81aaa99-1207-11dd-8a3f-111d34530985)</cms:link>' /></center>");
	$.post(
		"<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_list.jsp:f11cf62d-ec2e-11dc-990f-dfec94299cf1)</cms:link>",
		{ cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
		function(html) { $("#commentbox").html(html); }
	);
  }
</script>
