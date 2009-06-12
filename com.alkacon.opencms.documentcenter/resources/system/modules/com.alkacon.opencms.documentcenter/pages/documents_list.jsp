<%@ page session="false" buffer="none"
	import="java.util.*,org.opencms.jsp.*,org.opencms.util.*,com.alkacon.opencms.documentcenter.*"%><%--
--%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--

This is the "document management main" template

--%>
<%
    // initialise Cms Action Element
    CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

    //get all properties of the file
    Map properties = cms.properties("search");

    // check type of page to display navigation and documentlist or not
    String pageType = CmsStringUtil.escapeHtml(request.getParameter("page_type"));
    if (pageType == null || "".equals(pageType)) {
        pageType = cms.property("page_type", "uri", "catalog");
       
    } 
    properties.put("page_type", pageType);

    //check if login form has to be displayed
    boolean loginForm = "true".equals(CmsStringUtil.escapeHtml(request.getParameter("__loginform")));

    // ----------- <template_head> --------------------
    String template = cms.property(
        "template",
        "search",
        "/system/modules/org.opencms.frontend.templatetwo/templates/main.jsp");
    cms.include(template, "head");
%><link type="text/css" rel="stylesheet"
	href="<cms:link>/system/modules/com.alkacon.opencms.documentcenter/resources/documents.css</cms:link>" />
<%
    out.println("<div class=\"download_onecol\">");
    //---------- </template_head> --------------------

    // ----------- <seitenkopf> --------------------
    CmsXmlDocumentContent content = new CmsXmlDocumentContent(cms);
    String header = content.getHeader();
    if (header != null) {
        out.print(header);
    }
    //---------- </seitenkopf> --------------------

    if (loginForm) {
        // *** include your form element here if necessary ***
    } else {
        if (Boolean.valueOf(cms.property("categorySearch", "search", "true")).booleanValue()) {
            // do not show search on version pages: 
            if (!"versions".equals(pageType)) {
                cms.include(
                    "/system/modules/com.alkacon.opencms.documentcenter/elements/doc_search_selection.jsp",
                    null,
                    properties);
            }
        }
        cms.include(
            "/system/modules/com.alkacon.opencms.documentcenter/elements/doc_documents.jsp",
            null,
            properties);
    }

    //---------- <seitenende> --------------------
    String footer = content.getFooter();
    if (footer != null) {
        out.print(footer);
    }
    // ---------- </seitenende> --------------------

    // ----------- <template_foot> --------------------
    out.println("</div>");
    cms.include(template, "foot");
    // ----------- <template_foot> --------------------
%>