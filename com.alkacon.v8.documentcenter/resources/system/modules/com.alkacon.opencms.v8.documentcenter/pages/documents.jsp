<%@ page session="false" buffer="none" import="java.util.*,org.opencms.jsp.*" %>

<%
	CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);
	
	//get all properties of the file
	Map properties = cms.properties("search");
	
	//check type of page to display documentlist or not
	String pageType = (String)org.opencms.util.CmsStringUtil.escapeHtml(request.getParameter("page_type"));
	if (pageType == null || "".equals(pageType)) {
		pageType = cms.property("page_type", "uri", "default");
	}
	properties.put("page_type", pageType);
	
	cms.include("/system/modules/com.alkacon.opencms.v8.documentcenter/pages/documents_list.jsp", null, properties);
%>
	
