<%@ page session="false" import="com.alkacon.opencms.v8.weboptimization.CmsOptimizationJs" %><%

  CmsOptimizationJs c = new CmsOptimizationJs(pageContext, request, response);
  try {
    c.optimize();
  } catch (Exception e) {
    e.printStackTrace();
    org.opencms.main.CmsLog.getLog(CmsOptimizationJs.class).error(e.getMessage(), e);
    throw e;
  }
%>