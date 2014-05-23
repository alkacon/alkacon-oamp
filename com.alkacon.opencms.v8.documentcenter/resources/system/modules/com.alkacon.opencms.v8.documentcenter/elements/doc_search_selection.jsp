<%@ page taglibs="c,cms" session="false" buffer="none" import="java.util.*,org.opencms.jsp.*,org.opencms.i18n.*, com.alkacon.opencms.v8.documentcenter.*"

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
	<form name="newdocuments" action="<%= cms.link(uri) %>" method="post" title="<%= messages.key("form.head.title") %>" class="form-inline" >
		<input type="hidden" name="page_type" value="newdocuments" />
		<% if (!folderPath.equals("")) { %><input type="hidden" name="uri" value="<%= folderPath %>" /><% } %>
		<div>
			<c:if test="${cms.template.name == 'mobile'}"><div class="form-group"></c:if>
			<label for="searchselect" class="hidden"><%= messages.key("form.head.newdocuments.newsince") %></label>
			<select name="action" id="searchselect" size="1" class="<c:choose><c:when test="${cms.template.name == 'mobile'}">form-control</c:when><c:otherwise>head</c:otherwise></c:choose>">
				<option value="14days" selected="selected"><%= messages.key("form.head.newdocuments.14days") %></option>
				<option value=""><%= messages.key("form.head.newdocuments.bydate") %></option>
				<option value="text"><%= messages.key("form.head.newdocuments.text") %></option>
			</select>
			<c:if test="${cms.template.name == 'mobile'}"></div><div class="form-group"></c:if>
			<input type="submit" class="<c:choose><c:when test="${cms.template.name == 'mobile'}">btn btn-default</c:when><c:otherwise>button</c:otherwise></c:choose>" value="<%= messages.key("form.head.search.button") %>" />
			<c:if test="${cms.template.name == 'mobile'}"></div></c:if>
		</div>
	</form>
</div>
