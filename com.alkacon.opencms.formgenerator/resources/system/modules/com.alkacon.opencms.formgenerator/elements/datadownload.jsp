<%--  
WARNING: Do not auto - reformat! In case of data download a linebreak will cause: 
"java.lang.IllegalStateException: getOutputStream() has already been called for this response".
--%><%@page buffer="none" session="false" import="org.apache.commons.logging.*,java.io.OutputStreamWriter,org.opencms.module.CmsModule,org.opencms.i18n.*,com.alkacon.opencms.formgenerator.database.export.*,org.opencms.flex.CmsFlexController,com.alkacon.opencms.formgenerator.*,java.util.*,org.opencms.util.*,org.opencms.widgets.*,org.opencms.main.*"%><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%! 
private static final Log LOG = CmsLog.getLog(CmsCvsExportBean.class);
%><%
    // initialize the form handler
    CmsFormHandler cms = new CmsFormHandler(pageContext, request, response);
    boolean isOffline = !cms.getRequestContext().currentProject().isOnlineProject();
    pageContext.setAttribute("offline", new Boolean(isOffline));

    // get the localized messages to create the form
    CmsMessages messages = cms.getMessages();
    Locale locale = cms.getRequestContext().getLocale();
    CmsMessages calendarMessages = new CmsMessages(
        org.opencms.workplace.Messages.get().getBundleName(),
        locale);

    // get the configured form elements
    CmsForm form = cms.getFormConfiguration();

    if (!cms.downloadData()) {
        // create the form head
%><%=org.opencms.widgets.CmsCalendarWidget.calendarIncludes(locale)%>
<style type="text/css">
.calendarinput {
<%=messages.key("form.html.calendar.style.inputfield")%>
}

span.norm{
<%=messages.key("form.html.calendar.style.norm")%>
}

span.push {
<%=messages.key("form.html.calendar.style.push")%>
}
span.over {
<%=messages.key("form.html.calendar.style.over")%>
}

.calendarbutton {
<%=messages.key("form.html.calendar.style.image")%>
}
a.button {
 color: ButtonText;
 text-decoration: none;
 cursor: pointer;
}


/* - internet explorer 6 specific definitions (hacks) - */
* html .ip_text {
	padding: 2px;
}
</style>
<%
    } else {
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
        ServletOutputStream output = null;
        OutputStreamWriter writer = null;
        try {	
            output = res.getOutputStream();
            CmsModule webformModule = OpenCms.getModuleManager().getModule(CmsForm.MODULE_NAME);
            String encoding = webformModule.getParameter(CmsForm.MODULE_PARAM_EXPORTENCODING);
            if(CmsStringUtil.isEmptyOrWhitespaceOnly(encoding)) {
                encoding = OpenCms.getSystemInfo().getDefaultEncoding();
            }
            writer = new OutputStreamWriter(output, encoding);
            writer.write(exportBean.exportData());
        } catch(RuntimeException f) { 
        	LOG.error("Error serving data.", f);
        	throw f;
        } finally {
          if (writer!= null) {
            writer.flush();
            writer.close();
          }
        }
    }

    if (!cms.downloadData()) {
      String dateStartValue = CmsCalendarWidget.getCalendarLocalizedTime(locale, calendarMessages, 0);
      String dateEndValue = CmsCalendarWidget.getCalendarLocalizedTime(locale, calendarMessages, System.currentTimeMillis());
%><form name="emailform" action="<%= cms.link(cms.getRequestContext().getUri()) %>" method="post" class="formarea">
<%= messages.key("form.html.start") %>
<%=messages.key("form.html.row.start")%>
 <%=messages.key("form.html.label.start")%>
   <label for="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>"><%=messages.key("form.label.dataexport.from")%></label>
 <%=messages.key("form.html.label.end")%>
 <%=messages.key("form.html.field.start")%>
  <table border="0">
   <tr>
    <td>
     <input id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>" type="text" value="<%=dateStartValue%>" name="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>" class="calendarinput" /> 
    </td>
    <td>  
     <a href="#" class="button" title="<%=messages.key("form.html.calendar.alttext")%>" id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>.calendar"><span unselectable="on" class="norm" onmouseover="className='over'" onmouseout="className='norm'" onmousedown="className='push'" onmouseup="className='over'"><img class="calendarbutton" src="<%= org.opencms.workplace.CmsWorkplace.getSkinUri() %>buttons/calendar.png" alt="<%=messages.key("form.html.calendar.alttext")%>"></span></a>
    </td>
   </tr>
  </table>
 <%=messages.key("form.html.field.end")%>
<%=messages.key("form.html.row.end")%>
<%=messages.key("form.html.row.start")%>
 <%=messages.key("form.html.label.start")%>
  <label for="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>"><%=messages.key("form.label.dataexport.to")%></label>
 <%=messages.key("form.html.label.end")%>
 <%=messages.key("form.html.field.start")%>
  <table border="0">
   <tr>
    <td>
     <input id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>" type="text" value="<%=dateEndValue%>" name="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>" class="calendarinput" /> 
    </td>
    <td>  
     <a href="#" class="button" title="<%=messages.key("form.html.calendar.alttext")%>" id="<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_END %>.calendar"><span unselectable="on" class="norm" onmouseover="className='over'" onmouseout="className='norm'" onmousedown="className='push'" onmouseup="className='over'"><img class="calendarbutton" src="<%= org.opencms.workplace.CmsWorkplace.getSkinUri() %>buttons/calendar.png" alt="<%=messages.key("form.html.calendar.alttext")%>"></span></a>
    </td>
   </tr>
  </table>
 <%=messages.key("form.html.field.end")%>
<%=messages.key("form.html.row.end")%>
<%=messages.key("form.html.row.start")%>
 <%=messages.key("form.html.button.start")%>
  <input type="submit" value="<%= messages.key("form.button.submit") %>" class="formbutton" /> <input type="reset" value="<%= messages.key("form.button.reset") %>" class="formbutton"/>
 <%=messages.key("form.html.button.end")%>
<%=messages.key("form.html.row.end")%>
<%= messages.key("form.html.end") %>
 <input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_DOWNLOAD_DATA_2 %>" />
</form>
 <script type="text/javascript">
  <!--	
	Calendar.setup({
		inputField     :    "<%= CmsCvsExportBean.PARAM_EXPORT_DATA_TIME_START %>",
		ifFormat       :    "<%=calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0)%> <%=" " + calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_TIME_FORMAT_0)%>",
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
		ifFormat       :    "<%=calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0)%> <%=" " + calendarMessages.key(org.opencms.workplace.Messages.GUI_CALENDAR_TIME_FORMAT_0)%>",
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