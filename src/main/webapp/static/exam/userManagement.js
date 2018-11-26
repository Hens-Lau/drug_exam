$(function() {
    //根据窗口调整表格高度
    $(window).resize(function () {
        $('#mytab').bootstrapTable('resetView', {
            height: tableHeight()
        })
    })
    console.log('执行了table的初始化')
    //生成用户数据
    $('#mytab').bootstrapTable({
        method: 'post',
        url: "/admin/getAllQuestionBank",
        dataType: "json",
        contentType: "application/json",
        height: tableHeight(),//高度调整
        toolbar: '#toolbar',
        striped: true, //是否显示行间隔色
        dataField: "rows",
        pageNumber: 1, //初始化加载第一页，默认第一页
        pagination: true,//是否分页
        queryParamsType: '',
        queryParams: queryParams,
        sidePagination: 'server',
        pageSize: 10,//单页记录数
        pageList: [5, 10, 20, 30],//分页步进值
        showRefresh: true,//刷新按钮
        showColumns: true,
        clickToSelect: true,//是否启用点击选中行
        toolbarAlign: 'right',
        buttonsAlign: 'right',//按钮对齐方式
        toolbar: '#toolbar',//指定工作栏
        columns: [
            {
                title: '全选',
                field: 'select',
                checkbox: true,
                width: 25,
                align: 'center',
                valign: 'middle'
            },
            {
                title: 'ID',
                field: 'id',
                visible: false
            },
            {
                title: '题目',
                field: 'title',
                sortable: true
            },
            {
                title: '选项',
                field: 'options',
            },
            {
                title: '题型',
                field: 'type',
                // formatter:operateFormatter
            },
            {
                title: '类别',
                field: 'catId',
                sortable: true
            },
            {
                title: '答案',
                field: 'answer',
                align: 'center'
            },
            {
                title: '解析',
                field: 'analysis',
                align: 'center'

            }
        ],
        responseHandler: function (res) {
            var nres = [];
            nres.push({total: res.total, rows: res.list})
            return nres[0];
        },
        locale: 'zh-CN',//中文支持,
    });

    //请求服务数据时所传参数
    function queryParams(params) {
        return {
            pageSize: params.pageSize,
            page: params.pageNumber,
            title: $('#search_name').val(),
            type: $('#search_tel').val()
        }
    }


    //删除按钮与修改按钮的出现与消失
    $('.bootstrap-table').change(function () {
        var dataArr = $('#mytab .selected');
        if (dataArr.length == 1) {
            $('#btn_edit').css('display', 'block').removeClass('fadeOutRight').addClass('animated fadeInRight');
        } else {
            $('#btn_edit').addClass('fadeOutRight');
            setTimeout(function () {
                $('#btn_edit').css('display', 'none');
            }, 400);
        }
        if (dataArr.length >= 1) {
            $('#btn_delete').css('display', 'block').removeClass('fadeOutRight').addClass('animated fadeInRight');
        } else {
            $('#btn_delete').addClass('fadeOutRight');
            setTimeout(function () {
                $('#btn_delete').css('display', 'none');
            }, 400);
        }
    });
})
    function tableHeight() {
        return $(window).height() - 140;
    }

