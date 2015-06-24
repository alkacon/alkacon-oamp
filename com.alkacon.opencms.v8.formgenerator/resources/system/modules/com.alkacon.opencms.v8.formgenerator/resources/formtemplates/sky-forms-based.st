group webform;

form_twocolumns() ::= "false"


form(formuri,enctype,errormessage,mandatorymessage,formconfig,fields,downloadbutton,submitbutton,resetbutton,hiddenfields,prevbutton,subfieldjs) ::= <<
$! ###### Main input form ###### !$
$if(formconfig.formText)$$formconfig.formText$$endif$

<form name="emailform$formconfig.configId$" id="emailform$formconfig.configId$" action="$formuri$"
	method="post"$enctype$ $formconfig.formAttributes$>

$! --- show validation errors -- !$
$if(errormessage)$
	<header>
		$errormessage$
	</header>
$endif$	

<div style="display: none;"><input type="hidden" name="formaction$formconfig.configId$" id="formaction$formconfig.configId$" value="submit" /></div>
<fieldset>
	$fields$
</fieldset>

$if(formconfig.formMiddleText)$
  $! --- show form middle text --- !$
  <div class="webform_middletext"><br/> 
  	$formconfig.formMiddleText$
  </div>
$endif$

<footer>
$! --- show mandatory note --- !$
$if(mandatorymessage)$
	<div class="webform_mandatory">$mandatorymessage$</div>
$endif$

$! --- show submit, reset and previous button --- !$
$if(submitbutton)$

		$hiddenfields$
		$if(prevbutton)$
			<input type="submit" value="$prevbutton$" name="back" class="formbutton btn btn-gray prevbutton" />
		$endif$
		<input type="submit" value="$submitbutton$" class="formbutton btn btn-default submitbutton" />  
		$if(resetbutton)$
			&nbsp;<input type="reset" value="$resetbutton$" class="formbutton btn btn-gray resetbutton" />
		$endif$

$endif$

$! --- show download button -- !$
$if(downloadbutton)$
	<input type="submit"
		onclick="javascript:document.getElementById('formaction$formconfig.configId$').value='export1';"
		value="$downloadbutton$"
		class="formbutton btn btn-gray downloadbutton" />
$endif$
</footer>
</form>

$! --- JavaScript for sub fields --- !$
$if(subfieldjs)$
	<script type="text/javascript">$subfieldjs$</script>
$endif$

$if(formconfig.formFooterText)$
  $! --- show form footer text --- !$
  $formconfig.formFooterText$
$endif$
>>


form_js(formconfig,sessionjs,sessionuri,subfieldjs) ::= <<
$! ###### JAVASCRIPT for Main input form ###### !$
$if(sessionjs)$
<script type="text/javascript" src="$sessionjs$"></script>
<script type="text/javascript">
	var formgenRefreshSessionTimeout = $formconfig.refreshSessionInterval$;var formgenKeepSessionURI = "$sessionuri$";
	setTimeout("formgenKeepSession();", $formconfig.refreshSessionInterval$);
</script>
$endif$
<script type="text/javascript" src="$subfieldjs$"></script>
>>


field_captcha(field,formconfig,attributes,errormessage,mandatory,captcha) ::= <<
$! ###### CAPTCHA field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<label class="input$if(errormessage)$ state-error$endif$">
	$captcha$
	<input type="text" name="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_checkbox(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### Checkbox field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
$if(field.parameters)$$if(field.parameters.toggle)$$else$<div class="inline-group">$endif$$else$<div class="inline-group">$endif$
	$field.items:{
		<label class="$if(field.parameters)$$if(field.parameters.toggle)$toggle$else$checkbox$endif$$else$checkbox$endif$$if(errormessage)$ state-error$endif$">
			<input type="checkbox" name="$field.name$" value="$it.value$" class="check"$if(it.selected)$ checked="checked"$endif$/><i></i>$it.label$
		</label>
	}$
