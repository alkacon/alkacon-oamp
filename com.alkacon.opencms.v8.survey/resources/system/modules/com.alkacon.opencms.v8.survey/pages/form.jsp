<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.alkacon.opencms.v8.survey.*" %>
<%@ page import="com.alkacon.opencms.v8.formgenerator.*" %>
<%--@ page import="com.alkacon.opencms.v8.formgenerator.*" --%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%! boolean debug=true; %>
<c:set var="locale">
<c:if test="${!empty cms:vfs(pageContext).context.locale}">${cms:vfs(pageContext).context.locale}</c:if>
<c:if test="${ empty cms:vfs(pageContext).context.locale}">
<cms:property name="locale" file="search" default="en" />
</c:if>
</c:set>
<fmt:setLocale value="${locale}" />

<fmt:bundle basename="com.alkacon.opencms.v8.survey.frontend">
<cms:formatter var="rootContent">


<c:set var="uri" value="${cms.element.sitePath}" />



<%
	// initialize the bean
	CmsFormReportingBean reporting = new CmsFormReportingBean(pageContext, request, response);
	request.setAttribute("reporting", reporting);

%>

<c:set var="reportLink"><br><a href="<cms:link>${reporting.requestContext.uri}</cms:link>"><fmt:message key="form.reportLinkText" /></a></c:set>
<c:set var="detailGroup" value="${rootContent.value.SurveyReport.value.DetailGroup}" />
<c:set var="content" value="${rootContent.value.Form}" />
<c:set var="inDetailGroup" value="${reporting.showDetail[detailGroup]}" />
<%	CmsFormHandler cmsF = null; 	%>
<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<div>
			<h3><fmt:message key="survey.init.newAlkaconSurvey" /></h3>
			<h4><fmt:message key="survey.init.pleaseEdit" /></h4>
		</div>
		<%
		// initialize the form handler
		cmsF = CmsFormHandlerFactory.create(pageContext, request, response);
		%>
	</c:when>
	<c:otherwise>
		<%
		// initialize the form handler
		cmsF = CmsFormHandlerFactory.create(pageContext, request, response, (String)pageContext.getAttribute("uri"));
		%>
	</c:otherwise>
</c:choose>


<%
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
	} else {
		if (!showFormF) {
			Cookie c = new Cookie(cookieName, value);
			c.setMaxAge(365 * 24 * 60 * 60);
			response.addCookie(c);
		}
	
	
		%>
		<c:set var="addMessage"><cms:property name="webformMessage" default="/com/alkacon/opencms/v8/survey/webform" /></c:set>
		<% pageContext.setAttribute("cmsF", cmsF); %>
		<%@include file="%(link.strong:/system/modules/com.alkacon.opencms.v8.formgenerator/elements/form.jsp:03ab6d00-0628-11e1-a876-6f6871313691)" %>
		
		<%
	}




%>



</cms:formatter>
</fmt:bundle>
