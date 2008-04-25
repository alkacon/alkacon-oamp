<%@ page session="false" buffer="none" import="java.util.*, com.alkacon.opencms.calendar.*, org.opencms.jsp.*, org.opencms.util.*" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%

CmsJspActionElement cms = new CmsJspActionElement(pageContext, request, response);

// get the template to use
String template = cms.property("template", "search", "");

if (CmsStringUtil.isNotEmpty(template)) {
	// include the template head
	cms.include(template, "head");
}

%>
<cms:contentload collector="singleFile" param="%(opencms.uri)" editable="true">
<cms:contentaccess var="content" scope="page" />

<%-- show optional text element above calendar entries --%>
<c:if test="${content.value.Text.exists}">
	${content.value.Text.exists}
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

out.println("</div>");

%>
</cms:contentload>
<%

if (CmsStringUtil.isNotEmpty(template)) {
	// include the template foot
	cms.include(template, "foot");
}
%>