<%@ page session="false" import="com.alkacon.opencms.weboptimization.CmsOptimizationCss" %><%

  CmsOptimizationCss c = new CmsOptimizationCss(pageContext, request, response);
  try {
    c.optimize();
  } catch (Exception e) {
    e.printStackTrace();
    org.opencms.main.CmsLog.getLog(CmsOptimizationCss.class).error(e.getMessage(), e);
    throw e;
  }
%>