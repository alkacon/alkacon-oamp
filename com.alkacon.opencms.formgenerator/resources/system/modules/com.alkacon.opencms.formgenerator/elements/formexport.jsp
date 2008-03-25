<%@page buffer="none" session="false" import="
	com.alkacon.opencms.formgenerator.database.export.*,
	java.util.Date,
	org.opencms.flex.CmsFlexController,
	com.alkacon.opencms.formgenerator.dialog.*"%>
<%
	
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
	    ServletOutputStream output = res.getOutputStream();
	    try {	
	      output.write(exportBean.exportData(formid,request.getLocale()).getBytes());
	    } finally {
	      if (output != null) {
	        output.flush();
	        output.close();
	      }
	    }
	}

%>