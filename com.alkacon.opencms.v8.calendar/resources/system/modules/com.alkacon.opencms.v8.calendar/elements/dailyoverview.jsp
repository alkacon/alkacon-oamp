<%@ page session="false" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.v8.calendar.*, org.opencms.util.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the calendar bean to display the day
CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.v8.calendar.holidays");

// get the entries for the time range
Map entries = calendarBean.getEntriesForCurrentPeriod(CmsCalendarDisplay.PERIOD_DAY);

Iterator dayIter = entries.keySet().iterator();
Date key = (Date)dayIter.next();
List dayEntries = (ArrayList)entries.get(key); %>

<h2>
	<%= calendarBean.getMessages().key("calendar.headline.day", new Object[] {calendarBean.getCurrentDate().getTime()}) %>
	<%
		String holidays = calendarBean.getHolidays(dayEntries); 
		if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(holidays)) {
			out.println(" (" + holidays + ")");
		}
	%>
</h2>

<% if (!calendarBean.hasRealEntries()) { %>
	<p><%= calendarBean.getMessages().key("calendar.entries.day.count.none") %></p>
<% } else {
    
	List reals = calendarBean.getRealEntries(dayEntries);
	if (reals.size() > 0) {
		
		// output list with entries
		out.println("<ul>");
		for (int j = 0; j < reals.size(); j++) {
			out.println("<li style=\"padding-bottom: 5px;\">");
			out.println(calendarBean.buildOverviewDayEntry((CmsCalendarEntry)reals.get(j)));
			out.println("</li>");
		}
		out.println("</ul>");
	}
} %>

<!-- ***** Time Navigation ***** -->
<div class="cal_nav_time">
	<!-- previous day -->
	<a href="<%= calendarBean.createLink(calendarBean.getPreviousPeriod(CmsCalendarDisplay.PERIOD_DAY)) %>">
		&lt;&lt;&nbsp;<%= calendarBean.getMessages().key("calendar.navigation.day.previous") %>
	</a>
	&nbsp;|&nbsp;
	<!-- current day -->
	<a href="<%= calendarBean.createLink(new GregorianCalendar(cms.getRequestContext().getLocale())) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.day.current") %>
	</a>
	&nbsp;|&nbsp;	
	<!-- next day -->
	<a href="<%= calendarBean.createLink(calendarBean.getNextPeriod(CmsCalendarDisplay.PERIOD_DAY)) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.day.next") %>&nbsp;&gt;&gt;
	</a>
</div>

<!-- ***** View Links ***** -->
<div class="cal_nav_switch">
	<%= calendarBean.getMessages().key("calendar.view") %>:

	<!-- day -->
	<%= calendarBean.getMessages().key("calendar.view.day") %>
	&nbsp;|&nbsp;

	<!-- week -->
	<a href="<%= calendarBean.createLink(calendarBean.getCurrentDate(), CmsCalendarDisplay.PERIOD_WEEK) %>">
		<%= calendarBean.getMessages().key("calendar.view.week") %>
	</a>
	&nbsp;|&nbsp;

	<!-- month -->
	<a href="<%= calendarBean.createLink(calendarBean.getCurrentDate(), CmsCalendarDisplay.PERIOD_MONTH) %>">
		<%= calendarBean.getMessages().key("calendar.view.month") %>
	</a>
	&nbsp;|&nbsp;

	<!-- year -->
	<a href="<%= calendarBean.createLink(calendarBean.getCurrentDate(), CmsCalendarDisplay.PERIOD_YEAR) %>">
		<%= calendarBean.getMessages().key("calendar.view.year") %>
	</a>
</div>