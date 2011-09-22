<%@ page session="false" buffer="none" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %><%--

This is the search page.

--%><%@ page import="org.opencms.file.*,org.opencms.jsp.*,org.opencms.i18n.*,java.util.*,org.opencms.search.*,com.alkacon.opencms.documentcenter.*" %><%

// initialise Cms Action Element
CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);

// Collect the objects required to access the OpenCms VFS from the request
CmsObject cmsObject = cms.getCmsObject();
String uri = cmsObject.getRequestContext().getUri(); 
String folderUri = cmsObject.getRequestContext().getFolderUri(); 

// get all properties of the file
Map properties = cms.properties("search");

// get locale and message properties
String locale = cms.getRequestContext().getLocale().toString();
properties.put("locale", locale);
CmsMessages messages = cms.getMessages("messages_intranet", locale);

// set page type property
properties.put("page_type", "search");

// determine the template to use
String template = cms.property("template", "search", "/system/modules/org.opencms.frontend.templatetwo/templates/main.jsp");

// include the template head
cms.include(template, "head", properties);
	%><link	type="text/css" rel="stylesheet" href="<cms:link>/system/modules/com.alkacon.opencms.v8.documentcenter/resources/documents.css</cms:link>" />
<%
String siteName = cms.property("Title", "/", "");

%>

<jsp:useBean id="search" scope="request" class="org.opencms.search.CmsSearch">
    <jsp:setProperty name="search" property="*"/>
    <% 
    	search.init(cmsObject); 
    	search.setIndex(siteName);
    	search.setField(new String[] {"title", "keywords", "description", "content"});
    	search.setMatchesPerPage(20);
    	search.setQueryLength(3);
    	search.setDisplayPages(11);
    %>
</jsp:useBean>
<%	

String queryString = search.getQuery();
List result;
if (queryString == null || "".equals(queryString.trim())) {
	queryString = "";
	search.setQuery("");
	result = new ArrayList();
} else {
	result = search.getSearchResultForPage();
}

%>

<h2><%= messages.key("search.headline") %></h2>
<%
if (search.getLastException() != null) { 
	String errorMessage = "";
	if (search.getLastException().toString().indexOf("too short,") != -1) {
		errorMessage = messages.key("search.error.wordlength");
	} else {
		errorMessage = messages.key("search.error.details");
	}
%>
<h3><%= messages.key("search.error") %></h3>
<p><%= errorMessage %></p>
<!-- Exception message:
<%= search.getLastException().toString() %>
//-->
<%
} else if (result == null || result.size() == 0) {
	out.print("<h3>" + messages.key("search.error.nomatch") + "</h3>");
}
%>

<p>
<form name="searchform" method="get" action="<%= cms.link(uri) %>" onSubmit="return parseSearchQuery(document.forms['searchform'], '<%= messages.key("search.error.wordlength") %>');">
<input type="hidden" name="action" value="search">
<input type="hidden" name="query" value="">
<table border="0" cellpadding="2" cellspacing="2">
<tr>
	<td><input type="text" name="query2" size="50" value="<%= queryString %>"></td>
	<td><input type="submit" value="<%= messages.key("form.head.search.button") %>" class="headbutton"></td>
</tr>
</table>
</form>
</p>

<!-- Body content end -->
        </td></tr>
        </table>
        <%

if (result != null && result.size() > 0) {

	List hiddenCols = cms.getHiddenColumns();
	boolean showDcColumn = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_DATECREATED);
	boolean showDmColumn = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_DATEMODIFIED);

	String cssClassDm = "docdatechanged";
	if (!showDcColumn) {
		cssClassDm = "docdatecreated";
	}

	String resPath = cms.link("../../resources/");
	String docNew = "<img src=\""+resPath+"ic_doc_new.gif\" width=\"12\" height=\"11\" border=\"0\" alt=\""+messages.key("documentlist.icon.new")+"\" title=\""+messages.key("documentlist.icon.new")+"\">";
	String docModified = "<img src=\""+resPath+"ic_doc_modified.gif\" width=\"8\" height=\"11\" border=\"0\" alt=\""+messages.key("documentlist.icon.updatesimple")+"\" title=\""+messages.key("documentlist.icon.updatesimple")+"\">";

%>
<table class="docs">
	<tr>
		<td class="docsheadleft" width="3%"><%= messages.key("documentlist.headline.type") %></td>
		<td class="docsheadleft" width="5%"><%= messages.key("documentlist.headline.lang") %></td>
		<td class="docsheadcenter"><%= messages.key("documentlist.headline.title") %></td>
		<td class="docsheadright" width="11%"><%= messages.key("documentlist.headline.folder") %></td>	
		<td class="docsheadright" align="right" width="8%"><%= messages.key("documentlist.headline.size") %></td>
		<% if (showDmColumn ) { %><td class="docsheadright" align="right" width="11%"><%= messages.key("documentlist.headline.datemodified") %></td><% } %>
		<% if (showDcColumn) { %><td class="docsheadright" align="right" width="11%"><%= messages.key("documentlist.headline.datecreated") %></td><% } %>
	</tr>
<%

