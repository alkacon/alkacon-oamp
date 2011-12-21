<%@page buffer="none" session="false" taglibs="c,cms,fmt,fn" import="com.alkacon.opencms.v8.formgenerator.*"%>
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>
<c:set var="uri" value="${cms.element.sitePath}" />
<%
	CmsFormHandler form = null;
%>
<c:set var="locale" value="${cms:vfs(pageContext).context.locale}" />

<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<%
		// initialize the form handler
		form = CmsFormHandlerFactory.create(pageContext, request, response);
		%>
		<div>
			<h3><%= form.getMessages().key("webform.init.newAlkaconWebform") %></h3>
			<h4><%= form.getMessages().key("webform.init.pleaseEdit") %></h4>
		</div>
	</c:when>
	<c:otherwise>
		<%
		// initialize the form handler
		form = CmsFormHandlerFactory.create(pageContext, request, response, (String)pageContext.getAttribute("uri"));
		%>
	</c:otherwise>
</c:choose>

<%
boolean dd = form.downloadData();
pageContext.setAttribute("dd", dd);
%>

<c:choose>
	<c:when test="${dd}">
		<%
		form.createForm();
		%>
	</c:when>
	<c:otherwise>
		<cms:formatter var="content" val="value">
		<div>
		<%
		form.createForm();
		%>
		<br style="clear:both;" />
		</div>
		</cms:formatter>
	</c:otherwise>
</c:choose>

