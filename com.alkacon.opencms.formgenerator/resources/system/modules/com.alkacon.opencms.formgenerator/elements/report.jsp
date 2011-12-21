<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%--
--%><%@page buffer="none" session="false" import="
	com.alkacon.opencms.formgenerator.*,
	java.lang.*,
	java.util.*,
	org.opencms.file.*"

	
%>
<%
	CmsFormReport report = null;
%>

<c:choose>
<c:when test="${cms.element == null}">
	<c:set var="formUri" value="${cms:vfs(pageContext).context.uri}" />
	<%
	// initialize the form report bean
	report = new CmsFormReport(pageContext, request, response, (String)pageContext.getAttribute("formUri"));
	%>
</c:when>
<c:otherwise>
	<c:set var="formUri" value="${cms.element.sitePath}" />
	<c:set var="loc" value="${cms.locale}" />
	<c:choose>
		<c:when test="${cms.element.inMemoryOnly}">
			<%
			// initialize the form report bean
			report = new CmsFormReport(pageContext, request, response);
			%>
			<div>
				<h3><%= report.getMessages().key("webformreport.init.newAlkaconWebform") %></h3>
				<h4><%= report.getMessages().key("webformreport.init.pleaseEdit") %></h4>
			</div>
		</c:when>
		<c:otherwise>
			<%
			// initialize the form report bean
			report = new CmsFormReport(pageContext, request, response, (String)pageContext.getAttribute("formUri"));
			%>
		</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>


<%
	if (report.isLoadDynamic() && !report.isShowReport()) {
		out.print(report.getReportDataDynamic());
	} else {
		// show report output
		pageContext.setAttribute("report", report);
%>

<cms:formatter var="content" val="value">
 <div> 

	<c:choose>
		<c:when test="${cms.edited}" >
			<div><h3><c:out value="Please reload!" /></h3>
			</div>
		</c:when>
		<c:otherwise>

			<%-- Handle the case the page was recently reloaded, execute the scripts --%>											
<c:if test="${report.includeStyleSheet}"><cms:include file="/system/modules/com.alkacon.opencms.formgenerator/elements/report_css.jsp" /></c:if>
<script type="text/javascript" src="<cms:link>/system/modules/com.alkacon.opencms.formgenerator/resources/js/dateformat.js</cms:link>"></script>  
<script type="text/javascript" src="<cms:link><%= report.getVfsPathGridMessages() %></cms:link>"></script>  
<script type="text/javascript" src="<cms:link>/system/modules/com.alkacon.opencms.formgenerator/resources/grid/grid/gt_grid_all.js</cms:link>"></script>
<script type="text/javascript">

<c:if test="${!report.loadDynamic}">var data1 = ${report.reportData};</c:if>

var dsOption= {
	fields :[
		<c:if test="${report.showDate}">{name: "creationdate", type: "float"},</c:if>
		<c:forEach var="col" items="${report.shownColumns}" varStatus="status">
		{name: "${col.columnId}"}
		<c:if test="${not status.last}">
		,
		</c:if>
		</c:forEach>
	],
	recordType : "array"
	<c:if test="${!report.loadDynamic}">, data: data1</c:if>
}

var colsOption = [
	<c:if test="${report.showDate}">{id: "creationdate",
		header: "<%= report.getMessages().key("form.report.column.creationdate") %>",
		width: "125",
		renderer: function(value, record, columnObj, grid, colNo, rowNo) {
				// format a date object using the given localized pattern
				return new Date(value).format("<%= report.getMessages().key("form.report.column.creationdate.format") %>");
			} 
		},
	</c:if>
	<c:forEach var="col" items="${report.shownColumns}" varStatus="status">
	{id: "${col.columnId}" , header: "<c:choose><c:when test="${report.showLabels}">${col.columnLabel}</c:when><c:otherwise>${col.columnDbLabel}</c:otherwise></c:choose>" , width: "${report.columnWidth}"}
	<c:if test="${not status.last}">
	,
	</c:if>
	</c:forEach>
];


var gridOption={
	id : "grid1",
	height: ${report.gridHeight},
	<c:if test="${report.loadDynamic}">loadURL : "<cms:link>${formUri}?__locale=${loc}</cms:link>",</c:if>
	container : "grid1_container",
	// nav | goto | pagesize | reload | add del save | print | filter chart | state
	toolbarContent : "<c:if test="${!report.heightAuto}">goto | nav | pagesize | reload | </c:if>print",
	pageSize : ${report.entriesPerPage},
	<c:if test="${!report.heightAuto}">pageSizeList : [5,10,20,50,100,150],</c:if>
	skin : "${report.skin}",
	showGridMenu : <c:choose><c:when test="${report.showMenu}">true</c:when><c:otherwise>false</c:otherwise></c:choose>,
	allowCustomSkin	: true,
	allowFreeze : true,
	allowHide: true,
	allowSkin: true,
	dataset : dsOption,
	columns : colsOption
}; 

var mygrid = new Sigma.Grid(gridOption);
Sigma.Util.onLoad(function(){
	mygrid.render();
});

</script>

</c:otherwise>		
</c:choose>


${content.value.Text}

<div id="grid1_container"></div> 

</div> 
</cms:formatter>
<%
	}
%>
