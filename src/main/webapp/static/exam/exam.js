$(function() {
    $(window).resize(function() {
        $('#examtab').bootstrapTable('resetView',{
            height:tableHeight()
        })
    });
    window.operateEvents = {
        'click #examer':function (e,value,row,index) {
            var examId = row.id
            console.log('examId='+examId)
            $.get(
                "/admin/startExam?examId="+examId,
                function (data,state) {
                    console.log('data='+JSON.stringify(data))
                    $('#examtab').bootstrapTable('refresh',{url:'/admin/getAllExamPaper'})
                }
            )
        }
    }
    $('#examtab').bootstrapTable({
        method:'post',
        url:'/admin/getAllExamPaper',
        dataType:'json',
        contentType:'application/json',
        height:tableHeight(),
        toolbar:'#toolbar',
        striped:true,
        dataField:'rows',
        pageNumber:1,
        pagination:true,
        queryParamsType:'',
        queryParams:queryParams,
        sidePagination:'server',
        pageSize:10,
        pageList:[5,10,20,30],
        showRefresh:true,
        showColumns:true,
        clickToSelect:true,
        toolbar:'#toolbar',
        toolbarAlign:'right',
        buttonsAlign:'right',
        columns:[
            {
                title:'全选',
                field:'select',
                checkbox:true,
                width:15
            },
            {
                title:'ID',
                field:'id',
                visible:false
            },
            {
                title:'考卷名',
                field:'name',
                align:'center'
            },
            {
                title:'类型',
                field:'type',
                formatter:operateFormatter
            },
            {
                title:'总分',
                field:'totalScore'
            },
            {
                title:'分数线',
                field:'passing'
            },
            {
                title:'状态',
                field:'status',
                align:'center',
                formatter:statusFormatter
            },
            {
                title:'operate',
                title:'操作',
                align:'center',
                width:100,
                events:operateEvents,
                formatter:operationFormatter
            }
        ],
        responseHandler:function (res) {
            var nres=[]
            nres.push({total:res.total,rows:res.list})
            return nres[0]
        },
        local:'zh-CN'
    })
    function operateFormatter(value,row,index) {
        if(value==1){
            return '练习'
        } else if(value==2){
            return '考试'
        } else {
            return '其他'
        }
    }
    function operationFormatter(value,row,index) {
        if(row.status==0){
            return ['<button type="button" id="examer" class="btn btn-primary btn-sm" style="margin-right: 15px">' +
            '                                开考' +
            '                            </button>']
        } else {
            return ['<button type="button" id="imexamer" class="btn btn-default btn-sm disabled" style="margin-right: 15px">' +
            '                                <span>开考 </span>' +
            '                            </button>']
        }
    }
    function statusFormatter(value,row,index) {
        if(value==0){
            return '初始化'
        } else if(value==1){
            return '考试中'
        } else if(value==2){
            return '已过期'
        } else {
            return '未知'
        }
    }
    function queryParams(params) {
        return {
            pageSize:params.pageSize,
            page:params.pageNumber,
            name:$('#search_name').val(),
            type:$('#search_type').val()
        }
    }
    $('#search_btn').click(function () {
        $('#examtab').bootstrapTable('refresh',{url:'/admin/getAllExamPaper'})
    })
    $('.bootstrap-table').change(function () {
        var dataArr = $('#examtab .selected')
        if(dataArr.length>=1){
            $('#btn_delete').css('display','block').removeClass('fadeOutRight').addClass('animated fadeInRight')
        } else {
            $('#btn_delete').addClass('fadeOutRight')
            setTimeout(function () {
                $('#btn_delete').css('display','none')
            })
        }
    })
    //    新增考卷
    $('#btn_add').click(function () {
        console.log('新增按钮点击事件')
        $('.tableBody').addClass('animated slideOutLeft')
        setTimeout(function () {
            $('.tableBody').removeClass('animated slideOutLeft').css('display','none')
        },500)
        $('.addBody').css('display','block')
        $('.addBody').addClass('animated slideInRight')
    })
    //新增返回按钮
    $('#add_back').click(function () {
        $('.addBody').addClass('animated slideOutLeft')
        setTimeout(function () {
            $('.addBody').removeClass('animated slideOutLeft').css('display','none')
        },500)
        $('.tableBody').css('display','block').addClass('animated slideInRight')
    })

    $('#startTime').datetimepicker({
        format:'yyyy-mm-dd 00:00:00',
        minView:'month',
        language:'zh-CN',
        autoclose:true,
        startDate:new Date()
    }).on('click',function () {
        $('#startTime').datetimepicker('setEndDate',$('#startTime').val())
    })
    $('#endTime').datetimepicker({
        format:'yyyy-mm-dd 23:59:59',
        minView:'month',
        language:'zh-CN',
        autoclose:true,
        startDate:new Date()
    }).on('click',function () {
        $('#endTime').datetimepicker('setStartDate',$('#endTime').val())
    })
});

function tableHeight() {
    
}