ListIterator iterator = result.listIterator();
while (iterator.hasNext()) {
	CmsSearchResult entry = (CmsSearchResult)iterator.next();
	String path = entry.getPath();
	
	// determine anchor target for current resource
	String openWin = "_self";
	if ("eLink".equals(CmsDocument.getPostfixType(((CmsIndexResource)entry.getResource()).getName()))) {
		openWin = "_blank";
	}
		
%>
	<tr>
        <td class="docrow doctype"><%= CmsDocumentFrontend.buildDocIcon(((CmsIndexResource)entry.getResource()).getName(), messages, resPath, false) %></td>
        <td class="doclang"><%= CmsDocument.getResourceLocale((CmsIndexResource)entry.getResource()).getName()) %></td>
	<td class="docrow docname"><%
        
        String docName = entry.getTitle();
        if (docName == null || "".equals(docName)) {
        	docName = ((CmsIndexResource)entry.getResource()).getName();
        }
        
        %><a href="<%= cms.link(path) %>" target="<%= openWin %>"><%= docName %></a>&nbsp;(<%= entry.getScore() %>%)&nbsp;<%
        
        // create icons for a new or modified document
		if (CmsDocument.isNew(4, entry.getDateCreated().getTime())) {
			out.print("&nbsp;" + docNew);
		}
		// 1st param: duration of "modified" mark, 2nd param: duration of "new" mark (days), 3rd: creation date, 4th: modification date
		else if (CmsDocument.isModified(4, 4, entry.getDateCreated().getTime(), entry.getDateLastModified().getTime())) {
			out.print("&nbsp;" + docModified);
		}
		
        %><br>
	<% 
	
	if (entry.getExcerpt() != null) {
		out.print(entry.getExcerpt() + "<br>");
	} 
	if (entry.getKeywords() != null) {
		%><%= messages.key("search.keywords") %>: <%= entry.getKeywords() %><br><%
	}
	if (entry.getDescription() != null) {
		%><%= messages.key("search.description") %>: <%= entry.getDescription() %><br><%
	}	
	%>
		</td>
        <td class="docrow docfolder"><%
        	String folderLink = CmsResource.getParentFolder(path);
        	String folderName = cms.property("Title", folderLink, CmsResource.getName(folderLink));
         %><a href="<%= cms.link(folderLink) %>" title="<%= messages.key("documentlist.link.open") %>"><%= folderName %></a></td>		
        <td class="docrow docsize"><% // Math.round(Math.ceil(entry.getLength() / 1024.0)) %>&nbsp;<%= messages.key("documentlist.size.kb") %></td>
        <% if (showDmColumn) { %><td class="docrow <%= cssClassDm %>"><a href="<%= cms.link(folderUri) %>?page_type=versions&document=<%= path %>" title="<%= messages.key("documentlist.link.showversions") %>"><%= messages.getDateTime(entry.getDateLastModified().getTime()) %></a></td><% } %>
	<% if (showDcColumn) { %><td class="docrow docdatecreated"><%= messages.getDateTime(entry.getDateCreated().getTime()) %></td><% } %>
<%

} // end of iteration
iterator = null;

%>
	<tr>
    	<td class="docrow doctype">&nbsp;</td>
        <td class="docrow docname center"><%
        	boolean showPageLinks = false;
        	if (search.getPreviousUrl() != null || search.getNextUrl() != null) {
        		showPageLinks = true;
        		%>&nbsp;<br><span class="pagelinks"><%
        	}
        	if (search.getPreviousUrl() != null) {
        		%><input type="button" class="searchbutton" value="&lt;&lt; <%= messages.key("search.previous") %>" onclick="location.href='<%= cms.link(search.getPreviousUrl()) %>';"><%
        	}
        	Map pageLinks = search.getPageLinks();
        	Iterator i =  pageLinks.keySet().iterator();
        	while (i.hasNext()) {
        		int pageNumber = ((Integer)i.next()).intValue();
        		String pageLink = cms.link((String)pageLinks.get(new Integer(pageNumber)));       		
        		out.print("&nbsp; &nbsp;");
        		if (pageNumber != search.getSearchPage()) {
        			%><a href="<%= pageLink %>"><%= pageNumber %></a><%
        		} else {
        			%><span class="currentpage"><%= pageNumber %></span><%
        		}
        	}
        	i = null;
        	if (search.getNextUrl() != null) {
        		%>&nbsp; &nbsp;<input type="button" class="searchbutton" value="<%= messages.key("search.next") %> &gt;&gt;" onclick="location.href='<%= cms.link(search.getNextUrl()) %>';"><%
        	} 
        	if (showPageLinks) {
        		%></span><br><%
        	}
        	%>&nbsp;</td>
        <td class="docrow docfolder">&nbsp;</td>
        <td class="docrow docsize">&nbsp;</td>
        <% if (showDmColumn) { %><td class="<%= cssClassDm  %>">&nbsp;</td><% } %>
	<% if (showDcColumn) { %><td class="docdatecreated">&nbsp;</td><% } %>	</tr>
	<tr>
    	<td class="doctypeend">&nbsp;</td>
        <td class="docnameend center">&nbsp;</td>
        <td class="docfolderend">&nbsp;</td>
        <td class="docsizeend">&nbsp;</td>
        <% if (showDmColumn) { %><td class="<%= cssClassDm  %>end">&nbsp;</td><% } %>
        <% if (showDcColumn) { %><td class="docdatecreatedend">&nbsp;</td><% } %>	</tr>
</table>
<%
	
} // end of test: result.size() > 0

// include the template foot
cms.include(template, "foot", properties);

%>    


%>
