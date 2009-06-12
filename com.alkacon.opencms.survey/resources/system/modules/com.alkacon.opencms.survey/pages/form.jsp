<%@page buffer="none" session="false" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%

// initialize the form handler
CmsFormHandler cmsF = new CmsFormHandler(pageContext, request, response);
boolean showFormF = cmsF.showForm();

String value = String.valueOf(cmsF.getFormConfiguration().getFormId().hashCode());

String cookieName = "Alkacon_OAMP_" + cmsF.getFormConfiguration().getFormId();
Cookie cookies [] = request.getCookies();

Cookie myCookie = null;
if (cookies != null) {
	for (int j = 0; j < cookies.length; j++) {
		if (cookies [j].getName().equals (cookieName)) {
			myCookie = cookies[j];
			break;
		}
	}
}

if ((myCookie != null) &&  (myCookie.getValue().equals(value))) {
	String template = "";
	template = cmsF.property("template", "search");
	cmsF.include(template, "head");
	%>
		<cms:contentload collector="singleFile" param="%(opencms.uri)">
			<cms:contentaccess var="content" scope="page" />
			${content.value['OptionalFormConfiguration'].value['CheckText']}
		</cms:contentload>
	<%
	cmsF.include(template, "foot");
} else {
	if (!showFormF) {
		Cookie c = new Cookie(cookieName, value);
		c.setMaxAge(365 * 24 * 60 * 60);
		response.addCookie(c);
	}


	%>
	<c:set var="addMessage"><cms:property name="webformMessage" default="/com/alkacon/opencms/survey/webform" /></c:set>
	<% pageContext.setAttribute("cmsF", cmsF); %>
	<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.formgenerator/pages/form.jsp:a424bd7e-11b7-11db-91cd-fdbae480baca)" %>
	<%
}

%>
