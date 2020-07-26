	function open_im(){
  var goods_id=arguments[0];
  var url=arguments[1];
  var type=arguments[2];  //打开类型，user为用户打开，store为商家打开，plat为平台打开
  var to_type=arguments[3];
  var store_id=arguments[4];
  var service_type=arguments[5];//客服类型，专门用于买家
  if(type=="store"){
  window.open (url+"/service_chatting",'plat','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');	  
	  }
  if(type=="user"){
	  if(service_type){
		 
  window.open (url+"/chatting_distribute?goods_id="+goods_id+"&service_type="+service_type+"&store_id="+store_id,'','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');		  
	  }else{
		window.open (url+"/chatting_index?goods_id="+goods_id+"&store_id="+store_id,'','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');	  
	 }
  }
  if(type=="plat"){
  window.open (url+"/service_chatting",'plat','height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
	 }
}
	
	//滑动添加商品详情
    jQuery(document).ready(function(){
		
		//弹出框开始
		 jQuery("a[dialog_uri],input[dialog_uri],dt[dialog_uri]").on("click",function(e){
    var dialog_uri=jQuery(this).attr("dialog_uri");
	var dialog_title=jQuery(this).attr("dialog_title");
    var dialog_id=jQuery(this).attr("dialog_id");
	var dialog_height=jQuery(this).attr("dialog_height");
	var dialog_width=jQuery(this).attr("dialog_width");
	var dialog_top=jQuery(this).attr("dialog_top");
	var dialog_left=300;
	jQuery("#"+dialog_id).remove();
	 if(dialog_uri!="undefined"){
       jQuery("body").append("<div id='"+dialog_id+"'><div class='white_content'> <a href='javascript:void(0);' dialog_uri='undefined' class='white_close' onclick='javascript:jQuery(\"#"+dialog_id+"\").remove();'>X</a><div class='white_box'><h1>"+dialog_title+"</h1><div class='content_load'></div></div></div><div class='black_overlay'></div></div>");
	   e.preventDefault(); 
	   if(dialog_top==undefined||dialog_top==""){
	     dialog_top=jQuery(window).scrollTop()+(jQuery(window).height()-jQuery(document).outerHeight())/2-dialog_height/2 - 30;
	   }else{
		 dialog_top=parseInt(dialog_top)+jQuery(window).scrollTop();
	   }
	   var h=jQuery(document).height();
       jQuery('.black_overlay').css("height",h);
	   var dialog_left=(jQuery(document).width()-dialog_width)/2;
       jQuery(".white_content").css("position","fixed").css("top",parseInt(dialog_top)+"px").css("left","3%");
	   jQuery.ajax({type:'POST',url:dialog_uri,async:false,data:'',success:function(html){
	    	jQuery(".content_load").remove(); 
			jQuery("#"+dialog_id+" .white_content").css("width","90%");
		    jQuery("#"+dialog_id+" .white_box h1").after(html);
		    jQuery("#"+dialog_id).show();  
	   }});
	   jQuery("#"+dialog_id+" .white_box h1").css("cursor","move")
	   jQuery("#"+dialog_id+" .white_content").draggable({handle:" .white_box h1"});
	 }
  });
  //需要结合页面中的checkbox选择框，必须选择一条数据才可以操作
  jQuery("a[ck_dialog_uri],input[ck_dialog_uri],dt[ck_dialog_uri]").on("click",function(e){
      var mulitId="";
	  jQuery("#ListForm").find(":checkbox:checked").each(function(){
	    if(jQuery(this).val()!=""){	  
	      mulitId+=jQuery(this).val()+",";
	    }
	  });
	  var dialog_uri=jQuery(this).attr("ck_dialog_uri");
	  var dialog_title=jQuery(this).attr("ck_dialog_title");
      var dialog_id=jQuery(this).attr("ck_dialog_id");
	  var dialog_height=jQuery(this).attr("ck_dialog_height");
	  var dialog_width=jQuery(this).attr("ck_dialog_width");
	  var dialog_top=jQuery(this).attr("ck_dialog_top");
	  var dialog_left=300;
	  if(dialog_uri!=undefined&&dialog_uri!=""){
	  if(mulitId!=""){ 
        jQuery("body").append("<div id='"+dialog_id+"'><div class='white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"+dialog_id+"\").remove();'>X</a><div class='white_box'><h1>"+dialog_title+"</h1><div class='content_load'></div></div></div><div class='black_overlay'></div></div>");
	   e.preventDefault(); 
	   if(dialog_top==undefined||dialog_top==""){
	     dialog_top=jQuery(window).scrollTop()+(jQuery(window).height()-jQuery(document).outerHeight())/2-dialog_height/2;
	   }else{
		 dialog_top=parseInt(dialog_top)+jQuery(window).scrollTop();
	   }
	   var h=jQuery(document).height();
       jQuery('.black_overlay').css("height",h);
	   var dialog_left=(jQuery(document).width()-dialog_width)/2;
	   if(dialog_uri.indexOf("?")>=0){
	     dialog_uri=dialog_uri+"&mulitId="+mulitId;
	   }else{
	     dialog_uri=dialog_uri+"?mulitId="+mulitId;
	   }
       jQuery(".white_content").css("position","absolute").css("top",parseInt(dialog_top)+"px").css("left",parseInt(dialog_left)+"px");
	   jQuery.ajax({type:'POST',url:dialog_uri,async:false,data:'',success:function(html){
	    	jQuery(".content_load").remove(); 
			jQuery("#"+dialog_id+" .white_content").css("width",dialog_width);
		    jQuery("#"+dialog_id+" .white_box h1").after(html);
		    jQuery("#"+dialog_id).show();  
	   }});
	   jQuery("#"+dialog_id+" .white_box h1").css("cursor","move")
	   jQuery("#"+dialog_id+" .white_content").draggable({handle:" .white_box h1"});
	  }else{
	    alert("至少选择一条记录");
	  }
	 }
  });
	});<!--end-->