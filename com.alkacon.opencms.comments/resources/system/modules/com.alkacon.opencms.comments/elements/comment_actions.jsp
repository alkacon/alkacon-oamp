<%@ page import="com.alkacon.opencms.comments.*" %><%
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
    alkaconCmt.doAction();
%>
