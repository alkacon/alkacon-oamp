<%@page buffer="none" session="false" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="org.opencms.file.*,org.opencms.*,org.opencms.jsp.*,java.util.*" %>

<%
request.setAttribute("surveyIsClosed", false);
CmsJspActionElement jsp = new CmsJspActionElement(pageContext, request, response);
CmsObject cms = jsp.getCmsObject();
CmsRequestContext rc = cms.getRequestContext();
String path = rc.getUri();
List props = cms.readPropertyObjects(path, false); // don't search in parent folders
CmsProperty expiry = null;
for (Iterator iter = props.iterator(); iter.hasNext();) {
	CmsProperty prop = (CmsProperty)iter.next();
	if ("expiry-date".equals(prop.getName())) {
		expiry = prop;
		break;
	}
}
if (expiry != null) {
	String value = expiry.getValue();
	long expiryTime = Long.parseLong(value);
	if (System.currentTimeMillis() > expiryTime) {
		request.setAttribute("surveyIsClosed", true);
	}
}
%>
<c:choose>
<c:when test="${param.report == 'true' || surveyIsClosed}">
<cms:include file="/system/modules/com.alkacon.opencms.v8.survey/pages/reporting.jsp" />
</c:when>
<c:otherwise>
<cms:include file="/system/modules/com.alkacon.opencms.v8.survey/pages/form.jsp" />
</c:otherwise>
</c:choose>
