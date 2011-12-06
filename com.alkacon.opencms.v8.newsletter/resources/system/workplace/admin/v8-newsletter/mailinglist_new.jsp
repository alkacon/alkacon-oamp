<%@ page import="com.alkacon.opencms.v8.newsletter.admin.*"%><%

	// initialize the workplace class
	CmsEditMailinglistDialog wp = new CmsEditMailinglistDialog(pageContext, request, response);
	// perform the dialog action	
	wp.displayDialog();
%>