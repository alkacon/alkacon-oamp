group commentform : webform;

form(formuri,enctype,errormessage,mandatorymessage,formconfig,fields,downloadbutton,submitbutton,resetbutton,hiddenfields,prevbutton,subfieldjs) ::= <<
$! ###### Main input form ###### !$
<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	<h4 class="modal-title" id="cmtFormModalLabel">$formconfig.title$</h4>
	$! --- show validation errors -- !$
	$if(errormessage)$
	<p><span class="webform_msg_error">
		$errormessage$
		</span></p>
	$endif$
</div>
<div class="modal-body" id="cmtModalBody">
	$formconfig.formText$
	<form class="form-horizontal" role="form" id="cmtFormComment" name="commentform"
		action="$formuri$"
		method="post"$enctype$ $formconfig.formAttributes$>
		$fields$
		
		$! --- show mandatory note --- !$
		$if(mandatorymessage)$
		<div class="help-block">$mandatorymessage$</div>
		$endif$
		$hiddenfields$
		<input type="hidden" name="formaction" id="formaction" value="submit" />
		$! <input class="cmtButton" type="button" value="$submitbutton$" onclick="cmtPost(); " /> !$
		$! <input class="cmtButton" type="button" value="$resetbutton$" onclick="\$.colorbox.close();" /> !$
	</form>
	$formconfig.formFooterText$
</div>
<div class="modal-footer" id="cmtModalFooter">
	<button id="cmtFormCancelButton" type="button" class="btn btn-default" data-dismiss="modal">$resetbutton$</button>
	<button id="cmtFormSubmitButton" type="button" class="btn btn-primary">$submitbutton$</button>
</div>
>>

label(field,formconfig,errormessage,mandatory) ::= <<
$! ###### Standard LABEL for a field ###### !$
<label class="control-label" for="$field.name$">
		$field.label$$mandatory$
</label>
>>

row_start(field,errormessage) ::= <<
$! ###### Standard start row for a field ###### !$
$if(field.showRowStart)$
	<div class="form-group
		$if(errormessage)$has-error$endif$
	">
$endif$
>>

field_text(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXT field ###### !$
$row_start(field=field, errormessage=errormessage)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
	<input 	type="text" 
		   	name="$field.name$" 
			id="$field.name$" 
			value="$field.valueEscaped$" 
			class="form-control" 
			$formconfig.formFieldAttributes$
	/>
	$if(errormessage)$
	<p class="help-block">
		$errormessage$
	</p>
	$endif$
$row_end(field=field,formconfig=formconfig)$
>>

field_textarea(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXTAREA field ###### !$
$row_start(field=field,errormessage=errormessage)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="webform_field_multi">
	<textarea cols="5" rows="5" name="$field.name$" id="$field.name$" class="form-control" $formconfig.formFieldAttributes$$attributes$>$field.valueEscaped$</textarea>
	$if(errormessage)$
	<p class="help-block">
		$errormessage$
	</p>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>

field_captcha(field,formconfig,attributes,errormessage,mandatory,captcha) ::= <<
$! ###### CAPTCHA field ###### !$
$row_start(field=field, errormessage=errormessage)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
	<div>
		$captcha$
		<input type="text" name="$field.name$" id="$field.name$" value="$field.valueEscaped$" class="form-control" $formconfig.formFieldAttributes$/>
		$if(errormessage)$
		<p class="help-block">
			$errormessage$
		</p>
		$endif$
	</div>
$row_end(field=field,formconfig=formconfig)$
>>

formscript(formlink,isguest,username,useremail,namefield,emailfield,commentfield,charleft) ::= <<
<script type="text/javascript">

function bindEnter(e,button) {
	var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
	if (key == 13 && button.css('display') != 'none') {
		button.click();
		return false;
	} else {
		return true;
	}
}

\$("form#cmtFormComment input").keypress(function (e) { 
	bindEnter(e, \$( "#cmtFormSubmitButton" ) ); 
});

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
</script>

>>


confirmationoutput(formconfig,closebutton) ::= <<
		<div class="modal-header">
			<button type="button" class="cmtReloadComments close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title" id="cmtFormModalLabel">$formconfig.title$</h4>
		</div>
		<div class="modal-body">
			$formconfig.formConfirmationText$
		</div>
		<div class="modal-footer">
			<button type="button" id="cmtFormConfirmationCloseButton" class="cmtReloadComments btn btn-default" data-dismiss="modal">$closebutton$</button>
		</div>
<script type="text/javascript">
function bindEnter(e,button) {
	var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
	if (key == 13 && button.css('display') != 'none') {
		button.click();
		return false;
	} else {
		return true;
	}
}

\$(window).keypress(function (e) { 
	bindEnter(e, \$( "#cmtFormConfirmationCloseButton" ) ); 
});
</script>
>>

emailerror(headline,text,error,closebutton) ::= <<
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h4 class="modal-title" id="cmtFormModalLabel">$headline$</h4>
		</div>
		<div class="modal-body">
			<h5>
				$error$
			</h5>
			<p>
				$text$
			</p>
		</div>
		<div class="modal-footer">
			<button id="cmtEmailErrorCloseButton" type="button" class="btn btn-default" data-dismiss="modal">$closebutton$</button>
		</div>
<script type="text/javascript">
function bindEnter(e,button) {
	var key = (window.event) ? window.event.keyCode : (e) ? e.which : 0;
	if (key == 13 && button.css('display') != 'none') {
		button.click();
		return false;
	} else {
		return true;
	}
}

\$(window).keypress(function (e) { 
	bindEnter(e, \$( "#cmtEmailErrorCloseButton" ) ); 
});
</script>	
>>