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
                field:'examName',
                align:'center'
            },
            {
                title:'考生编号',
                field:'userId',
                align:'center'
            },
            {
              title:'姓名',
              field:'userName',
              align:'center'
            },
            {
                title:'得分',
                field:'score',
                align:'center'
            },
            {
                title:'是否通过',
                field:'awardStatus',
                align:'center',
                formatter:statusFormatter
            }
        ],
        responseHandler:function (res) {
            var nres=[];
            nres.push({total:res.total,rows:res.list})
            return nres[0]
        },
        local:'zh-CN',
    })

    function statusFormatter(value,row,index) {
        if(value==0){
            return '未通过'
        } else if(value==1){
            return '通过'
        } else {
            return '未知'
        }
    }

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
        // $('#qrcode').append('<img src="/static/images/certificate.jpg" class="img1"/><div class="body text"><h3>尤振强\t</h3></div>' +
        //     '<div class="year"><h4>18</h4></div><div class="month"><h4>12</h4></div><div class="day"><h4>06</h4></div>')
       /* $('#myModal').printThis({
            debug:false,
            importCSS:false,
            importStyle:false,
            printContainer:true,
            // pageTitle:'合格证书',
            removeInline:false,
            // loadCss:"/static/css/background.css",
            printDelay:333,
            header:null,
            formValues:false,
            // base:'./'
        })*/
       $('#myModal').modal('show')


        // $('#qrcode').empty()
    })

    $("#button_print").click(function(event) {
        /* Act on the event */
        console.log('执行了')
        $("#qrcode").printThis({
            /* debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
          importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
          printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
          operaSupport: true,//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
          */
            pageTitle:'',
            importCSS:true,
            base:"./"
        });
        console.log('执行了2')
//  alert("等待打印");
    });
})

function tableHeight() {
    return $(window).height() - 140;
}