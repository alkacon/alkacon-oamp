<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
	if (!alkaconCmt.isUserCanView()) {
		return;
	}
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="com.alkacon.opencms.comments.frontend" />
<!-- start: header -->
<div class="cmtHeader">
<c:choose>
<c:when test="${alkaconCmt.userCanManage}">
	<fmt:message key="titel.view.comments" />
</c:when>
<c:when test="${alkaconCmt.userCanPost}">
	<fmt:message key="titel.view.comments" />
</c:when>
<c:when test="${alkaconCmt.userCanView}">
	<fmt:message key="titel.view.comments" />
</c:when>
<c:otherwise>
	<fmt:message key="titel.view.comments" />
</c:otherwise>
</c:choose>
</div>
<!-- end: header -->
<!-- start: post form link -->
<p>
<c:choose>
<c:when test="${alkaconCmt.userCanPost}">
	    <a 
	       title="<fmt:message key="form.message.post" />" 
	       href="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_form.jsp:dfbece22-1112-11dd-ba60-111d34530985)?cmturi=${param.cmturi}&__locale=${cms:vfs(pageContext).requestContext.locale}&width=800&height=520</cms:link>" 
	       class="thickbox" >
			<fmt:message key="post.0" />
		</a>
	</a>
</c:when>
<c:otherwise>
	<c:if test="${alkaconCmt.guestUser}">
	        <a 
	           title="<fmt:message key="login.message.title" />" 
	           href="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_login.jsp:87972a79-12be-11dd-a2ad-111d34530985)?cmturi=${param.cmturi}&__locale=${cms:vfs(pageContext).requestContext.locale}&width=400&height=200</cms:link>" 
	           class="thickbox" >
			<fmt:message key="post.user.login.0" />
		</a>
	</c:if>
</c:otherwise>
</c:choose>
<p>
<!-- end: post form link -->
<!-- start: comments list -->
<div id="comments" style="width: 100%;">
	<cms:include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_innerlist.jsp:7c811b84-1dcd-11dd-b28b-111d34530985)">
		<cms:param name="cmturi" value="${param.cmturi}" />
		<cms:param name="cmtpage" value="0" />
	</cms:include>
</div>
<script>
  tb_init('a.thickbox'); //pass where to apply thickbox
  imgLoader = new Image(); // preload image
  imgLoader.src = '../resources/load.gif';

function reloadComments(state, page) {

	$('body').css("cursor", "wait");
	var data = { cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />', cmtstate: '${alkaconCmt.state}', cmtpage: 0 }; 
	if (state !== "undefined" && page !== "undefined") {
		data = { cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />', cmtstate: state, cmtpage: page };
	} else if (state !== "undefined") {
		data = { cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />', cmtstate: state, cmtpage: 0 };
	}
	$.post(
		'<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_innerlist.jsp:7c811b84-1dcd-11dd-b28b-111d34530985)</cms:link>',
		data,
		function(html){ $("#comments").html(html); }
	);
	$('body').css("cursor", "auto");
}
</script>
<!-- end: comments list -->
