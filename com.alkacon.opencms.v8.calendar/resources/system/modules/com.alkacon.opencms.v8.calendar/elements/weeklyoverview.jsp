<%@ page session="false" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.v8.calendar.*, org.opencms.util.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the calendar bean to display the week
CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.v8.calendar.holidays");

// get the entries for the time range
Map entries = calendarBean.getEntriesForCurrentPeriod(CmsCalendarDisplay.PERIOD_WEEK);

// date formatter for the date in the header
java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("ww/yyyy", cms.getRequestContext().getLocale());%>

<!-- Header -->
<h2><%= calendarBean.getMessages().key("calendar.headline.week", new Object[] {df.format(calendarBean.getCurrentDate().getTime())}) %></h2>

<% if (!calendarBean.hasRealEntries()) { %>
	<p><%= calendarBean.getMessages().key("calendar.entries.week.count.none") %></p>
<% } else {

// iterate through all days in time period
Iterator dayIter = entries.keySet().iterator();
while (dayIter.hasNext()) {
    Date key = (Date)dayIter.next();
    
    List dayEntries = (ArrayList)entries.get(key);
    List reals = calendarBean.getRealEntries(dayEntries);
    if (reals.size() > 0) {
    	
    	// output day header
    	out.println("<h3>");
			out.println(calendarBean.getMessages().key("calendar.day.entry.headline", new Object[] {key}));
			String holidays = calendarBean.getHolidays(dayEntries); 
			if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(holidays)) {
				out.println(" (" + holidays + ")");
			}				
    	out.println("</h3>");
    	
    	// output list with entries
    	out.println("<ul>");
    	for (int j = 0; j < reals.size(); j++) {
	            out.println("<li style=\"padding-bottom: 5px;\">");
	            out.println(calendarBean.buildOverviewDayEntry((CmsCalendarEntry)reals.get(j)));
	            out.println("</li>");
    	}
    	out.println("</ul>");
    }
}
} %>

<!-- ***** Time Navigation ***** -->
<div class="cal_nav_time">
	<!-- previous week -->
	<a href="<%= calendarBean.createLink(calendarBean.getPreviousPeriod(CmsCalendarDisplay.PERIOD_WEEK)) %>">
		&lt;&lt;&nbsp;<%= calendarBean.getMessages().key("calendar.navigation.week.previous") %>
	</a>
	&nbsp;|&nbsp;
	<!-- current week -->
	<a href="<%= calendarBean.createLink(new GregorianCalendar(cms.getRequestContext().getLocale())) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.week.current") %>
	</a>
	&nbsp;|&nbsp;	
	<!-- next week -->
	<a href="<%= calendarBean.createLink(calendarBean.getNextPeriod(CmsCalendarDisplay.PERIOD_WEEK)) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.week.next") %>&nbsp;&gt;&gt;
	</a>
</div>
	
<!-- View links -->
<div class="cal_nav_switch">
	<%= calendarBean.getMessages().key("calendar.view") %>:

	<!-- day -->
	<a href="<%= calendarBean.createLink(calendarBean.getCurrentDate(), CmsCalendarDisplay.PERIOD_DAY) %>">
		<%= calendarBean.getMessages().key("calendar.view.day") %>
	</a>
	&nbsp;|&nbsp;
	
	<!-- week -->
	<%= calendarBean.getMessages().key("calendar.view.week") %>
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