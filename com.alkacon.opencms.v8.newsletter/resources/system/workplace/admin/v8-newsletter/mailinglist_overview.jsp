<%@ page import="com.alkacon.opencms.v8.newsletter.admin.*"%><%

	// initialize the widget dialog
	CmsMailinglistOverviewDialog wpWidget = new CmsMailinglistOverviewDialog(pageContext, request, response);
	// perform the widget actions   
	wpWidget.displayDialog(true);
	if (wpWidget.isForwarded()) {
		return;
	}
	// initialize the list dialog
	CmsShowMailinglistSubscribersList wpList = new CmsShowMailinglistSubscribersList(wpWidget.getJsp());
	// perform the list actions 
	wpList.displayDialog(true);
	// write the content of widget dialog
	wpWidget.writeDialog();
	// write the content of list dialog
	wpList.writeDialog();
%>