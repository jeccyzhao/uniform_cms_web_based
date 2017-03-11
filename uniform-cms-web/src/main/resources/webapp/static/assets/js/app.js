function enableAjaxLoading(text) 
{  
     $("<div class=\"datagrid-mask\"></div>").css(
    		{ display: "block", height: $(window).height()}).appendTo("body");  
     $("<div class=\"datagrid-mask-msg\"></div>").html(text).appendTo("body").css(
    		 { display: "block", left: ($(document.body).outerWidth(true) - 190) / 2, top: ($(window).height() - 45) / 2 });  
}  
 
function disableAjaxLoading() 
{  
     $(".datagrid-mask").remove();  
     $(".datagrid-mask-msg").remove();  
}

String.prototype.replaceAll = function(search, replacement) 
{
    var target = this;
    return target.replace(new RegExp(search, 'g'), replacement);
};

String.prototype.stripHTML = function() 
{  
    var reTag = /<(?:.|\s)*?>/g;  
    return this.replace(reTag,"");  
}

Array.prototype.inArray=function(e)
{
    for(i=0; i < this.length; i++)
    {
        if(this[i] == e)
            return true;
    }

    return false;
}

Date.prototype.Format = function(fmt)   
{ //author: meizz   
  var o = {   
	"M+" : this.getMonth()+1,                 //月份   
	"d+" : this.getDate(),                    //日   
	"h+" : this.getHours(),                   //小时   
	"m+" : this.getMinutes(),                 //分   
	"s+" : this.getSeconds(),                 //秒   
	"q+" : Math.floor((this.getMonth()+3)/3), //季度   
	"S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
	fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
	if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}

function initJqxTable (container_id, columns, data, editable, width, height)
{
    var source = { localdata: data, datatype: "array"};
    var dataAdapter = new $.jqx.dataAdapter(source);

    $("#" + container_id).jqxGrid({
        width: width != undefined ? width : '100%',
        height: height != undefined ? height : 520,
        source: dataAdapter,
        columnsResize: true,
        columnsreorder: true,
        pageable: true,
        //selectionmode: 'multiplerowsadvanced',
        editmode: 'click',
        columns: columns,
        filterable: true,
        showfilterrow: true,
        sortable: true,
        altRows: true,
        enableBrowserSelection: true,
        autoshowcolumnsmenubutton: true,
        altRows: true,
        scrollBarSize: 8,
        pagerheight: 40,
        pagesize: 15,
        rowsheight: 26,
        columnsheight: 26,
        editable: editable ? editable : false,
        pagerrenderer: $.grid.pagerrenderer.bind(null, "#" + container_id, true),
        handlekeyboardnavigation: function (e) {
           return $.grid.handlekeyboardnavigation(e);
        },
        ready: function() {
            disableAjaxLoading();
        }
    });
}

function showPopupDialog(dialog_id)
{
    $("#" + dialog_id).modal('show');
}

function showErrorDialog(errorMessage, dialog_id)
{
    var dialog_id = dialog_id ? dialog_id : "ErrorDialog";
    $("#" + dialog_id).find("#errorMessage").html(errorMessage);
    $("#" + dialog_id).modal('show');
}

function showConfirmationDialog (title, body_message, callback, dialog_id)
{
    var dialog_id = dialog_id ? dialog_id : "ConfirmationDialog";
    $("#" + dialog_id).find("#confirm_title").html(title);
    $("#" + dialog_id).find("#confirm_body").html(body_message);
    $("#" + dialog_id).modal('show');

    $("#dialog-confirmation-buttons-ok").unbind('click');
    $("#dialog-confirmation-buttons-ok").click(function() {
        callback();
    });
}

function getCurrentGridRowData (grid_id, rowIndex)
{
    var rowIndex = rowIndex ? rowIndex : getCurrentGridRowIndex(grid_id);
    if (rowIndex > -1)
    {
        return $('#' + grid_id).jqxGrid('getrowdata', rowIndex);
    }

    return {}
}

function getCurrentGridRowIndex (grid_id)
{
    return $('#' + grid_id).jqxGrid('getselectedrowindex');
}

function updateGridRowData (grid_id, newdata, rowIndex)
{
    rowIndex = rowIndex ? rowIndex : getCurrentGridRowIndex(grid_id);
    if (rowIndex > -1)
    {
        $('#' + grid_id).jqxGrid('updaterow', rowIndex, newdata);
    }
}

function removeGridRowData (grid_id, rowIndex)
{
    rowIndex = rowIndex ? rowIndex : getCurrentGridRowIndex(grid_id);
    if (rowIndex > -1)
    {
        rowData = getCurrentGridRowData (grid_id, rowIndex);
        if (rowData != null) {
            $('#' + grid_id).jqxGrid('deleterow', rowData.uid);
            $('#' + grid_id).jqxGrid("refresh");
        }
    }
}

function sendAjaxRequest(uri, type, data, callback, contentType, loadingMessage)
{
    var msg_loading = loadingMessage ? loadingMessage : "Loading Data...";
    $.ajax(openApiContextPath + "/" + uri,
    {
        data: data,
        type: type,
        contentType: contentType ? contentType : "application/x-www-form-urlencoded",
        success: function(response)
        {
            var jsonResult = eval(response);
            if (jsonResult.success)
            {
                callback(jsonResult.data);
            }
            else
            {
                showErrorDialog(jsonResult.error);
            }
            disableAjaxLoading();
        },
        beforeSend: function()
        {
            enableAjaxLoading(msg_loading);
        },
        error: function(response)
        {
            showErrorDialog(response);
            disableAjaxLoading();
        }
    });
}

function clearFormInputs (form_id)
{
    $("#" + form_id).find("input[type=text]").val("");
    $("#" + form_id).find("textarea").val("");
}


function makeElementWithIcon (title, icon_class)
{
    return "<div class='n-cell-icon'><span title='" + title + "' class='icon " + icon_class + "'><span></div>";
}

function reformatDate (timestamp_num)
{
    if (timestamp_num != null)
    {
        var date = new Date();
        date.setTime(timestamp_num);
        return date.Format('yyyy-MM-dd hh:mm:ss');
    }

    return "---";
}

function updateRowWithConfirmation (title, body_message, callback)
{
    showConfirmationDialog(title, body_message, function() {
        callback();
    });
}

function removeObjectAttribute (json_obj, attributes)
{
    for (var i = 0; i < attributes.length; i++)
    {
        delete json_obj[attributes[i]];
    }

    return json_obj;
}