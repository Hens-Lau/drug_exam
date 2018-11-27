$(function(){
    console.log('页面加载完毕后输出')
	//根据窗口调整表格高度
    $(window).resize(function() {
        $('#mytab').bootstrapTable('resetView', {
            height: tableHeight()
        })
    })
    //生存问题列表
    $('#mytab').bootstrapTable({
    	method: 'post',
        url:"/admin/getAllQuestionBank",
        dataType:"json",
        contentType: "application/json",
        height:tableHeight(),//高度调整
    	toolbar: '#toolbar',
    	striped: true, //是否显示行间隔色
    	dataField: "rows",
        pageNumber: 1, //初始化加载第一页，默认第一页
    	pagination:true,//是否分页
    	queryParamsType:'',
    	queryParams:queryParams,
    	sidePagination:'server',
    	pageSize:10,//单页记录数
    	pageList:[5,10,20,30],//分页步进值
    	showRefresh:true,//刷新按钮
    	showColumns:true,
    	clickToSelect: true,//是否启用点击选中行
    	toolbarAlign:'right',
    	buttonsAlign:'right',//按钮对齐方式
    	toolbar:'#toolbar',//指定工作栏
    	columns:[
        	{
        		title:'全选',
        		field:'select',
        		checkbox:true,
        		width:25,
        		align:'center',
        		valign:'middle'
        	},
        	{
        		title:'ID',
        		field:'id',
        		visible:false
        	},
        	{
        		title:'题目',
        		field:'title',
        		sortable:true
        	},
        	{
        		title:'选项',
        		field:'options',
        	},
        	{
        		title:'题型',
        		field:'type',
                formatter:operateFormatter
        	},
        	{
        		title:'类别',
        		field:'catId',
        		sortable:true
        	},
        	{
        		title:'答案',
        		field:'answer',
        		align:'center'
        	},
            {
                title:'解析',
                field:'analysis',
                align:'center'

            }
    	],
		responseHandler:function(res){
    		var nres = [];
    		nres.push({total:res.total,rows:res.list})
			return nres[0];
		},
    	locale:'zh-CN',//中文支持,
    })
    /*
     * 用户管理首页事件
     */
    //请求后台数据获取角色列表
   function operateFormatter(value,row,index){
		//题型,1:单选,2:多选,3:填空,4:判断,5:综合
    	if(value==1){
    		return '单选'
    	}else if(value==2){
    		return '多选'
    	}else if(value==3){
    		return '填空'
        }else if(value==4){
    		return '判断'
		}else if(value==5){
    		return '综合'
		}else{
    		return '其他题型'
    	}
    }

    //请求服务数据时所传参数
    function queryParams(params){
    	return{
    		pageSize: params.pageSize,
    		page:params.pageNumber,
    		title:$('#search_name').val(),
    		type:$('#search_tel').val()
    	}
    }
    //查询按钮事件
    $('#search_btn').click(function(){
    	$('#mytab').bootstrapTable('refresh', {url: '/admin/getAllQuestionBank'});
    })
    
    //增加按钮事件
    $('#btn_add').click(function(){
		$('.tableBody').addClass('animated slideOutLeft');
		setTimeout(function(){
			$('.tableBody').removeClass('animated slideOutLeft').css('display','none');
		},500)
		$('.addBody').css('display','block');
		$('.addBody').addClass('animated slideInRight');
    })
    //删除按钮与修改按钮的出现与消失
    $('.bootstrap-table').change(function(){
    	var dataArr=$('#mytab .selected');
    	if(dataArr.length==1){
    		$('#btn_edit').css('display','block').removeClass('fadeOutRight').addClass('animated fadeInRight');
    	}else{
    		$('#btn_edit').addClass('fadeOutRight');
    		setTimeout(function(){
    			$('#btn_edit').css('display','none');
    		},400);	
    	}
    	if(dataArr.length>=1){
    		$('#btn_delete').css('display','block').removeClass('fadeOutRight').addClass('animated fadeInRight');
    	}else{
    		$('#btn_delete').addClass('fadeOutRight');
    		setTimeout(function(){
    			$('#btn_delete').css('display','none');
    		},400);	
    	}
    });
    //修改按钮事件
    $('#btn_edit').click(function(){
    	var dataArr=$('#mytab').bootstrapTable('getSelections');
    	$('.tableBody').addClass('animated slideOutLeft');
		setTimeout(function(){
			$('.tableBody').removeClass('animated slideOutLeft').css('display','none');
		},500)
		$('.changeBody').css('display','block');
		$('.changeBody').addClass('animated slideInRight');
		$('#edit_Id').val(dataArr[0].id);
		$('#edit_Title').val(dataArr[0].title);
		$('#edit_Answer').val(dataArr[0].answer);
		$('#edit_Analysis').val(dataArr[0].analysis);
		var type = dataArr[0].type
        for(var i=0;i<5;i++){
            $("#editForm input[name=type]:eq("+i+")").prop("checked",(i+1)==type);
        }
        if(type==1){
            $("#options-edit").show()
            var opVal = JSON.parse(dataArr[0].options)
            var opArr = [opVal['A'],opVal['B'],opVal['C'],opVal['D']]
            for(var i=0;i<opArr.length;i++){
                console.log('选项内容依次是:'+typeof(dataArr[0].options))
                $("#editForm input[name=optionList]:eq("+i+")").val(opArr[i])
            }
        } else {
            $("#options-edit").hide()
        }
		// if(dataArr[0].Attribute==1){
		// 	$("#editForm input[name=Attribute]:eq(0)").prop("checked",true);
		// 	$("#editForm input[name=Attribute]:eq(1)").prop("checked",false);
		// }
		// else if(dataArr[0].Attribute==2){
		// 	$("#editForm input[name=Attribute]:eq(1)").prop("checked",true);
		// 	$("#editForm input[name=Attribute]:eq(0)").prop("checked",false);
		// }
		//先清空角色复选框
	    $('#editForm .edit input').prop('checked',false);
    })
    /*
     * 用户管理增加用户页面所有事件
    */
    //增加页面表单验证   
    // Validate the form manually
    $('#add_saveBtn').click(function() {
       //点击保存时触发表单验证
       // $('#addForm').bootstrapValidator('validate');
       //如果表单验证正确，则请求后台添加用户
       /*if($("#addForm").data('bootstrapValidator').isValid())*/{
    	   var _info = JSON.stringify($('#addForm').serializeObject());
    	   console.log('新增的问题是:'+_info)
    	  $.ajax({
              type:"post",
              url: "/admin/addquestionbank",
              async:false,
              data: _info,
              success:function(data) {
                  //后台返回添加成功
                  if (data == 1) {
                      $('.addBody').addClass('animated slideOutLeft');
                      setTimeout(function () {
                          $('.addBody').removeClass('animated slideOutLeft').css('display', 'none');
                      }, 500);
                      $('.tableBody').css('display', 'block').addClass('animated slideInRight');
                      $('#mytab').bootstrapTable('refresh', {url: '/admin/getAllQuestionBank'});
                      // $('#addForm').data('bootstrapValidator').resetForm(true);
                      $("#addForm").val('')
                      //隐藏修改与删除按钮
                      $('#btn_delete').css('display', 'none');
                      $('#btn_edit').css('display', 'none');
                  }
                  //否则
                  else {
                  }
              },
              dataType:"json",
              contentType:"application/json"
          })
       }
    });
    //增加页面返回按钮事件
    $('#add_backBtn').click(function() {
    	$('.addBody').addClass('animated slideOutLeft');
    	setTimeout(function(){
			$('.addBody').removeClass('animated slideOutLeft').css('display','none');
		},500)
    	$('.tableBody').css('display','block').addClass('animated slideInRight');  
    	// $('#addForm').data('bootstrapValidator').resetForm(true);
    });
    /*
     * 用户管理修改用户页面所有事件
    */
    //修改页面回退按钮事件
    $('#edit_backBtn').click(function(){
    	$('.changeBody').addClass('animated slideOutLeft');
    	setTimeout(function(){
			$('.changeBody').removeClass('animated slideOutLeft').css('display','none');
		},500)
    	$('.tableBody').css('display','block').addClass('animated slideInRight'); 
    	// $('#editForm').data('bootstrapValidator').resetForm(true);
    })
    //修改页面保存按钮事件
    $('#edit_saveBtn').click(function(){
    	$('#editForm').bootstrapValidator('validate');
    	/*if($("#editForm").data('bootstrapValidator').isValid())*/{
    		 $.ajax({
                 type:"post",
                 url: "/admin/updatequestionbank",
                 async:false,
                 data: JSON.stringify($('#editForm').serializeObject()),
                 dataType:'json',
                 contentType:'application/json',
                success:function(data){
					if(data==true){
						//隐藏修改与删除按钮
						$('#btn_delete').css('display','none');
						$('#btn_edit').css('display','none');
						//回退到人员管理主页
						$('.changeBody').addClass('animated slideOutLeft');
				    	setTimeout(function(){
							$('.changeBody').removeClass('animated slideOutLeft').css('display','none');
						},500)
				    	$('.tableBody').css('display','block').addClass('animated slideInRight'); 
				    	//刷新人员管理主页
				    	$('#mytab').bootstrapTable('refresh', {url: '/admin/getAllQuestionBank'});
				    	//修改页面表单重置
				    	// $('#editForm').data('bootstrapValidator').resetForm(true);
					}else{
					    console.log('修改失败')
					}
			    }
         })
    	}
    })
    //删除事件按钮
    $('#btn_delete').click(function(){
    	console.log('居然执行了')
    	var dataArr=$('#mytab').bootstrapTable('getSelections');
    	$('.popup_de .show_msg').text('确定要删除该考题吗?');
    	$('.popup_de').addClass('bbox');
    	$('.popup_de .btn_submit').one('click',function(){
    		var ID=[];
    		// console.log('元素信息:'+JSON.stringify(dataArr))
        	for(var i=0;i<dataArr.length;i++){
        		ID[i]=dataArr[i].id;
        	}
        	$.ajax({
                type:'post',
                async:false,
                contentType:'application/json',
        	    url:"/admin/deletequestionbank",
        		data:JSON.stringify({idList:ID}),
                success:function(data){
                    if(data==true){
                        $('.popup_de .show_msg').text('删除成功！');
                        $('.popup_de .btn_cancel').css('display','none');
                        $('.popup_de').addClass('bbox');
                        $('.popup_de .btn_submit').one('click',function(){
                            $('.popup_de').removeClass('bbox');
                        })
                        $('#mytab').bootstrapTable('refresh', {url: '/admin/getAllQuestionBank'});
                    }else{
                        console.log('删除失败:'+ID)
                        $('.popup_de .show_msg').text('删除失败！');
                    }
        	    }
        	});
    	})
    })
    //弹出框取消按钮事件
   　　$('.popup_de .btn_cancel').click(function(){
	   $('.popup_de').removeClass('bbox');
   　　})
    //弹出框关闭按钮事件
     $('.popup_de .popup_close').click(function(){
	   $('.popup_de').removeClass('bbox');
   　　})
    $("#editForm input[type=radio]").change(function () {
        if(this.value==1){
            $("#options-edit").show()
        } else {
            $("#options-edit").hide()
        }
    })
})
function tableHeight() {
    return $(window).height() - 140;
}

//定义serializeObject方法，序列化表单
$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [ o[this.name] ];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    if(o['optionList']==''){
        o['optionList']=null
    }
    return o;
};