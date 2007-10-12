<%@ page import="com.alkacon.opencms.newsletter.admin.*" %>
<%
	// initialize the list dialog
	CmsNewsletterEditorWrapper wp = new CmsNewsletterEditorWrapper(pageContext, request, response);
	// write the content of list dialog
	wp.displayDialog();
%>