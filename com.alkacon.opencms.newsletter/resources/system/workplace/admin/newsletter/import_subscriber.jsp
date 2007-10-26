<%@ page import="com.alkacon.opencms.newsletter.admin.*"%><%

    	CmsDummyList dl = new CmsDummyList(pageContext, request, response);

	CmsSubscriberImportDialog wp = new CmsSubscriberImportDialog(pageContext, request, response);	
	wp.displayDialog();
%>