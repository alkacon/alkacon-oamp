<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="${alkaconCmt.resourceBundle}" />
<div class="cmtHeader">
<c:choose>
<c:when test="${alkaconCmt.userCanManage}">
	<c:choose>
	<c:when test="${alkaconCmt.config.moderated}">
		<a href="javascript:loadComments();" >
			<fmt:message key="header.user.manage.2" >
				<fmt:param value="${alkaconCmt.countApprovedComments}" />
				<fmt:param value="${alkaconCmt.countNewComments}" />
			</fmt:message>
		</a>
	</c:when>
	<c:otherwise>
		<a href="javascript:loadComments();" >
			<fmt:message key="header.user.manage.1" >
				<fmt:param value="${alkaconCmt.countComments}" />
			</fmt:message>
		</a>
	</c:otherwise>
	</c:choose>
</c:when>
<c:when test="${alkaconCmt.userCanPost}">
	<a href="javascript:loadComments();" >
		<fmt:message key="header.user.post.1" >
			<fmt:param value="${alkaconCmt.countComments}" />
		</fmt:message>
	</a>
</c:when>
<c:when test="${alkaconCmt.userCanView}">
	<a href="javascript:loadComments();" >
		<fmt:message key="header.user.read.1" >
			<fmt:param value="${alkaconCmt.countComments}" />
		</fmt:message>
	</a>
</c:when>
<c:otherwise>
	<c:if test="${alkaconCmt.guestUser && alkaconCmt.config.offerLogin}">
	        <a 
	           title="<fmt:message key="login.message.title" />" 
	           href="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_login.jsp:87972a79-12be-11dd-a2ad-111d34530985)?cmturi=${param.cmturi}&__locale=${cms:vfs(pageContext).requestContext.locale}&width=400&height=200</cms:link>" 
	           class="cmt_thickbox" >
			<fmt:message key="header.user.login.1" >
				<fmt:param value="${alkaconCmt.countComments}" />
			</fmt:message>
		</a>
	</c:if>
</c:otherwise>
</c:choose>
</div>
<script type="text/javascript" >
  tb_init('a.cmt_thickbox'); //pass where to apply thickbox
  imgLoader = new Image(); // preload image
  imgLoader.src = '<cms:link>%(link.weak:/system/workplace/resources/jquery/css/thickbox/loading.gif:d20e3a58-12ae-11dd-86ab-111d34530985)</cms:link>';

  function loadComments() {
    $("#commentbox").html("<div class='cmtLoading'></div>");
	$.post(
		"<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_list.jsp:f11cf62d-ec2e-11dc-990f-dfec94299cf1)</cms:link>",
		{ cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
		function(html) { $("#commentbox").html(html); }
	);
  }
</script>
