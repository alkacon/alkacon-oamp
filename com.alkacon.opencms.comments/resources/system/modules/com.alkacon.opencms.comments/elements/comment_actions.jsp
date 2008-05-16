<%@ page import="com.alkacon.opencms.comments.*" %><%
	CmsCommentsAccess cmt = new CmsCommentsAccess(pageContext, request, response);
    cmt.doAction();
%>
