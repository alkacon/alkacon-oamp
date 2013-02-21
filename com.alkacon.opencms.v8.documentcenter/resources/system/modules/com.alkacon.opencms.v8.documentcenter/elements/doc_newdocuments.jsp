<%@ page import="
	org.opencms.file.*,
	org.opencms.i18n.*,
	java.util.*,
	com.alkacon.opencms.v8.documentcenter.*,
	org.opencms.util.*,
	org.opencms.search.*" 
	buffer="none"
%><%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);
CmsObject cmsObject = cms.getCmsObject();
String uri = cms.getRequestContext().getUri();
String folderUri = cmsObject.getRequestContext().getFolderUri();

String timeString = cms.property("categoryRecentDocs", "search", "14");
long days = 14;
try {
	days = new Integer(timeString).intValue();
} catch (Exception e) {
	// do nothing, keep the default value
}

String locale = request.getParameter("locale");
CmsMessages messages = cms.getMessages("com.alkacon.opencms.v8.documentcenter.messages_documents", locale);

String paramAction = request.getParameter("action");
String newSearchLink = uri;
String paramUri = request.getParameter("uri");
String paramUseUri = request.getParameter("useUri");

newSearchLink += "?page_type=newdocuments";
if (paramUri != null) {
    newSearchLink += "&uri=" + paramUri;
}


// get all properties of the file
Map properties = cms.properties("search");

List newResources = new ArrayList();
List documentList = new ArrayList();

if ("14days".equals(paramAction)) {

	out.print("<h3>" +messages.key("newdocuments.text.14days") +" " +days +" " +messages.key("newdocuments.text.days") +"</h3>");

	// get the document list for the simple "14 days" query
	long curTime = new Date().getTime();
	String folder = (String)request.getAttribute(CmsDocumentFrontend.ATTR_FULLPATH);
	if ((paramUri != null) && (paramUseUri == null)) {
		folder = paramUri;
	}
		
	CmsResourceFilter filter = CmsResourceFilter.IGNORE_EXPIRATION;
	filter = filter.addRequireLastModifiedAfter(curTime - (days * 86400000));
	filter = filter.addRequireLastModifiedBefore(curTime);
	newResources = cmsObject.readResources(folder, filter); 
        
    if (paramUri != null) {%>
		<p>
			<input type="checkbox" id="useuri" <% if (paramUseUri == null) out.print("checked"); %> onclick="toggleFilter(this);" style="vertical-align:middle;"/>
			<label for="useuri">
				<%= messages.key("newdocuments.text.filter") %>
				<strong><%= cmsObject.readPropertyObject(paramUri, CmsCategory.CATEGORY_TITLE, false).getValue() %></strong>
			</label>
		</p><%
     }
	    
	// filter the linked resources from the list
	newResources = NewDocumentsTree.filterLinkedResources(newResources);
	
} else if ("searchText".equals(paramAction)) {
    
    CmsDocumentSearch search = new CmsDocumentSearch(cmsObject, request);
    String indexName = (String)properties.get(CmsDocumentSearch.PROPERTY_SEARCHINDEX);
    if (CmsStringUtil.isEmpty(indexName)) {
    	indexName = "Online project EN (VFS)";
    }
	newResources = search.execute(indexName);
	newSearchLink += "&action=text";%>

    <h3><%= messages.key("newdocuments.query.header") %></h3>
	<p>
	<strong><%= messages.key("newdocuments.query.input") %>:</strong>
	<%= (String)request.getSession().getAttribute(CmsDocumentSearch.SEARCH_PARAM_QUERY) %><br/>		
	<strong><%= messages.key("newdocuments.categories.title") %></strong>
	<%= NewDocumentsTree.getCategories(cmsObject, request, messages.key("newdocuments.categories.all")) %>
	</p>
<% } else {

	// get the document list for the site and category based query (links are already filtered)
	newResources = NewDocumentsTree.getNewResources(cmsObject, request);
	//	 get request parameters for displaying the date on result page
	String paramStartDate = (String)session.getAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_STARTDATE);
	String paramEndDate = (String)session.getAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_ENDDATE);
	
	%>		
		<h3>
			<%= messages.key("newdocuments.form.text") %>
			<%= messages.key("newdocuments.date.from") %>
			<%= NewDocumentsTree.getNiceDate(paramStartDate, locale) %>&nbsp;
			<%= messages.key("newdocuments.date.to") %>
			<%= NewDocumentsTree.getNiceDate(paramEndDate, locale) %>.
		</h3>
		<p>
	   		<strong><%= messages.key("newdocuments.categories.title") %></strong>
	        <%= NewDocumentsTree.getCategories(cmsObject, request, messages.key("newdocuments.categories.all")) %>
		</p>
	<% 

}
// get the sorted (by date) document list 	
documentList = CmsDocumentFactory.getSortedDocuments(cms, newResources, "D", false);%>

	<p style="margin:8px;">
		<% if (paramUri != null) { %>
			<a class="button-w btn" href="<%= cms.link(uri) %>"><%= messages.key("newdocuments.button.back") %></a>&nbsp;
		<% } %>
		<a class="button-w btn" href="<%= cms.link(newSearchLink) %>"><%= messages.key("newdocuments.button.newsearch") %></a>
	</p>
    <% if (documentList.size() > 0) { 
        	// show the folder column, this boolean is used in the included "list_documents.txt" file
    		boolean showFolderColumn = true;
        	%>
        	<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/include/documents_list.jsp:9a77ce69-fa2d-11e0-9654-c9a60a7588dd)" %>
        	
	<%} else {
		out.print("<p class=\"error\">" +messages.key("newdocuments.error.nodocuments") +"</p>");
	}
%>
<script type="text/javascript" language="JavaScript">
<!--
	function toggleFilter(checkbox) {
			if (checkbox.checked) {
				document.location.href = "<%= cms.link(uri + "?action=14days&page_type=newdocuments&uri=" + paramUri) %>";
			} else {
				document.location.href = "<%= cms.link(uri + "?action=14days&page_type=newdocuments&uri=" + paramUri + "&useUri=false") %>";
			}
		
	}
//-->
</script>