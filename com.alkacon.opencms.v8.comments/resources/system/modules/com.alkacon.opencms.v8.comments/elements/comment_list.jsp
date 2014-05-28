<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><cms:secureparams />
<%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
pageContext.setAttribute("alkaconCmt", alkaconCmt);
CmsCommentStringTemplateHandler templateHandler = new CmsCommentStringTemplateHandler(alkaconCmt);
// output the post options HTML
out.println(templateHandler.buildPostOptionsHtml());
%>
<c:if test="${alkaconCmt.userCanView}">
	<!-- start: comments list -->
	<div id="comments">
		<%-- include of comment_innerlist.jsp happens via JavaScript --%>
	</div>
	<!-- end: comments list -->
</c:if>
