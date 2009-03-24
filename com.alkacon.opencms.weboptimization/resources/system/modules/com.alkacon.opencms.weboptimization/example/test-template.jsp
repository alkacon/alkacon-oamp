<%@ page session="false" import="com.alkacon.opencms.weboptimization.*" %><%

  CmsOptimizationJs js = new CmsOptimizationJs(pageContext, request, response);
  // including js originals
  js.includeOriginal("%(link.strong:/system/modules/com.alkacon.opencms.weboptimization/example/test-folder.js:5c949006-127f-11de-a1a4-e30a09549266)");
  // including js optimized
  js.includeOptimized("%(link.strong:/system/modules/com.alkacon.opencms.weboptimization/example/test-folder.js:5c949006-127f-11de-a1a4-e30a09549266)");
  // including js depending of project
  js.includeDefault("%(link.strong:/system/modules/com.alkacon.opencms.weboptimization/example/test-folder.js:5c949006-127f-11de-a1a4-e30a09549266)");

  CmsOptimizationCss css = new CmsOptimizationCss(pageContext, request, response);
  // including css originals
  css.includeOriginal("%(link.strong:/system/modules/com.alkacon.opencms.weboptimization/example/test-folder.css:629b60f9-127f-11de-a1a4-e30a09549266)");
  // including css optimized
  css.includeOptimized("%(link.strong:/system/modules/com.alkacon.opencms.weboptimization/example/test-folder.css:629b60f9-127f-11de-a1a4-e30a09549266)");
  // including css depending of project
  css.includeDefault("%(link.strong:/system/modules/com.alkacon.opencms.weboptimization/example/test-folder.css:629b60f9-127f-11de-a1a4-e30a09549266)");
%>