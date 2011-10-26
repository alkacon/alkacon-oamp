<%@ page import="com.alkacon.opencms.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>

<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />
<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.comments.frontend">
<cms:jquery dynamic='true' />
<script type='text/javascript' >
load_script('<cms:link>%(link.weak:/system/modules/org.opencms.jquery/resources/packed/jquery.js)</cms:link>', 'js');
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/resources/js/pagination.js)</cms:link>', 'js');
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/resources/css/jquery.colorbox.css)</cms:link>', 'css');
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/resources/js/jquery.colorbox.js)</cms:link>', 'js');
<c:choose>
<c:when test="${!empty alkaconCmt.config.styleSheet}" >
load_script('<cms:link>${alkaconCmt.config.styleSheet}</cms:link>', 'css');
</c:when>
<c:otherwise>
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.comments/resources/comments.css:96522ded-1204-11dd-8a3f-111d34530985)</cms:link>', 'css');
</c:otherwise>
</c:choose>
</script>
<div id="commentbox" class="commentbox">
	<div class="cmtLoading" ></div>
</div>
<c:set var="url" value="%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_header.jsp:fe055c44-120a-11dd-8a3f-111d34530985)" />
<c:if test="${alkaconCmt.maximized}" >
	<c:set var="url" value="%(link.weak:/system/modules/com.alkacon.opencms.comments/elements/comment_list.jsp:f11cf62d-ec2e-11dc-990f-dfec94299cf1)" />
</c:if>
<script type='text/javascript'>
     /**
      * Insert translated strings as inline javascript code          
      **/
      var colorboxConfig_comments = {
              close: '<fmt:message key="comment.image.close" />',
              next: '<fmt:message key="comment.image.next" />',
              previous: '<fmt:message key="comment.image.prev" />',
              current: '<fmt:message key="comment.image.count" />',
              innerWidth: '700px',
              maxWidth: '98%',
   	      maxHeight: '98%'
            };
</script>
<script type="text/javascript" >
	$.ajax({
	    type: 'POST', 
	    url: '<cms:link>${url}</cms:link>', 
	    data: { cmturi:'${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
	    success: function(html){ $("#commentbox").html(html); },
	    error: function(){ $("#commentbox").html(""); }
	});
</script>
</fmt:bundle>
