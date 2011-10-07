<%@ page import="com.alkacon.opencms.comments.*" %><%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"));
    alkaconCmt.doAction();
%>
