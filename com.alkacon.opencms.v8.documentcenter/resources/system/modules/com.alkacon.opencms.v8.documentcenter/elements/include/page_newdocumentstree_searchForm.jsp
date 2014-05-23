<%
	// ############### display the search form ###############

	CategoryTree categoryTree = new CategoryTree(cmsObject, request, startfolder, maxTreeDepth);
	List treeList = categoryTree.getCategoryTree();
	int treeSize = treeList.size();	
	
	long curTime = new Date().getTime();

	String action = "search";
	if (("text".equalsIgnoreCase(paramAction)) || ("searchText".equals(org.opencms.util.CmsStringUtil.escapeHtml(request.getParameter("type"))))) {	
		action = "searchText";
	} 
	
	String headerText = messages.key("newdocuments.form.text");
	if ("searchText".equalsIgnoreCase(action)) {
		headerText = messages.key("newdocuments.query.header");
	}
	
	java.text.DateFormat df = new java.text.SimpleDateFormat(
            CmsCalendarWidget.getCalendarJavaDateFormat(messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0)));
	%>

	<%= CmsCalendarWidget.calendarIncludes(new Locale(locale)) %>


  <h3><%= headerText %></h3>
  
  <p style="margin:8px;">
  	<% if (paramUri != null) { %>
		<a class="<c:choose><c:when test="${cms.template.name == 'mobile'}">btn btn-default</c:when><c:otherwise>button-w btn</c:otherwise></c:choose>" href="<%= cms.link(uri) %>"><%= messages.key("newdocuments.button.back") %></a>&nbsp;
	<% } %>
  </p>
  
  <form name="searchnew" action="<%= cms.link(uri) %>" method="post" title="<%= headerText %>">
    <table border="0" cellpadding="2" cellspacing="6"<c:if test="${cms.template.name == 'mobile'}"> class="table"</c:if>>
  	<% if ("searchText".equalsIgnoreCase(action)) { %>
	    <tr>
	      <td valign="top"><strong><label for="docquery"><%= messages.key("newdocuments.query.input") %>:</label></strong></td>
	      <td valign="top">
			<input type="text" name="query" class="form-control" id="docquery" style="width:300px" />
	      </td>
	    </tr>		
	<% } else { %>
	    <tr>
	      <td valign="top"><strong><label for="fromDate"><%= messages.key("newdocuments.date.from") %></label></strong></td>
	      <td valign="top">
		  	<div class="input-group">
			<input type="text" class="form-control" name="fromDate" id="fromDate" value="<%= df.format(new Date()) %>" />
			<span class="input-group-addon"><img src="<%=cms.link("/system/modules/com.alkacon.opencms.v8.documentcenter/resources/calendar.png")%>" id="triggercalendarfrom" border="0" title="<%= messages.key("newdocuments.calendar.title") %>" alt="<%= messages.key("newdocuments.calendar.alt") %>"/></span>
			</div>
	      </td>
	    </tr>
	    <tr>
	      <td valign="top"><strong><label for="toDate"><%= messages.key("newdocuments.date.to") %></label></strong></td>
	      <td valign="top">
			<div class="input-group">
			<input type="text" class="form-control" name="toDate" id="toDate" value="<%= df.format(new Date()) %>" />
			<span class="input-group-addon"><img src="<%=cms.link("/system/modules/com.alkacon.opencms.v8.documentcenter/resources/calendar.png")%>" id="triggercalendarto" border="0" title="<%= messages.key("newdocuments.calendar.title") %>" alt="<%= messages.key("newdocuments.calendar.alt") %>"/></span>
			</div>
	      </td>
	    </tr>
	<% } %>
    <tr>
      <td valign="top"><strong><label for="categories"><%= messages.key("newdocuments.categories.title") %></label></strong></td>
      <td valign="top" id="categories">
        <input type="checkbox" name="all" id="all" value="true" onclick="toggleCategories(<%= treeSize %>);" /><label for="all"><%= messages.key("newdocuments.categories.all") %></label>
        <br />
        <%= categoryTree.buildCategoryTree(treeList, null, "", "<span style='margin-left:20px;'>&nbsp;</span>") %>
      </td>
    </tr>
    <tr>
      <td valign="top">&nbsp;</td>
      <td valign="top">
        <input type="hidden" name="action" value="<%= action %>" />
        <input type="hidden" name="redirect" value="redirect_a" />
        <input type="hidden" name="site" value="/" />
        <input type="hidden" name="startdate" value="" />
        <input type="hidden" name="enddate" value="" />
        <input type="hidden" name="categorylist" value="<%= categoryTree.getSelectedCategories() %>" />
        <input type="hidden" name="openedCategories" value="<%= categoryTree.getOpenedCategories() %>" />
        <input type="hidden" name="page_type" value="newdocuments" />
        <input type="hidden" name="toggleMode" value="" />
        <input type="hidden" name="toggleCategory" value="" />	
        <input type="hidden" name="timestamp" value="<%= new CmsUUID().toString() %>" />
        <input type="hidden" name="type" value="<%= action %>" />
        <input type="hidden" name="uri" value="<%= CmsFileUtil.addTrailingSeparator(startfolder) %>" />
        
        <input type="button" class="<c:choose><c:when test="${cms.template.name == 'mobile'}">btn btn-default</c:when><c:otherwise>button btn</c:otherwise></c:choose>" value="<%= messages.key("newdocuments.button.startsearch") %>" onclick="startSearch();" />
      </td>
    </tr>
    </table>
  </form> 
	
  <script type="text/javascript" language="JavaScript">
  <!--
	function startSearch() {
		var searchForm = document.forms['searchnew'];
		if (searchForm.elements['all'].checked) {
			searchForm.elements['categorylist'].value = "";
		} else {
			var categories = "";
			for (var i=0; i<<%= treeSize %>; i++) {
				var catField = searchForm.elements['cat' + i];
				if (catField.checked == true) {
					categories += catField.value + "<%= CategoryTree.C_LIST_SEPARATOR %>";
				}
			}
			
			categories = categories.substring(0, categories.length-1);
			searchForm.elements['categorylist'].value = categories; 
		}
		var searchForm = document.forms['searchnew'];
		
		<% if ("searchText".equalsIgnoreCase(action)) { %>
			searchForm.submit();
		<% } else { %>
			var startDate = Date.parseDate(document.getElementById("fromDate").value, "<%= messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0) %>");
			startDate.setHours(0);
			startDate.setMinutes(0);
			startDate.setSeconds(0);
			var endDate = Date.parseDate(document.getElementById("toDate").value, "<%= messages.key(org.opencms.workplace.Messages.GUI_CALENDAR_DATE_FORMAT_0) %>");
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			
			if (startDate > endDate) {
				alert("<%= messages.key("newdocuments.error.date") %>");
			} else {
				if (startDate != null) {
					searchForm.elements['startdate'].value = startDate.getTime();
				}
				if (endDate != null) {
					searchForm.elements['enddate'].value = endDate.getTime();
				}
				searchForm.submit();
			}
		<% } %>
	}
	
	function initCategories(size) {
		var form = document.forms.searchnew;
		form.all.checked = true;
		toggleCategories(size);
	}
	function toggleCategories(size) {
		var theForm = document.forms.searchnew;
		var activate = false;
		if (theForm.all.checked == true) {
			activate = true;
		}
		for (var i=0; i<size; i++) {
			var curElem =  document.getElementById("cat"+i);
			curElem.disabled = activate;
			curElem.checked = activate;
		}
	}
	function toggleTree(categoryCount, category, mode) {
		var selectedCategories = "";
		var form = document.forms.searchnew;
		
		for (var i=0; i<categoryCount; i++) {
			var checkbox = document.getElementById("cat"+i);
			if (checkbox.checked == true) {
				if (selectedCategories != "") {
					selectedCategories += ",";
				}
				selectedCategories += checkbox.value;
			}
		}	
	 
		form.categorylist.value = "" + selectedCategories;
		//alert("categorylist: " + form.categorylist.value);
		//alert("categorylist length: " + selectedCategories.length); 	
		
		form.toggleMode.value = "" + mode;
		//alert("mode: " + form.toggleMode.value);
		
		form.toggleCategory.value = "" + category;	
		//alert("category: " + form.toggleCategory.value);
		
		form.action.value = "toggleTree";
		//alert("action: " + form.action.value);
		
		form.submit();		
	}
	
	// pre-select all categories by checking the "all" checkbox programmatically
	<% if ("text".equalsIgnoreCase(paramAction)) { %>
		if ((<%= org.opencms.util.CmsStringUtil.escapeHtml(request.getParameter("all")) != null %>) || (<%= CmsStringUtil.isEmptyOrWhitespaceOnly(categoryTree.getSelectedCategories()) %>)) {
			initCategories(<%= treeSize %>);
		}
	<% } else { %>
		if (<%= categoryTree.getPreSelectAllCategories() %>) {
			initCategories(<%= treeSize %>);
		}
	<% } %>
  //-->
  </script>
  
  <% if (!"searchText".equalsIgnoreCase(action)) { %>
  	<%= CmsCalendarWidget.calendarInit(messages, "fromDate", "triggercalendarfrom", "cR", false, false, true, null, false) %>
  	<%= CmsCalendarWidget.calendarInit(messages, "toDate", "triggercalendarto", "cR", false, false, true, null, false) %>
  <% } %>
  
