<%@ page import="com.alkacon.opencms.comments.*" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
%><%
    CmsCommentsAccess alkaconCmt = new CmsCommentsAccess(pageContext, request, response);
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
			<input type="hidden" name="__locale" value="${param.__locale}" />
			<input class="cmtButton" type="button" value="<fmt:message key="login.label.login" />" onclick="cmtLogin();"/>
			<input class="cmtButton" type="button" value="<fmt:message key="login.label.cancel" />" onclick="tb_remove();"/>
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
     $.post("<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_login.jsp:87972a79-12be-11dd-a2ad-111d34530985)</cms:link>",
     		$("form#fid").serializeArray(), 
	      function(txt) {
			  if (txt == 'ok') {
			       tb_remove();
				$.post(
					"<cms:link>%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_list.jsp:f11cf62d-ec2e-11dc-990f-dfec94299cf1)</cms:link>",
					{ cmturi:'${param.cmturi}', __locale: '<cms:info property="opencms.request.locale" />' },
					function(html) { $("#commentbox").html(html); }
				);
			  } else {
			     $("div#errmsg").addClass("cmtErrorMessage").html('<fmt:message key="login.message.failed" />:<br />' + txt);
			  }
	      }
     );
}
$("#TB_title").addClass("cmt_TB_title");
$("#TB_closeAjaxWindow").addClass("cmt_TB_closeAjaxWindow");
$("#TB_ajaxContent").addClass("cmt_TB_ajaxContent");
$("#TB_ajaxWindowTitle").addClass("cmt_TB_ajaxWindowTitle");
</script>
