<%

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
	if ((!"true".equals(org.opencms.util.CmsStringUtil.escapeHtml(request.getParameter("print")))) && (!messages.key("disclaimer.decline").equals(paramAction))) { %>
	<form action="<%= cms.link(uriForm) %>" method="get" name="disclaimer" target="_blank" ><p>
		<input type="button" value="<%= messages.key("disclaimer.accept") %>" class="button" onclick="redir(1);" />
		&nbsp;&nbsp;&nbsp;
		<input type="button" value="<%= messages.key("disclaimer.decline") %>" class="button" onclick="redir(2);" />
	</p></form>
<% }
	// show some debug information
	if (DEBUG > 0) { %>
		<h4>Debug information</h4>
		<ul><li>Requested resource: <b><%= curResource %></b></li>
		<li>Session value for resource: <b><%= sess.getAttribute(curResource) %></b></li>
		<li>Locale: <b><%= locale %></b></li>
		<li>Disclaimer page: <b><%= disclaimer %></b></li>
		<li>Disclaimer content: <b><%= disclaimerContent %></b></li>
		<li>Folder: <b><%= folder %></b></li></ul>
	<% } 
%>