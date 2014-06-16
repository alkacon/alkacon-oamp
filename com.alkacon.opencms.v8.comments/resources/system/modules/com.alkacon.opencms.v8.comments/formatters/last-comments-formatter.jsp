<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<%@page import="java.util.List, java.util.Arrays, com.alkacon.opencms.v8.comments.CmsLastCommentStringTemplateHandler" %>
<%@page taglibs="c,cms,fmt,fn" %>

<fmt:setLocale value="${cms.locale}" />
<%-- get the stringtemplate Handler --%>
<%	CmsLastCommentStringTemplateHandler templateHandler = new CmsLastCommentStringTemplateHandler();
	templateHandler.init(pageContext,request,response);
	String entries = "";
	String bundle = templateHandler.getResourceBundle();
	pageContext.setAttribute("bundle", bundle);
%>
<cms:bundle basename="${bundle}">
	<cms:formatter var="content">
	  <div>
			<c:choose>
				<c:when test="${cms.element.inMemoryOnly}"><fmt:message key="commentlist.memoryonly" /></c:when>
				<c:otherwise>
					<c:set var="formId">${content.value.FormId}</c:set>
					<c:set var="maxComments">
						<c:choose>
							<c:when test="${content.value.MaxNumber.exists}">${content.value.MaxNumber}</c:when>
							<c:otherwise>-1</c:otherwise>
						</c:choose>	
					</c:set>
					<jsp:useBean id="commentCollector" class="com.alkacon.opencms.v8.formgenerator.collector.CmsFormCollectorBean">
						<% commentCollector.init(pageContext, request, response); %>
						<jsp:setProperty name="commentCollector" property="formId" value="${formId}" />
						<jsp:setProperty name="commentCollector" property="numForms" value="${maxComments}" />
						<c:set var="comments" value="${commentCollector.formDataSets}" />
						<c:set var="boxColor"><c:out value="${cms.element.settings.color}" default="default" /></c:set>
						<c:set var="title">${content.value.Title}</c:set>
						
						<%-- Real formatting code --%>
						<%-- build the HTML for the entries --%>
						<c:set var="commentNum" value="${fn:length(comments)}" />
						<c:choose>
							<c:when test="${commentNum < 1}">
								<% entries = templateHandler.buildLastCommentsNoEntryHtml(); %>
							</c:when>
							<c:otherwise>
								<c:if test="${content.value.Fields.exists}">
									<c:set var="fields" value="${fn:split(content.value.Fields, ',')}" />
								</c:if>
								<% Object fieldsAttribute = pageContext.getAttribute("fields");
								   List<String> fields = null;
								   if (fieldsAttribute != null) {
								   		fields = Arrays.asList((String[])fieldsAttribute); 
								    }
								%>
								<c:forEach var="comment" items="${comments}">
									<% entries += templateHandler.buildLastCommentsEntryHtml(
											(com.alkacon.opencms.v8.formgenerator.collector.CmsFormBean) pageContext.getAttribute("comment")
											, fields
											, (String) pageContext.getAttribute("boxColor")); %>
								</c:forEach>										
							</c:otherwise>
						</c:choose>
						<%-- build the HTML for the whole list, injecting the entries HTML --%>
						<%= templateHandler.buildLastCommentsListHtml(entries,(String) pageContext.getAttribute("title")) %>
					</jsp:useBean>						
				</c:otherwise>
			</c:choose>
	  </div>
	</cms:formatter>
</cms:bundle>