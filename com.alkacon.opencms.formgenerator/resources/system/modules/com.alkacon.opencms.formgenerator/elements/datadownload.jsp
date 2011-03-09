<%--  
WARNING: Do not auto - reformat! In case of data download a linebreak will cause: 
"java.lang.IllegalStateException: getOutputStream() has already been called for this response".
--%><%@page buffer="none" session="false" import="org.apache.commons.logging.*,java.io.OutputStreamWriter,org.opencms.module.CmsModule,org.opencms.i18n.*,com.alkacon.opencms.formgenerator.database.export.*,org.opencms.flex.CmsFlexController,com.alkacon.opencms.formgenerator.*,java.util.*,org.opencms.util.*,org.opencms.widgets.*,org.opencms.main.*,org.antlr.stringtemplate.*"%><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%! 
private static final Log LOG = CmsLog.getLog(CmsCvsExportBean.class);
%><%
    // initialize the form handler
    CmsFormHandler cms = CmsFormHandlerFactory.create(pageContext, request, response);
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

    if (cms.downloadData()) {
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
	// get output HTML template
	StringTemplate sTemplate = cms.getOutputTemplate("datadownloadpage");
	// set the necessary attributes to use in the string template
	sTemplate.setAttribute("formuri", cms.link(cms.getRequestContext().getUri()));
	sTemplate.setAttribute("formconfig", form);
	sTemplate.setAttribute("skinuri", org.opencms.workplace.CmsWorkplace.getSkinUri());
	sTemplate.setAttribute("labelfrom", messages.key("form.label.dataexport.from"));
	sTemplate.setAttribute("labelto", messages.key("form.label.dataexport.to"));
	sTemplate.setAttribute("datefrom", CmsCalendarWidget.getCalendarLocalizedTime(locale, calendarMessages, 0));
	sTemplate.setAttribute("dateto", CmsCalendarWidget.getCalendarLocalizedTime(locale, calendarMessages, System.currentTimeMillis()));
	sTemplate.setAttribute("calendaralttext", messages.key("form.html.calendar.alttext"));
	sTemplate.setAttribute("submitbutton", messages.key("form.button.submit"));
	sTemplate.setAttribute("resetbutton", messages.key("form.button.reset"));
	%><%=org.opencms.widgets.CmsCalendarWidget.calendarIncludes(locale)%><%= sTemplate.toString() %>

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