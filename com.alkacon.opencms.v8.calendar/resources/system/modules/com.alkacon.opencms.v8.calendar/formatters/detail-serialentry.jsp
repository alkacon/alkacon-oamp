<%@page import="com.alkacon.opencms.v8.calendar.CmsCalendarEntry"%>
<%@page import="javax.servlet.ServletRequest"%>
<%@page import="org.opencms.file.CmsResource"%>
<%@page import="org.opencms.file.CmsObject"%>
<%@page import="org.opencms.jsp.CmsJspActionElement"%>
<%@ page session="false" buffer="none" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.main.CmsLog" %>

<%
    Log LOG = CmsLog.getLog(this.getClass());
    LOG.debug("Requested detail-serialentry.jsp");

    // Initialize the CmsCalendarEntry from the detail content of the Content Page
    // and make it available through the "calEntry" page attribute
    CmsJspActionElement jsp = new CmsJspActionElement(pageContext, request, response);
    CmsObject cmsObject = jsp.getCmsObject();
    CmsResource calResource = org.opencms.jsp.util.CmsJspStandardContextBean.getInstance(request).getElement().getResource();
    CmsCalendarEntry calEntry = com.alkacon.opencms.v8.calendar.CmsSerialDateContentBean.getSerialEntryFrom(cmsObject, calResource);
    pageContext.setAttribute("calEntry", calEntry);
%>

<c:set var="vfs" value="${cms:vfs(pageContext)}" />
<c:set var="locale" value="${vfs.requestContext.locale}"/>


<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.v8.calendar.display">

    <cms:formatter var="content" val="value">
        <div>
            <%-- Headline --%>  
            <h1 class="cal_detail_headline">${cms.title}</h1>

            <%-- Dates --%>
            <p class="cal_detail_date">
                <span class="cal_detail_label"><fmt:message key="calendar.detail.date" /></span>:
                <c:set var="dateType" value="date"/>
                <c:if test="${value.Showtime.toBoolean}">
                    <c:set var="dateType" value="both"/>
                </c:if>
                
                <fmt:formatDate value="${cms:convertDate(calEntry.entryDate.startDay)}" dateStyle="long" timeStyle="short" type="${dateType}" />
                <c:if test="${not empty calEntry.entryDate}">
                        <fmt:formatDate var="shortStartDate" value="${cms:convertDate(calEntry.entryDate.startDay)}" dateStyle="short" type="date" />
                        <fmt:formatDate var="shortEndDate" value="${calEntry.entryDate.endDate.time}" dateStyle="short" type="date" />
                        <c:if test="${shortStartDate == shortEndDate && value.Showtime.toBoolean}">
                                <fmt:message key="calendar.detail.date.to" />
                                <fmt:formatDate value="${calEntry.entryDate.endDate.time}" timeStyle="short" type="time" />
                        </c:if>
                        <c:if test="${shortStartDate != shortEndDate}">
                                <fmt:message key="calendar.detail.date.to" />
                                <fmt:formatDate value="${calEntry.entryDate.endDate.time}" dateStyle="long" timeStyle="short" type="${dateType}" />
                        </c:if>
                </c:if>
            </p>

            <%-- Location --%>
            <c:if test="${not empty value.Location.isEmptyOrWhitespaceOnly}">
                <p class="cal_detail_location">
                    <span class="cal_detail_label"><fmt:message key="calendar.detail.location" /></span>: ${value.Location}
                </p>
            </c:if>

            <%-- Text --%>
            <div class="cal_detail_text">
                ${value.Text}
            </div>

            <%-- Links --%>
            <c:if test="${content.value.Links.exists}">
                <p class="cal_detail_links"><span class="cal_detail_label"><fmt:message key="calendar.detail.links" /></span>:</p>
                <ul>
                    <c:forEach var="link" items="${content.valueList.Links}">
                        <c:if test="${fn:indexOf(link.value.Uri, '/') == 0 && vfs.existsResource[link.value.Uri] || fn:indexOf(link.value.Uri, '/') != 0}">
                            <li>
                                <c:set var="linktext">${link.value.Uri}</c:set>
                                <c:if test="${link.value.Description.exists}">
                                    <c:set var="linktext">${link.value.Description}</c:set>
                                </c:if>
                                <a href="<cms:link>${link.value.Uri}</cms:link>">${linktext}</a>
                                </li>
                        </c:if>
                    </c:forEach>
                </ul>
            </c:if>

            <p>
                <a href="javascript:history.back();"><fmt:message key="calendar.detail.back" /></a>
            </p>
        </div>
    </cms:formatter>
</fmt:bundle>

