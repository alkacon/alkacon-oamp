<%@ page import="com.alkacon.opencms.v8.comments.*" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
%><%
    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response, request.getParameter("configUri"));
    if ("login".equals(request.getParameter("action"))) {
        alkaconCmt.login(request.getParameter("name"), request.getParameter("password"), "Online");
        if (alkaconCmt.getLoginException() == null) {
            out.print("ok");
        } else {
            out.print(alkaconCmt.getLoginException().getLocalizedMessage());
        }
    	return; 
    }
    pageContext.setAttribute("alkaconCmt", alkaconCmt);
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="${alkaconCmt.resourceBundle}" />
<div class="cmtDialog">
	<form class="cmtForm" id="fid">
		<div id="errmsg" ><fmt:message key="login.message.enterdata" /><br>&nbsp;</div>
		<div class="cmtFormRow">
			<label for="name"><fmt:message key="login.label.username" />:</label>
			<input type="text" name="name">
		</div>
		<div class="cmtFormRow">
			<label for="password"><fmt:message key="login.label.password" />:</label>
			<input type="password" name="password">
		</div>
		<div class="cmtFormRow cmtButtonRow">
			<input type="hidden" name="action" value="login" />
			<input type="hidden" name="cmturi" value="${param.cmturi}" />
			<input type="hidden" name="cmtminimized" value="${param.cmtminimized}" />
	    	<input type="hidden" name="cmtlist" value="${param.cmtlist}" />
	    	<input type="hidden" name="cmtsecurity" value="${param.cmtsecurity}" />
			<input type="hidden" name="__locale" value="${param.__locale}" />
			<input class="cmtButton" type="button" value="<fmt:message key="login.label.login" />" onclick="cmtLogin();"/>
			<input class="cmtButton" type="button" value="<fmt:message key="login.label.cancel" />" onclick="$.colorbox.close();"/>
		</div>
	</form>
</div>
<script type="text/javascript">
function submitEnter(e) {
    var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
    if (key == 13) {
       cmtLogin();
       return false;
    } else {
       return true;
    }
}
$("form#fid input").keypress(submitEnter);

function cmtLogin() {
     $("div#errmsg_cnt").html('&nbsp;<br>&nbsp;');
     $.post("<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_login.jsp:563c05e2-15df-11e1-aeb4-9b778fa0dc42)</cms:link>",
     		$("form#fid").serializeArray(), 
	      function(txt) {
			  if (txt == 'ok') {
			      $.colorbox.close();
				$.post(
					"<cms:link>%(link.weak:/system/modules/com.alkacon.opencms.v8.comments/elements/comment_list.jsp:5639bbed-15df-11e1-aeb4-9b778fa0dc42)</cms:link>",
					{ 
					    cmturi:'${param.cmturi}', 
					    cmtminimized:"${param.cmtminimized}",
				        cmtlist:"${param.cmtlist}",
				        cmtsecurity:"${param.cmtsecurity}",
					    configUri: '${param.configUri}', 
					    __locale: '<cms:info property="opencms.request.locale" />' },
					function(html) { $("#commentbox").html(html); }
				);
			  } else {
			     $("div#errmsg").addClass("cmtErrorMessage").html('<fmt:message key="login.message.failed" />:<br />' + txt);
			  }
	      }
     );
}
$("#cboxTitle").addClass("cmt_TB_title");
$("#cboxClose").addClass("cmt_TB_closeAjaxWindow");
$("#cboxLoadedContent").addClass("cmt_TB_ajaxContent");
</script>
