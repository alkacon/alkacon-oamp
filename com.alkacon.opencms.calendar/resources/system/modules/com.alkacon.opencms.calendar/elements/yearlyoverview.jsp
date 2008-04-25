<%@ page session="false" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.calendar.*, org.opencms.util.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the calendar bean to display the year
CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.calendar.holidays");

// get the entries for the time range
Map entries = calendarBean.getEntriesForCurrentPeriod(CmsCalendarDisplay.PERIOD_YEAR);

java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy", cms.getRequestContext().getLocale());
java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MMMMM", cms.getRequestContext().getLocale());
%>

<h2><%= calendarBean.getMessages().key("calendar.headline.year", new Object[] {yearFormat.format(calendarBean.getCurrentDate().getTime())}) %></h2>
<% if (!calendarBean.hasRealEntries()) { %>
	<p><%= calendarBean.getMessages().key("calendar.entries.year.count.none") %></p>
<% } else {

// iterate through all days in time period
Iterator dayIter = entries.keySet().iterator();
int actMonth = -1;
while (dayIter.hasNext()) {
    Date key = (Date)dayIter.next();
    
    List dayEntries = (ArrayList)entries.get(key);
    List reals = calendarBean.getRealEntries(dayEntries);
    if (reals.size() > 0) {
    	
    	// calculate current month
    	Calendar currMonth = new GregorianCalendar(cms.getRequestContext().getLocale());
    	currMonth.setTime(key);
    	if (actMonth != currMonth.get(Calendar.MONTH)) {
    		actMonth = currMonth.get(Calendar.MONTH);
    		
    		// output month
    		out.println("<h3>");
    		out.println(monthFormat.format(key));
    		out.println("</h3>");
    	}
    	
    	// output day header
    	out.println("<h4>");
			out.println(calendarBean.getMessages().key("calendar.day.entry.headline", new Object[] {key}));
			String holidays = calendarBean.getHolidays(dayEntries); 
			if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(holidays)) {
				out.println(" (" + holidays + ")");
			}
    	out.println("</h4>");
    	
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
	<!-- previous month -->
	<a href="<%= calendarBean.createLink(calendarBean.getPreviousPeriod(CmsCalendarDisplay.PERIOD_YEAR)) %>">
		&lt;&lt;&nbsp;<%= calendarBean.getMessages().key("calendar.navigation.year.previous") %>
	</a>
	&nbsp;|&nbsp;
	<!-- current month -->
	<a href="<%= calendarBean.createLink(new GregorianCalendar(cms.getRequestContext().getLocale())) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.year.current") %>
	</a>
	&nbsp;|&nbsp;
	<!-- next month -->
	<a href="<%= calendarBean.createLink(calendarBean.getNextPeriod(CmsCalendarDisplay.PERIOD_YEAR)) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.year.next") %>&nbsp;&gt;&gt;
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
	<%= calendarBean.getMessages().key("calendar.view.year") %>
</div>