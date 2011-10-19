  <%@ page session="false" buffer="none"
 import="org.opencms.file.*, 
 org.opencms.i18n.*,
 java.util.*,
 com.alkacon.opencms.v8.documentcenter.*,
 org.opencms.util.*" %>

<%
	// helper variables for layout of main categories (initialize with "right" so first test switches to "left")
	String column = "right";

	// get the main categories of the document center folder
	String startPath = (String)request.getAttribute(CmsDocumentFrontend.ATTR_STARTPATH);
	CmsCategory categories = new CmsCategory(cms.getCmsObject(), startPath, "category");
	List mainCat = categories.getMainCategories();

	String sorting = cms.property("categorySorting", "search", CmsCategory.C_CATEGORY_SORT_ORDER_RIGHT);

	Iterator mainIterator = mainCat.iterator();

	if( !mainIterator.hasNext() ) {
		out.println( "<p class=\"error\">" +messages.key("categories.error.nocategories") +"</p>" );
	} else {
		out.println( "</div>" ); // zunaechst: <div class="download_onecol"> aus uebergeordnetem JSP schliessen

// iterate over all main categories
while (mainIterator.hasNext()) {
	CmsCategory curMain = (CmsCategory)mainIterator.next();
	
	// determine the current column
	if( column.equals("right") ) {
		column = "left";
	} else {
		column = "right";
	}

	out.println( "<div class=\"download_" +column +"col\">" );

	if (!"".equals(curMain.getTitle())) { 
	    %><h3><a href="<%= CmsDocumentFactory.getLink(cms, curMain.getCmsResource()) %>"><%= curMain.getTitle() %></a></h3><%
	} else { 
		%>&nbsp;<%
	}

	// ------------------------- <subcategory> --------------------------------
	// get the subcategories of the current main category
	List subCat = new ArrayList();
	if (!"".equals(curMain.getPosition())) {
		subCat = categories.getSubCategory(curMain.getPosition(), sorting);
	}
	Iterator subIterator = subCat.iterator();
	
	if( !subIterator.hasNext() ) {
		out.print("&nbsp;");
	} else {
		out.println("<ul>");
		while (subIterator.hasNext()) {
			CmsCategory curSub = (CmsCategory)subIterator.next();
			if (!"".equals(curSub.getTitle())) { 
				%><li class="nobullet"><a class="link-folder" href="<%= CmsDocumentFactory.getLink(cms, curSub.getCmsResource()) %>"><%= curSub.getTitle() %></a></li><%
			}
		}
		out.println("</ul>");
	}

	// clear objects to release memory
	subIterator = null;
	subCat = null;

	// ------------------------- <subcategory> --------------------------------


	out.println( "</div>" );
	if( column.equals("right") ) {
		out.println( "<div class=\"download_onecol\"></div>" );
	}
}	 // end of main categories iteration
		out.println( "<div class=\"download_onecol\">" ); // zunaechst: <div class="download_onecol"> fuer uebergeordnetes JSP oeffnen
	} // end-if( !categoriesPresent )

	// clear objects to release memory
	mainIterator = null;
	mainCat = null;
%>