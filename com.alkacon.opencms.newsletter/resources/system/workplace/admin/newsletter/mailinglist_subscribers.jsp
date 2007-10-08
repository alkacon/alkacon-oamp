<%@ page import="com.alkacon.opencms.newsletter.admin.*"%><%

	// initialize info dialog
	CmsMailinglistOverviewDialog wpInfo = new CmsMailinglistOverviewDialog(pageContext, request, response);
	// perform the widget actions
	wpInfo.displayDialog(true);
	if (wpInfo.isForwarded()) {
		return;
	}
	// initialize list dialogs
	CmsMailinglistSubscribersList wpMailinglistSubscribers = new CmsMailinglistSubscribersList(pageContext, request, response);
	CmsNotMailinglistSubscribersList wpNotMailinglistSubscribers = new CmsNotMailinglistSubscribersList(pageContext, request, response);
	org.opencms.workplace.list.CmsTwoListsDialogsWOStart wpTwoLists = new org.opencms.workplace.list.CmsTwoListsDialogsWOStart(wpMailinglistSubscribers, wpNotMailinglistSubscribers);
	// perform the active list actions
	wpTwoLists.displayDialog(true);
	// write the content of widget dialog
	wpInfo.writeDialog();
	// write the content of list dialogs
	wpTwoLists.writeDialog();   
%>