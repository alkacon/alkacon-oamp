<%@ page import="
	javax.servlet.jsp.*,
	org.opencms.file.*,
	org.opencms.jsp.*,
	org.opencms.i18n.*,
	org.opencms.util.*,
	java.util.*,
	com.alkacon.opencms.documentcenter.*" 
	buffer="none"
%><%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);
CmsObject cmsObject = cms.getCmsObject();
String uri = cms.getRequestContext().getUri();
String folderUri = cmsObject.getRequestContext().getFolderUri();

//
String timeString = cms.property("categoryRecentDocs", "search", "14");
long days = 14;
try {
	days = new Integer(timeString).intValue();
} catch (Exception e) {
 // do nothing, keept the default value
}

String locale = (String)request.getParameter("locale");
CmsMessages messages = cms.getMessages("com.alkacon.opencms.documentcenter.messages_documents", locale);

String paramAction = (String)request.getParameter("action");
String pageType = "newdocuments";

// get all properties of the file
Map properties = cms.properties("search");

List newResources = new ArrayList();
List documentList = new ArrayList();

	if ("14days".equals(paramAction)) {
		// get the document list for the simple "14 days" query
		long curTime = new Date().getTime();
		newResources = cmsObject.getResourcesInTimeRange(folderUri, curTime - (days*86400000), curTime);
		// filter the linked resources from the list
		newResources = NewDocumentsTree.filterLinkedResources(newResources);
		out.print("<p>"+messages.key("newdocuments.text.14days") + " " + days + " " + messages.key("newdocuments.text.days") + "</p>");
		//out.print(CmsDateUtil.getDateTimeShort(curTime - (days*86400000))+ " - "+ CmsDateUtil.getDateTimeShort(curTime));
	
	} else {		
		// get the document list for the site and category based query (links are already filtered)
		newResources = NewDocumentsTree.getNewResources(cmsObject, request);		
	}
	
	// get the sorted (by date) document list 	
	documentList = CmsDocumentFactory.getSortedDocuments(cms, newResources, "D", false, true);
	
	if (documentList.size() == 0) { %>
		<h3><%= messages.key("newdocuments.error.nodocuments") %></h3>
	<%
	}
	
	// get request parameters for displaying the date on result page
	//String paramStartDate = (String)request.getParameter("startdate");
    //String paramEndDate = (String)request.getParameter("enddate");

    String paramStartDate = (String)session.getAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_STARTDATE);
    String paramEndDate = (String)session.getAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_ENDDATE);
    
	%>
	<table border="0" cellpadding="2" cellspacing="0">
	<% if ("search".equals(paramAction)) { %>
	<tr>
		<td class="bold" valign="top"><%= messages.key("newdocuments.categories.title") %></td>
		<td><%= NewDocumentsTree.getCategories(cmsObject, request, messages.key("newdocuments.categories.all")) %></td>
	</tr>
	<tr>
		<td><%= messages.key("newdocuments.date.start") %></td>
		<td><%= NewDocumentsTree.getNiceDate(paramStartDate, locale) %></td>
	</tr>
	<tr>
		<td><%= messages.key("newdocuments.date.end") %></td>
		<td><%= NewDocumentsTree.getNiceDate(paramEndDate, locale) %></td>
	</tr>
	<% } %>
	<tr>
		<td colspan="2"><input type="button" value="<%= messages.key("newdocuments.button.newsearch") %>" class="formbutton" onclick="location.href='<%= cms.link(uri) %>';"></td>
	</tr>
	</table>
	</div>

	<!-- Body content end -->
        <% if (documentList.size() > 0) { 
        	// show the folder column, this boolean is used in the included "list_documents.txt" file
    		boolean showFolderColumn = true;
        %>
        <%@include file="/system/modules/com.alkacon.opencms.documentcenter/jsptemplates/list_documents.txt" %> 
        <% } %>