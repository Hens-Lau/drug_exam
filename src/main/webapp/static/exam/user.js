$(function () {
    $(window).resize(function () {
        $('#usertab').bootstrapTable('resetView',{height:tableHeight()})
    })
    $('#usertab').bootstrapTable({
        method:'post',
        url:'/admin/getUserList',
        dataType:'json',
        contentType:'application/json',
        height:tableHeight,
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
                title:'姓名',
                field:'name',
                align:'center'
            },
            {
                title:'序号',
                field:'studentNo',
                align:'center'
            },
            {
                title:'状态',
                field:'status',
                align:'center',
                formatter:operateFormatter
            }
        ],
        responseHandler:function (res) {
            var nres=[];
            nres.push({total:res.total,rows:res.list})
            return nres[0]
        },
        local:'zh-CN'
    })
    function operateFormatter(value,row,index) {
        console.log('调用formatter方法')
        if(value==0){
            return '待审核'
        } else if(value==1){
            return '已审核'
        } else {
            return '未知'
        }
    }
    function queryParams(params) {
        return {
            pageSize:params.pageSize,
            page:params.pageNumber,
            studentNo:$('#search_studentNo').val(),
            status:$('#search_status').val()
        }
    }
    $('#search_btn').click(function () {
        $('#usertab').bootstrapTable('refresh',{url:'/admin/getUserList'})
    })
    $('.bootstrap-table').change(function () {
        console.log('change事件发生')
        var dataArr=$('#usertab .selected');
        if(dataArr.length>=1){
            $('#btn_valid').css('display','block').removeClass('fadeOutRight').addClass('animated fadeInRight')
        } else {
            $('#btn_valid').addClass('fadeOutRight')
            setTimeout(function () {
                $('#btn_valid').css('display','none')
            })
        }
    })
    $('#btn_valid').click(function () {
        console.log('点击审核按钮')
        var dataArr = $('#usertab').bootstrapTable('getSelections');
        $('.popup_btn .show_msg').text('确定通过审核吗?');
        $('.popup_btn').addClass('bbox');
        console.log('点击审核按钮')
        var ID = [];
        for(var i=0;i<dataArr.length;i++){
            ID[i]=dataArr[i].studentNo;
        }
        $.ajax({
            type:'post',
            async:false,
            contentType:'application/json',
            url:'/admin/batchVerifyUser',
            data:JSON.stringify({idList:ID}),
            success:function (data) {
                if(data==true){
                    $('.popup_btn .show_msg').text('审核通过!');
                    $('.popup_btn .btn_cancel').css('display','none');
                    $('#usertab').bootstrapTable('refresh',{url:'/admin/getUserList'});
                } else {
                    $('.popup_btn .show_msg').text('审核失败,请稍后重试!');
                }
            }
        })
    })

})

function tableHeight() {

}