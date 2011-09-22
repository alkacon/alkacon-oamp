<%@ page session="false" buffer="none"
	import="org.opencms.i18n.*,
 java.util.*,
 com.alkacon.opencms.v8.documentcenter.*"%><%--

This is the "document management main" element

--%>
<%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);
    
// Collect the objects required to access the OpenCms VFS from the request
String uri = cms.getRequestContext().getUri(); 
String folderUri = cms.getRequestContext().getFolderUri();

// get all properties of the file
Map properties = cms.properties("search");

// get locale and message properties
String locale = cms.getRequestContext().getLocale().toString();
CmsMessages messages = cms.getMessages("com.alkacon.opencms.v8.documentcenter.messages_documents", locale);

// check type of page to display navigation and documentlist or not
String pageType = request.getParameter("page_type");
if (pageType == null || "".equals(pageType)) {
	pageType = cms.property("page_type", "uri", "default");
} 

// check if login form has to be displayed
boolean loginForm = "true".equals(request.getParameter("__loginform"));


// show document list
if (!loginForm) {
	// show navigation on document list folders
	cms.include("doc_navigation.jsp", pageType, properties);

	// show documentlist on default pages, category overview on catalog pages
	if (pageType.equals("default")) {
		
		// show document list
		List documentList = CmsDocumentFactory.getDocumentsForFolder(cms, folderUri, cms.getSortMethod());
		boolean showFolderColumn = false;%>
<%@ include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/include/documents_list.jsp:158d080e-1883-11de-8181-03821fc5d684)"%>
<%} else if (pageType.equals("catalog")) { 
		
		// show category overview%>

<%@ include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/include/categories_list.jsp:157ed73b-1883-11de-8181-03821fc5d684)"%>
<%} else if (pageType.equals("versions")) {
    	// show the backed up versions of a document
    	cms.include("docversions.jsp", null, properties); 
    } else if (pageType.equals("attachments")) { 
	cms.include("docattachments.jsp", null, properties);
    } 
} 

%>