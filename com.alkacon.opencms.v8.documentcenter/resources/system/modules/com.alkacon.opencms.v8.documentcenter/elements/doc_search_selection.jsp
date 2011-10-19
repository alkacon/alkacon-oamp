<%@ page session="false" buffer="none" import="java.util.*,org.opencms.jsp.*,org.opencms.i18n.*, com.alkacon.opencms.v8.documentcenter.*"

%><%
	// initialise Cms Action Element
	CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

	String uri = cms.getRequestContext().getUri();
	String folderPath = (String)request.getAttribute(CmsDocumentFrontend.ATTR_FULLPATH);
	if (folderPath == null || folderPath.equals((String)request.getAttribute(CmsDocumentFrontend.ATTR_STARTPATH))) {
		folderPath = "";
	}
		
	// get locale and message properties
	String locale = cms.getRequestContext().getLocale().toString();
	CmsMessages messages = cms.getMessages("com.alkacon.opencms.v8.documentcenter.messages_documents", locale);

	// New documents search selector
	%>
	
<div class="searchSelection">
	<form name="newdocuments" action="<%= cms.link(uri) %>" method="post" title="<%= messages.key("form.head.title") %>" >
		<input type="hidden" name="page_type" value="newdocuments" />
		<% if (!folderPath.equals("")) { %><input type="hidden" name="uri" value="<%= folderPath %>" /><% } %>
		<div>
			<label for="searchselect" class="hidden"><%= messages.key("form.head.newdocuments.newsince") %></label>
			<select name="action" id="searchselect" class="head" size="1">
				<option value="14days" selected="selected"><%= messages.key("form.head.newdocuments.14days") %></option>
				<option value=""><%= messages.key("form.head.newdocuments.bydate") %></option>
				<option value="text"><%= messages.key("form.head.newdocuments.text") %></option>
			</select>
			<input type="submit" class="button" value="<%= messages.key("form.head.search.button") %>" />
		</div>
	</form>
</div>
