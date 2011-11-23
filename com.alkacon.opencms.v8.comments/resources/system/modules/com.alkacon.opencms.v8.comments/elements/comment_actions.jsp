<%@ page import="com.alkacon.opencms.v8.comments.*" %><%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"));
    alkaconCmt.doAction();
%>
