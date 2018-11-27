var selectionIds = []; //保存选中ids
$(function () {
    var $addexam = $('#addexamtab')
    //模态框可见是触发
    $('#myModal').on('shown.bs.modal', function () {
        console.log('模态显示后触发')
        $addexam.bootstrapTable({
            method: 'post',
            url: '/admin/getAllQuestionBank',
            dataType: 'json',
            contentType: 'application/json',
            height: tableHeight(),
            // toolbar:'#toolbar',
            striped: true,
            dataField: 'rows',
            pageNumber: 1,
            pagination: true,
            queryParamsType: '',
            queryParams: queryParams,
            sidePagination: 'server',
            pageSize: 10,
            pageList: [5, 10, 20, 30],
            showRefresh: true,
            showColumns: true,
            clickToSelect: true,
            // toolbar:'#toolbar',
            // toolbarAlign:'right',
            // buttonsAlign:'right',
            columns: [
                {
                    title: '全选',
                    field: 'select',
                    checkbox: true,
                    width: 15,
                    formatter:function (i,row) {
                        if($.inArray(row.id,overAllIds)!=-1){
                            return {checked:true}
                        }
                    }
                },
                {
                    title: 'ID',
                    field: 'id',
                    visible: false
                },
                {
                    title: '类别',
                    field: 'catId'
                },
                {
                    title: '题目',
                    field: 'title'
                },
                {
                    title: '选项',
                    field: 'options'
                },
                {
                    title: '分数',
                    field: 'initScore'
                },
                {
                    title: '难度',
                    field: 'level'
                }
            ],
            responseHandler: function (res) {
                // console.log(JSON.stringify(res))
                var nres = []
                nres.push({total: res.total, rows: res.list})
                return nres[0];
            },
            local: 'zh-CN'
        })
    })
    $addexam.bootstrapTable('refresh',{url:'/admin/getAllQuestionBank'})
    $addexam.on('uncheck.bs.table check.bs.table check-all.bs.table uncheck-all.bs.table',function(e,rows){
        var datas = $.isArray(rows) ? rows : [rows];        // 点击时获取选中的行或取消选中的行
        examine(e.type,datas);                              // 保存到全局 Array() 里

    });
    //modal视图
    $('#modal_cancel').on('click',function () {
        $('#myModal').modal('hide')
        overAllIds = []
        $('#question_list').text('已选考题为空')
    })
    $('#modal_save').on('click',function () {
        $('#myModal').modal('hide')
        $('#question_list').text('已选考题:'+overAllIds)
    })

    function queryParams(params) {
        return {
            pageSize: params.pageSize,
            page: params.pageNumber
        }
    }

    // $('#startTime').datetimepicker({
    //     format: 'yyyy-mm-dd 00:00:00',
    //     minView: 'month',
    //     language: 'zh-CN',
    //     autoclose: true,
    //     startDate: new Date()
    // }).on('click', function () {
    //     console.log('点击日期控件')
    //     $('#startTime').datetimepicker('setEndDate', $('#startTime').val())
    // })
    // $('#endTime').datetimepicker({
    //     format: 'yyyy-mm-dd 23:59:59',
    //     minView: 'month',
    //     language: 'zh-CN',
    //     autoclose: true,
    //     startDate: new Date()
    // }).on('click', function () {
    //     $('#endTime').datetimepicker('setStartDate', $('#endTime').val())
    // })


    function tableHeight() {

    }
})

var overAllIds = new Array();  //全局数组

function examine(type,datas){
    console.log('type='+type+',data='+JSON.stringify(datas))
    if(type.indexOf('uncheck')==-1){
        $.each(datas,function(i,v){
            // 添加时，判断一行或多行的 id 是否已经在数组里 不存则添加　
            overAllIds.indexOf(v.id) == -1 ? overAllIds.push(v.id) : -1;
        });
    }else{
        $.each(datas,function(i,v){
            overAllIds.splice(overAllIds.indexOf(v.id),1);    //删除取消选中行
        });
    }
}

function saveExam() {
    var name=$('#name').val()
    var type=$('input[name="type"]:checked').val()
    var startTime=$('#startTime').val()
    var endTime=$('#endTime').val()
    var totalScore=$('#add_totalScore').val()
    var passing=$('#add_passingScore').val()
    var questionIdList= overAllIds //$('#question_list').val()
    var params = {name,type,startTime,endTime,totalScore,passing,questionIdList}
    console.log('请求参数是:'+JSON.stringify(params))
    $.ajax({
        url: "/admin/addExamPaper",
        async: false,
        type: 'post',
        contentType:'application/json',
        data: JSON.stringify(params),
        success:function(data) {
            if(data){
                alert('保存成功')
                // window.location='/admin/testPaper.html'
                $('.addBody').addClass('animated slideOutLeft')
                setTimeout(function () {
                    $('.addBody').removeClass('animated slideOutLeft').css('display','none')
                },500)
                $('.tableBody').css('display','block').addClass('animated slideInRight')
            } else {
                alert('保存失败')
            }
        }
    })
}
