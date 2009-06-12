<%@ page import="
	org.opencms.file.*, 
	org.opencms.i18n.CmsMessages,
	org.opencms.site.*,
	org.opencms.util.*,
	java.util.*,
	com.alkacon.opencms.documentcenter.*" 
	buffer="none"
%><%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);
    
// Collect the objects required to access the OpenCms VFS from the request
CmsObject cmsObject = cms.getCmsObject();
String uri = cmsObject.getRequestContext().getUri(); 
String folderUri = cms.getRequestContext().getFolderUri();

// get locale and message properties
String locale = (String)request.getParameter("locale");
CmsMessages messages = cms.getMessages("com.alkacon.opencms.documentcenter.messages_documents", locale);

List hiddenCols = cms.getHiddenColumns();
boolean showDcColumn = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_DATECREATED);
boolean showDmColumn = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_DATEMODIFIED);

String cssClassDm = "docdatechanged";
if (!showDcColumn) {
	cssClassDm = "docdatecreated";
}

String attDoc = (String)request.getParameter("document");

CmsDocument doc = CmsDocumentFactory.createDocumentWithVersions(cms, attDoc);

%><h2>&nbsp;&nbsp;<%= messages.key("attachments.headline") %><%

if (CmsStringUtil.isNotEmpty(doc.getDocumentId())) {
	out.print("<br>&nbsp;&nbsp;" + messages.key("attachments.subline", doc.getDocumentId()));
}

%></h2>
<p>
<form>
	&nbsp;&nbsp;<input type="button" class="headbutton" value="<%= messages.key("link.back") %>" onclick="history.back();">
</form>
</p>
<!-- doclist start -->
<table class="docs">
	<tr>
		<td class="docsheadleft" width="3%"><%= messages.key("documentlist.headline.type") %></td>
		<td class="docsheadcenter"><%= messages.key("documentlist.headline.title") %></td>
		<td class="docsheadright" align="right" width="8%"><%= messages.key("documentlist.headline.size") %></td>
		<% if (showDmColumn ) { %><td class="docsheadright" align="right" width="11%"><%= messages.key("documentlist.headline.datemodified") %></td><% } %>
		<% if (showDcColumn) { %><td class="docsheadright" align="right" width="11%"><%= messages.key("documentlist.headline.datecreated") %></td><% } %>
	</tr><%

String resources = "/system/modules/com.alkacon.opencms.documentcenter/resources/";
String resPath = cms.link(resources);
	
//String docNew = "<img src=\""+resPath+"ic_doc_new.gif\" width=\"12\" height=\"11\" border=\"0\" alt=\""+messages.key("documentlist.icon.new")+"\" title=\""+messages.key("documentlist.icon.new")+"\">";
//String docModified = "<img src=\""+resPath+"ic_doc_modified.gif\" width=\"8\" height=\"11\" border=\"0\" alt=\""+messages.key("documentlist.icon.update")+"\" title=\""+messages.key("documentlist.icon.update")+"\">";

for (int i = 0; i< doc.getLocales().size(); i++) {
	Locale docLoc = (Locale)doc.getLocales().get(i);
	CmsDocument currDoc = (CmsDocument)doc.getLocalizedDocument(docLoc);

	if (cms.isUseLanguages()) {

	String cellClass = " doclanguagehead";
	if (i > 0) {
		cellClass = " doclanguageheadborder";
	}
%>
	<tr>
        	<td class="doctype<%= cellClass %>"></td>
        	<td class="docname<%= cellClass %>"><b><%= docLoc.getDisplayLanguage(new Locale(locale)) %></b></td>
        	<td class="docsize<%= cellClass %>"></td>
        	<% if (showDmColumn ) { %><td class="<%= cssClassDm %><%= cellClass %>"></td><% } %>
        	<% if (showDcColumn) { %><td class="docdatecreated<%= cellClass %>"></td><% } %>
    	</tr>

	}

	<tr>
        <td class="doctype"><%= cms.buildDocIcon(currDoc.getPath(), messages, resources, false) %></td>
        <td class="docname"><%
                
        %><a href="<%= cms.link(currDoc.getPath()) %>" title="<%= messages.key("documentlist.link.open") %>"><%= currDoc.getTitle() %></a><%
	
		if (currDoc.hasAttachments()) {
			%><div class="docattachments"><span class="docatthead">
			<%= messages.key("documentlist.head.attachments") %></span><%
			Iterator it = currDoc.getAttachedDocuments().iterator();
			while (it.hasNext()) {
				CmsDocument att = (CmsDocument)it.next();
				String attTarget = "";
				if ("eLink".equals(att.getPostfixType())) {
					attTarget = " target=\"_blank\"";
				}

				out.print("<br><a href=\"" + cms.link(att.getPath()) + "\"" + attTarget + ">" + att.getTitle() + "</a>");
			}
			out.print("</div>");
		}

	%></td>
        <td class="docsize"><%
        
        int docSize = 0;
        try {
        	docSize = currDoc.getSize();
        } catch (Exception e) {}
        
        %><%= Math.round(Math.ceil(docSize / 1024.0)) + "&nbsp;" + messages.key("documentlist.size.kb") %></td>
        <% if (showDmColumn ) { %><td class="<%= cssClassDm %>"><%= doc.formatDate(currDoc.getDateLastModified(), true) %></td><% } %>
        <% if (showDcColumn ) { %><td class="docdatecreated"><%= doc.formatDate(currDoc.getDateCreated(), true) %></td><% } %>
    </tr><%							

}

%>
	<tr>
    	<td class="doctypeend">&nbsp;</td>
        <td class="docnameend">&nbsp;</td>
        <td class="docsizeend">&nbsp;</td>
        <% if (showDmColumn) { %><td class="<%= cssClassDm  %>end">&nbsp;</td><% } %>
        <% if (showDcColumn) { %><td class="docdatecreatedend">&nbsp;</td><% } %>
	</tr>
</table>
<!-- doclist end --><%

messages = null;
doc = null;
}
%>