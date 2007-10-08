<%@ page import="com.alkacon.opencms.newsletter.admin.*"%><%

	// initialize the workplace class
	CmsEditMailinglistDialog wp = new CmsEditMailinglistDialog(pageContext, request, response);
	// perform the dialog action	
	wp.displayDialog();
%>