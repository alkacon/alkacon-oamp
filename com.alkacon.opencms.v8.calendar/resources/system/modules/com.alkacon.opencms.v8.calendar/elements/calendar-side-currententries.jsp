<%@ page session="false" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.v8.calendar.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the number of entries to show from the request parameter "count", use 3 as default
String paramCount = request.getParameter("count");
int count = 3;
if (paramCount != null) {
	try {
		count = Integer.parseInt(paramCount);
	} catch (Exception e) {}
}

// get the calendar bean to display the day
CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// get most current entries
List entries = calendarBean.getMostCurrentEntries(count);

if (entries.size() > 0) {

	// set the list to a context variable
	pageContext.setAttribute("entries", entries);

%>
<c:set var="vfs" value="${cms:vfs(pageContext)}" />
<c:set var="locale" value="${vfs.requestContext.locale}"/>

<fmt:setLocale value="${locale}" />
<div class="cal_side_entry_list">

<c:forEach var="entry" items="${entries}">
	<c:set var="link">${entry.entryData.detailUri}</c:set>
	<c:if test="${entry.entryDate.serialDate == true}">
		<c:set var="link">${link}?calDat=${entry.entryDate.startDate.timeInMillis}</c:set>
	</c:if>
	<div class="cal_side_entry_wrapper">
		<div class="cal_side_entry_head"><a href="<cms:link>${link}</cms:link>">${entry.entryData.title}</a></div>
		<div class="cal_side_entry_teaser">
			<c:set var="dateType" value="date"/>
			<c:if test="${entry.entryData.showTime == true}">
				<c:set var="dateType" value="both"/>
			</c:if>
			<span class="cal_side_entry_date"><fmt:formatDate value="${entry.entryDate.startDate.time}" dateStyle="short" timeStyle="short" type="${dateType}" /></span> -
			${entry.entryData.description}
		</div>
	</div>
</c:forEach>

</div><%
}
%>