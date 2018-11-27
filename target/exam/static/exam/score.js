$(function () {
    $(window).resize(function () {
        $('#scoretab').bootstrapTable('resetView'),{
            height:tableHeight()
        }
    })
    console.log('执行js脚本')
    $('#scoretab').bootstrapTable({
        method:'post',
        url:'/admin/getAllScore',
        dataType:'json',
        contentType:'application/json',
        height:tableHeight(),
        toolbar:'#toolbar',
        striped:true,
        dataField:'rows',
        pageNumber: 1,
        pagination:true,
        queryParamsType:'',
        queryParams:queryParams,
        sidePagination:'server',
        pageSize:10,
        pageList:[5,10,20,30],
        showRefresh:true,
        showColumns:true,
        clickToSelect:true,
        toolbarAlign:'right',
        buttonsAlign:'right',
        toolbar:'#toolbar',
        columns:[
            {
                title:'全选',
                field:'select',
                checkbox:true,
                width:15,
                align:'center',
                valign:'middle'
            },
            {
                title:'ID',
                field:'id',
                visible:false
            },
            {
                title:'考场',
                field:'roomId',
                align:'center'
            },
            {
                title:'用户',
                field:'userId',
                align:'center'
            },
            {
                title:'得分',
                field:'score',
                align:'center'
            }
        ],
        responseHandler:function (res) {
            var nres=[];
            nres.push({total:res.total,rows:res.list})
            return nres[0]
        },
        local:'zh-CN',
    })

    function queryParams(params) {
        return {
            pageSize:params.pageSize,
            page:params.pageNumber,
            score:$('#search_score').val(),
            userId:$('#search_userId').val()
        }
    }
    //查询按钮
    $('#search_btn').click(function () {
        $('#scoretab').bootstrapTable('refresh',{url:'/admin/getAllScore'});
    })
    //打印功能
    $('.bootstrap-table').change(function () {
        var dataArr=$('#scoretab .selected');
        if(dataArr.length>=1){
            $('#btn_print').css('display','block').removeClass('fadeOutRight').addClass('animated fadeInRight')
        } else {
            $('#btn_print').addClass('fadeOutRight')
            setTimeout(function () {
                $('#btn_print').css('display','none')
            })
        }
    })
    $("#btn_print").click(function () {
        $('#scoretab').printThis({
            debug:false,
            importCSS:false,
            importStyle:false,
            printContainer:true,
            pageTitle:'合格证书',
            removeInline:false,
            printDelay:333,
            header:null,
            formValues:false
        })
    })
})
function tableHeight() {
    return $(window).height() - 140;
}