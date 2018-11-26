$(document).ready(function() {
    // 初始化表格
    initTable();
});

// 表格初始化
function initTable() {
    $('#eventTable').bootstrapTable({
        method: 'post', // 向服务器请求方式
        contentType: "application/json;charset=UTF-8", // 如果是post必须定义
        url: '/admin/getAllQuestionBank', // 请求url
        cache: false, // 是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
        striped: true, // 隔行变色
        dataType: "json", // 数据类型
        pagination: true, // 是否启用分页
        showPaginationSwitch: false, // 是否显示 数据条数选择框
        pageSize: 5, // 每页的记录行数（*）
        pageNumber: 1, // table初始化时显示的页数
        search: false, // 不显示 搜索框
        sidePagination: 'server', // 服务端分页
        classes: 'table table-bordered', // Class样式

        // showRefresh : true, // 显示刷新按钮

        silent: true, // 必须设置刷新事件

        toolbar: '#toolbar', // 工具栏ID
        toolbarAlign: 'right', // 工具栏对齐方式

        queryParams: queryParams, // 请求参数，这个关系到后续用到的异步刷新

        columns: [{
            field: 'title',
            title: '题目',
            align: 'center'
        }, {
            field: 'catId',
            title: '类型',
            align: 'center'
        }, {
            field: 'options',
            title: '选项',
            align: 'center'
        }, {
            field: 'level',
            title: '难度',
            align: 'center'
        }, {
            field: 'analysis',
            title: '分析',
            align: 'center'
        }/*, {
            field: 'id',
            title: '操作',
            align: 'center',
            width: '280px',
            formatter: function(value, row, index) {
                var view = '查看 ';
                var update = '修改 ';
                var review = '申请专家会审 ';

                // console.log(JSON.stringify(row));

                if (row.status === '已得到控制') {
                    return view + update
                } else {
                    return view + update + review;
                }

            }
        }*/],
    });
}

// 分页查询参数，是以键值对的形式设置的
function queryParams(params) {
    return {
        eventName: $('#eventqueryform input[name=\'eventName\']').val(), // 请求时向服务端传递的参数
        status: $('#eventqueryform input[name=\'status\']').val(), // 请求时向服务端传递的参数
        location: $('#eventqueryform input[name=\'location\']').val(), // 请求时向服务端传递的参数
        startdate: $('#eventqueryform input[name=\'startdate\']').val(), // 请求时向服务端传递的参数
        enddate: $('#eventqueryform input[name=\'enddate\']').val(), // 请求时向服务端传递的参数

        limit: params.limit, // 每页显示数量
        offset: params.offset, // SQL语句偏移量
    }
}

// 搜索按钮触发事件
$(function() {
    $("#eventquery").click(function() {
        $('#eventTable').bootstrapTable(('refresh')); // 很重要的一步，刷新url！
        // console.log("/program/area/findbyItem?offset="+0+"&"+$("#areaform").serialize())
        $('#eventqueryform input[name=\'eventName\']').val('')
        $('#eventqueryform input[name=\'status\']').val('')
        $('#eventqueryform input[name=\'location\']').val('')
        $('#eventqueryform input[name=\'startdate\']').val('')
        $('#eventqueryform input[name=\'enddate\']').val('')
    });
});