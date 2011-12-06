<%@page buffer="none" session="false" taglibs="c,cms,fmt,fn" import="org.opencms.i18n.*,com.alkacon.opencms.v8.formgenerator.*, com.alkacon.opencms.v8.registration.*, java.util.*, org.opencms.util.*" %>
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>
<c:set var="uri" value="${cms.element.sitePath}" />
<%
	CmsRegistrationFormHandler profileForm = null;
%>
<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />

<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<%
		// initialize the form handler
		profileForm = (CmsRegistrationFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsRegistrationFormHandler.class.getName(), null);
		%>
		<div>
			<h3><%= profileForm.getMessages().key("profile.init.newProfileEditionForm") %></h3>
			<h4><%= profileForm.getMessages().key("profile.init.pleaseEdit") %></h4>
		</div>
	</c:when>
	<c:otherwise>
		<%
		// initialize the form handler
		profileForm = (CmsRegistrationFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsRegistrationFormHandler.class.getName(), (String)pageContext.getAttribute("uri"));
		%>
	</c:otherwise>
</c:choose>

<cms:formatter var="content" val="value">
	<div>
		<%
		profileForm.createForm();
		%>
	</div>
</cms:formatter>

