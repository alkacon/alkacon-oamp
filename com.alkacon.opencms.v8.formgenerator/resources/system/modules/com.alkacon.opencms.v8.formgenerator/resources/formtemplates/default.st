group webform;

form_twocolumns() ::= "false"


form(formuri,enctype,errormessage,mandatorymessage,formconfig,fields,downloadbutton,submitbutton,resetbutton,hiddenfields,prevbutton,subfieldjs) ::= <<
$! ###### Main input form ###### !$
$formconfig.formText$
$! --- show validation errors -- !$
$if(errormessage)$
	<p><span class="webform_msg_error">
	$errormessage$
	</span></p>
$endif$

<form id="emailform" action="$formuri$"
	method="post"$enctype$ $formconfig.formAttributes$>
<div style="display: none;"><input type="hidden" name="formaction" id="formaction" value="submit" /></div>
<div class="webform_wrapper">
$fields$

$! --- show form middle text --- !$
<div class="webform_middletext"><br/> 
	$formconfig.formMiddleText$
</div>

$! --- show mandatory note --- !$
$if(mandatorymessage)$
	<div class="webform_mandatory">$mandatorymessage$</div>
$endif$

$! --- show submit, reset and previous button --- !$
$if(submitbutton)$
	<div class="webform_button">
		$hiddenfields$
		$if(prevbutton)$
			<input type="submit" value="$prevbutton$" name="back" class="formbutton prevbutton" />
		$endif$
		<input type="submit" value="$submitbutton$" class="formbutton submitbutton" />  
		$if(resetbutton)$
			&nbsp;<input type="reset" value="$resetbutton$" class="formbutton resetbutton" />
		$endif$
	</div>
$endif$

$! --- show download button -- !$
$if(downloadbutton)$
	<div class="webform_button">
	<input type="submit"
		onclick="javascript:document.getElementById('formaction').value='export1';"
		value="$downloadbutton$"
		class="formbutton downloadbutton" />
	</div>
$endif$
</div></form>

$! --- JavaScript for sub fields --- !$
$if(subfieldjs)$
	<script type="text/javascript">$subfieldjs$</script>
$endif$

