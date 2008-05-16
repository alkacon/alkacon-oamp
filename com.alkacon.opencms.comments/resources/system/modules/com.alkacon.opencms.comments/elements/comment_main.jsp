<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%
	CmsCommentsAccess cmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("cmt", cmt);
%>
<cms:jquery dynamic='true' />
<cms:jquery js='jquery' dynamic='true' />
<cms:jquery js='jquery.pagination' css='pagination' dynamic='true' />
<cms:jquery js='jquery.ui' css='flora/flora.all' dynamic='true' />
<script type='text/javascript' >
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/comments.css:96522ded-1204-11dd-8a3f-111d34530985)</cms:link>', 'css');
$('body').addClass('flora');
</script>
<div id="commentbox" class="commentbox">
	<center>
		<img src="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/load.gif:d81aaa99-1207-11dd-8a3f-111d34530985)</cms:link>" />
	</center>
</div>
<c:set var="url" value="%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_header.jsp:fe055c44-120a-11dd-8a3f-111d34530985)" />
<c:if test="${(!empty param.cmtshow) || !cmt.config.minimized}" >
	<c:set var="url" value="%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_list.jsp:f11cf62d-ec2e-11dc-990f-dfec94299cf1)" />
</c:if>
<script type="text/javascript" >
	$.post(
		"<cms:link>${url}</cms:link>",
		{ cmturi:'${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
		function(html) { $("#commentbox").html(html); }
	);
</script>