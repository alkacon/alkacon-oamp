<%@ page session="false" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.calendar.*, org.opencms.util.*" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the calendar bean to display the month
CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.calendar.holidays");

// get the entries for the time range
Map entries = calendarBean.getEntriesForCurrentPeriod(CmsCalendarDisplay.PERIOD_MONTH);

java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MMMMM yyyy", cms.getRequestContext().getLocale());
java.text.SimpleDateFormat dfDay = new java.text.SimpleDateFormat("EE, d. MMMM", cms.getRequestContext().getLocale());%>

<h2><%= calendarBean.getMessages().key("calendar.headline.month", new Object[] {df.format(calendarBean.getCurrentDate().getTime())}) %></h2>
<% if (!calendarBean.hasRealEntries()) { %>
	<p><%= calendarBean.getMessages().key("calendar.entries.month.count.none") %></p>
<% } else {
	// iterate through all days in time period
	Iterator dayIter = entries.keySet().iterator();

	out.println("<table cellpadding=\"2\" cellspacing=\"1\" class=\"cal_monthlist\">");
	while (dayIter.hasNext()) {
		Date key = (Date)dayIter.next();
		GregorianCalendar cal = new GregorianCalendar(cms.getRequestContext().getLocale());
		cal.setTime(key);
          
		List dayEntries = (ArrayList)entries.get(key);
		List reals = calendarBean.getRealEntries(dayEntries);
		List holidayEntries = calendarBean.getHolidayEntries(dayEntries);
		int weekdayStatus = I_CmsCalendarEntryData.WEEKDAYSTATUS_WORKDAY;
		for (int i=0; i<holidayEntries.size(); i++) {
			int test = ((CmsCalendarEntry)holidayEntries.get(i)).getEntryData().getWeekdayStatus();
			if (test > weekdayStatus) {
        			weekdayStatus = test;
    			}
		}

		out.print("<tr");
		if (cal.get(Calendar.DAY_OF_WEEK) == calendarBean.getWeekdayHoliday() || weekdayStatus == I_CmsCalendarEntryData.WEEKDAYSTATUS_HOLIDAY) {
			out.print(" class=\"holiday\"");
		} else if (cal.get(Calendar.DAY_OF_WEEK) == calendarBean.getWeekdayMaybeHoliday() || weekdayStatus == I_CmsCalendarEntryData.WEEKDAYSTATUS_MAYBEHOLIDAY) {
			out.print(" class=\"maybeholiday\"");
		}
		out.println(">");      	
		// output day header
		out.print("<td class=\"day\"><strong>");
		out.println(dfDay.format(key) + "</strong>");
		String holidays = calendarBean.getHolidays(dayEntries); 
		if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(holidays)) {
			out.println("<br/>" + holidays);
		}
		out.println("</td><td class=\"entries\">");
    	
		// output list with entries
		if (reals.size() > 0) {
			
			for (int j = 0; j < reals.size(); j++) {
				if (j < reals.size() - 1) {
					out.println("<div>");
				} else {
					out.println("<div class=\"lastentry\">");
				}
				out.println(calendarBean.buildOverviewDayEntry((CmsCalendarEntry)reals.get(j), true));
				out.println("</div>");
			}

		}
    		out.println("</td></tr>");
	}	
	out.println("</table><br/>&nbsp;");
} %>

<!-- ***** Time Navigation ***** -->
<div class="cal_nav_time">
	<!-- previous month -->
	<a href="<%= calendarBean.createLink(calendarBean.getPreviousPeriod(CmsCalendarDisplay.PERIOD_MONTH)) %>">
		&lt;&lt;&nbsp;<%= calendarBean.getMessages().key("calendar.navigation.month.previous") %>
	</a>
	&nbsp;|&nbsp;
	<!-- current month -->
	<a href="<%= calendarBean.createLink(new GregorianCalendar(cms.getRequestContext().getLocale())) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.month.current") %>
	</a>
	&nbsp;|&nbsp;
	<!-- next month -->
	<a href="<%= calendarBean.createLink(calendarBean.getNextPeriod(CmsCalendarDisplay.PERIOD_MONTH)) %>">
		<%= calendarBean.getMessages().key("calendar.navigation.month.next") %>&nbsp;&gt;&gt;
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
	<%= calendarBean.getMessages().key("calendar.view.month") %>
	&nbsp;|&nbsp;
	
	<!-- year -->
	<a href="<%= calendarBean.createLink(calendarBean.getCurrentDate(), CmsCalendarDisplay.PERIOD_YEAR) %>">
		<%= calendarBean.getMessages().key("calendar.view.year") %>
	</a>
</div>