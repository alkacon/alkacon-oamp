<%@ page session="false" import="com.alkacon.opencms.v8.weboptimization.*" %><%

  CmsOptimizationJs js = new CmsOptimizationJs(pageContext, request, response);
  // including js originals
  js.includeOriginal("%(link.strong:/system/modules/com.alkacon.opencms.v8.weboptimization/example/test-folder.js:de1ce92b-1b47-11e1-85b6-9b778fa0dc42)");
  // including js optimized
  js.includeOptimized("%(link.strong:/system/modules/com.alkacon.opencms.v8.weboptimization/example/test-folder.js:de1ce92b-1b47-11e1-85b6-9b778fa0dc42)");
  // including js depending of project
  js.includeDefault("%(link.strong:/system/modules/com.alkacon.opencms.v8.weboptimization/example/test-folder.js:de1ce92b-1b47-11e1-85b6-9b778fa0dc42)");

  CmsOptimizationCss css = new CmsOptimizationCss(pageContext, request, response);
  // including css originals
  css.includeOriginal("%(link.strong:/system/modules/com.alkacon.opencms.v8.weboptimization/example/test-folder.css:de182e38-1b47-11e1-85b6-9b778fa0dc42)");
  // including css optimized
  css.includeOptimized("%(link.strong:/system/modules/com.alkacon.opencms.v8.weboptimization/example/test-folder.css:de182e38-1b47-11e1-85b6-9b778fa0dc42)");
  // including css depending of project
  css.includeDefault("%(link.strong:/system/modules/com.alkacon.opencms.v8.weboptimization/example/test-folder.css:de182e38-1b47-11e1-85b6-9b778fa0dc42)");
%>