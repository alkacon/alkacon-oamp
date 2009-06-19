<%@ page session="false"
	import="com.alkacon.opencms.survey.CmsFormReportingBean"%>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	// initialize the bean
	CmsFormReportingBean cms = new CmsFormReportingBean(pageContext, request, response);
	pageContext.setAttribute("cms", cms);
%>


<%@page import="com.alkacon.opencms.survey.CmsFormWorkBean"%><cms:include
	property="template" element="head" />

<%-- set the parameters --%>
<c:set var="curPage">
	<c:if test="${!empty param.page}">${param.page}</c:if>
	<c:if test="${empty param.page}">1</c:if>
</c:set>

<c:set var="locale">
	<c:if test="${!empty cms:vfs(pageContext).context.locale}">${cms:vfs(pageContext).context.locale}</c:if>
	<c:if test="${ empty cms:vfs(pageContext).context.locale}">
		<cms:property name="locale" file="search" default="en" />
	</c:if>
</c:set>

<%-- start --%>
<fmt:setLocale value="${locale}" />
<fmt:bundle basename="com.alkacon.opencms.survey.frontend">

	<c:catch var="error">
		<cms:contentload collector="singleFile" param="%(opencms.uri)">
			<cms:contentaccess var="content" />

			<c:set var="color" value="${content.valueList['Color']}" />
			<c:set var="group" value="${content.value['DetailGroup'] }" />
			<c:set var="showCount" value="${content.value['ShowCount']}" />
			<c:set var="AddText" value="${content.value['AddText']}" />


			<c:if
				test="${!empty content.value['Text'] && (!param.detail || !cms.showDetail[group])}">
				<c:out value="${content.value['Text']}" escapeXml="false" />
			</c:if>

			<c:set var="webformpath" value="${content.value['Webform']}" />
			<c:if test="${!webformpath.isEmptyOrWhitespaceOnly}">
				<cms:contentload collector="singleFile" param="${webformpath}">
					<cms:contentaccess var="webform" />

					<%-- look if the work bean already initialized --%>
					<c:if test="${empty workBean}">
						<c:if
							test="${!empty webform.value['DataTarget'] && fn:indexOf(webform.value['DataTarget/Transport'],'database') >= 0}">
							<c:set var="formid" value="${webform.value['DataTarget/FormId']}" />
							<c:if test="${!empty formid}">
								<%--special case if resource filter: <c:set var="itemParam" value="${formid}${cms.separator}${cms.requestContext.siteRoot}${resPath}"/>--%>
								<%
					            CmsFormWorkBean workbean = cms.getReporting(String.valueOf(pageContext.getAttribute("formid")), String.valueOf(pageContext.getAttribute("webformpath")));
						        pageContext.setAttribute("workBean", workbean);
						        %>
							</c:if>
						</c:if>
					</c:if>

					<%-- print the content --%>
					<c:if test="${!empty workBean}">
						<div id="webformReport"><%-- special caption for the overview page --%>
						<c:if test="${!param.detail || !cms.showDetail[group]}">
							<c:if test="${showCount == 'true'}">
								<h2><fmt:message key="report.count.headline">
									<fmt:param value="${fn:length(workBean.list)}" />
								</fmt:message></h2>
							</c:if>
							<c:if test="${cms.showDetail[group]}">
								<a class="linkDetail"
									href="<cms:link>${cms.requestContext.uri}?detail=true</cms:link>"
									title="<fmt:message key='report.next.detail.title'/>"><fmt:message
									key="report.next.detail.headline" /></a>
							</c:if>
						</c:if> <%-- special caption for the detail page --%> <c:if
							test="${param.detail && cms.showDetail[group]}">
							<h2><fmt:message key="report.detail.headline">
								<fmt:param value="${curPage}" />
								<fmt:param value="${fn:length(workBean.list)}" />
							</fmt:message></h2>
							<a class="linkDetail"
								href="<cms:link>${cms.requestContext.uri}</cms:link>"
								title="<fmt:message key='report.back.overview.title'/>"><fmt:message
								key="report.back.overview.headline" /></a>
							<%@include
								file="%(link.strong:/system/modules/com.alkacon.opencms.survey/elements/include_paging.jsp:6eace4bb-1052-11dd-84af-371bb8ed1b84)"%>
						</c:if> <%-- for each field print the answers --%> <c:forEach var="field"
							items="${webform.valueList['InputField']}">
							<c:if
								test="${(param.detail && cms.showDetail[group]) || cms.fieldTypeCorrect[field.value['FieldType']]}">

								<%-- get the label for the field --%>
								<c:set var="labeling"
									value="${cms.labeling[field.value['FieldLabel']]}" />
								<h3><c:out value="${labeling[0]}" /></h3>
								<br />

								<%-- is the detail page --%>
								<c:if test="${param.detail && cms.showDetail[group]}">
									<c:set var="itemParam"
										value="${labeling[1]}${cms.separator}${curPage}${cms.separator}${field.value['FieldType']}" />
									<c:forEach var="item"
										items="${workBean.answerByField[itemParam]}">
										<div class="reportitem"><c:set var="defValue"
											value="${item}" /> <c:forTokens
											items="${field.value['FieldDefault']}" delims="|" var="def">
											<c:if
												test="${fn:contains(def, ':') && fn:substringBefore(def, ':') == item}">
												<c:set var="defValue" value="${fn:substringAfter(def, ':')}" />
											</c:if>
										</c:forTokens>
										<p class="reportanswer"><c:out value="${defValue}" /></p>
										</div>
									</c:forEach>
								</c:if>

								<%-- is the overview page --%>
								<c:if test="${!param.detail || !cms.showDetail[group]}">
									<c:set var="answerCounts"
										value="${workBean.answers[labeling[1]]}" />
									<c:set var="answerList"
										value="${fn:split(field.value['FieldDefault'], '|')}" />
									<c:forEach var="item" items="${answerList}" varStatus="status">
										<c:set var="itemKey" value="${item}" />
										<c:if test="${fn:contains(itemKey, ':')}">
											<c:set var="itemKey"
												value="${fn:substringBefore(itemKey, ':')}" />
										</c:if>
										<c:set var="itemValue" value="${answerCounts[itemKey]}" />
										<c:if test="${!empty itemValue}">
											<div class="reportitem"><c:set var="width"
												value="${ (itemValue/fn:length(workBean.list)) }" /> <c:set
												var="defValue" value="${itemKey}" /> <c:forTokens
												items="${field.value['FieldDefault']}" delims="|" var="def">
												<c:if
													test="${fn:contains(def, ':') && fn:substringBefore(def, ':') == itemKey}">
													<c:set var="defValue"
														value="${fn:substringAfter(def, ':')}" />
												</c:if>
											</c:forTokens>
											<p class="reportanswer"><c:out value="${defValue}" /></p>
											<span class="processbar"> <c:set var="curColor"
												value="${color[(status.index%fn:length(color))]}" /> <span
												class="bar"
												style="width:${width * 100}%; background-color:${curColor}; color:${cms.textColor[curColor]};">
											<fmt:formatNumber value="${width}" type="percent" /> </span> </span> <span
												class="reportcount"><c:if
												test="${showCount == 'true'}">(<c:out
													value="${itemValue}" />)</c:if><br />
											<br />
											</span></div>
										</c:if>
									</c:forEach>
								</c:if>

							</c:if>
						</c:forEach></div>
					</c:if>
				</cms:contentload>
			</c:if>
			<c:if
				test="${!empty content.value['AddText'] && (!param.detail || !cms.showDetail[group])}">
				<c:out value="${content.value['AddText']}" escapeXml="false" />
			</c:if>
		</cms:contentload>
	</c:catch>

	<c:if test="${empty workBean && empty error}">
		<h1><fmt:message key="report.error.headline" /></h1>
		<p><fmt:message key="report.nodatabase.text" /></p>
	</c:if>

	<c:if test="${!empty error}">
		<h1><fmt:message key="report.error.headline" /></h1>
		<p><fmt:message key="report.error.text" /><c:out
			value="${ error }" /></p>
	</c:if>

</fmt:bundle>

<cms:include property="template" element="foot" />
