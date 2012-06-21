<%@ page session="false" taglibs="c,cms,fmt,fn" buffer="none" import="java.util.*, org.opencms.jsp.*, com.alkacon.opencms.v8.calendar.*" %>
<c:set var="boxschema"><cms:elementsetting name="boxschema" default="box_schema1" /></c:set>

<c:set var="containerUri" value="${cms:vfs(pageContext).context.uri}" />

		<cms:formatter var="content" val="value">
<div class="box ${cms.element.settings.boxschema}">
	<%-- Title of the article --%>
	<c:if test="${cms.element.settings.hidetitle ne 'true'}">
		<h4>${value.Title}</h4>
	</c:if>	
	
	<div class="boxbody">


<%

// initialize the calendar bean
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the calendar bean to display the month
CmsCalendarMonthBean calendarBean = new CmsCalendarMonthBean(cms);

// initialize the calendar entries to show
calendarBean.initCalendarEntries();

// set the Uri of the calendar to the Uri of the container page
calendarBean.setViewUri((String)pageContext.getAttribute("containerUri"));

// add the holiday entries
calendarBean.addHolidays("com.alkacon.opencms.v8.calendar.holidays");

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

// show the calender monthly view for the requested locale
%><%= calendarBean.buildCalendarMonth() %>

		</div>
		</div>
		</cms:formatter>
