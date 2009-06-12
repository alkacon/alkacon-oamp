<%@ page buffer="none"
         session="false"
         import="
         java.util.*,
         org.opencms.jsp.*,
         org.opencms.file.*,
         org.opencms.i18n.*,
         org.opencms.main.CmsException,
         org.opencms.util.*,
         org.opencms.main.CmsLog"
%><%--

This is the cookie check JSP

Usage:
Write the following code in your JSP where you want to check if cookies are stored:
if (!request.isRequestedSessionIdFromCookie()) {
	cmsObject.getRequestContext().getResponse().sendCmsRedirect("/path/to/page_disclaimer_check_cookies.jsp?file="+curResource);
}
else {
	...some code which needs (session) cookies
}

--%><%

// initialise Cms Action Element
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// Debug variable
final int DEBUG = 0;
    
// Collect the objects required to access the OpenCms VFS from the request
CmsObject cmsObject = cms.getCmsObject();

// the download resource
String curResource = CmsStringUtil.escapeHtml(request.getParameter("file"));

if (curResource != null && !"".equals(curResource)) {

	// Collect the objects required to access the OpenCms VFS from the request
	//String path = request.getContextPath() + request.getServletPath();
    	
	// get the displayed disclaimer page
	String disclaimer = cmsObject.readPropertyObject(curResource, "disclaimer_page", true).getValue("(none)"); 

	// set requested resource to disclaimer page (to display localized navigation!) 
	//cmsObject.getRequestContext().getRequest().setRequestedResource(disclaimer);
    	
	// get all properties of the file
	Map properties = cms.properties("search");
    	
	// get locale and message properties  
	String locale = cmsObject.readPropertyObject(curResource, "locale", true).getValue("en").toLowerCase();	
	CmsMessages messages = cms.getMessages("com.lgt.internet.frontend.documents_messages", locale);
	properties.put("locale", locale);
    
	// set printable to false
	properties.put("printable", "false");


	// ----------- <seitenkopf> --------------------
	String template = cms.property("template", "search", "");
	cms.include(template, "head"); 
	out.println("<div class=\"download_onecol\">");
	String content = cms.property("content", "search", null);
	if (content != null) {
		cms.includeSilent(content , "header", true);
	}
	// ----------- </seitenkopf> --------------------

	if (request.isRequestedSessionIdFromCookie()) {
	  // session is from cookie, redirect again to the download resource...
		response.sendRedirect(cms.link(curResource));
	} else {
	  // no cookies allowed, display the error output!

		out.println("<h3>"+messages.key("disclaimer.nocookies_headline")+"</h3>");
		out.println("<p>"+messages.key("disclaimer.nocookies_text")+"</p>");
		
		// print some debug information
		if (DEBUG > 0) {
			out.println("<h3>Debug information</h3>");
			out.println("<ul><li>Locale: <b>"+locale+"</b></li>");
			out.println("<li>Disclaimer: <b>"+disclaimer+"</b></li></ul>");
		}
	}

	// ----------- <seitenende> --------------------- 
	if (content != null) {
		cms.includeSilent(content, "footer", true);
	}
	out.println("</div>");
	cms.include(template, "food");
	// ----------- </seitenende> ------		--------------

} else {
  // file parameter missing, show error message

	%>
<html><body>
		<h1>Error detected!</h1>
		<p>Called the "page_disclaimer_check_cookies.jsp" without the necessary "file" parameter!</p>
		<p>Please go back to the <a href="<%=cms.link("./")%>">index page</a>.</p>
	</body></html><%

	CmsLog.getLog(CmsException.class).error( "###ERROR IN: /com.alkacon.opencms.documentcenter/pages/jsp_pages/page_disclaimer_check_cookies.jsp"
                                                  +" - requestUri: " +(String)CmsJspTagInfo.infoTagAction("opencms.request.uri", request)
                                                  +" - curResource/file: " +curResource );

}

%>