<%@ page session="false" taglibs="c,cms,fmt,fn" buffer="none" import="java.util.*, com.alkacon.opencms.v8.calendar.*, org.opencms.jsp.*, org.opencms.util.*" %>
<%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

// set view period (not really necessary for this view type)
calendarBean.setViewPeriod(CmsCalendarDisplay.PERIOD_MONTH);

// set flag to use AJAX links
calendarBean.setUseAjaxLinks(true);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.v8.calendar.holidays");

cms.getRequestContext().setUri("/index.html");
// create start and end date from request parameters
Calendar start = new GregorianCalendar(cms.getRequestContext().getLocale());

start.set(Calendar.YEAR, Integer.parseInt(request.getParameter("sYear")));
start.set(Calendar.MONTH, Integer.parseInt(request.getParameter("sMonth")));
start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(request.getParameter("sDay")));


Calendar end = new GregorianCalendar(cms.getRequestContext().getLocale());
end.set(Calendar.YEAR, Integer.parseInt(request.getParameter("eYear")));
end.set(Calendar.MONTH, Integer.parseInt(request.getParameter("eMonth")));
end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(request.getParameter("eDay")));


// get the entries for the time range
Map entries = calendarBean.getEntriesForDays(start, end);


// iterate through all days in time period
Iterator dayIter = entries.keySet().iterator();
boolean isFirst = true;

out.println("[");

while (dayIter.hasNext()) {
	Date key = (Date)dayIter.next();

	List dayEntries = (ArrayList)entries.get(key);
	List reals = calendarBean.getRealEntries(dayEntries);
	List holidayEntries = calendarBean.getHolidayEntries(dayEntries);
	
	// output holidays entries
	if (holidayEntries.size() > 0) {
		String holidays = calendarBean.getHolidays(dayEntries);
		Calendar holDate = ((CmsCalendarEntry)holidayEntries.get(0)).getEntryDate().getStartDate();
		if (!isFirst) {
			out.println(",");
		} else {
			isFirst = false;
		}
		int weekdayStatus = I_CmsCalendarEntryData.WEEKDAYSTATUS_WORKDAY;
		for (int i=0; i<holidayEntries.size(); i++) {
			int test = ((CmsCalendarEntry)holidayEntries.get(i)).getEntryData().getWeekdayStatus();
			if (test > weekdayStatus) {
				weekdayStatus = test;
			}
		}
		String cssClass = "maybeholiday";
		if (weekdayStatus == I_CmsCalendarEntryData.WEEKDAYSTATUS_HOLIDAY) {
			cssClass = "holiday";
		}
		holidays = CmsStringUtil.escapeJavaScript(holidays);
		out.print("{ title: \"" + holidays + "\", description: \"" + holidays + "\", allDay: true, type: \"holiday\", className: \"cal" + cssClass + "\", start: \"" + String.format("%1$tF", holDate) + "\"}");
	}

	// output list with entries
	for (int j = 0; j < reals.size(); j++) {
		CmsCalendarEntry entry = (CmsCalendarEntry)reals.get(j);
		CmsCalendarEntryData entryData = (CmsCalendarEntryData)entry.getEntryData();
		if (!isFirst) {
			out.println(",");
		} else {
			isFirst = false;
		}

		// the title of the item

		String title = entryData.getTitle();
		out.print("{ title: \"" + CmsStringUtil.escapeJavaScript(title) + "\"");
		out.print(", description: \"" + CmsStringUtil.escapeJavaScript(title) + "\"");

		// description
		String description = "<strong>" + entryData.getTitle() + "</strong>";
%>
<%
		out.print(", description: \"" + CmsStringUtil.escapeJavaScript(description) + "\"");

		// type
		out.print(", type: \"" + entryData.getType() + "\"");
		
		// CSS class
		String variant = "";
%>
<%
		out.print(", className: \"cal" + entryData.getType() + variant + "\"");

		// no time is shown
		if (!entryData.isShowTime()) {
			out.print(", allDay: true");
		}

		// start and end date
		out.print(", start: \"" + String.format("%1$tFT%1$tT", entry.getEntryDate().getStartDate()) + "\"");
		out.print(", end: \"" + String.format("%1$tFT%1$tT", entry.getEntryDate().getEndDate()) + "\"");

		// link to detail page
		String link = entryData.getDetailUri();
%>
<%
		if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(link)) {
			// important: substitute escaped & chars to make the links work with the calendar
			link = CmsStringUtil.substitute(link, "&amp;", "&");

			link = calendarBean.getJsp().link(link);
%>
<%
			out.print(", url: \"" + link + "\"");
		}
%>
<%
		
		out.print("}");
	}
}	

out.println("]");

%>