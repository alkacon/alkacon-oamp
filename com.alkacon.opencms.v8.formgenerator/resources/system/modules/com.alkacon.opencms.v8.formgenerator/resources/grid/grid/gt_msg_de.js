//

if (!window.Sigma){
	window.Sigma={};
}
Sigma.Msg=Sigma.Msg || {};
SigmaMsg=Sigma.Msg;

Sigma.Msg.Grid = Sigma.Msg.Grid || {};

Sigma.Msg.Grid.de={
	LOCAL	: "DE",
	ENCODING		: "UTF-8",
	NO_DATA : "Keine Daten",


	GOTOPAGE_BUTTON_TEXT: 'Gehe zu',

	FILTERCLEAR_TEXT: "Entferne alle Filter",
	SORTASC_TEXT	: "Aufsteigend",
	SORTDESC_TEXT	: "Absteigend",
	SORTDEFAULT_TEXT: "Original",

	ERR_PAGENUM		: "Seitenzahl muss eine NUmmer zwischen 1 und #{1} sein.",

	EXPORT_CONFIRM	: "This operation will affect all records of the whole table.\n\n( Press \"Cancel\" to limit scope to current page.)",
	OVER_MAXEXPORT	: "Number of record exceeds #{1}, the maximum allowed.",

	PAGE_STATE	: "#{1} - #{2} angezeigt,  #{3}Seiten #{4} Einträge.",
	PAGE_STATE_FULL	: "Seite #{5}, #{1} - #{2} angezeigt,  #{3}Seiten #{4} Einträge.",

	SHADOWROW_FAILED: "Relevant info not available",
	NEARPAGE_TITLE	: "",
	WAITING_MSG : 'Bitte warten...',

	NO_RECORD_UPDATE: "Nothing Modified",
	UPDATE_CONFIRM	: "Are you sure to save them?",
	NO_MODIFIED: "Nothing Modified",

	
	PAGE_BEFORE : 'Seite',
	PAGE_AFTER : '',

	PAGESIZE_BEFORE :   '',
	PAGESIZE_AFTER :   'Einträge pro Seite',

	RECORD_UNIT : '',
	
	CHECK_ALL : 'Check All',

	COLUMNS_HEADER : 'Spalten',

	DIAG_TITLE_FILTER : 'Filter Options',
	DIAG_NO_FILTER : 'No Filter',
	TEXT_ADD_FILTER	: "Add",
	TEXT_CLEAR_FILTER	: "Remove All",
	TEXT_OK	: "OK",
	TEXT_DEL : "Löschen",
	TEXT_CANCEL	: "Abbr.",
	TEXT_CLOSE	: "Schl.",
	TEXT_UP : "Hoch",
	TEXT_DOWN : "Runter",

	NOT_SAVE : "Do you want to save the changes? \n Click \"Cancel\" to discard.",

	DIAG_TITLE_CHART  : 'Chart',

	CHANGE_SKIN : "Skins",

	STYLE_NAME_DEFAULT : "Classic",
	STYLE_NAME_PINK : "Pink",
	STYLE_NAME_VISTA : "Vista",
	STYLE_NAME_MAC : "Mac",

	MENU_FREEZE_COL : "Sperre Spalten",
	MENU_SHOW_COL : "Verberge Sp.",
	MENU_GROUP_COL : "Gruppieren",

	TOOL_RELOAD : "Neu laden" ,
	TOOL_ADD : "Hinzufügen" ,
	TOOL_DEL : "Löschen" ,
	TOOL_SAVE : "Speichern" ,

	TOOL_PRINT : "Drucken" ,
	TOOL_XLS : "Export to xls" ,
	TOOL_PDF : "Export to pdf" ,
	TOOL_CSV : "Export to csv" ,
	TOOL_XML : "Export to xml",
	TOOL_FILTER : "Filter" ,
	TOOL_CHART : "Chart" 

};

Sigma.Msg.Grid['default']=Sigma.Msg.Grid.de;


if (!Sigma.Msg.Validator){
	Sigma.Msg.Validator={ };
}

Sigma.Msg.Validator.de={

		'required'	: '{0#This field} is required.',
		'date'		: '{0#This field} must be in proper format ({1#YYYY-MM-DD}).',
		'time'		: '{0#This field} must be in proper format ({1#HH:mm}).',
		'datetime'	: '{0#This field} must be in proper format ({1#YYYY-MM-DD HH:mm}).',
		'email'		: '{0#This field} must be in proper email format.',
		'telephone'	: '{0#This field} must be in proper phone no format.',
		'number'	: '{0} must be a number.',
		'integer'	: '{0} must be an integer.',
		'float'		: '{0} must be integer or decimal.',
		'money'		: '{0} must be integer or decimal with 2 fraction digits.',
		'range'		: '{0} must be between {1} and {2}.',
		'equals'	: '{0} must be same as {1}.',
		'lessthen'	: '{0} must be less than {1}.',
		'idcard'	: '{0} must be in proper ID format',

		'enchar'	: 'Letters, digits or underscore allowed only for {0}',
		'cnchar'	: '{0} must be Chinese charactors',
		'minlength'	: '{0} must contain more than {1} characters.',
		'maxlength'	: '{0} must contain less than {1} characters.'

}

Sigma.Msg.Validator['default'] = Sigma.Msg.Validator.de;

//