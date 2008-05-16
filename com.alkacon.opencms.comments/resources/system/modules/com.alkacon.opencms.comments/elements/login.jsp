<%@ page import="org.opencms.jsp.*" %><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%><%--
--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%
%><%
    CmsJspLoginBean cms = new CmsJspLoginBean(pageContext, request, response);
    if ("login".equals(request.getParameter("action"))) {
        cms.login(request.getParameter("name"), request.getParameter("password"), "Online");
        if (cms.getLoginException() == null) {
            out.print("ok");
        } else {
            out.print(cms.getLoginException().getLocalizedMessage());
        }
    	return; 
    }
%>
<fmt:setLocale value="${cms:vfs(pageContext).requestContext.locale}" />
<fmt:setBundle basename="com.alkacon.opencms.comments.frontend" />
<div id="login_dlg" style="display: none">
	<div id="errmsg" class="login-errormessage" >&nbsp;<br>&nbsp;</div>
	<form class="loginform" id="fid">
		<div class="boxform">
			<label for="name"><fmt:message key="login.label.username" />:</label>
			<input type="text" name="name">
		</div>
		<div class="boxform">
			<label for="password"><fmt:message key="login.label.password" />:</label>
			<input type="password" name="password">
		</div>
		<input type="hidden" name="action" value="login" />
		<input type="hidden" name="requestedResource" value="${param.requestedResource}" />
		<input type="hidden" name="__locale" value="${param.__locale}" />
	</form>
</div>
<script type="text/javascript">
var loginInit = false;
function loginDialog() {
	if (!loginInit) {
		$('#login_dlg').dialog({
			buttons: {
				'<fmt:message key="login.label.login" />': function() { 
				     $("div#errmsg_cnt").html('&nbsp;<br>&nbsp;');
				     $.post("<cms:link>/system/modules/com.alkacon.opencms.comments/elements/login.jsp</cms:link>?" + $("form#fid").serialize(), 
					      function(txt) {
							  if (txt == 'ok') {
							     location.href = '<cms:link>${param.requestedResource}</cms:link>';
							  } else {
							     $("div#errmsg").html('<fmt:message key="login.message.failed" />:<br />' + txt);
							  }
					      }
					);
				},
				'<fmt:message key="login.label.cancel" />': function() { 
				     $("div#errmsg_cnt").html('&nbsp;<br>&nbsp;');
				     $(this).dialog('close'); 
				}
			},
			autoOpen: false, modal: true, resizable: false, 
			width: 300, height: 200,
			overlay: { backgroundColor:'#000', opacity: '0.6' },
			title: '<fmt:message key="login.message.enterdata" />'
		});
		$('#login_dlg').show();
		loginInit = true;
	}
	$('#login_dlg').dialog('open');	
}
</script>
