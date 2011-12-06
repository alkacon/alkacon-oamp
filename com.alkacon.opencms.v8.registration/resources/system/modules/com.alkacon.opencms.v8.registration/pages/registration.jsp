<%@page buffer="none" session="false" taglibs="c,cms,fmt,fn" import="org.opencms.i18n.*,com.alkacon.opencms.v8.formgenerator.*, com.alkacon.opencms.v8.registration.*, java.util.*, org.opencms.util.*,org.antlr.stringtemplate.*" %>
<c:set var="uri" value="${cms.element.sitePath}" />
<%
	CmsRegistrationFormHandler regForm = null;
%>
<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />

<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<%
		// initialize the form handler
		regForm = (CmsRegistrationFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsRegistrationFormHandler.class.getName(), null);
		%>
		<div>
			<h3><%= regForm.getMessages().key("registration.init.newRegistrationForm") %></h3>
			<h4><%= regForm.getMessages().key("registration.init.pleaseEdit") %></h4>
		</div>
	</c:when>
	<c:otherwise>
		<%
		// initialize the form handler
		regForm = (CmsRegistrationFormHandler)CmsFormHandlerFactory.create(pageContext, request, response, CmsRegistrationFormHandler.class.getName(), (String)pageContext.getAttribute("uri"));
		%>
	</c:otherwise>
</c:choose>


<cms:formatter var="content" val="value">
	<div>
		<%
		regForm.createForm();
		%>
	</div>
</cms:formatter>

