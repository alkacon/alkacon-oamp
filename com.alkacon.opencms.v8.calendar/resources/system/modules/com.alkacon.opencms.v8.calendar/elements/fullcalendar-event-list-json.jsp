<%@ page session="false" buffer="none" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.jsp.CmsJspActionElement" %>
<%@ page import="com.alkacon.opencms.v8.calendar.CmsCalendarDisplay" %>
<%@ page import="com.alkacon.opencms.v8.calendar.CmsCalendarEntry" %>
<%@ page import="com.alkacon.opencms.v8.calendar.I_CmsCalendarEntryData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%
Log LOG = CmsLog.getLog(this.getClass());
String pSYear = request.getParameter("sYear");
String pSMonth = request.getParameter("sMonth");
String pSDay = request.getParameter("sDay");
String pEYear = request.getParameter("eYear");
String pEMonth = request.getParameter("eMonth");
String pEDay = request.getParameter("eDay");
String pLocale = request.getParameter("__locale");
String pCalendarViewObject = request.getParameter(CmsCalendarDisplay.PARAM_CALENDARVIEWRESOURCE);
if (LOG.isDebugEnabled()) {
    LOG.debug(String.format("Requested fullcalendar-event-list-json.jsp with parameters: \n"
        + "sYear:%s, sMonth:%s, sDay:%s, eYear:%s, eMonth:%s, eDay:%s\n"
        + "__locale:%s, calendarViewObject:%s",
        pSYear, pSMonth, pSDay, pEYear, pEMonth, pEDay, pLocale, pCalendarViewObject));
}

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

CmsCalendarDisplay calendarBean = new CmsCalendarDisplay()
    .init(cms)
    .setCalendarViewFilePath(pCalendarViewObject)
    .setViewPeriod(CmsCalendarDisplay.PERIOD_MONTH)
    .setUseAjaxLinks(true)
    .initCalendarEntries()
    .addHolidays("com.alkacon.opencms.v8.calendar.holidays");

// create start and end date from request parameters
Calendar start = new GregorianCalendar(cms.getRequestContext().getLocale());

start.set(Calendar.YEAR, Integer.parseInt(pSYear));
start.set(Calendar.MONTH, Integer.parseInt(pSMonth));
start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pSDay));


Calendar end = new GregorianCalendar(cms.getRequestContext().getLocale());
end.set(Calendar.YEAR, Integer.parseInt(pEYear));
end.set(Calendar.MONTH, Integer.parseInt(pEMonth));
end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pEDay));

// get the entries for the time range
Map<Date,List<CmsCalendarEntry>> entries = calendarBean.getEntriesForDays(start, end);

// iterate through all days in time period
Iterator<Date> dayIter = entries.keySet().iterator();
boolean isFirst = true;

out.println("[");

while (dayIter.hasNext()) {
    Date key = dayIter.next();

    List<CmsCalendarEntry> dayEntries = entries.get(key);
    List<CmsCalendarEntry> reals = calendarBean.getRealEntries(dayEntries);
    List<CmsCalendarEntry> holidayEntries = calendarBean.getHolidayEntries(dayEntries);

    // output holidays entries
    if (holidayEntries.size() > 0) {
            String holidays = calendarBean.getHolidays(dayEntries);
            Calendar holDate = holidayEntries.get(0).getEntryDate().getStartDate();
            if (!isFirst) {
                out.println(",");
            } else {
                isFirst = false;
            }
            int weekdayStatus = I_CmsCalendarEntryData.WEEKDAYSTATUS_WORKDAY;
            for (int i=0; i<holidayEntries.size(); i++) {
                int test = holidayEntries.get(i).getEntryData().getWeekdayStatus();
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
        CmsCalendarEntry entry = reals.get(j);
        I_CmsCalendarEntryData entryData = entry.getEntryData();
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
        out.print(", description: \"" + CmsStringUtil.escapeJavaScript(description) + "\"");

        // type
        out.print(", type: \"" + entryData.getType() + "\"");

        // CSS class
        String variant = "";
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
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(link)) {
            // important: substitute escaped & chars to make the links work with the calendar
            link = CmsStringUtil.substitute(link, "&amp;", "&");

            link = calendarBean.getJsp().link(link);
            out.print(", url: \"" + link + "\"");
        }
        out.print("}");
    }
}
out.println("]");
%>
