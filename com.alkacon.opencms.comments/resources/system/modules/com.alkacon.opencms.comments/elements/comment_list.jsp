<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%
	CmsCommentsAccess cmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("cmt", cmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="com.alkacon.opencms.comments.frontend" />
<!-- start: header -->
<p align="center">
<c:choose>
<c:when test="${cmt.userCanManage}">
	<fmt:message key="titel.view.comments" />
</c:when>
<c:when test="${cmt.userCanPost}">
	<fmt:message key="titel.view.comments" />
</c:when>
<c:when test="${cmt.userCanView}">
	<fmt:message key="titel.view.comments" />
</c:when>
<c:otherwise>
What??
</c:otherwise>
</c:choose>
</p>
<!-- end: header -->
<!-- start: post form link -->
<p>
<c:choose>
<c:when test="${cmt.userCanPost}">
        <a href="#" onclick="postDialog()" >
		<fmt:message key="post.0" />
	</a>
	<div id='post_dlg' style='display: none' />
	<script type="text/javascript">
	var postInit = false;
	function postDialog() {
        $("div#post_dlg").html("<center><img src='<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/load.gif:d81aaa99-1207-11dd-8a3f-111d34530985)</cms:link>' /></center>");
		$.post(
			"<cms:link>/system/modules/com.alkacon.opencms.comments/elements/comment_form.jsp</cms:link>",
			{ cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
			function(html) { $("div#post_dlg").html(html); }
		);
		if (!postInit) {
			$('div#post_dlg').dialog({
				buttons: {
					'<fmt:message key="form.button.submit" />': function() { 
					     $.post("<cms:link>/system/modules/com.alkacon.opencms.comments/elements/comment_form.jsp</cms:link>?" + $("form#fid").serialize(), 
						      function(txt) {
								  if (txt == 'ok') {
								     $('div#post_dlg').dialog('close'); 
								     reloadComments();
								  } else {
								     $("div#post_dlg").html(txt);
								  }
						      }
						);
					},
					'<fmt:message key="form.button.cancel" />': function() { 
					     $(this).dialog('close'); 
					}
				},
				autoOpen: false, modal: true, resizable: false, 
				width: 680, height: 300,
				overlay: { backgroundColor:'#000', opacity: '0.6' },
				title: '<fmt:message key="form.message.post" />'
			});
			$('div#post_dlg').show();
			postInit = true;
		}
		$('div#post_dlg').dialog('open');	
		return false;
	}
	</script>
</c:when>
<c:otherwise>
	<c:if test="${cmt.guestUser}">
	        <a href="#" onclick="loginDialog(this)" >
			<fmt:message key="post.user.login.0" />
		</a>
		<cms:include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/login.jsp:87972a79-12be-11dd-a2ad-111d34530985)">
			<cms:param name="requestedResource" value="${param.cmturi}?cmtshow=true" />
		</cms:include>
	</c:if>
	<c:if test="${!cmt.guestUser}">
		again!
	</c:if>
</c:otherwise>
</c:choose>
<p>
<!-- end: post form link -->
<!-- start: comments list -->
<div id="comments" style="width: 100%;">
	<cms:include file="%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_innerlist.jsp:7c811b84-1dcd-11dd-b28b-111d34530985)">
		<cms:param name="cmturi" value="${param.cmturi}" />
	</cms:include>
</div>
<script>
function reloadComments() {

	$('body').css("cursor", "wait");
	$.post(
		'<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_innerlist.jsp:7c811b84-1dcd-11dd-b28b-111d34530985)</cms:link>',
		{ cmturi: '${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
		function(html){ $("#comments").html(html); }
	);
	$('body').css("cursor", "auto");
	return false;
}
</script>
<!-- end: comments list -->
