<%@ page session="false" buffer="none" taglibs="c,cms,fmt"
	import="java.util.*,
		org.opencms.file.*,
		org.opencms.jsp.*,
		org.opencms.util.*,
		com.alkacon.opencms.v8.documentcenter.*
"%><%--

This is the "document management" formatter

--%><cms:formatter var="content" val="value"><div class="paragraph"><div class="download_onecol"><%--
--%><c:set var="startfolder">${value.Folder}</c:set><%--
--%><c:set var="showcategories">${value.ShowCategories}</c:set><%--
--%><c:set var="showsearch">${value.ShowSearch}</c:set><%--
--%><c:choose><%--
--%><c:when test="${cms.element.inMemoryOnly || empty startfolder}"><%--
--%><fmt:setLocale value="${cms.locale}" /><fmt:bundle basename="com.alkacon.opencms.v8.documentcenter.workplace"><fmt:message key="warning.v8.documentcenter.folder" /></fmt:bundle><%--
--%></c:when><%--
--%><c:otherwise><%--
--%><%
	// initialize document frontend
	CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext, request, response);

	if (request.getAttribute(CmsDocumentFrontend.ATTR_STARTPATH) == null) {
		String startPath = (String)pageContext.getAttribute("startfolder");
		CmsShowDocumentCenter.setDocumentCenterAttributes(startPath, "/", request);
	}

	if (request.getAttribute(CmsDocumentFrontend.ATTR_DISCLAIMER) != null) {
		// show disclaimer
		cms.include("%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/page_disclaimer_code.jsp:9a69767b-fa2d-11e0-9654-c9a60a7588dd)");
	} else {
	
		//get all properties of the file
		Map properties = cms.properties("search");
		
		// check type of page to display navigation and documentlist or not
		String defaultPageType = "default";
		String inDocPath = (String)request.getAttribute(CmsDocumentFrontend.ATTR_PATHPART);
		boolean showCategories = Boolean.valueOf((String)pageContext.getAttribute("showcategories")).booleanValue();
		if (showCategories && (CmsStringUtil.isEmptyOrWhitespaceOnly(inDocPath) || "/".equals(inDocPath))) {
			defaultPageType = "catalog";
		}
		
		String pageType = CmsStringUtil.escapeHtml(request.getParameter("page_type"));
		if (CmsStringUtil.isEmptyOrWhitespaceOnly(pageType)) {
			pageType = defaultPageType;
		} 
		properties.put("page_type", pageType);
		
		//check if login form has to be displayed
		boolean loginForm = "true".equals(CmsStringUtil.escapeHtml(request.getParameter("__loginform")));
		
		// ----------- <header text> --------------------
		CmsXmlDocumentContent content = new CmsXmlDocumentContent(cms);
		String header = content.getHeader();
		if (header != null) {
			out.print(header);
		}
		//---------- </header text> --------------------
		
		if (loginForm) {
			// *** include your form element here if necessary ***
		} else {
			if (Boolean.valueOf(cms.property("categorySearch", "search", "true")).booleanValue() && Boolean.valueOf((String)pageContext.getAttribute("showsearch")).booleanValue()) {
				// do not show search on version pages: 
				if (!"versions".equals(pageType) && !"newdocuments".equals(pageType)) {
					cms.include(
					"%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/doc_search_selection.jsp:9a5419a6-fa2d-11e0-9654-c9a60a7588dd)",
					null,
					properties);
				}
			}
			cms.include(
			"%(link.weak:/system/modules/com.alkacon.opencms.v8.documentcenter/elements/doc_documents.jsp:9a4a7caa-fa2d-11e0-9654-c9a60a7588dd)",
			null,
			properties);
		}
		
		//---------- <footer text> --------------------
		String footer = content.getFooter();
		if (footer != null) {
			out.print(footer);
		}
		// ---------- </footer text> --------------------
	}
	%><%--
--%></c:otherwise><%--
--%></c:choose><%--
--%></div></div></cms:formatter>