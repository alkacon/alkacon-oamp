<%@ page session="false" import="com.alkacon.opencms.weboptimization.CmsOptimizationSprite" %><%

  CmsOptimizationSprite c = new CmsOptimizationSprite(pageContext, request, response);
  try {
    c.optimize();
  } catch (Exception e) {
    e.printStackTrace();
    org.opencms.main.CmsLog.getLog(CmsOptimizationSprite.class).error(e.getMessage(), e);
    throw e;
  }
%>