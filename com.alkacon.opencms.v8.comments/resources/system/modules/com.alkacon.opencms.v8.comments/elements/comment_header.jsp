<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %><cms:secureparams /><%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
	CmsCommentStringTemplateHandler templateHandler = new CmsCommentStringTemplateHandler(alkaconCmt);
	out.println(templateHandler.buildHeaderHtml());
%>