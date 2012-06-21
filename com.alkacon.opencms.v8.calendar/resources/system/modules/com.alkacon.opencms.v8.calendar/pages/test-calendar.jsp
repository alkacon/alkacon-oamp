<%@ page session="false" taglibs="c,cms,fmt,fn" buffer="none" import="java.util.*, com.alkacon.opencms.v8.calendar.*, org.opencms.jsp.*, org.opencms.util.*" %>
<%
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);
%>


<cms:formatter var="content" val="value">
<%--
<div class="box ${cms.element.settings.boxschema}">
--&>

<%-- show optional text element above calendar entries --%>
<c:if test="${content.value.Title.exists}">
	${content.value.Title}
</c:if>
<c:set var="defaultView">${content.value.DefaultView}</c:set>

<div class="cal_wrapper">
<%


Calendar cal = new GregorianCalendar(cms.getRequestContext().getLocale());

int currDay = cal.get(Calendar.DATE);
int currMonth = cal.get(Calendar.MONTH);
int currYear = cal.get(Calendar.YEAR);

String pDay = request.getParameter("calDay");
String pMonth = request.getParameter("calMonth");
String pYear = request.getParameter("calYear");

if (CmsStringUtil.isNotEmpty(pDay)) {
	currDay = Integer.parseInt(pDay);
}

if (CmsStringUtil.isNotEmpty(pMonth)) {
	currMonth = Integer.parseInt(pMonth);
}

if (CmsStringUtil.isNotEmpty(pYear)) {
	currYear = Integer.parseInt(pYear);
}

// Calendar documentation: http://arshaw.com/fullcalendar/
%>
<link rel='stylesheet' type='text/css' href="<cms:link>/system/modules/com.alkacon.opencms.v8.calendar/resources/fullcalendar.css</cms:link>" />
<script type="text/javascript" src="<cms:link>/system/modules/com.alkacon.opencms.v8.calendar/resources/fullcalendar.min.js</cms:link>"></script>
<script type="text/javascript" src="<cms:link>/system/modules/com.alkacon.opencms.v8.calendar/resources/jquery.qtip.min.js</cms:link>"></script>
<script type="text/javascript">



	$(document).ready(function() {
	
		var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();
		
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			editable: true,
			events: [
				{
					title: 'All Day Event',
					start: new Date(y, m, 1)
				},
				{
					title: 'Long Event',
					start: new Date(y, m, d-5),
					end: new Date(y, m, d-2)
				},
				{
					id: 999,
					title: 'Repeating Event',
					start: new Date(y, m, d-3, 16, 0),
					allDay: false
				},
				{
					id: 999,
					title: 'Repeating Event',
					start: new Date(y, m, d+4, 16, 0),
					allDay: false
				},
				{
					title: 'Meeting',
					start: new Date(y, m, d, 10, 30),
					allDay: false
				},
				{
					title: 'Lunch',
					start: new Date(y, m, d, 12, 0),
					end: new Date(y, m, d, 14, 0),
					allDay: false
				},
				{
					title: 'Birthday Party',
					start: new Date(y, m, d+1, 19, 0),
					end: new Date(y, m, d+1, 22, 30),
					allDay: false
				},
				{
					title: 'Click for Google',
					start: new Date(y, m, 28),
					end: new Date(y, m, 29),
					url: 'http://google.com/'
				}
			]
		});
		
	});

</script>

<div id='calendar'></div>

<%--
	</div>
--%>	
	</div>
</cms:formatter>