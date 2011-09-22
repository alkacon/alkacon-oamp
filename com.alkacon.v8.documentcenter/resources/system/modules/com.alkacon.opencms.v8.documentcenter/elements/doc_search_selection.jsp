<%@ page session="false" buffer="none" import="java.util.*,org.opencms.jsp.*,org.opencms.i18n.*"

%><%
	// initialise Cms Action Element
	CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

	String folderUri = cms.getRequestContext().getFolderUri();
	
		
	// get locale and message properties
	String locale = cms.getRequestContext().getLocale().toString();
	CmsMessages messages = cms.getMessages("com.alkacon.opencms.v8.documentcenter.messages_documents", locale);

	// New documents search selector
	%>
	
<div class="searchSelection">
	<form name="newdocuments" action="<%= cms.link(cms.property("link_newdocuments", "search", folderUri  + "newdocuments.html")) %>" method="get" title="<%= messages.key("form.head.title") %>" >
		<input type="hidden" name="uri" value="<%= cms.getRequestContext().getFolderUri() %>" />
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
