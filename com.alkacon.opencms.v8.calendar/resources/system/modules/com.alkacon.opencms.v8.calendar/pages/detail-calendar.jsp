<%@ page session="false" taglibs="c,cms,fmt,fn" buffer="none" import="java.util.*, com.alkacon.opencms.v8.calendar.*, org.opencms.jsp.*, org.opencms.util.*" %>
<%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);


%>


<cms:formatter var="content" val="value">
<div class="box ${cms.element.settings.boxschema}">


<%-- show optional text element above calendar entries --%>
<c:if test="${content.value.Text.exists}">
	${content.value.Text}
</c:if>
<c:set var="defaultView">${content.value.DefaultView}</c:set>

<div class="cal_wrapper">
<%

// get the calendar bean to display the day
CmsCalendarDisplay calendarBean = new CmsCalendarDisplay(cms);

Map params = new HashMap(1);

if (calendarBean.getViewPeriod() == -1) {
	String view = (String)pageContext.getAttribute("defaultView");
	try {
		calendarBean.setViewPeriod(Integer.parseInt(view));
		params.put(CmsCalendarDisplay.PARAM_VIEWTYPE, view);
		params.put(CmsCalendarDisplay.PARAM_MONTH, request.getParameter(CmsCalendarDisplay.PARAM_MONTH));
	} catch (Exception e) {}
}

switch (calendarBean.getViewPeriod()) {
    case CmsCalendarDisplay.PERIOD_DAY:
        cms.include("/system/modules/com.alkacon.opencms.v8.calendar/elements/dailyoverview.jsp", null, params);
        break;

    case CmsCalendarDisplay.PERIOD_WEEK:
        cms.include("/system/modules/com.alkacon.opencms.v8.calendar/elements/weeklyoverview.jsp", null, params);
        break;

    case CmsCalendarDisplay.PERIOD_MONTH:
        cms.include("/system/modules/com.alkacon.opencms.v8.calendar/elements/monthlyoverview.jsp", null, params);
        break;

    case CmsCalendarDisplay.PERIOD_YEAR:
        cms.include("/system/modules/com.alkacon.opencms.v8.calendar/elements/yearlyoverview.jsp", null, params);
        break;

    default:
    	cms.include("/system/modules/com.alkacon.opencms.v8.calendar/elements/dailyoverview.jsp", null, params);
        break;
}

out.println("</div>");

%>
	</div>
</cms:formatter>

