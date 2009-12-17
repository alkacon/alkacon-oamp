<%@ page import="com.alkacon.opencms.mapwidget.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>
<cms:include property="template" element="head" />
<% int i = 0; %>
<cms:contentload collector="singleFile" param="%(opencms.uri)" editable="true">
    <cms:contentaccess var="content" />
    <cms:contentloop element="Place">
        <% i++; %>
        <h2><cms:contentshow element="Title" /></h2>
        <c:set var="date" ><cms:contentshow element="Date" /></c:set>
        <c:set var="date" value="${cms:convertDate(date)}" />
        <h3><fmt:formatDate pattern="dd. MMM yyyy" value="${date}"/></h3>
        <c:set var="loc" ><cms:contentshow element="Location" /></c:set>
        <div class='map {<%= new CmsGoogleMapWidgetValue((String)pageContext.getAttribute("loc")).toString()%>}'></div>
    </cms:contentloop>
</cms:contentload>

<cms:include property="template" element="foot" />