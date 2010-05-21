//

if (!window.Sigma){
	window.Sigma={};
}
Sigma.Msg=Sigma.Msg || {};
SigmaMsg=Sigma.Msg;

Sigma.Msg.Grid = Sigma.Msg.Grid || {};

Sigma.Msg.Grid.cn={
	LOCAL	: "CN",
	ENCODING		: "UTF-8",
	NO_DATA : "没有要展现的数据...",


	GOTOPAGE_BUTTON_TEXT: '跳转到指定的页',

	FILTERCLEAR_TEXT: "清除全部过滤条件",
	SORTASC_TEXT	: "升序排列",
	SORTDESC_TEXT	: "降序排列",
	SORTDEFAULT_TEXT: "清除排序状态",

	ERR_PAGENUM		: "跳转页数只能是 1 至 #{1} 的整数!",

	EXPORT_CONFIRM	: "处理全部数据吗?\n\n( \"取消\" 为处理当前页面)",
	OVER_MAXEXPORT	: "数据总数超过了所允许的最大值( #{1} 条 ).",

	PAGE_STATE	: "第 #{1} - #{2}条, 共 #{3}页 #{4}条数据",
	PAGE_STATE_FULL	: "第 #{5}页 #{1} - #{2}条, 共 #{3}页 #{4}条数据",

	SHADOWROW_FAILED: "无法取得相关信息",
	NEARPAGE_TITLE	: "",
	WAITING_MSG : '操作进行中,请稍候...',

	NO_RECORD_UPDATE: "没有记录需要被更新.",
	UPDATE_CONFIRM	: "确定要执行保存操作吗?",
	NO_MODIFIED: "没有记录被更改,无需保存.",

	
	PAGE_BEFORE : '第',
	PAGE_AFTER : '页',

	PAGESIZE_BEFORE :   '每页',
	PAGESIZE_AFTER :   '条',

	RECORD_UNIT : '条',
	
	CHECK_ALL : '全选',

	COLUMNS_HEADER : '--列 名--',

	DIAG_TITLE_FILTER : '过滤选项',
	DIAG_NO_FILTER : '无过滤条件',
	TEXT_ADD_FILTER	: "添加条件",
	TEXT_CLEAR_FILTER	: "清除所有条件",
	TEXT_OK	: "确定",
	TEXT_DEL : "删除",
	TEXT_CANCEL	: "取消",
	TEXT_CLOSE	: "关闭",
	TEXT_UP : "上移",
	TEXT_DOWN : "下移",

	NOT_SAVE : "数据已修改,但未保存,要现在保存吗? \n 若点击\"取消\",未保存的信息将丢失.",

	DIAG_TITLE_CHART  : '图表',

	CHANGE_SKIN : "换肤",

	STYLE_NAME_DEFAULT : "默认蓝色风格",
	STYLE_NAME_PINK : "中国红风格",
	STYLE_NAME_VISTA : "vista风格",
	STYLE_NAME_MAC : "mac风格",

	MENU_FREEZE_COL : "锁定/解锁 列",
	MENU_SHOW_COL : "显示/隐藏 列",
	MENU_GROUP_COL : "编组/解除组 列",

	TOOL_RELOAD : "刷新" ,
	TOOL_ADD : "添加" ,
	TOOL_DEL : "删除" ,
	TOOL_SAVE : "保存" ,

	TOOL_PRINT : "打印" ,
	TOOL_XLS : "导出 xls" ,
	TOOL_PDF : "导出 pdf" ,
	TOOL_CSV : "导出 csv" ,
	TOOL_XML : "导出 xml",
	TOOL_FILTER : "过滤" ,
	TOOL_CHART : "图表" 

};

Sigma.Msg.Grid['default']=Sigma.Msg.Grid.cn;


if (!Sigma.Msg.Validator){
	Sigma.Msg.Validator={ };
}

Sigma.Msg.Validator.cn={

		'required'	: '{0#该项}是必填项目！',
		'date'		: '{0#该项}必须是正确的日期({1#YYYY-MM-DD})！',
		'time'		: '{0#该项}必须是正确时间({1#HH:mm})！',
		'datetime'	: '{0#该项}必须是正确的日期和时间({1#YYYY-MM-DD HH:mm})！',
		'email'		: '{0#该项}必须是正确的email格式！',
		'telephone'	: '{0#该项}必须是正确的电话号码！',
		'number'	: '{0#该项}必须是数字形式！',
		'integer'	: '{0#该项}必须是整数形式！',
		'float'		: '{0#该项}必须是整数或小数形式！',
		'money'		: '{0#该项}必须是整数或两位小数形式！',
		'range'		: '{0#该项}的范围必须要在{1}和{2}之间！',
		'equals'	: '{0#该项}必须与{1#另一项}相等！',
		'lessthen'	: '{0#该项}不能大于{1#另一项}！',
		'idcard'	: '{0#该项}必须是正确的身份证号码！',

		'enchar'	: '{0#该项}必须是普通英文字符：字母，数字和下划线。',
		'cnchar'	: '{0#该项}必须是中文字符。',
		'minlength'	: '{0#该项}的长度不能小于{1}个字符。',
		'maxlength'	: '{0#该项}的长度不能大于{1}个字符。'

};

Sigma.Msg.Validator['default'] = Sigma.Msg.Validator.cn;

//