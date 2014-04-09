<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %><%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
    alkaconCmt.doAction();
%>