$! --- show form footer text --- !$
$formconfig.formFooterText$
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
<div class="webform_field">
	$captcha$
	<input type="text" name="$field.name$" id="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_checkbox(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### Checkbox field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="webform_field">
	$field.items:{
		$if(it.showInRow)$
			<div class="webform_checkbox_row_wrapper"><div class="webform_field_checkbox_row">
		$else$
			<div class="webform_checkbox_wrapper"><div class="webform_field_checkbox">
		$endif$
		<input type="checkbox" name="$field.name$" id="$field.name$" value="$it.value$" class="check"$if(it.selected)$ checked="checked"$endif$/>
		$if(it.showInRow)$
			</div><div class="webform_label_checkbox_row">
			$it.label$
			</div>
		$else$
			</div><div class="webform_label_checkbox">
			$it.label$
			</div>
		$endif$
		</div>
	}$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_display(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### DISPLAY field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="webform_field">
	$if(field.valueEscaped)$
		$field.valueEscaped$
	$else$
		&nbsp;
	$endif$
	<input type="hidden" name="$field.name$" id="$field.name$" value="$field.valueEscaped$"/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_email(field,formconfig,errormessage,mandatory) ::= <<
$! ###### EMAIL field ###### !$
$field_text(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
>>


field_empty(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### EMPTY field ###### !$
$row_start(field=field)$
<div class="webform_label">&nbsp;</div>
<div class="webform_field">
	$if(field.valueEscaped)$
		$field.valueEscaped$
	$else$
		&nbsp;
	$endif$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_file(field,formconfig,attributes,errormessage,mandatory,infomessage) ::= <<
$! ###### FILE UPLOAD field ###### !$
$row_start(field=field)$
<div class="webform_label">
	$if(errormessage)$
		<span class="webform_msg_error">
			<label for="$field.name$">$field.label$$mandatory$</label>
		</span>
	$elseif(infomessage)$
		<span class="webform_msg_info">
			<label for="$field.name$">$field.label$$mandatory$</label>
		</span>
	$else$
		<label for="$field.name$">$field.label$$mandatory$</label>
	$endif$
</div>
<div class="webform_field">
	<input type="file" name="$field.name$" id="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
	$if(infomessage)$
	<br/><span class="webform_label_info">$infomessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_hidden(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### HIDDEN field ###### !$
<div style="display: none;"><input type="hidden" name="$field.name$" id="$field.name$" value="$field.valueEscaped$"/></div>
$subtext(field=field,formconfig=formconfig)$
>>


field_hiddendisplay(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### HIDDEN DISPLAY field ###### !$
$row_start(field=field)$
<div class="webform_label">&nbsp;</div>
<div class="webform_field">

	<input type="hidden" name="$field.name$" id="$field.name$" value="$field.valueEscaped$"/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_paging(field,formconfig,attributes,errormessage,mandatory,hiddenfields,prevbutton,nextbutton,page) ::= <<
$! ###### PAGING field ###### !$
$row_start(field=field)$
<div class="webform_label">&nbsp;</div>
<div class="webform_field">
	$hiddenfields$
	$if(prevbutton)$
	<input type="submit" value="$prevbutton$" name="back" class="formbutton prevbutton" />&nbsp;
	$endif$
	<input type="submit" value="$nextbutton$" class="formbutton nextbutton" />
	<input type="hidden" name="page" value="$page$" />
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_password(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### PASSWORD field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="webform_field">
	<input type="password" name="$field.name$" id="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_privacy(field,formconfig,attributes,errormessage,mandatory,label,link,linktext) ::= <<
$! ###### PRIVACY field ###### !$
$row_start(field=field)$
<div class="webform_label">
	$if(errormessage)$
		<span class="webform_msg_error">
			$label$
		</span>
	$else$
		<label for="$field.name$">$label$</label>
	$endif$
</div>
<div class="webform_field">
	$field.items:{
		<input type="checkbox" name="$field.name$" id="$field.name$" value="$it.value$" class="check"$if(it.selected)$ checked="checked"$endif$/>
	}$
	<a href="$link$" rel="_blank">$linktext$</a>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_radio(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### RADIO BUTTONS field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="webform_field">
	$field.items:{
		$if(it.showInRow)$
			<div class="webform_radio_row_wrapper"><div class="webform_field_radio_row">
		$else$
			<div class="webform_radio_wrapper"><div class="webform_field_radio">
		$endif$
		<input type="radio" name="$field.name$" id="$field.name$" value="$it.value$" class="radio"$if(it.selected)$ checked="checked"$endif$$if(field.hasSubFields)$ onclick="toggleWebformSubFields(this);"$endif$/>
		$if(it.showInRow)$
			</div><div class="webform_label_radio_row">
			$it.label$
			</div>
		$else$
			</div><div class="webform_label_radio">
			$it.label$
			</div>
		$endif$
		</div>
	}$
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_select(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### SELECT field ###### !$
$row_start(field=field)$
$label(field=field,formconfig=formconfig,errormessage=errormessage,mandatory=mandatory)$
<div class="webform_field">
	<select name="$field.name$" id="$field.name$" $if(formconfig.formFieldAttributes)$ $formconfig.formFieldAttributes$$endif$$if(field.hasSubFields)$ onchange="toggleWebformSubFields(this);"$endif$>
	$field.items:{
		<option value="$it.value$" $if(it.selected)$ selected="selected"$endif$>$it.label$</option>
	}$
	</select>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
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
					<label for="$field.name$">$field.label$$mandatory$</label>
				</span>
			$else$
				<label for="$field.name$">$field.label$$mandatory$</label>
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
<div class="webform_field">
	<input type="text" name="$field.name$" id="$field.name$" value="$field.valueEscaped$" $formconfig.formFieldAttributes$/>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


field_textarea(field,formconfig,attributes,errormessage,mandatory) ::= <<
$! ###### TEXTAREA field ###### !$
$row_start(field=field)$
<div class="webform_label_multi">
	$if(errormessage)$
		<span class="webform_msg_error">
			<label for="$field.name$">$field.label$$mandatory$</label>
		</span>
	$else$
		<label for="$field.name$">$field.label$$mandatory$</label>
	$endif$
</div>
<div class="webform_field_multi">
	<textarea cols="5" rows="5" name="$field.name$" id="$field.name$" $formconfig.formFieldAttributes$$attributes$>$field.valueEscaped$</textarea>
	$if(errormessage)$
	<br/><span class="webform_label_error">$errormessage$</span>
	$endif$
</div>
$row_end(field=field,formconfig=formconfig)$
>>


label(field,formconfig,errormessage,mandatory) ::= <<
$! ###### Standard LABEL for a field ###### !$
<div class="webform_label">
	$if(errormessage)$
		<span class="webform_msg_error">
			<label for="$field.name$">$field.label$$mandatory$</label>
		</span>
	$else$
		<label for="$field.name$">$field.label$$mandatory$</label>
	$endif$
</div>
>>


row_start(field) ::= <<
$! ###### Standard start row for a field ###### !$
$if(field.showRowStart)$
	<div class="webform_row">
$endif$
>>


row_end(field,formconfig) ::= <<
$! ###### Standard end row for a field ###### !$
$if(field.showRowEnd)$
	</div>
$endif$
$subtext(field=field,formconfig=formconfig)$
>>


subfieldwrapper(subfields,style,id) ::= <<
$! ###### Sub field wrapper ###### !$
<div class="webform_subfields" id="$id$" style="$style$">
$subfields$
</div>
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
			document.getElementById("webformconfirmvalues").$captchafield.name$.value = "" + document.getElementById("webformcaptcha").$captchafield.name$.value;
			return true;
		}
	</script>
	<form id="webformcaptcha" action="" method="post" enctype="multipart/form-data">
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
		<form id="webformconfirmvalues" method="post" enctype="multipart/form-data" action="$formuri$" onsubmit="return runConfirmValues();">
		<div style="display: none;">
		<input type="hidden" name="formaction" value="confirmed" />
		<input type="hidden" name="captchaphrase" value="" />
		$hiddenfields$
		</div><div class="webform_check_button">
		<input type="submit" value="$checkbutton$" class="formbutton" />&nbsp;&nbsp;&nbsp;&nbsp;</div>
		</form>
		<form id="webformdisplayvalues" method="post" enctype="multipart/form-data" action="$formuri$">
		<div style="display: none;">
		<input type="hidden" name="formaction" value="correct" />
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


datadownloadpage(formuri,formconfig,skinuri,labelfrom,datefrom,labelto,dateto,calendaralttext,submitbutton,resetbutton) ::= <<
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
	<form name="emailform" action="$formuri$" method="post">
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
		<input type="hidden" name="formaction" value="export2" />
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