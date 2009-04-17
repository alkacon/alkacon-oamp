<%@page buffer="none" session="false" import="
	com.alkacon.opencms.formgenerator.database.export.*,
	com.alkacon.opencms.formgenerator.*,
	java.util.Date,
	java.io.*,
	org.opencms.module.*,
	org.opencms.main.*,
	org.opencms.util.*,
	org.opencms.flex.CmsFlexController,
	com.alkacon.opencms.formgenerator.dialog.*,
	org.apache.commons.logging.*"%><%! 
private static final Log LOG = CmsLog.getLog(CmsCvsExportBean.class);
%><%
	
	// get the form id 
	String formid=request.getParameter(CmsFormListDialog.PARAM_FORM_ID);
	if(formid!=null)
	{
		CmsCvsExportBean exportBean = new CmsCvsExportBean(null);
		exportBean.setEndTime(new Date(Long.MAX_VALUE));
		exportBean.setStartTime(new Date(0));
		
		
		
        CmsFlexController controller = CmsFlexController.getController(request);
        HttpServletResponse res = controller.getTopResponse();
        res.setContentType("text/csv");
        // try "inline" instead "attachment" and ie will open within browser window. 
        res.addHeader("Content-Disposition", "attachment; filename=" + formid + ".csv;");
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
            writer.write(exportBean.exportData(formid,request.getLocale()));
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

%>