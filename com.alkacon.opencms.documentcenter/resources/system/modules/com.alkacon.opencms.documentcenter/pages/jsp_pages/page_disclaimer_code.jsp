<%@ page session="false" buffer="none"
         import="
         java.util.*,
         org.opencms.jsp.*,
         org.opencms.util.*,
         org.opencms.file.*,
         org.opencms.i18n.*,
         javax.servlet.*,
         javax.servlet.http.*,
         com.alkacon.opencms.documentcenter.*" %><%--

This is the disclaimer page

--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"

%><%

// initialise Cms Action Element
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// debug variable
final int DEBUG = 0;

String uriForm = cms.getRequestContext().getUri();
    
// Collect the objects required to access the OpenCms VFS from the request
CmsObject cmsObject = cms.getCmsObject();

// the requested download resource
String curResource = cmsObject.getRequestContext().getUri();

// the displayed disclaimer page
String disclaimer = cms.property("disclaimer_page", "search", "(none)");

// set requested resource to disclaimer page (to get the right properties!) 
//cmsObject.getRequestContext().getRequest().setRequestedResource(disclaimer);
    	
// get all properties of the file
Map properties = cms.properties("search");

// get locale and message properties for disclaimer text!
String locale = cms.property("locale", "search", "en").toLowerCase();
CmsMessages messages = cms.getMessages("com.alkacon.opencms.documentcenter.messages_documents", locale);
properties.put("locale", locale);

HttpSession sess = request.getSession();

// if session ID is not from a cookie, examine closer...
if (!request.isRequestedSessionIdFromCookie()) {
	response.sendRedirect( cms.link( "/system/modules/com.alkacon.opencms.documentcenter/pages/jsp_pages/page_disclaimer_check_cookies.jsp?file="
	                                 +curResource ) );
}

// get action parameter from form
String paramAction = CmsStringUtil.escapeHtml(request.getParameter("action"));

// get link to content page and page when disclaimer was declined
String disclaimerContent = cms.property("disclaimer_page_content", "search", null);

if (paramAction == null || "".equals(paramAction) || messages.key("disclaimer.decline").equals(paramAction)) { 
  // show the disclaimer form

	// get the disclaimer page uri and folder
	String uri = cms.getRequestContext().getUri();
	String folder = uri.substring(0, uri.lastIndexOf('/')+1);		
	
	String template = cms.property("template", "search", "");
	cms.include(template, "head"); 
	out.println("<div class=\"download_onecol\">");%>
	
	<%@ include file="%(link.strong:/system/modules/com.alkacon.opencms.documentcenter/pages/jsp_pages/page_disclaimer_code_showDisclaimer.jsp:c810e3b3-4b6a-11de-a99c-03821fc5d684)" %>
	
	<%out.println("</div>");
	cms.include(template, "foot");



} else if (messages.key("disclaimer.accept").equals(paramAction)) {
  // user accepted disclaimer for current resource

	sess.setAttribute(disclaimer, "true");
	response.sendRedirect(cms.link(curResource));

} else if (messages.key("disclaimer.decline").equals(paramAction)) {
  	// user did not accept disclaimer for current resource
	//response.sendRedirect(cms.link(declinedPage));
}

%>

<script type="text/javascript">

	function redir(type) {
		if (type == 1) {
			var uri = "<%= cms.link(uriForm + "?action=" + messages.key("disclaimer.accept")) %>";
			// window.open(uri, "_blank");
			// window.location.href = "<%= cms.link(cms.getRequestContext().getFolderUri()) %>";
			window.location.href = uri;
		} else {
			var uri = "<%= cms.link(uriForm + "?action=" + messages.key("disclaimer.decline")) %>";
			window.location.href = uri;
		}
	}

</script>