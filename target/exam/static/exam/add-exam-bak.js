var selectionIds = []; //保存选中ids
$(function () {
    var $addexam=$('#addexamtab')
    //模态框可见是触发
    $('#myModal').on('shown.bs.modal',function () {
        console.log('模态显示后触发')
        $addexam.bootstrapTable({
            method:'post',
            url:'/admin/getAllQuestionBank',
            dataType:'json',
            contentType:'application/json',
            height:tableHeight(),
            // toolbar:'#toolbar',
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
            // toolbar:'#toolbar',
            // toolbarAlign:'right',
            // buttonsAlign:'right',
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
                    title:'类别',
                    field:'catId'
                },
                {
                    title:'题目',
                    field:'title'
                },
                {
                    title:'选项',
                    field:'options'
                },
                {
                    title:'分数',
                    field:'initScore'
                },
                {
                    title:'难度',
                    field:'level'
                }
            ],
            responseHandler:function (res) {
                console.log(JSON.stringify(res))
                $.each(res.list, function (i, row) {
                    console.log('row type='+(typeof row))
                    row.ck = $.inArray(row.fid, selectionIds) != -1;
                    //判断当前行的数据id是否存在与选中的数组，存在则将多选框状态变为true
                });
                var nres=[]
                nres.push({total:res.total,rows:res.list})
                return nres[0];
            },
            local:'zh-CN'
        })
    })
    function queryParams(params){
        return {
            pageSize:params.pageSize,
            page:params.pageNumber
        }
    }
    $('#startTime').datetimepicker({
        format:'yyyy-mm-dd 00:00',
        minView:'month',
        language:'zh-CN',
        autoclose:true,
        startDate:new Date()
    }).on('click',function () {
        console.log('点击日期控件')
        $('#startTime').datetimepicker('setEndDate',$('#startTime').val())
    })
    $('#endTime').datetimepicker({
        format:'yyyy-mm-dd 23:59',
        minView:'month',
        language:'zh-CN',
        autoclose:true,
        startDate:new Date()
    }).on('click',function () {
        $('#endTime').datetimepicker('setStartDate',$('#endTime').val())
    })


function tableHeight() {
    
}


//选中事件操作数组
var union = function(array,ids){
    $.each(ids, function (i, id) {
        if($.inArray(id,array)==-1){
            console.log('数组:'+array+',type='+(typeof array))
            array[array.length] = id;
        }
    });
    return array;
};
//取消选中事件操作数组
var difference = function(array,ids){
    $.each(ids, function (i, id) {
        var index = $.inArray(id,array);
        if(index!=-1){
            array.splice(index, 1);
        }
    });
    return array;
};
var _ = {"union":union,"difference":difference};
//绑定选中事件、取消事件、全部选中、全部取消
$addexam.on('check.bs.table check-all.bs.table uncheck.bs.table uncheck-all.bs.table',
    function (e, rows) {
    var ids = $.map(!$.isArray(rows) ? [rows] : rows, function (row) { return row.fid; });
    func = $.inArray(e.type, ['check', 'check-all']) > -1 ? 'union' : 'difference';
    selectionIds = _[func](selectionIds, ids); });
})
//表格分页之前处理多选框数据
// function responseHandler(res) {
//     $.each(res.rows, function (i, row) {
//         row.ck = $.inArray(row.fid, selectionIds) != -1;
//         //判断当前行的数据id是否存在与选中的数组，存在则将多选框状态变为true
//     });
//     return res;
// };