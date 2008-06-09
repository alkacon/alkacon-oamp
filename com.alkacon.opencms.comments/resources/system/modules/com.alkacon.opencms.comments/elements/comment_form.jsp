<%@page buffer="none" session="false" import="org.opencms.i18n.*,com.alkacon.opencms.comments.*, com.alkacon.opencms.formgenerator.*, java.util.*" %><%

// initialize the form handler
CmsCommentFormHandler cms = new CmsCommentFormHandler(pageContext, request, response, new CmsCommentsAccess(pageContext, request, response));
CmsCommentForm formConfiguration = cms.getCommentFormConfiguration();

// get the localized messages to create the form
CmsMessages messages = cms.getMessages();

boolean showForm = cms.showForm();
if (!showForm) {
	// form has been submitted with correct values
	// try to send a notification email with the submitted form field values
	if (cms.sendData()) {
	    // successfully submitted
	    if (cms.getFormConfirmationText().equals("")) {
	        // and no confirmation required
                out.print("ok");
	            return;
            }
            out.print(formConfiguration.getFormConfirmationText()); %>
<div class="cmtDialog">
	<form class="cmtForm" id="fid" name="commentform" <%= formConfiguration.getFormAttributes() %>>
		<div class="cmtButtonRow">
			<input class="cmtButton" type="button" value="<%= messages.key("form.button.close") %>" onclick="tb_remove(); reloadComments();"/>
		</div>
	</form>
</div>      
<script type="text/javascript">
function submitEnter(e) {
    var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
    if (key == 13) {
       tb_remove(); 
       reloadComments();
       return false;
    } else {
       return true;
    }
}
$(window).keypress(submitEnter);
</script>
<%	} else {
	    // problem while submitting
	    out.println("<h3>" + messages.key("form.error.mail.headline") + "</h3>");
	    out.println("<p>" + messages.key("form.error.mail.text") + "</p>");
	    out.println("<!-- Error description: " + cms.getErrors().get("sendmail") + "//-->");
	}
	return;
} 

// get the configured form elements
List fields = formConfiguration.getFields();

// show form text 
out.print(formConfiguration.getFormText());

// show global error message if validation failed
if (cms.hasValidationErrors()) {
	out.print("<p>");
	out.print(messages.key("form.html.label.error.start"));
	out.print(messages.key("form.error.message"));
	out.print(messages.key("form.html.label.error.end"));
	out.println("</p>");
}
%>
<!-- create the form head  -->
<div class="cmtDialog">
	<form class="cmtForm" id="fid" name="commentform" <%= formConfiguration.getFormAttributes() %>>
<!-- Hidden form fields:  -->
<%= messages.key("form.html.start") %>
<%
// create the html output to display the form fields
int pos=0;
int place=0;
for( int i = 0, n = fields.size(); i < n; i++) {
	// loop through all form input fields 
	I_CmsField field = (I_CmsField)fields.get(i);
	
	if(i==n-1)place=1; //the last one must close the tr
	field.setPlaceholder(place);
	field.setPosition(pos);
	String errorMessage = (String)cms.getErrors().get(field.getName());
	
	out.println(field.buildHtml(cms, messages, errorMessage, formConfiguration.isShowMandatory()));
	pos=field.getPosition();
	place=field.getPlaceholder();
}

// create the form foot 
if (formConfiguration.hasMandatoryFields() && formConfiguration.isShowMandatory()) {
	%><%= messages.key("form.html.row.start") %>
	<%= messages.key("form.html.button.start") %><%= messages.key("form.message.mandatory") %><%= messages.key("form.html.button.end") %>
	<%= messages.key("form.html.row.end") %>
	<%
}
%>
<%= messages.key("form.html.end") %>
		<div class="cmtButtonRow">
			<input type="hidden" name="<%= CmsFormHandler.PARAM_FORMACTION %>"  id="<%= CmsFormHandler.PARAM_FORMACTION %>" value="<%= CmsFormHandler.ACTION_SUBMIT %>"/>
			<input type="hidden" name="<%= CmsCommentsAccess.PARAM_URI %>" value="${param.cmturi}" />
			<input type="hidden" name="__locale" value="${param.__locale}" />
			<input class="cmtButton" type="button" value="<%= messages.key("form.button.submit") %>" onclick="cmtPost(); "/>
			<input class="cmtButton" type="button" value="<%= messages.key("form.button.cancel") %>" onclick="tb_remove();"/>
		</div>
</form>
<%
// show form footer text
out.print(formConfiguration.getFormFooterText());
%>
<script type="text/javascript">
function submitEnter(e) {
    var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
    if (key == 13) {
       cmtPost();
       return false;
    } else {
       return true;
    }
}
$("form#fid input").keypress(submitEnter);

function cmtPost() {
     $.post("<%=cms.link("%(link.strong:/system/modules/com.alkacon.opencms.comments/elements/comment_form.jsp:dfbece22-1112-11dd-ba60-111d34530985)")%>", 
     		$("form#fid").serializeArray(),
	      function(txt) {
			  if (txt == 'ok') {
			     tb_remove();
			     reloadComments();
			  } else {
			     $("div#TB_ajaxContent").html(txt);
			  }
	      }
	);
}
<% if (!cms.getRequestContext().currentUser().isGuestUser()) { 
       if (formConfiguration.getFieldByDbLabel("name") != null) { %>
var nameField = '<%=formConfiguration.getFieldByDbLabel("name").getName()%>';
var nameFieldValue = '<%= ("" + cms.getRequestContext().currentUser().getFirstname() + " " + cms.getRequestContext().currentUser().getLastname()).trim() %>';
if ($("input[@name='"+nameField+"']").attr('value')) {
	if ($("input[@name='"+nameField+"']").attr('value') == '') {
		$("input[@name='"+nameField+"']").attr('value', nameFieldValue);
	}
} else {
	$("input[@name='"+nameField+"']").attr('value', nameFieldValue);
}
<%     } 
       if (formConfiguration.getFieldByDbLabel("email") != null) { %>
var emailField = '<%=formConfiguration.getFieldByDbLabel("email").getName()%>';
var emailFieldValue = '<%=cms.getRequestContext().currentUser().getEmail()%>';
if ($("input[@name='"+emailField+"']").attr('value')) {
	if ($("input[@name='"+emailField+"']").attr('value') == '') {
		$("input[@name='"+emailField+"']").attr('value', emailFieldValue);
	}
} else {
	$("input[@name='"+emailField+"']").attr('value', emailFieldValue);
}
<%     } 
   } 
   if (formConfiguration.getFieldByDbLabel("comment") != null) { %>
var commentField = '<%=formConfiguration.getFieldByDbLabel("comment").getName()%>';
var maxLength = 1000;
$("textarea[@name='"+commentField+"']").before("<div style='width: 99%; text-align: right; font-size: 9px;'><%=messages.key("form.comment.char.left")%></div>");
function updateComment() {
        var value = $("textarea[@name='"+commentField+"']").attr('value');
        if (value && (value.length > maxLength)) {
		$("textarea[@name='"+commentField+"']").attr('value', value.substring(0, maxLength));
	} else {
		$("#comment_charleft").text(maxLength - (value? value.length:0));
	}
}
$("textarea[@name='"+commentField+"']").keydown(updateComment);
$("textarea[@name='"+commentField+"']").keyup(updateComment);
$("textarea[@name='"+commentField+"']").change(updateComment);
updateComment();
<%     } %>
</script>
