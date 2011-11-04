//


Sigma.Chart=Sigma.$class( {
	
	initialize : function( options){
		
		this.defaultColor='66BBFF';
		this.type = 'column2D';
		this.swfPath='./charts/';
		this.swf = Sigma.Chart.SWFMapping[this.type];
		this.width="100%";
		this.height="100%";
		this.data=null;
		this.container = null;
		this.chart = null;

		Sigma.$extend(this,options);
		this.swf =  Sigma.Chart.SWFMapping[this.type] || this.swf;
		if (this.swfPath.lastIndexOf('/')==this.swfPath.length-1){
			this.swfPath=this.swfPath.substring(0,this.swfPath.length-1);
		}
		this.container=Sigma.$(this.container);
		this.chart= this.chart || new FusionCharts(this.swfPath+'/'+this.swf, this.container.id+'_chart', this.width, this.height);	

	},
	json2Params : function(json){
		if (json.isNull){
			if (json.name){
				json={name : json.name };
			}else{
				return '';
			}
		}else if (json.color){
			json.color= json.color || this.defaultColor;
		}
		var str=[];
		for (var k in json){
			str.push(k+"='"+json[k]+"'");
		}
		return;
	},
	createSetsXML : function( dataset ){
		// link hoverText
			dataset= dataset || this.data;

			var setsXML=[],str=[];
			for (var i=0;i< dataset.length;i++){
				var record= dataset[i], setXML,name,value,color;
				if ( record instanceof Array ){
					name= record[0];
					value= record[1];
					color= record[2];

					color=(value===null || value===undefined)?null:(color || this.defaultColor);
					name=(name===null || name===undefined)?value:name;

					str=  [name!==null&&name!==undefined?"name='"+name+"'":'',
						value!==null&&value!==undefined?"value='"+value+"'":'',
						color!==null&&color!==undefined?"color='"+color+"'":''
					].join(' ');

				}else if ( record ){
					str=this.json2Params(record);
				}

				setXML=['<set', str,'/>' ];
				setXML=setXML.join(' ');
				if (setXML=='<set />' || (value===null || value===undefined) )	{
					// todo ;
				}
				setsXML.push(setXML);
			}
			this.setsXML = setsXML.join('');

			return this.setsXML;
		},

		createChartXML : function( options,setsXML ){
			setsXML = setsXML || this.setsXML;

			var chartXML=['<graph',
				"caption='"+(this.caption||'')+"'",
				"subCaption='"+(this.subCaption||'')+"'",
				"outCnvBaseFontSize='12'",
				"animation='0'"			
			
			];
			chartXML.push('>'+setsXML+'</graph>');


			this.chartXML = chartXML.join(' ');

			return this.chartXML;
		},

		updateChart :function(container,chartXML){
			container = container || this.container;
			chartXML = chartXML || this.chartXML;
			window.updateChartXML && (window.updateChartXML(container,chartXML));

		},

		generateChart : function(container,data){
			this.data=data || this.data ;
			this.createSetsXML();
			this.createChartXML();

			container = container || this.container;
			this.chart.setDataXML( this.chartXML);
			this.chart.render(container);
		}

} );

Sigma.Chart.SWFMapping = {
	'column2D' : 'FCF_Column2D.swf',
	'pie3D' : 'FCF_Pie3D.swf'
};

//