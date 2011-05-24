<%@page buffer="none" session="false" taglibs="c,cms,fmt,fn" import="com.alkacon.opencms.formgenerator.*"%>

<cms:formatter var="content" val="value">

<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>
<c:set var="uri" value="${cms.element.sitePath}" />
<div class="box ${boxschema}">
<h4>Form</h4>
<div class="boxbody">
<%
	// initialize the form handler
	CmsFormHandler cms = CmsFormHandlerFactory.create(pageContext, request, response, (String)pageContext.getAttribute("uri"));

	cms.createForm();

%>
</div>
</div>
</cms:formatter>