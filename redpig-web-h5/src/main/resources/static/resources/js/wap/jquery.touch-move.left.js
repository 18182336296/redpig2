function touch_move(div_width,div_id_prefix,div_class){ 
	var start;
	var deltaX;
	var den;
	var did;
	var del_onclick;
	var div_wi = div_width;	
	document.addEventListener("touchstart",function(event){
	del_onclick = event.target.getAttribute("touch_id");
	if(""==div_class||jQuery("#"+div_class+"_"+del_onclick).attr("class")==div_class){
	did = div_id_prefix+del_onclick;
	}
	    start = {
            pageX: event.touches[0].pageX,
        };
	    den  = jQuery("#"+did).position().left;	
	deltaX = 0;
	}, false);
	document.addEventListener("touchmove", function (event){
		deltaX = event.touches[0].pageX - start.pageX;
		var pa = parseInt(deltaX)*100/parseInt(div_wi);	
		var de = parseInt(den)*100/parseInt(div_wi); 
		var mo = jQuery("#"+did).position().left;    
		var move = parseInt(mo)*100/parseInt(div_wi); 
			if(Math.abs(de)>8){
				if(move > 1){
				jQuery("#"+did).css("right","1%");	
				}else if(move<-17){
				jQuery("#"+did).css("left","-17%");	
				}else{
					jQuery("#"+did).css("left",pa-16+"%");
				}
			}else{
				if(move>1){
				jQuery("#"+did).css("right","1%");			
				}else if(move<-17){
				jQuery("#"+did).css("left","-17%");		
				}else{
				jQuery("#"+did).css("left",pa+"%");
				}
		}
   },false);
   document.addEventListener("touchend", function (event){
		del = jQuery("#"+did).position().left;
		if(del<0){
		var pa = Math.abs(del)*100/parseInt(div_wi);
		if(pa>8){
			jQuery("#"+did).animate({left:"-16%"},200);
			jQuery("#goods_del_a_"+del_onclick).attr("onclick","big_cart_remove("+del_onclick+")");
		}else{
			jQuery("#"+did).animate({left:"0%"},200);
			jQuery("#goods_del_a_"+del_onclick).attr("onclick","");
			}
		}else{
			jQuery("#"+did).animate({left:"0%"},200);
			jQuery("#goods_del_a_"+del_onclick).attr("onclick","");
		}
		did="";
   },false);  
}