<%@ page session="false" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.calendar.*, org.opencms.file.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"  %>

<div>
<%

// initialize the calendar bean
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the calendar bean to display the month
CmsCalendarMonthBean calendarBean = new CmsCalendarMonthBean(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.calendar.holidays");

// set the display settings to display the calendar
calendarBean.getStyle().setStyleTable("cal_month_table");
calendarBean.getStyle().setStyleNavigation("cal_mon_nav");
calendarBean.getStyle().setStyleWeekdays("cal_mon_weekday");
calendarBean.getStyle().setStyleDay("cal_mon_day");
calendarBean.getStyle().setStyleDayCurrent("cal_mon_day_current");
calendarBean.getStyle().setStyleDayEmpty("cal_mon_day_empty");
calendarBean.getStyle().setStyleDayEntryLink("cal_mon_day_event");
calendarBean.getStyle().setStyleDayHoliday("cal_mon_day_holiday");
calendarBean.getStyle().setStyleDayMaybeHoliday("cal_mon_day_maybeholiday");

// set the URI to the view which is displayed when clicking on a day
//calendarBean.setViewUri("/flower-en/calendario/");

// show the calender monthly view for the requested locale
%><%= calendarBean.buildCalendarMonth() %>


<%
CmsObject cmsObject = cms.getCmsObject();
CmsRequestContext cmsContext = cmsObject.getRequestContext();
CmsProject Project = cmsContext.currentProject();
if (!Project.isOnlineProject()) {
%>
	<cms:contentload collector="allInFolderDateReleasedDesc" param="/.content/calendar-entry/entry_%(number).html|alkacon-cal-entry|1" editable="true"><div>
	Click + for new calendar entry
	</div></cms:contentload>
	
	<cms:contentload collector="allInFolderDateReleasedDesc" param="/.content/calendar-serial/serial_%(number).html|alkacon-cal-serial|1" editable="true"><div>
	Click + for new serial calendar entry
	</div></cms:contentload>	
<%
} // end if (!(Project.isOnlineProject()))
%>


</div>