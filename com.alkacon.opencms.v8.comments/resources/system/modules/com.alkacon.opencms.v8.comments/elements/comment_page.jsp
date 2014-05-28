<%@ page import="com.alkacon.opencms.v8.comments.*, java.util.Map, com.alkacon.opencms.v8.formgenerator.database.CmsFormDataBean" %>
<%@ page taglibs="c" %>

<%
	Map<String, String> dynamicConfig = CmsCommentsAccess.generateDynamicConfig(request.getParameter("cmtformid"));
	CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"), dynamicConfig);
	pageContext.setAttribute("alkaconCmt", alkaconCmt);
	CmsCommentStringTemplateHandler templateHandler = new CmsCommentStringTemplateHandler(alkaconCmt);
%>

<c:set var="boxColor"><c:out value="${param.cmtcolor}" default="default" /></c:set>
<%-- <c:if test="${alkaconCmt.config.allowReplies}">
	<jsp:useBean id="alkaconReplies" class="com.alkacon.opencms.v8.comments.CmsRepliesAccessBean" />
</c:if> --%>
<!-- start: page -->
<c:choose>
<c:when test="${alkaconCmt.userCanManage}" >
	<c:forEach var="comment" items="${alkaconCmt.comments}" >
	   <!-- start: manager comment -->
	   <% CmsFormDataBean comment = (CmsFormDataBean) pageContext.getAttribute("comment");
	      String boxColor = (String)pageContext.getAttribute("boxColor");
	   %>
	   <%= templateHandler.buildManagerHtml(comment, boxColor) %>
	   <!-- end: manager comment -->
	</c:forEach>
</c:when>
<c:otherwise>
	<c:forEach var="comment" items="${alkaconCmt.comments}" >
	   <!-- start: comment -->
	   <% CmsFormDataBean comment = (CmsFormDataBean) pageContext.getAttribute("comment");
	      String boxColor = (String)pageContext.getAttribute("boxColor");
	   %>
	   <%= templateHandler.buildViewHtml(comment, boxColor) %>
	   <!-- end: comment -->
	</c:forEach>
</c:otherwise>
</c:choose>
<!-- end: page -->
