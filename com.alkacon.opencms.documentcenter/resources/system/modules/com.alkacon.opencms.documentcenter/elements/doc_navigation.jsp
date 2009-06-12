<%@ page import="
	org.opencms.file.*,
	org.opencms.jsp.*,
	org.opencms.i18n.CmsMessages,
	com.alkacon.opencms.documentcenter.*,
	java.util.*" 
	buffer="none"
	session="false"
%><%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);

// get locale and message properties
String locale = cms.property("locale", "search", "en").toLowerCase();
CmsMessages messages = cms.getMessages("com.alkacon.opencms.documentcenter.messages_documents", locale);


// only show navigation if no news or categories are displayed
if (cms.template("default")) {

	// get the Startfolder for the breadcrumb nav
	CmsFolder ancestor = cms.getCmsObject().readAncestor(cms.getRequestContext().getUri(), 260);
    String startfolder = cms.getCmsObject().getSitePath(ancestor);

	// get the bread crumb navigation
	List navPath = cms.getNavigation().getNavigationBreadCrumb(1, true);
	int navSize = navPath.size();
	CmsJspNavElement navElement = new CmsJspNavElement();

	if (navSize > 0) {

	    String separator = "&nbsp;&raquo; ";
	    
		// print the bread crumb navigation 
	    out.print("<p class=\"downloadcenter\">");

		out.print(cms.buildBreadCrumbNavigation(startfolder, navPath, "breadcrumb", separator, false));

		// check if the link "up one folder" can be displayed
		if (navSize > 1) {
			navElement = (CmsJspNavElement)navPath.get(navSize - 2);
			String link = navElement.getResourceName();


%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="<%= cms.link(link) %>"><img src="<%= cms.link("/system/modules/com.alkacon.opencms.documentcenter/resources/ic_folder_up.gif") %>"
  alt="<%= messages.key("navigation.link.folderup") %>" title="<%= messages.key("navigation.link.folderup") %>" /></a><%
		}
		out.println("</p>");
	}
}

%>