<%@ page
	import="org.opencms.util.*,javax.servlet.jsp.*,org.opencms.file.*,org.opencms.jsp.*,org.opencms.i18n.CmsMessages,org.opencms.file.history.*,org.opencms.site.*,java.util.*,com.alkacon.opencms.v8.documentcenter.*,org.opencms.file.history.*"
	buffer="none"%>
<%
    // initialise Cms Action Element
			CmsDocumentFrontend cms = new CmsDocumentFrontend(pageContext,
					request, response);

			// Collect the objects required to access the OpenCms VFS from the request
			CmsObject cmsObject = cms.getCmsObject();
			String uri = cmsObject.getRequestContext().getUri();
			String folderUri = cms.getRequestContext().getFolderUri();

			// get locale and message properties
			String locale = request.getParameter("locale");
			if (locale == null) {
				locale = cms.getRequestContext().getLocale().toString();
			}
			CmsMessages messages = cms.getMessages(
					"com.alkacon.opencms.v8.documentcenter.messages_documents",
					locale);

			List hiddenCols = cms.getHiddenColumns();
			boolean showDcColumn = !hiddenCols
					.contains(CmsDocumentFrontend.COLUMN_NAME_DATECREATED);
			boolean showDmColumn = !hiddenCols
					.contains(CmsDocumentFrontend.COLUMN_NAME_DATEMODIFIED);
			String cssClassDm = "docdatechanged";
			if (!showDcColumn) {
				cssClassDm = "docdatecreated";
			}

			String versionDoc = request.getParameter("document");

			CmsDocument doc = CmsDocumentFactory.createDocumentWithVersions(
					cms, versionDoc);
			if (!doc.isNullDocument()) {
%>
<div style="margin: 8px"><h2><%= messages.key("versions.headline") %></h2>
<p>
<form>
	&nbsp;&nbsp;<a class="button-w btn" onclick="history.back()"><%= messages.key("link.back") %></a>
</form>
</p>
</div>

<!-- doclist start -->
<table class="downloadcenter" border="0" cellspacing="0"
	summary="Table with historic versions of document <%=versionDoc%>">
	<thead>
		<tr>
			<th><%=messages.key("documentlist.headline.type")%></th>
			<th><%=messages.key("documentlist.headline.version")%></th>
			<th><%=messages.key("documentlist.headline.title")%></th>
			<th><%=messages.key("documentlist.headline.size")%></th>
			<%
			    if (showDmColumn) {
			%><th><%=messages
									.key("documentlist.headline.datemodified")%></th>
			<%
			    }
			%>
			<%
			    if (showDcColumn) {
			%><th><%=messages
											.key("documentlist.headline.datecreated")%></th>
			<%
			    }
			%>
		</tr>
		<tr>
			<%
			    for (int i = 0; i < doc.getLocales().size(); i++) {
								Locale docLoc = (Locale) doc.getLocales().get(i);
								CmsDocument currDoc = (CmsDocument) doc
										.getLocalizedDocument(docLoc);

								if (cms.isUseLanguages()) {
			%>
		
		<tr>
			<td class="litte_font"></td>
			<td class="litte_font"></td>
			<td class="litte_font"><b><%=docLoc.getDisplayLanguage(new Locale(
												locale))%></b></td>
			<td class="litte_font"></td>
			<%
			    if (showDmColumn) {
			%><td class="litte_font"></td>
			<%
			    }
			%>
			<%
			    if (showDcColumn) {
			%><td class="litte_font"></td>
			<%
			    }
			%>
		</tr>
		<%
		    }

							List documentList = cmsObject
									.readAllAvailableVersions(currDoc.getPath());

							// some documents are present, show list
							if (documentList.size() > 0) {

								String resources = "/system/modules/com.alkacon.opencms.v8.documentcenter/resources/";
								String resPath = cms.link(resources);
								String docNew = "<img src=\""
										+ resPath
										+ "ic_doc_new.gif\" width=\"12\" height=\"11\" border=\"0\" alt=\""
										+ messages.key("documentlist.icon.new")
										+ "\" title=\""
										+ messages.key("documentlist.icon.new") + "\">";
								String docModified = "<img src=\""
										+ resPath
										+ "ic_doc_modified.gif\" width=\"8\" height=\"11\" border=\"0\" alt=\""
										+ messages
												.key("documentlist.icon.updatesimple")
										+ "\" title=\""
										+ messages
												.key("documentlist.icon.updatesimple")
										+ "\">";

								Iterator allDocuments = documentList.iterator();
								int rowCount = 0;
								while (allDocuments.hasNext()) {
									String rowClass = "";
									if (rowCount % 2 == 1) {
										rowClass = " class=\"alternate\"";
									}

									CmsHistoryFile currentDocument = (CmsHistoryFile) allDocuments
											.next();
									if (cmsObject.existsResource(cmsObject
											.getRequestContext().removeSiteRoot(
													currentDocument.getRootPath()))
											&& currentDocument
													.getRootPath()
													.startsWith(
															cmsObject
																	.getRequestContext()
																	.getSiteRoot())) {
		%>
		<tr <%=rowClass%>>
			<td class="little_font"><%=cms.buildDocIcon(currentDocument
												.getName(), messages,
												resources, false)%></td>
			<td class="little_font">
			<%
			    int versionId = 0;
											int tagId = 0;
											try {
												versionId = currentDocument.getVersion();
												tagId = currentDocument.getPublishTag();
											} catch (Exception e) {
											}
			%><%=versionId%></td>
			<td class="little_font">
			<%
			    List properties = cmsObject
													.readHistoryPropertyObjects(currentDocument);
											Map backupProperties = CmsProperty
													.toMap(properties);
											String backupTitle = (String) backupProperties
													.get("Title");
											if (backupTitle == null
													|| "".equals(backupTitle.trim())) {
												backupTitle = currentDocument.getName();
											}
			%><a
				href="<%=cms
														.link(CmsHistoryResourceHandler.HISTORY_HANDLER)%><%=currentDocument.getRootPath()%>?version=<%=versionId%>"
				title="<%=messages
												.key("documentlist.link.open")%>"><%=backupTitle%></a><br>
			(<%=currentDocument.getName()%>)</td>
			<td class="little_font">
			<%
			    int docSize = 0;
											try {
												docSize = currentDocument.getLength();
											} catch (Exception e) {
											}

											CmsUUID idUserLm = currentDocument
													.getUserLastModified();
											CmsUUID idUserCr = currentDocument
													.getUserCreated();
											CmsUser userLm = cms.getCmsObject().readUser(
													idUserLm);
											CmsUser userCr = cms.getCmsObject().readUser(
													idUserCr);
			%><%=Math.round(Math
												.ceil(docSize / 1024.0))
										+ "&nbsp;"
										+ messages.key("documentlist.size.kb")%></td>
			<%
			    if (showDmColumn) {
			%><td class="little_font"><%=currDoc
															.formatDate(
																	currentDocument
																			.getDateLastModified(),
																	true)%><br><%=userLm.getName()%></td>
			<%
			    }
			%>
			<%
			    if (showDcColumn) {
			%><td class="little_font"><%=currDoc.formatDate(
													currentDocument
															.getDateCreated(),
													true)%><br><%=userCr.getName()%></td>
			<%
			    }
			%>
		</tr>
		<%
		    rowCount++;
									} // end test if resource exists
								}
								allDocuments = null;

							}
							// no documents present, show message
							else {
		%>
		<tr>
			<td class="little_font">&nbsp;</td>
			<td class="little_font">&nbsp;</td>
			<td class="little_font"><%=messages
												.key("versions.error.nodocumentversions")%></td>
			<td class="little_font">&nbsp;</td>
			<%
			    if (showDmColumn) {
			%><td class="little_font">&nbsp;</td>
			<%
			    }
			%>
			<%
			    if (showDcColumn) {
			%><td class="little_font">&nbsp;</td>
			<%
			    }
			%>
		</tr>
		<%
		    }

						}
		%>
		<tr>
			<td class="little_font">&nbsp;</td>
			<td class="little_font">&nbsp;</td>
			<td class="little_font">&nbsp;</td>
			<td class="little_font">&nbsp;</td>
			<%
			    if (showDmColumn) {
			%><td class="little_font">&nbsp;</td>
			<%
			    }
			%>
			<%
			    if (showDcColumn) {
			%><td class="little_font">&nbsp;</td>
			<%
			    }
			%>
		</tr>
</table>
<!-- doclist end -->
<%
    } else { // end if null document
%>
<div style="margin: 8px">
<h2><%=messages.key("versions.error.nodocumentversions")%></h2>
<p>
<form>&nbsp;&nbsp;<input type="button" class="button-w"
	value="<%=messages.key("link.back")%>" onclick="history.back();"/>

</form>
</p>
</div>
<%
    }
%>