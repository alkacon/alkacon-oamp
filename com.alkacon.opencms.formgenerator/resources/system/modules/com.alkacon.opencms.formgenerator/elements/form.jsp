<%@page buffer="none" session="false" taglibs="c,cms,fmt,fn" import="com.alkacon.opencms.formgenerator.*"%>
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>
<c:set var="uri" value="${cms.element.sitePath}" />
<%
	CmsFormHandler form = null;
%>
<c:choose>
	<c:when test="${cms.element.inMemoryOnly}">
		<div>
			<h3><c:out value="New Alkacon Webform" /></h3>
			<h4><c:out value="Please edit!" /></h4>
		</div>

		<%
		// initialize the form handler
		form = CmsFormHandlerFactory.create(pageContext, request, response);

		%>
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
		</div>
		</cms:formatter>
	</c:otherwise>
</c:choose>

