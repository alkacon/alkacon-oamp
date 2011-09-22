<%@ page session="false" buffer="none" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %><%--

This is the new documents form page.

--%><%@ page import="org.opencms.file.*,
					 org.opencms.jsp.*,
					 org.opencms.i18n.*,
					 org.opencms.util.*,
					 java.util.*,					 
					 com.alkacon.opencms.v8.documentcenter.*,
	        		 org.opencms.widgets.*" %><%

// initialise Cms Action Element
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);
    
// Collect the objects required to access the OpenCms VFS from the request
CmsObject cmsObject = cms.getCmsObject();
String uri = cmsObject.getRequestContext().getUri(); 

// get all properties of the file
Map properties = cms.properties("search");

// get locale and message properties
String locale = cms.getRequestContext().getLocale().toString();
properties.put("locale", locale);
CmsMessages messages = cms.getMessages("com.alkacon.opencms.v8.documentcenter.messages_documents", locale);

//determine the template to use
String template = cms.property("template", "search", "/system/modules/org.opencms.frontend.templatetwo/templates/main.jsp");

//set page type property
properties.put("page_type", "newdocuments");

//get the request parameters
String paramAction = CmsStringUtil.escapeHtml(request.getParameter("action"));
String paramRedirect = CmsStringUtil.escapeHtml(request.getParameter("redirect"));
String paramType = CmsStringUtil.escapeHtml(request.getParameter("type"));
String paramUri = CmsStringUtil.escapeHtml(request.getParameter("uri"));

String maxTreeDepth = cms.property(CategoryTree.C_PROPERTY_MAX_TREE_DEPTH, "search", "10");

String startfolder = cmsObject.getRequestContext().getFolderUri();

if ("redirect_a".equalsIgnoreCase(paramRedirect)) {

	// ############### redirect a: save the values submitted by the searchnew form in the user session ###############		
	// do _not_ remove this, even if Eclipse marks it as unread!
	CategoryTree categoryTree = new CategoryTree(cmsObject, request, "/", maxTreeDepth);
	
	String paramSite = CmsStringUtil.escapeHtml(request.getParameter("site"));
	String paramAll = CmsStringUtil.escapeHtml(request.getParameter("all"));
	String paramStartDate = CmsStringUtil.escapeHtml(request.getParameter("startdate"));
	String paramEndDate = CmsStringUtil.escapeHtml(request.getParameter("enddate"));	
	String categorylist = CmsStringUtil.escapeHtml(request.getParameter("categorylist"));
	String query = CmsStringUtil.escapeHtml(request.getParameter("query"));
 
	HttpSession ses = request.getSession();
 
	ses.setAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_SITE, paramSite);
	ses.setAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_ALL, paramAll);
	ses.setAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_CATEGORYLIST, categorylist); 
	
	if ("searchText".equals(paramAction)) {
	    ses.setAttribute(CmsDocumentSearch.SEARCH_PARAM_QUERY, query);
	} else {
	    ses.setAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_STARTDATE, paramStartDate);
	    ses.setAttribute(NewDocumentsTree.C_DOCUMENT_SEARCH_PARAM_ENDDATE, paramEndDate);
	}
 
	String uriRedirectB = cms.link(uri + "?redirect=redirect_b&action=" + paramAction + "&uri=" + paramUri);
		// + "&timestamp=" + new CmsUUID().toString());
		//System.out.println("uriRedirectB: " + uriRedirectB);
	
	if ("searchText".equals(paramType)) {
	    uriRedirectB += "&type=" + paramType; 
	}
	response.sendRedirect(uriRedirectB);

} else if ("redirect_b".equalsIgnoreCase(paramRedirect)) {

	// ############### redirect b: redirect the user to the search result list ###############	
	
	String uriSearch = cms.link(uri + "?action=" + paramAction + "&uri=" + paramUri);
		// + "&timestamp=" + new CmsUUID().toString());
		//System.out.println("uriSearch: " + uriSearch);

	if ("searchText".equals(paramType)) {
	    uriSearch += "&type=" + paramType; 
	}
	
	response.sendRedirect(uriSearch);	


} else if ("14days".equalsIgnoreCase(paramAction) || "search".equalsIgnoreCase(paramAction) || "searchText".equalsIgnoreCase(paramAction)) {               

	// ############### display the search results ###############	

  // include the template head
 cms.include(template, "head");
 %><link	type="text/css" rel="stylesheet" href="<cms:link>/system/modules/com.alkacon.opencms.v8.documentcenter/resources/documents.css</cms:link>" /><%
	out.println("<div class=\"download_onecol\">");
	cms.include("../../elements/doc_newdocuments.jsp", null, properties);
	out.println("</div>");
 // include the template foot
 cms.include(template, "foot");

} else {

	// ############### display the search form ###############
 
  // include the template head
 cms.include(template, "head");
 %><link	type="text/css" rel="stylesheet" href="<cms:link>/system/modules/com.alkacon.opencms.v8.documentcenter/resources/documents.css</cms:link>" /><%
	out.println("<div class=\"download_onecol\">");%>
	<%@ include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.documentcenter/pages/jsp_pages/page_newdocumentstree_code_searchForm.jsp:f8e2cbb2-4acc-11de-9a1a-03821fc5d684)" %>
	<% out.println("</div>");
 // include the template foot
 cms.include(template, "foot");
}

%>