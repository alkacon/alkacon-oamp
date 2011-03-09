<%@page buffer="none" session="false" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.alkacon.opencms.survey.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%! boolean debug=true; %>
<c:set var="locale">
<c:if test="${!empty cms:vfs(pageContext).context.locale}">${cms:vfs(pageContext).context.locale}</c:if>
<c:if test="${ empty cms:vfs(pageContext).context.locale}">
<cms:property name="locale" file="search" default="en" />
</c:if>
</c:set>
		
<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.survey.frontend">


<cms:contentload collector="singleFile" param="%(opencms.uri)">
<%
	// initialize the bean
	CmsFormReportingBean reporting = new CmsFormReportingBean(pageContext, request, response);
	request.setAttribute("reporting", reporting);
%>
<c:set var="reportLink"><br><a href="<cms:link>${reporting.requestContext.uri}?report=true</cms:link>"><fmt:message key="form.reportLinkText" /></a></c:set>
<cms:contentaccess var="rootContent" scope="page" />
<c:set var="detailGroup" value="${rootContent.value.SurveyReport.value.DetailGroup}" />
<c:set var="content" value="${rootContent.value.Form}" />
<c:choose>
	<c:when test="${!content.value.DataTarget.exists}">
	<fmt:message key="form.noDataTarget" />
	</c:when>
	<c:otherwise>

	<c:set var="inDetailGroup" value="${reporting.showDetail[detailGroup]}" />
	<%
	
	// initialize the form handler
	CmsFormHandler cmsF = CmsFormHandlerFactory.create(pageContext, request, response);
	boolean showFormF = cmsF.showForm();
	Object inDetailGroupObj = pageContext.getAttribute("inDetailGroup");
	Boolean inDetailGroupB = (Boolean)inDetailGroupObj;
	boolean inDetailGroup = inDetailGroupB.booleanValue();
	
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
	
		
	
				<c:choose>
					<c:when test="${content.hasValue['OptionalFormConfiguration']}">
						${content.value.OptionalFormConfiguration.value.CheckText}
						${reportLink}
					</c:when>
					<c:otherwise>
						<fmt:message key="form.checktext" />
						${reportLink}
					</c:otherwise>
				</c:choose>
			

		
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
</c:otherwise></c:choose>
</cms:contentload>
</fmt:bundle>