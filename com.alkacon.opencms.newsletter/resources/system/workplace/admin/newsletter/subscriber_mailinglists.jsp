<%@ page import="com.alkacon.opencms.newsletter.admin.*"%><%

	// initialize info dialog
	CmsSubscriberOverviewDialog wpInfo = new CmsSubscriberOverviewDialog(pageContext, request, response);
	// perform the widget actions 
	wpInfo.displayDialog(true);
	if (wpInfo.isForwarded()) {
		return;
	}
	// initialize list dialogs
	CmsSubscriberMailinglistsList wpSubscriberMailinglists = new CmsSubscriberMailinglistsList(pageContext, request, response);
	CmsNotSubscriberMailinglistsList wpNotSubscriberMailinglists = new CmsNotSubscriberMailinglistsList(pageContext, request, response);
	org.opencms.workplace.list.CmsTwoListsDialogsWOStart wpTwoLists = new org.opencms.workplace.list.CmsTwoListsDialogsWOStart(wpSubscriberMailinglists, wpNotSubscriberMailinglists);
	// perform the active list actions
	wpTwoLists.displayDialog(true);

	// write the content of widget dialog
	wpInfo.writeDialog();
	// write the content of list dialog
	wpTwoLists.writeDialog();   
%>