<%@ page session="false" buffer="none" taglibs="c,cms" import="java.util.*, org.opencms.util.*, org.opencms.jsp.*" %><%--
--%><c:set var="loc" value="${cms.locale}" /><%

Calendar cal = new GregorianCalendar((Locale)pageContext.getAttribute("loc"));

int currMonth = cal.get(Calendar.MONTH);
int currYear = cal.get(Calendar.YEAR);

int dispMonth = currMonth;
int dispYear = currYear;

String pMonth = request.getParameter("calMonth");
String pYear = request.getParameter("calYear");

if (CmsStringUtil.isNotEmpty(pMonth)) {
	dispMonth = Integer.parseInt(pMonth);
}

if (CmsStringUtil.isNotEmpty(pYear)) {
	dispYear = Integer.parseInt(pYear);
}

%>
<div>
<div id="calendarside" style="text-align: center;">
<cms:formatter var="content" val="value">
	<c:if test="${cms.edited || cms.element.inMemoryOnly}">
		<h2>Calendar</h2>
		<p>The calendar has been edited, please reload!</p>
	</c:if>
</cms:formatter>
</div>

<c:if test="${!cms.edited && !cms.element.inMemoryOnly}">
	<link rel="stylesheet" type="text/css" href="<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.calendar/resources/calendar.css:e03db19d-20be-11e1-9585-27f2fbbdf126)?config=${cms.element.sitePath}&__locale=${cms.locale}&site=${cms.requestContext.siteRoot}&prefix=CSS</cms:link>"/>
	<script type="text/javascript">
	
	// store current date
	var currSideMonth = <%= currMonth %>;
	var currSideYear = <%= currYear %>;
	
	var dispSideMonth = <%= dispMonth %>;
	var dispSideYear = <%= dispYear %>;
	
	var calSideAjaxJsp = "<cms:link>/system/modules/com.alkacon.opencms.v8.calendar/formatters/side_ajax.jsp</cms:link>";
	
	var calLoadingImage = "<img src=\"<cms:link>/system/modules/com.alkacon.opencms.v8.calendar/resources/load.gif</cms:link>\" alt=\"\" style=\"padding: 24px 8px;\" />";
	
	function calendarSidePagination(direction) {
	
		if (direction == "prev") {
			if (dispSideMonth == 0) {
				dispSideMonth = 11;
				dispSideYear -= 1;
			} else {
				dispSideMonth -= 1;
			}
		} else if (direction == "next") {
			if (dispSideMonth == 11) {
				dispSideMonth = 0;
				dispSideYear += 1;
			} else {
				dispSideMonth += 1;
			}
		} else if (direction == "current") {
			dispSideMonth = currSideMonth;
			dispSideYear = currSideYear;
		} else {
			$("#calendarside").html(calLoadingImage);
		}
		
		$.post(calSideAjaxJsp, { uri: "${cms.requestContext.uri}", curi: "${cms.element.sitePath}", __locale: "${cms.locale}", calMonth: dispSideMonth, calYear: dispSideYear }, function(data){ showSideCalendar(data); });
	}
	
	function showSideCalendar(data) {
		$("#calendarside").html(data);
	}
	
	calendarSidePagination("same");
	</script>
</c:if>
</div>