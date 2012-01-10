group commentform : webform;

form(formuri,enctype,errormessage,mandatorymessage,formconfig,fields,downloadbutton,submitbutton,resetbutton,hiddenfields,prevbutton,subfieldjs) ::= <<
$! ###### Main input form ###### !$
$formconfig.formText$
$! --- show validation errors -- !$
$if(errormessage)$
	<p><span class="webform_msg_error">
	$errormessage$
	</span></p>
$endif$
<div class="cmtDialog">
<form class="cmtForm" id="fid" name="commentform"
	action="$formuri$"
	method="post"$enctype$ $formconfig.formAttributes$>
<div class="webform_wrapper">
$fields$

$! --- show mandatory note --- !$
$if(mandatorymessage)$
	<div class="webform_mandatory">$mandatorymessage$</div>
$endif$

</div>
$! --- show form footer text --- !$
$formconfig.formFooterText$

<div class="cmtButtonRow">
	$hiddenfields$
	<input type="hidden" name="formaction" id="formaction" value="submit" />
	<input class="cmtButton" type="button" value="$submitbutton$" onclick="cmtPost(); " />
	<input class="cmtButton" type="button" value="$resetbutton$" onclick="\$.colorbox.close();" />
</div>
</form>
</div>
>>


formscript(formlink,isguest,username,useremail,namefield,emailfield,commentfield,charleft) ::= <<
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
\$("form#fid input").keypress(submitEnter);

function cmtPost() {
     \$.post("$formlink$", 
     		\$("form#fid").serializeArray(),
	      function(txt) {
			  if (txt == 'ok') {
			     \$.colorbox.close();
			     reloadComments();
			  } else {
			     \$("div#cboxLoadedContent").html(txt);
			  }
	      }
	);
}
$if(!isguest)$
	$if(namefield)$
		var nameField = "$namefield.name$";
		var nameFieldValue = "$username$";
		if (\$("input[name='"+nameField+"']").attr('value')) {
			if (\$("input[name='"+nameField+"']").attr('value') == '') {
				\$("input[name='"+nameField+"']").attr('value', nameFieldValue);
			}
		} else {
			\$("input[name='"+nameField+"']").attr('value', nameFieldValue);
		}
	$endif$ 
       $if(emailfield)$
		var emailField = "$emailfield.name$";
		var emailFieldValue = '$useremail$';
		if (\$("input[name='"+emailField+"']").attr('value')) {
			if (\$("input[name='"+emailField+"']").attr('value') == '') {
				\$("input[name='"+emailField+"']").attr('value', emailFieldValue);
			}
		} else {
			\$("input[name='"+emailField+"']").attr('value', emailFieldValue);
		}
	$endif$
$endif$
$if(commentfield)$
	var commentField = "$commentfield.name$";
	var maxLength = 1000;
	\$("textarea[name='"+commentField+"']").before("<div style='width: 99%; text-align: right; font-size: 9px;'>$charleft$</div>");
	function updateComment() {
	        var value = \$("textarea[name='"+commentField+"']").attr('value');
	        if (value && (value.length > maxLength)) {
			\$("textarea[name='"+commentField+"']").attr('value', value.substring(0, maxLength));
		} else {
			\$("#comment_charleft").text(maxLength - (value? value.length:0));
		}
	}
	\$("textarea[name='"+commentField+"']").keydown(updateComment);
	\$("textarea[name='"+commentField+"']").keyup(updateComment);
	\$("textarea[name='"+commentField+"']").change(updateComment);
	updateComment();
$endif$

\$("#cboxTitle").addClass("cmt_TB_title");
\$("#cboxClose").addClass("cmt_TB_closeAjaxWindow");
\$("#cboxLoadedContent").addClass("cmt_TB_ajaxContent");
\$("#cboxTitle").addClass("cmt_TB_ajaxWindowTitle");
\$("input[name^='InputField'][type='text']").css("width", "100%");
\$("textarea[name^='InputField']").css("width", "100%");
</script>

>>


confirmationoutput(formconfig,closebutton) ::= <<
$formconfig.formConfirmationText$
<div class="cmtDialog">
	<form class="cmtForm" id="fid" name="commentform" $formconfig.formAttributes$>
		<div class="cmtButtonRow">
			<input class="cmtButton" type="button" value="$closebutton$" onclick="\$.colorbox.close(); reloadComments();"/>
		</div>
	</form>
</div>      
<script type="text/javascript">
function submitEnter(e) {
    var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
    if (key == 13) {
       \$.colorbox.close(); 
       reloadComments();
       return false;
    } else {
       return true;
    }
}
\$(window).keypress(submitEnter);
\$("#cboxTitle").addClass("cmt_TB_title");
\$("#cboxClose").addClass("cmt_TB_closeAjaxWindow");
\$("#cboxLoadedContent").addClass("cmt_TB_ajaxContent");
\$("#cboxTitle").addClass("cmt_TB_ajaxWindowTitle");
\$("input[name^='InputField'][type='text']").css("width", "100%");
\$("textarea[name^='InputField']").css("width", "100%");
</script>
>>