$if(field.parameters)$$if(field.parameters.toggle)$$else$</div>$endif$$else$</div>$endif$
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_display(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### DISPLAY field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<label class="input$if(errormessage)$ state-error$endif$">
	$if(field.valueEscaped)$
		$field.valueEscaped$
	$else$
		&nbsp;
	$endif$
	<input type="hidden" name="$field.name$" value="$field.valueEscaped$"/>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_email(field,formconfig,errormessage,mandatory) ::= <<
$! ###### EMAIL field ###### !$
$field_text(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
>>


field_empty(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### EMPTY field ###### !$
$row_start(field=field)$
<label class="label">&nbsp;</label>
<label class="input">
	$if(field.valueEscaped)$
		$field.valueEscaped$
	$else$
		&nbsp;
	$endif$
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_file(field,formconfig,attributes,errormessage,mandatory,infomessage) ::= <<
$! ###### FILE UPLOAD field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<label for="$field.name$" class="input input-file$if(errormessage)$ state-error$endif$$if(infomessage)$ state-info$endif$">
	<div class="button">
		<input type="file" name="$field.name$" id="$field.name$" onchange="document.getElementById('$field.name$-Visible').value = this.value" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
		$if(field.parameters)$$if(field.parameters.buttontext)$$field.parameters.buttontext$$else$Browse$endif$$else$Browse$endif$
	</div>
	<input type="text" id="$field.name$-Visible" readonly=""$if(field.parameters)$$if(field.parameters.placeholder)$ placeholder="$field.parameters.placeholder$"$endif$$endif$>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$if(infomessage)$
	<div class="note note-info">$infomessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_hidden(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### HIDDEN field ###### !$
<div style="display: none;"><input type="hidden" name="$field.name$" value="$field.valueEscaped$"/></div>
$subtext(field=field,formconfig=formconfig)$
>>


field_hiddendisplay(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### HIDDEN DISPLAY field ###### !$
$row_start(field=field)$
<label class="input">
	<input type="hidden" name="$field.name$" value="$field.valueEscaped$"/>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_paging(field,formconfig,attributes,errormessage,mandatory,hiddenfields,prevbutton,nextbutton,page) ::= <<
$! ###### PAGING field ###### !$
$row_start(field=field)$
<div class="webform_label">&nbsp;</div>
<div class="webform_field">
	$hiddenfields$
	$if(prevbutton)$
	<input type="submit" value="$prevbutton$" name="back$formconfig.configId$" class="formbutton btn prevbutton" />&nbsp;
	$endif$
	<input type="submit" value="$nextbutton$" class="formbutton btn nextbutton" />
	<input type="hidden" name="page$formconfig.configId$" value="$page$" />
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_password(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### PASSWORD field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<label class="input$if(errormessage)$ state-error$endif$">
	$if(field.parameters)$
		$if(field.parameters.icon)$
			<i class="icon-$if(field.parameters.position)$$field.parameters.position$$else$prepend$endif$ fa fa-$field.parameters.icon$"></i>
		$endif$
	$endif$
	<input type="password" name="$field.name$" value="$field.valueEscaped$"$if(field.parameters)$$if(field.parameters.placeholder)$ placeholder="$field.parameters.placeholder$"$endif$$endif$ $formconfig.formFieldAttributes$/>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_privacy(field,formconfig,attributes,errormessage,mandatory,label,link,linktext) ::= <<
$! ###### PRIVACY field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="inline-group">
	$field.items:{
		<label class="checkbox$if(errormessage)$ state-error$endif$">
			<input type="checkbox" name="$field.name$" value="$it.value$" class="check"$if(it.selected)$ checked="checked"$endif$/><i></i><a href="$link$" rel="_blank">$linktext$</a>
		</label>
	}$
</div>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_radio(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### RADIO BUTTONS field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
$field.items:{
	<label class="$if(field.parameters)$$if(field.parameters.toggle)$toggle$else$radio$endif$$else$radio$endif$$if(errormessage)$ state-error$endif$">
	<input type="radio" name="$field.name$" value="$it.value$" class="radio"$if(it.selected)$ checked="checked"$endif$$if(field.hasSubFields)$ onchange="toggleWebformSubFields(this);"$endif$/><i></i>$it.label$
	</label>
}$
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_select(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### SELECT field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<label class="select$if(errormessage)$ state-error$endif$">
	<select name="$field.name$" $if(formconfig.formFieldAttributes)$ $formconfig.formFieldAttributes$$endif$$if(field.hasSubFields)$ onchange="toggleWebformSubFields(this);"$endif$>
	$field.items:{
		<option value="$it.value$" $if(it.selected)$ selected="selected"$endif$>$it.label$</option>
	}$
	</select>
	<i></i>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_table(field,formconfig,attributes,errormessage,mandatory,cols,rows,rowswithitems,editable) ::= <<
$! ###### TABLE field ###### !$
$if(!field.showRowStart)$
	</div>
$endif$
<div class="webform_row">
<div class="webform_label_table">
	$field_table_labels(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory,rows=rows)$
</div>
<div class="webform_field_table">
	$field_table_fields(formconfig=formconfig,cols=cols,rowswithitems=rowswithitems,editable=editable)$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
</div>
$subtext(field=field,formconfig=formconfig)$
>>


field_table_labels(field,formconfig,errormessage,mandatory,rows) ::= <<
$! ###### Labels for the TABLE field, separate sub template because also used for email generation ###### !$
<table cellpadding="1" cellspacing="1" class="webform_label_table" width="100%"><thead>
	<tr style="height: 20px;">
		<th align='left' valign='top' style='font-weight: normal; '>
			$if(errormessage)$
				<span class="webform_msg_error">
					$field.label$$mandatory$
				</span>
			$else$
				$field.label$$mandatory$
			$endif$
		</th>
	</tr>
	$! --- iterate the row names --- !$
	$rows:{
	<tr style="height: 20px;">
		<th align='right' >$it$</th>
	</tr>
	}$
</thead><tbody><tr><td></td></tr></tbody></table>
>>


field_table_fields(formconfig,cols,rowswithitems,editable) ::= <<
$! ###### Rows for the TABLE field, separate sub template because also used for email generation ###### !$
<table cellpadding="1" cellspacing="1" class="webform_field_table" width="100%"><thead>
	<tr style="height: 20px;">
	$! --- iterate the column names --- !$
	$cols:{
		<th align='center' >$it$</th>
	}$
	</tr>
	</thead>
	<tbody>
	$! --- iterate the rows --- !$
	$rowswithitems:{
	<tr style="height: 20px;">
		$! --- iterate the items of the row --- !$
		$it:{
		<td>
			$if(editable)$
			<input type="text" name="$it.name$" value="$it.valueEscaped$" class="table"/>
			$else$
			<span class="table">$it.valueEscaped$</span>
			$endif$
		</td>
		}$
	</tr>
	}$
</tbody></table>
>>


field_text(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXT field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<label class="input$if(errormessage)$ state-error$endif$">
	$if(field.parameters)$
		$if(field.parameters.icon)$
			<i class="icon-$if(field.parameters.position)$$field.parameters.position$$else$prepend$endif$ fa fa-$field.parameters.icon$"></i>
		$endif$
	$endif$
	<input type="text" name="$field.name$" value="$field.valueEscaped$"$if(field.parameters)$$if(field.parameters.placeholder)$ placeholder="$field.parameters.placeholder$"$endif$$endif$ $formconfig.formFieldAttributes$/>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


field_textarea(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXTAREA field ###### !$
$row_start(field=field)$
<label class="label">
	$field.label$$mandatory$
</label>
<label class="textarea$if(errormessage)$ state-error$endif$">
	$if(field.parameters)$
		$if(field.parameters.icon)$
			<i class="icon-$if(field.parameters.position)$$field.parameters.position$$else$prepend$endif$ fa fa-$field.parameters.icon$"></i>
		$endif$
	$endif$
	<textarea cols="5" rows="5" name="$field.name$"$if(field.parameters)$$if(field.parameters.placeholder)$ placeholder="$field.parameters.placeholder$"$endif$$endif$ $formconfig.formFieldAttributes$$attributes$>$field.valueEscaped$</textarea>
</label>
$if(errormessage)$
	<div class="note note-error">$errormessage$</div>
$endif$
$row_end(field=field,formconfig=formconfig)$
>>


label(field,formconfig,errormessage,mandatory) ::= <<
$! ###### Standard LABEL for a field ###### !$
<label class="label">
	$field.label$$mandatory$
</label>
>>


row_start(field) ::= <<
$! ###### Standard start row for a field ###### !$
$if(field.showRowStart)$
	<section>
$endif$
>>


row_end(field,formconfig) ::= <<
$! ###### Standard end row for a field ###### !$
$if(field.showRowEnd)$
	</section>
$endif$
$subtext(field=field,formconfig=formconfig)$
>>


subfieldwrapper(subfields,style,id) ::= <<
$! ###### Sub field wrapper ###### !$
<div class="webform_subfields" id="$id$" style="$style$"><fieldset>
$subfields$
</fieldset></div>
>>


subtext(field,formconfig) ::= <<
$! ###### Intermediate texts for the fields ###### !$
$if(field.hasText)$
	$if(field.showRowStart)$
		<div class="webform_row">
	$endif$
	$if(field.text.columnLeft)$
		<div class="webform_text_left">
		$field.text.text$
		</div>
		<div class="webform_field"></div>
	$elseif(field.text.columnRight)$
		<div class="webform_label"></div>
		<div class="webform_text_right">
		$field.text.text$
		</div>
	$else$
		<div class="webform_text">
		$field.text.text$
		</div>
	$endif$
	$if(field.showRowEnd)$
		</div>
	$endif$
$endif$
>>


checkpage(formuri,formconfig,checktext,captchafield,captchaerror,captchaimagelink,checkfields,hiddenfields,checkbutton,correctbutton) ::= <<
$! ###### CHECK page of the form ###### !$
$checktext$
$if(captchafield)$
	<script type="text/javascript">
		function runConfirmValues() {
			document.getElementById("webformconfirmvalues$formconfig.configId$").elements["$captchafield.name$"].value = "" + document.getElementById("webformcaptcha$formconfig.configId$").elements["$captchafield.name$"].value;
			return true;
		}
	</script>
	<form id="webformcaptcha$formconfig.configId$" action="" method="post" enctype="multipart/form-data">
$endif$
<div class="webform_check_wrapper">
$checkfields:{
	<div class="webform_row">
		<div class="webform_label">$it.label$</div>
		<div class="webform_field">$it.value$</div>
	</div>
}$

$if(captchafield)$
	<div class="webform_row">
		<div class="webform_label">
			$if(captchaerror)$
				<span class="webform_msg_error">
					$captchafield.label$
				</span>
			$else$
				$captchafield.label$
			$endif$
		</div>
		<div class="webform_field">
			<img src="$captchaimagelink$" width="$captchafield.captchaSettings.imageWidth$" height="$captchafield.captchaSettings.imageHeight$" alt="" /><br />
			<input type="text" name="$captchafield.name$" value="" />
			$if(captchaerror)$
				<br/><span class="webform_label_error">
				$captchaerror$
				</span>
			$endif$
		</div>
	</div>
$endif$
</div>
$if(captchafield)$
	</form>
$endif$

<div class="webform_check_wrapper">
	<div class="webform_row">
		<form id="webformconfirmvalues$formconfig.configId$" method="post" enctype="multipart/form-data" action="$formuri$" onsubmit="return runConfirmValues();">
		<div style="display: none;">
		<input type="hidden" name="formaction$formconfig.configId$" value="confirmed" />
		<input type="hidden" name="captchaphrase$formconfig.configId$" value="" />
		$hiddenfields$
		</div><div class="webform_check_button">
		<input type="submit" value="$checkbutton$" class="formbutton" />&nbsp;&nbsp;&nbsp;&nbsp;</div>
		</form>
		<form id="webformdisplayvalues$formconfig.configId$" method="post" enctype="multipart/form-data" action="$formuri$">
		<div style="display: none;">
		<input type="hidden" name="formaction$formconfig.configId$" value="correct" />
		$hiddenfields$
		</div><div class="webform_check_button">
		<input type="submit" value="$correctbutton$" class="formbutton" /></div>
		</form>
	</div>
</div>
>>


confirmationpage(formconfig,confirmtext,confirmfields) ::= <<
$! ###### CONFIRMATION page of the form ###### !$
$confirmtext$
<div class="webform_confirm_wrapper">
$confirmfields:{
	<div class="webform_row">
		<div class="webform_label">$it.label$</div>
		<div class="webform_field">$it.value$</div>
	</div>
}$
</div>
>>


datadownloadpage(formuri,formconfig,locale,skinuri,labelfrom,datefrom,labelto,dateto,calendaralttext,submitbutton,resetbutton) ::= <<
$! ###### DATA DOWNLOAD page of the form ###### !$
<style type="text/css">
.webform_row {
	display: block;
	margin-bottom: 8px;
}
.webform_row label {
	display: inline-block;
	width: 160px;
}
.calendarinput {
	padding: 1px 2px 1px 2px; border-top: 1px solid #868686; border-right: 1px solid #ccc; border-bottom: 1px solid #ccc; border-left: 1px solid #868686; background-color: #fff; width: 150px;
}
.calendarbutton {
	height: 20px; width: 20px; border: 0px none; vertical-align: middle;
}
a.button {
	color: ButtonText; text-decoration: none; cursor: pointer;
}
</style>
<div>
	<form name="emailform$formconfig.configId$" action="$formuri$" method="post">
		<div class="webform_row">
			<label for="starttime">$labelfrom$</label>
			<input id="starttime" type="text" value="$datefrom$" name="starttime" class="calendarinput" />  
			<a href="#" class="button" title="$calendaralttext$" id="starttime.calendar"><img class="calendarbutton" src="$skinuri$buttons/calendar.png" alt="$calendaralttext$"></a>
		</div>
		<div class="webform_row">
			<label for="endtime">$labelto$</label>
			<input id="endtime" type="text" value="$dateto$" name="endtime" class="calendarinput" /> 
			<a href="#" class="button" title="$calendaralttext$" id="endtime.calendar"><img class="calendarbutton" src="$skinuri$buttons/calendar.png" alt="$calendaralttext$"></a>
		</div>
		<div class="webform_row">
			<input type="submit" value="$submitbutton$" class="formbutton" />&nbsp;&nbsp;
			<input type="reset" value="$resetbutton$" class="formbutton"/>
		</div>
		<input type="hidden" name="formaction$formconfig.configId$" value="export2" />
		<input type="hidden" name="__locale" value="$locale$" />
	</form>
</div>
>>


htmlemail(mailcss,mailtext,errorheadline,errors) ::= <<
$! ###### HTML email for the form ###### !$
<html><head>
<style type="text/css">
$if(mailcss)$
	$mailcss$
$else$
* { font-family: Verdana, Arial, Helvetica, sans-serif;  font-size: 12px; color: #000; }
h1 { font-size: 16px; font-weight: bold; }
p.foot { font-size: 9px; color: #555; }
table.dataform { border-collapse: collapse; border: 1px solid #333; }
table.dataform tr td { border-top: 1px solid #ccc; vertical-align:top; padding: 8px; }
table.dataform tr td.first { border-top: none; }
td.data { font-weight: bold; }
table.webform_label_table { vertical-align: top; }
table.webform_label_table tr th, table.webform_label_table tr td, table.webform_field_table tr th, table.webform_field_table tr td { white-space: nowrap; }
td.webform_label_table { vertical-align: top; }
table.webform_field_table thead, table.webform_field_table tbody, table.webform_label_table thead { margin: 0; padding: 0; }
table.webform_label_table tr th, table.webform_field_table tr td, table.webform_field_table tr th { height: 25px; }
table.webform_field_table tr td { padding-top: 0 !important; padding-bottom: 0 !important; }
table.webform_label_table tr th:first-child { vertical-align: middle; }
$endif$
</style>
</head><body>
$mailtext$
$if(errorheadline)$
	<h1>$errorheadline$</h1>
	<p>
	$errors:{
		$it$<br/>
	}$
	</p>
$endif$
</body></html>
>>


htmlemailfields(mailcss,fields) ::= <<
$! ###### FIELDS for the HTML email ###### !$
<table border="0" class="dataform$if(!mailcss)$ fields$endif$">
$first(fields):{
	<tr>
		<td class="first$if(!mailcss)$ fieldlabel$endif$"> $it.label$</td>
		<td class="first$if(mailcss)$ data$else$ fieldvalue$endif$">$it.value$</td>
	</tr>
}$
$rest(fields):{
	<tr>
		<td class="$if(!mailcss)$fieldlabel$endif$"> $it.label$</td>
		<td class="$if(mailcss)$data$else$fieldvalue$endif$">$it.value$</td>
	</tr>
}$
</table>
>>


emailerror(headline,text,error) ::= <<
$! ###### EMAIL error message ###### !$
<h3 class="webform_error_start">$headline$</h3>
<p class="webform_error_text_start">$text$</p>
<!--
        Error description: $error$
//-->
>>


initerror(headline,text) ::= <<
$! ###### Initialization error message ###### !$
<h1 class="webform_error_start">$headline$</h1>
<p class="webform_error_text_start">$text$</p>
>>