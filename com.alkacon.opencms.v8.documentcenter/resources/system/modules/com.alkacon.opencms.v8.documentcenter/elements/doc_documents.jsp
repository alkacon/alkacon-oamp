<%@ page session="false" buffer="none"
	import="org.opencms.i18n.*,
		org.opencms.file.*,
		java.util.*,
		com.alkacon.opencms.v8.documentcenter.*"%><%--

This is the "document management main" element

--%>
<%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);
    
// Collect the objects required to access the OpenCms VFS from the request
String docsFolder = (String)request.getAttribute(CmsDocumentFrontend.ATTR_FULLPATH);

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
	if (pageType.equals("newdocuments")) {
		cms.include("%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/page_newdocumentstree.jsp:9a6e5880-fa2d-11e0-9654-c9a60a7588dd)", null, properties);
	} else {
		// show navigation on document list folders
		cms.include("%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/doc_navigation.jsp:9a4f5eae-fa2d-11e0-9654-c9a60a7588dd)", pageType, properties);
	
		// show documentlist on default pages, category overview on catalog pages
		if (pageType.equals("default")) {
			
			// show document list
			List documentList = CmsDocumentFactory.getDocumentsForFolder(cms, docsFolder, cms.getSortMethod());
			boolean showFolderColumn = false;%>
			<%@ include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/include/documents_list.jsp:9a77ce69-fa2d-11e0-9654-c9a60a7588dd)"%>
		<%} else if (pageType.equals("catalog")) { 
			
			// show category overview%>
			<%@ include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/include/categories_list.jsp:9a758476-fa2d-11e0-9654-c9a60a7588dd)"%>
		<%} else if (pageType.equals("versions")) {
		    	// show the backed up versions of a document
		    	cms.include("%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/docversions.jsp:9a5d8f90-fa2d-11e0-9654-c9a60a7588dd)", null, properties); 
		} else if (pageType.equals("attachments")) { 
			cms.include("%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/docattachments.jsp:9a56639a-fa2d-11e0-9654-c9a60a7588dd)", null, properties);
		}
	}
} 

%>