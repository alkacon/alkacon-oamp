<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
%><%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
    if ("login".equals(request.getParameter("action"))) {
        alkaconCmt.login(request.getParameter("name"), request.getParameter("password"), "Offline");
        if (alkaconCmt.getLoginException() == null) {
            out.print("ok");
        } else {
            out.print(alkaconCmt.getLoginException().getLocalizedMessage());
        }
    	return; 
    }
    pageContext.setAttribute("alkaconCmt", alkaconCmt);
	CmsCommentStringTemplateHandler templateHandler = new CmsCommentStringTemplateHandler(alkaconCmt);
	out.println(templateHandler.buildLoginHtml());
%>
