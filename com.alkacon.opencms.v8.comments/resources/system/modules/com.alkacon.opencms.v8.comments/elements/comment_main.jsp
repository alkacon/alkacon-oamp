<%@ page import="com.alkacon.opencms.v8.comments.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>

<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />
<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.v8.comments.frontend">
<cms:jquery dynamic='true' />
<script type='text/javascript' >
load_script('<cms:link>%(link.weak:/system/modules/org.opencms.jquery/resources/packed/jquery.js:49f92dba-8a92-11e0-ad6c-bff56146ac12)</cms:link>', 'js');
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/resources/js/pagination.js:831d7f3d-d959-11e0-bdea-c9a60a7588dd)</cms:link>', 'js');
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/resources/css/jquery.colorbox.css:7812b5c8-e519-11e0-9c23-c9a60a7588dd)</cms:link>', 'css');
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.commons/resources/js/jquery.colorbox.js:5dcf61aa-e519-11e0-9c23-c9a60a7588dd)</cms:link>', 'js');
<c:choose>
<c:when test="${!empty alkaconCmt.config.styleSheet}" >
load_script('<cms:link>${alkaconCmt.config.styleSheet}</cms:link>', 'css');
</c:when>
<c:otherwise>
load_script('<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/resources/comments.css:56708391-15df-11e1-aeb4-9b778fa0dc42)</cms:link>', 'css');
</c:otherwise>
</c:choose>
</script>
<div id="commentbox" class="commentbox">
	<div class="cmtLoading" ></div>
</div>
<c:set var="url" value="%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_header.jsp:56328ff3-15df-11e1-aeb4-9b778fa0dc42)" />
<c:if test="${alkaconCmt.maximized}" >
	<c:set var="url" value="%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_list.jsp:5639bbed-15df-11e1-aeb4-9b778fa0dc42)" />
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
