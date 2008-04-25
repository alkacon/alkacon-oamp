<%@ page session="false" import="com.alkacon.opencms.calendar.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- This bean is needed to determine serial date changes and the eventually changed values --%>
<jsp:useBean id="bean" class="com.alkacon.opencms.calendar.CmsSerialDateContentBean" scope="request"><%

	// initialise bean
	bean.init(pageContext, request, response);

%></jsp:useBean>

<cms:include property="template" element="head" />

<c:set var="vfs" value="${cms:vfs(pageContext)}" />
<c:set var="locale" value="${vfs.requestContext.locale}"/>

<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.calendar.display">

<cms:contentload collector="singleFile" param="%(opencms.uri)" editable="true">
<cms:contentaccess var="content" scope="page" />

<%-- Headline --%>  
<h1 class="cal_detail_headline">${bean.valueTitle}</h1>

<%-- Dates --%>
<p class="cal_detail_date">
	<span class="cal_detail_label"><fmt:message key="calendar.detail.date" /></span>:
	<c:set var="dateType" value="date"/>
	<c:if test="${bean.showTime == true}">
		<c:set var="dateType" value="both"/>
	</c:if>
	<fmt:formatDate value="${bean.startDate}" dateStyle="long" timeStyle="short" type="${dateType}" />
	<c:set var="starttime"><fmt:formatDate value="${bean.startDate}" dateStyle="short" type="time" /></c:set>
	<c:set var="endtime"><fmt:formatDate value="${bean.endDate}" dateStyle="short" type="time" /></c:set>
	<c:if test="${bean.sameDate && starttime != endtime && bean.showTime == true}">
		<fmt:message key="calendar.detail.date.to" />
		<fmt:formatDate value="${bean.endDate}" timeStyle="short" type="time" />
	</c:if>
	<c:if test="${!bean.sameDate}">
		<fmt:message key="calendar.detail.date.to" />
		<fmt:formatDate value="${bean.endDate}" dateStyle="long" timeStyle="short" type="${dateType}" />
	</c:if>
</p>

<%-- Location --%>
<c:set var="location">${bean.valueLocation}</c:set>
<c:if test="${!empty location}">
<p class="cal_detail_location">
	<span class="cal_detail_label"><fmt:message key="calendar.detail.location" /></span>: ${location}
</p>
</c:if>

<%-- Text --%>
<div class="cal_detail_text">
	${bean.valueText}
</div>

<%-- Links --%>
<c:if test="${content.value.Links.exists}">
	<p class="cal_detail_links"><span class="cal_detail_label"><fmt:message key="calendar.detail.links" /></span>:</p><ul>
	<c:forEach var="link" items="${content.valueList.Links}">
		<c:if test="${fn:indexOf(link.value.Uri, '/') == 0 && vfs.existsResource[link.value.Uri] || fn:indexOf(link.value.Uri, '/') != 0}">
			<li>
				<c:set var="linktext">${link.value.Uri}</c:set>
				<c:if test="${link.value.Description.exists}">
					<c:set var="linktext">${link.value.Description}</c:set>
				</c:if>
				<a href="<cms:link>${link.value.Uri}</cms:link>">${linktext}</a>
			</li>
		</c:if>
	</c:forEach>
	</ul>
</c:if>

<p>
	<a href="javascript:history.back();"><fmt:message key="calendar.detail.back" /></a>
</p>

</cms:contentload>

</fmt:bundle>

<cms:include property="template" element="foot" /> 