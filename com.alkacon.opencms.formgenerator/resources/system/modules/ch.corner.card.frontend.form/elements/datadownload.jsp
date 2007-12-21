<%@page buffer="none" session="false"
	import="org.opencms.i18n.*,ch.corner.card.frontend.form.database.export.*,org.opencms.flex.CmsFlexController,ch.corner.card.frontend.form.*,ch.corner.card.frontend.base.*,java.util.*,org.opencms.util.*,org.opencms.widgets.*"%><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%
    // initialize the form handler
    CmsFormHandler cms = new CmsFormHandler(pageContext, request, response);
    boolean isOffline = !cms.getRequestContext().currentProject().isOnlineProject();
    pageContext.setAttribute("offline", new Boolean(isOffline));

    // get the localized messages to create the form
    CmsMessages messages = cms.getMessages();
    CmsMessages calendarMessages = new CmsMessages(
        org.opencms.workplace.Messages.get().getBundleName(),
        cms.getRequestContext().getLocale());

    // get the configured form elements
    CmsForm form = cms.getFormConfiguration();

    if (!cms.downloadData()) {
        // create the form head
%><%=org.opencms.widgets.CmsCalendarWidget.calendarIncludes(cms.getRequestContext().getLocale())%>
<style type="text/css">
.ip_text_calendar {
	padding: 1px 2px 1px 2px;
	border-top: 1px solid #868686;
	border-right: 1px solid #ccc;
	border-bottom: 1px solid #ccc;
	border-left: 1px solid #868686;
	background-color: #fff;
	background-image: url(../img/bgd_input.gif);
	background-repeat: no-repeat;
	width: 140px;
}

/* - internet explorer 6 specific definitions (hacks) - */
* html .ip_text {
	padding: 2px;
}
</style>
<%
    } else {
%>
<%
    CmsCvsExportBean exportBean = new CmsCvsExportBean(cms);

        // Preparing the date values for the export bean: 
        Date startDate;
        Date endDate;
        String startDateStr = request.getParameter(CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START);
        String endDateStr = request.getParameter(CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END);
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(startDateStr)) {
            long startDateLong = CmsCalendarWidget.getCalendarDate(calendarMessages, startDateStr, true);
            startDate = new Date(startDateLong);
            exportBean.setStartTime(startDate);
        }
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(endDateStr)) {
            long endDateLong = CmsCalendarWidget.getCalendarDate(calendarMessages, endDateStr, true);
            endDate = new Date(endDateLong);
            exportBean.setEndTime(endDate);
        }

        CmsFlexController controller = CmsFlexController.getController(request);
        HttpServletResponse res = controller.getTopResponse();
        res.setContentType("text/csv");
        // try "inline" instead "attachment" and ie will open within browser window. 
        res.addHeader("Content-Disposition", "attachment; filename=" + form.getFormId() + ".csv;");
        res.addHeader("Content-Transfer-Encoding", "binary");
        ServletOutputStream output = res.getOutputStream();
        try {	
          output.write(exportBean.exportData().getBytes());
        } finally {
          if (output != null) {
            output.flush();
            output.close();
          }
        }
    }

    if (!cms.downloadData()) {
%><form name="emailform" action="<%= cms.link(cms.getRequestContext().getUri()) %>" method="post" class="formarea">
<div class="formgroup bgdgrey linetop linewhite">
 <div class="formdescription">
  <label for="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>"><%=messages.key("form.label.dataexport.from")%></label>
 </div>
 <div class="formelements" >
  <input id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>" type="text" value="" name="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>" class="ip_text_calendar" /> 
  <a href="#" class="button" title="Choose date" id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>.calendar"><span unselectable="on" class="norm" onmouseover="className='over'" onmouseout="className='norm'" onmousedown="className='push'" onmouseup="className='over'"><img class="button" src="<%= org.opencms.workplace.CmsWorkplace.getSkinUri() %>buttons/calendar.png" alt="Choose date"></span></a>
 </div>
 <div class="clearer"></div>
 <div class="formdescription">
  <label for="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>"><%=messages.key("form.label.dataexport.to")%></label>
 </div>
 <div class="formelements" >
  <input id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>" type="text" value="" name="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>" class="ip_text_calendar" /> 
  <a href="#" class="button" title="Choose date" id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>.calendar"><span unselectable="on" class="norm" onmouseover="className='over'" onmouseout="className='norm'" onmousedown="className='push'" onmouseup="className='over'"><img class="button" src="<%= org.opencms.workplace.CmsWorkplace.getSkinUri() %>buttons/calendar.png" alt="Choose date"></span></a>
 </div>
 <div class="clearer"></div> 
</div>
<div class="formbuttons bgdbluebright linegreydark">
  <input type="submit" value="<%= messages.key("form.button.submit") %>" class="ip_submit" /> <input type="reset" value="<%= messages.key("form.button.reset") %>" class="ip_submit"/>
 </div>
 <input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_DOWNLOAD_DATA_2 %>" />
</form>
 <script type="text/javascript">
  <!--	
	Calendar.setup({
		inputField     :    "<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>",
		ifFormat       :    "<%=calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0)%> <%=calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_TIME_FORMAT_0)%>",
		button         :    "<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>.calendar",
		align          :    "cR",
		singleClick    :    false,
		weekNumbers    :    false,
		mondayFirst    :    true,
		showsTime      :    true,
		timeFormat     :    "12"
	});
  //-->
  </script> 
  <script type="text/javascript">
  <!--
	Calendar.setup({
		inputField     :    "<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>",
		ifFormat       :    "<%=calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0)%> <%=calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_TIME_FORMAT_0)%>",
		button         :    "<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>.calendar",
		align          :    "cR",
		singleClick    :    false,
		weekNumbers    :    false,
		mondayFirst    :    true,
		showsTime      :    true,
		timeFormat     :    "12"
	});
  //-->
  </script> 

<%
    }
%>