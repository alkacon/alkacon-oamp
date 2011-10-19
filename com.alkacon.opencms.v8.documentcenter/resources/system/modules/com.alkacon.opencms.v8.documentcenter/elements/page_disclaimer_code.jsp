<%@ page session="false" buffer="none"
         import="
         java.util.*,
         org.opencms.jsp.*,
         org.opencms.util.*,
         org.opencms.file.*,
         org.opencms.i18n.*,
         javax.servlet.*,
         javax.servlet.http.*,
         com.alkacon.opencms.v8.documentcenter.*" %><%--

This is the disclaimer page

--%><%

// initialise document frontend
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);
	
// debug variable
final int DEBUG = 0;

String inDocPath = (String)request.getAttribute(CmsDocumentFrontend.ATTR_PATHPART);

String uriForm = CmsFileUtil.removeTrailingSeparator(cms.getRequestContext().getFolderUri()) + inDocPath;
    

// get all properties of the file
Map properties = cms.properties("search");

// get locale and message properties for disclaimer text
String locale = cms.getRequestContext().getLocale().toString();
CmsMessages messages = cms.getMessages("com.alkacon.opencms.v8.documentcenter.messages_documents", locale);
properties.put("locale", locale);

HttpSession sess = request.getSession();

// get action parameter from form
String paramAction = CmsStringUtil.escapeHtml(request.getParameter("action"));

if (paramAction == null || "".equals(paramAction) || messages.key("disclaimer.decline").equals(paramAction)) { 
  	// show the disclaimer form
	CmsXmlDocumentContent xmlContent = new CmsXmlDocumentContent(cms);
	if (messages.key("disclaimer.decline").equals(paramAction)) {
		String declinedText = xmlContent.getDisclaimerDeclined();
		if (declinedText != null) {
			out.println(declinedText);
		}
	} else {
		String disclaimerText = xmlContent.getDisclaimer();
		if (disclaimerText != null) {
			out.println(disclaimerText);
		}
	}
	if ( !messages.key("disclaimer.decline").equals(paramAction)) { %>
	<form action="<%= cms.link(uriForm) %>" method="get" name="disclaimer" target="_blank" ><p>
		<input type="button" value="<%= messages.key("disclaimer.accept") %>" class="button" onclick="redir(1);" />
		&nbsp;&nbsp;&nbsp;
		<input type="button" value="<%= messages.key("disclaimer.decline") %>" class="button" onclick="redir(2);" />
	</p></form>
	<% }
	// show some debug information
	if (DEBUG > 0) { %>
		<h4>Debug information</h4>
		<ul><li>Requested resource: <b><%= uriForm%></b></li>
		<li>Session value for resource: <b><%= sess.getAttribute(uriForm) %></b></li>
		<li>Locale: <b><%= locale %></b></li>
		<li>Disclaimer page: <b><%= cms.property("disclaimer_page", "search", "(none)") %></b></li>
		<li>Real URI: <b><%= cms.getRequestContext().getUri() %></b></li></ul>
	<% } 

} else if (messages.key("disclaimer.accept").equals(paramAction)) {
	// user accepted disclaimer for current resource
	String disclaimer = (String)request.getAttribute(CmsDocumentFrontend.ATTR_DISCLAIMER);
	sess.setAttribute(disclaimer, "true");
	response.sendRedirect(cms.link(uriForm));

}

%>
<script type="text/javascript">

	function redir(type) {
		if (type == 1) {
			var uri = "<%= cms.link(uriForm + "?action=" + messages.key("disclaimer.accept")) %>";
			window.location.href = uri;
		} else {
			var uri = "<%= cms.link(uriForm + "?action=" + messages.key("disclaimer.decline")) %>";
			window.location.href = uri;
		}
	}

</script>