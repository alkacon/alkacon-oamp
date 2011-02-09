<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@page buffer="none" session="false" import="
	com.alkacon.opencms.formgenerator.*,
	java.lang.*,
	java.util.*"
%><%

	// initialize the form report bean
	CmsFormReport cms = new CmsFormReport(pageContext, request, response);
	
	if (cms.isLoadDynamic() && !cms.isShowReport()) {
		out.print(cms.getReportDataDynamic());
	} else {
		// show report output
		pageContext.setAttribute("cms", cms);

%><cms:include property="template" element="head" />

<c:if test="${cms.includeStyleSheet}"><cms:include file="/system/modules/com.alkacon.opencms.formgenerator/elements/report_css.jsp" /></c:if>
<script type="text/javascript" src="<cms:link>/system/modules/com.alkacon.opencms.formgenerator/resources/js/dateformat.js</cms:link>"></script>  
<script type="text/javascript" src="<cms:link><%= cms.getVfsPathGridMessages() %></cms:link>"></script>  
<script type="text/javascript" src="<cms:link>/system/modules/com.alkacon.opencms.formgenerator/resources/grid/grid/gt_grid_all.js</cms:link>"></script>
<script type="text/javascript">

<c:if test="${!cms.loadDynamic}">var data1 = ${cms.reportData};</c:if>

var dsOption= {
	fields :[
		<c:if test="${cms.showDate}">{name: "creationdate", type: "float"},</c:if>
		<c:forEach var="col" items="${cms.shownColumns}" varStatus="status">
		{name: "${col.columnId}"}
		<c:if test="${not status.last}">
		,
		</c:if>
		</c:forEach>
	],
	recordType : "array"
	<c:if test="${!cms.loadDynamic}">, data: data1</c:if>
}

var colsOption = [
	<c:if test="${cms.showDate}">{id: "creationdate",
		header: "<%= cms.getMessages().key("form.report.column.creationdate") %>",
		width: "125",
		renderer: function(value, record, columnObj, grid, colNo, rowNo) {
				// format a date object using the given localized pattern
				return new Date(value).format("<%= cms.getMessages().key("form.report.column.creationdate.format") %>");
			} 
		},
	</c:if>
	<c:forEach var="col" items="${cms.shownColumns}" varStatus="status">
	{id: "${col.columnId}" , header: "<c:choose><c:when test="${cms.showLabels}">${col.columnLabel}</c:when><c:otherwise>${col.columnDbLabel}</c:otherwise></c:choose>" , width: "${cms.columnWidth}"}
	<c:if test="${not status.last}">
	,
	</c:if>
	</c:forEach>
];

var gridOption={
	id : "grid1",
	height: ${cms.gridHeight},
	<c:if test="${cms.loadDynamic}">loadURL : "<cms:link>${cms.requestContext.uri}</cms:link>",</c:if>
	container : "grid1_container",
	// nav | goto | pagesize | reload | add del save | print | filter chart | state
	toolbarContent : "<c:if test="${!cms.heightAuto}">goto | nav | pagesize | reload | </c:if>print",
	pageSize : ${cms.entriesPerPage},
	<c:if test="${!cms.heightAuto}">pageSizeList : [5,10,20,50,100,150],</c:if>
	skin : "${cms.skin}",
	showGridMenu : <c:choose><c:when test="${cms.showMenu}">true</c:when><c:otherwise>false</c:otherwise></c:choose>,
	allowCustomSkin	: true,
	allowFreeze : true,
	allowHide: true,
	allowSkin: true,
	dataset : dsOption,
	columns : colsOption
}; 

var mygrid = new Sigma.Grid(gridOption);
Sigma.Util.onLoad(Sigma.Grid.render(mygrid));

</script>
<cms:contentload collector="singleFile" param="%(opencms.uri)" editable="true">
<cms:contentaccess var="content" />

${content.value.Text}

<div id="grid1_container"></div> 

</cms:contentload>

<cms:include property="template" element="foot" />
<%
	}
%>