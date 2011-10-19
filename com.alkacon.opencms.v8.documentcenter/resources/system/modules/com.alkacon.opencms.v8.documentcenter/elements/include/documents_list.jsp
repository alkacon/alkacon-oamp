<!-- doclist start -->
<%
	List hiddenCols = cms.getHiddenColumns();
	boolean showIdColumn = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_ID);
	boolean showLangColumn = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_LANGUAGE);
	
	boolean showDateCreated = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_DATECREATED);
	boolean showDateModified = !hiddenCols.contains(CmsDocumentFrontend.COLUMN_NAME_DATEMODIFIED);
	
	boolean showNewModIcon = !hiddenCols.contains("overlay");


    String resources = "/system/modules/com.alkacon.opencms.v8.documentcenter/resources/";
    String resPath = cms.link(resources);
   
// some documents are present, show list
if (documentList.size() > 0) { 
%>
 
<%@page import="java.util.ArrayList"%><table class="downloadcenter" border="0" cellspacing="0" summary="<%= messages.key("documentlist.table.summary") %>" >
 <thead>
  <tr>
 	<%-- ID --%>
  	<% if (showIdColumn ) { %><th><%= cms.getColumnHeader(CmsDocumentFrontend.COLUMN_NAME_ID, resPath, messages) %></th><% } %>	
	<%-- TITLE + DESCRIPTION --%>	
    <th><%= cms.getColumnHeader(CmsDocumentFrontend.COLUMN_NAME_TITLE, resPath, messages) %></th>
	<%-- ICON NEW MODIFIED --%>	 
	<% if (showNewModIcon) {%><th>&nbsp;</th><% } %>
    <%-- LANGUAGE --%>  
    <% if (showLangColumn) { %><th><%= cms.getColumnHeader("lang", resPath, messages) %></th><% } %>
	<%-- FOLDER COLUMN --%>	    
    <% if (showFolderColumn) {%><th><%= cms.getColumnHeader("folder", resPath, messages) %></th><% }
    if (cms.isUseTypes()) {%>
		<%-- TYPE --%> 
	    <th class="align_right"><%= messages.key("documentlist.headline." + CmsDocumentFrontend.COLUMN_NAME_TYPE) %></th>
		<%-- SIZE --%>	
	    <th class="align_right"><%= messages.key("documentlist.headline." + CmsDocumentFrontend.COLUMN_NAME_SIZE) %></th>
		<%-- DATE MODIFIED --%>
	    <% if (showDateModified ) { %><th class="align_right"><%= messages.key("documentlist.headline." + CmsDocumentFrontend.COLUMN_NAME_DATEMODIFIED) %></th><% } %>
		<%-- DATE CREATED --%>
	    <% if (showDateCreated ) { %><th class="align_right"><%= messages.key("documentlist.headline." + CmsDocumentFrontend.COLUMN_NAME_DATECREATED) %></th><% }
	} else {%>    
    	<th><%= cms.getColumnHeader(CmsDocumentFrontend.COLUMN_NAME_TYPE, resPath, messages) %></th>
    	<th class="align_right"><%= cms.getColumnHeader(CmsDocumentFrontend.COLUMN_NAME_SIZE, resPath, messages) %></th>
    	<% if (showDateModified ) { %><th class="align_right"><%= cms.getColumnHeader(CmsDocumentFrontend.COLUMN_NAME_DATEMODIFIED, resPath, messages) %></th><% } %>
    	<% if (showDateCreated ) { %><th class="align_right"><%= cms.getColumnHeader(CmsDocumentFrontend.COLUMN_NAME_DATECREATED, resPath, messages) %></th><% }
    }%>
  </tr>
 </thead>
 <tbody>
<% 
	String docNewSm =(showNewModIcon? " style=\"background: transparent url("+resPath+"ic_doc_new_sm.gif) no-repeat top center;\"":" ");
	String docModifiedSm =(showNewModIcon? " style=\"background: transparent url("+resPath+"ic_doc_mod_sm.gif) no-repeat top center;\"":" ");

	//if not exits a locale image, then use the default
	String imgsrc=resPath+"ic_doc_new_"      +locale +".gif";
	if(!cms.getCmsObject().existsResource(imgsrc))
	    imgsrc=resPath+"ic_doc_new.gif";
	
	
    String docNew =      "<img src=\""+imgsrc +"\" border=\"0\" "
                             +"alt=\""+messages.key("documentlist.icon.newsimple","")+"\" title=\""+messages.key("documentlist.icon.newsimple","")+"\" />";

    imgsrc=resPath+"ic_doc_modified_"      +locale +".gif";
	if(!cms.getCmsObject().existsResource(imgsrc))
	    imgsrc=resPath+"ic_doc_modified.gif";
    String docModified = "<img src=\""+imgsrc +"\" border=\"0\" "
                             +"alt=\""+messages.key("documentlist.icon.updatesimple")+"\" title=\""+messages.key("documentlist.icon.updatesimple")+"\" />";
    String showHistory = cms.property("categoryHistory", "search", "true");
 
    // get the description information
    String descriptionMode = "";
    String descriptionProperty = "Description";
    String descriptionData = cms.property("categoryDescription", "search", "");
    List descriptionList = CmsStringUtil.splitAsList(descriptionData,";");
    if (descriptionList.size() >= 2) {
	descriptionProperty = (String) descriptionList.get(1);
    } 
    if (descriptionList.size() >= 1) {
	descriptionMode = (String) descriptionList.get(0);
    }
 
    // get the link mode
    String linkMode =  cms.property("categoryWindow", "search", "");
    
    // rel attribute
    String relAttrib = "";

    // get the css-class (alternating at end of while-loop)
    String style="";

	Iterator allDocuments = documentList.iterator();

	while (allDocuments.hasNext()) {
		//CmsDocument currentDocument = (CmsDocument)allDocuments.next();
		CmsDocument mainDocument = (CmsDocument)allDocuments.next();

		CmsDocument currentDocument = mainDocument.getDocumentForCurrentLocale();

		// check target window for current document
		String openWin = "_self";
		if ("eLink".equals(currentDocument.getPostfixType())) {
			openWin = "_blank";
		}

		// check if all resources should be opened in a new window
		if (!currentDocument.isFolder() && !linkMode.equals("")) {
			openWin = linkMode;
		}

		// create icons for a new or modified document
		String iconNewOrModified = "";
		if (!currentDocument.isFolder()) {
		
			if (currentDocument.isNew(true)) {
				iconNewOrModified = docNew;
			}
			// 1st param: duration of "modified" mark, 2nd param: duration of "new" mark (days)
			else if (currentDocument.isModified()) {
				iconNewOrModified = docModified;
			}
		}

		// description-text in addition to document-title
		String description = "";
		if (descriptionMode.equals("text")) {               
			description = cms.property(descriptionProperty, currentDocument.getPath());
			if (description != null) { description = "\n<br />"+description; }
			else                     { description = ""; }
		}

		String docTitle = "";
		if (!currentDocument.isFolder()) {
			String postfix = CmsDocument.getPostfix(currentDocument.getPath());
			docTitle = messages.key("documentlist.link.open." + postfix, true);
			if (docTitle == null) {
				docTitle = messages.keyDefault("documentlist.link.open", "");
			}
			relAttrib = "rel=\"external\"";
		}
		
		%>
  <tr <%= style %>>
		  
<%-- ID --%>  
  	<% if (showIdColumn ) { %><td><% if (CmsStringUtil.isNotEmpty(currentDocument.getDocumentId())) { out.print(currentDocument.getDocumentId()); } %></td><% } %>
			
<%-- TITLE + DESCRIPTION --%>	
    <td><a href="<%= CmsDocumentFactory.getLink(cms, currentDocument.getPath()) %>" <%= relAttrib %> title="<%= docTitle
        %>"><%= currentDocument.getTitle() %></a><%=description%></td>
		
<%-- ICON NEW MODIFIED --%>	  
	<% if (showNewModIcon) {%>  
    	<td><%=iconNewOrModified%></td>
    <% } %>
   
   <%-- LANGUAGE --%>    
    <% if (showLangColumn) { %><td>
    	<table border="0" class="doclanguages"><tr><%
		if (!currentDocument.isFolder()) {     
			Iterator i = org.opencms.main.OpenCms.getLocaleManager().getDefaultLocales().iterator();
			while (i.hasNext()) {
				Locale loc = (Locale)i.next();
				
				CmsDocument locDoc = mainDocument.getLocalizedDocument(loc);
				if (locDoc != null) {
				    out.print("<td class=\"doclanguages\"");
					String linkTitle = messages.key("documentlist.icon.common", loc.getDisplayLanguage(cms.getRequestContext().getLocale()));
					if (locDoc.isNew(4)) {
						out.print(docNewSm);
						linkTitle = messages.key("documentlist.icon.new", loc.getDisplayLanguage(cms.getRequestContext().getLocale()));
					}
					// 1st param: duration of "modified" mark, 2nd param: duration of "new" mark (days)
					else if (locDoc.isModified(4, 4)) {
						out.print(docModifiedSm);
						linkTitle = messages.key("documentlist.icon.update", loc.getDisplayLanguage(cms.getRequestContext().getLocale()));
					}
					%>><a href="<%= CmsDocumentFactory.getLink(cms, locDoc.getPath()) %>" <%= relAttrib %> title="<%= linkTitle %>"><%= loc %></a><%
					out.print("</td>");
				} 
				
			}
		}%></tr></table></td><% } %>
    
<%-- FOLDER COLUMN --%>	
    <% if (showFolderColumn) {
          String folderLink = CmsResource.getParentFolder(currentDocument.getPath());
          String folderName = cms.property("Title", folderLink, org.opencms.file.CmsResource.getName(folderLink));
          out.println( "<td><a href=\"" +CmsDocumentFactory.getLink(cms, folderLink) +"\" title=\"" +messages.key("documentlist.link.open") +"\">" +folderName +"</a></td>" );
       } %>
<%-- TYPE //, 24, 16--%>    
  	<td>
		<a href="<%= CmsDocumentFactory.getLink(cms, currentDocument.getPath()) %>" <%= relAttrib %> title="<%= messages.key("documentlist.link.open")%>">
			<%= cms.buildDocIcon(currentDocument.getPath(), messages, resources , currentDocument.isFolder()) %>
		</a><%
		if(currentDocument.hasTypes()){
			CmsDocument typeVersion;
		    Iterator it = currentDocument.getDocumentTypes().iterator();
			
			while(it.hasNext()) {
			    typeVersion = (CmsDocument) it.next();%>
			    <a href="<%= CmsDocumentFactory.getLink(cms, typeVersion.getPath()) %>" <%= relAttrib %> title="<%= messages.key("documentlist.link.open")%>"><%= cms.buildDocIcon(typeVersion.getPath(), messages, resources , currentDocument.isFolder()) %></a>
			    <%
			}
		}
		%>
	
	</td>
<%-- SIZE --%>	  
    <td class="litte_font"><% 	if( currentDocument.isFolder()
 		 || "eLink".equals(currentDocument.getPostfixType()) ) {
            out.print(currentDocument.getSubDocuments(cms.getCmsObject())); 
		} else {
			out.print(Math.round(Math.ceil(currentDocument.getSize() / 1024.0)) +messages.key("documentlist.size.kb"));
		} %></td>
<%-- DATE MODIFIED --%>
    <% if (showDateModified ) { %><td class="litte_font"><%
		if (!currentDocument.isFolder()) {
			String versionLink = cms.getRequestContext().getUri();
			if (showFolderColumn) {
				versionLink = cms.getRequestContext().getFolderUri();
			}
			if( showHistory.equals("false") ) {
				out.print( messages.getDate(currentDocument.getDateLastModified()) );
			} else {
				out.print( "<a href=\"" +cms.link(versionLink) +"?page_type=versions&document=" + cms.getRequestContext().removeSiteRoot(currentDocument.getRootPath()) +"\" "
				          +"title=\"" +messages.key("documentlist.link.showversions") +"\" target=\"" + openWin + "\" >"
				          + messages.getDate(currentDocument.getDateLastModified()) +"</a>" );
			}
		} else {
			out.print( messages.getDate(currentDocument.getDateLastModified()) );
		} %></td>
    <% } %>
<%-- DATE CREATED --%>
    <% if (showDateCreated ) { %><td class="litte_font"><%= messages.getDate(currentDocument.getDateCreated()) %></td><% } %>
    
  </tr><%
		
		style = style.equals("") ? "class=\"alternate\"" : "";

	} //end-while
	allDocuments = null;
%>
 </tbody>
 </table>
<%

} else {
	// no documents present, show message
	String folderEmptyString = messages.key("documentlist.error.nodocuments");
	
	CmsXmlDocumentContent content = new CmsXmlDocumentContent(cms);
	String text = content.getFolderEmpty();
	if (text != null) {
		folderEmptyString = text;
	}

	out.println("<p class=\"error\">" + folderEmptyString + "</p>" );
} // end-if

%>