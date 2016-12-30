<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
	String path = request.getContextPath();/*获得当前项目的根路径 */
%>

<!DOCTYPE html>
<html lang="en">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
    
    <META HTTP-EQUIV="pragma" CONTENT="no-cache">    
    <META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">    
    <META HTTP-EQUIV="expires" CONTENT="0">    
  
 <head>
  <link rel="stylesheet" href="<%=path%>/stylesheets/weui.css"/>
  <link rel="stylesheet" href="<%=path%>/stylesheets/jquery-weui.css"/>
  <link rel="stylesheet" href="<%=path%>/stylesheets/projectcss.css"/>
  <script src="<%=path%>/javascripts/jquery-2.1.4.js"></script>
  <script src="<%=path%>/javascripts/jquery-weui.js"></script>
  <script type="text/javascript" src="http://g.alicdn.com/ilw/ding/0.7.3/scripts/dingtalk.js"></script>
  <title>项目变更审批</title>
 </head>
 <style>
 .assess{
 background:#04BE02;
 color:white;
 width:40px;
 height:40px;
 border-radius:40px; 
 text-align:center;
 line-height:40px;
 margin-right:20px;
 }
 </style>
 <body >
  <div class="weui-pull-to-refresh-layer">
    <div class="pull-to-refresh-arrow"></div> <!-- 上下拉动的时候显示的箭头 -->
    <div class="pull-to-refresh-preloader"></div> <!-- 正在刷新的菊花 -->
    <div class="down">下拉刷新</div><!-- 下拉过程显示的文案 -->
    <div class="up">释放刷新</div><!-- 下拉超过50px显示的文案 -->
    <div class="refresh">正在刷新...</div><!-- 正在刷新时显示的文案 -->
  </div>
 
 <form>
<div class="weui_cells weui_cells_form " id="divform">

  <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">变动部门</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="department" id="department">
        <option value="${itemChange.department}">${itemChange.department}</option>
      </select>
    </div>
   </div>
   </div>
  </div>
  
  
    <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">变动项目</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="project" id="project">
      <option  >${itemChange.project}</option>
      </select>
    </div>
   </div>
   </div>
  </div>
  
  
  
    <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">变动订单</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="orderName" id="orderName">
        <option >${itemChange.orderName}</option>
      </select>
    </div>
   </div>
   </div>
  </div>
  
  
    <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">商务属性</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="businessProperty" id="businessProperty">
        <option  value="${itemChange.businessProperty}"> ${itemChange.businessProperty}</option>       
      </select>
    </div>
   </div>
   </div>
  </div>
  
  
  
    <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">商务等级</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="yindaIdentify" id="yindaIdentify">
      <c:forEach items="${identifyList}" var="staff"> 
       <option value="${staff.yindaIdentify}"  <c:if test="${staff.yindaIdentify eq itemChange.yindaIdentify }">selected="selected"</c:if>>${staff.yindaIdentify} </option>
      </c:forEach>
      </select>
    </div>
   </div>
   </div>
  </div>
  
        <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">合同类型</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="contractType" id="contractType">
       <option >${itemChange.contractType}</option>
      </select>
    </div>
   </div>
   </div>
  </div>
  

   <div class="weui_cell">
    <div class="weui_cell_hd"><label class="weui_label">变动省份</label></div>
    <div class="weui_cell_bd weui_cell_primary">
      <input class="weui_input" type="text" placeholder="请输入省份" id="changeProvince" name="changeProvince" value="${itemChange.changeProvince}" readOnly>
    </div>
  </div>
  
     <div class="weui_cell">
    <div class="weui_cell_hd"><label class="weui_label">变动城市</label></div>
    <div class="weui_cell_bd weui_cell_primary">
      <input class="weui_input" type="text" placeholder="请输入城市" id="changeCity" name="changeCity" value="${itemChange.changeCity}" readOnly>
    </div>
  </div>

  
  
       <div class="weui_cell weui_vcode">
    <div class="weui_cell_hd"><label class="weui_label">室外工作</label></div>
    <div class="weui_cell_bd weui_cell_primary">
    <div class="weui_cell weui_cell_select">
    <div class="weui_cell_bd weui_cell_primary">
      <select class="weui_select" name="select1">
        <option  value="${itemChange.outdoorJob}">${itemChange.outdoorJob}</option>  
      </select>
    </div>
   </div>
   </div>
  </div>
  
    <div class="weui_cell">
    <div class="weui_cell_hd"><label for="" class="weui_label">生效日期</label></div>
    <div class="weui_cell_bd weui_cell_primary">
      <input class="weui_input" type="text" value="${itemChange.modifyTime}">
    </div>
  </div>

 
  
  <div class="weui_cell">
    <div class="weui_cell_hd"><label for="" class="weui_label">申请人</label></div>
    <div class="weui_cell_bd weui_cell_primary">
     <div class="assess">${fn:substring(approveName, 1,alength)}<div>
    </div>
  </div>
  </div>
  
 
</div>

   <div class="weui_cell">
    
    <div class="weui_cell_bd weui_cell_primary">
     <c:if test="${itemChange.nowAcess eq staff_user_id}"> <a href="javascript:pass_approve();" class="weui_btn weui_btn_primary">通过</a> </c:if> 
    </div>
    
      <div class="weui_cell_bd weui_cell_primary">
      <c:if test="${itemChange.nowAcess eq staff_user_id}">  <a href="javascript:refuse_approve();" class="weui_btn weui_btn_disabled weui_btn_warn">拒绝</a> </c:if> 
    </div>
   
  </div>
 </div></form>		
    <script>
    /** 页面下拉刷新**/
    $(document.body).pullToRefresh();   
    $(document.body).on("pull-to-refresh", function() {   	 
    	window.location.reload();  	 	
    	$(document.body).pullToRefreshDone();
    	});


    
    function pass_approve(){
    	
    	var id ="${itemChange.id}";
    	var yindaIdentify =$("#yindaIdentify").val();
    	$.post("<%=path%>/orderChange/pass_approve.do",{"id":id,"identify":yindaIdentify},function(data){
    	if("success"==data){
    		$.alert("操作成功！");
    		$.post("<%=path%>/orderChange/get_approve_un.do",function(data){
    			$("#content").html(data);
    		
    		});
    	}else{   		
    		$.alert("操作失败！");
    	}    		
    	});
    	
    }
  

    function refuse_approve(){
    	 var id ="${itemChange.id}";
    		$.post("<%=path%>/orderChange/refuse_approve.do",{"id":id},function(data){
    			if("success"==data){
    	    		$.alert("操作成功！");
    	    		$.post("<%=path%>/orderChange/get_approve_un.do",function(data){
    	    			$("#content").html(data);
    	    		
    	    		});
    	    	}else{   		
    	    		$.alert("操作失败！");
    	    	}
    	    		
    			
    		});
        	
    
    	
    }

     
        
      
     
        
    </script>
 </body> 
</html>
