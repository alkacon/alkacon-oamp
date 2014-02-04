<%@ page session="false" buffer="none" import="java.util.*, com.alkacon.opencms.calendar.*, org.opencms.jsp.*, org.opencms.util.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="cal_wrapper">

<%
CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);
%>

<%--
<cms:contentload collector="singleFile" param="%(property.calendar.uri)" editable="true">
<cms:contentaccess var="content" scope="page" />

<c:if test="${content.value.Text.exists}">
	${content.value.Text}
</c:if>
<c:set var="defaultView">${content.value.DefaultView}</c:set>
--%>

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
        cms.include("/system/modules/com.alkacon.opencms.calendar/elements/dailyoverview.jsp", null, params);
        break;

    case CmsCalendarDisplay.PERIOD_WEEK:
        cms.include("/system/modules/com.alkacon.opencms.calendar/elements/weeklyoverview.jsp", null, params);
        break;

    case CmsCalendarDisplay.PERIOD_MONTH:
        cms.include("/system/modules/com.alkacon.opencms.calendar/elements/monthlyoverview.jsp", null, params);
        break;

    case CmsCalendarDisplay.PERIOD_YEAR:
        cms.include("/system/modules/com.alkacon.opencms.calendar/elements/yearlyoverview.jsp", null, params);
        break;

    default:
    	cms.include("/system/modules/com.alkacon.opencms.calendar/elements/dailyoverview.jsp", null, params);
        break;
}
%>
<%--
</cms:contentload>
--%>
</div>