var alert_timer_id;
function WapshowDialog() {
	var type = arguments[0];// 0为倒计时提示框，1为确认框（包含2个按钮，点击确定执行回调）,2为提示确认框（只含有一个确认按钮）
	var content = arguments[1];// 提示内容
	var confirm_action = arguments[2];// callback方法
	var back_function_args = arguments[3];// 带参数回调函数发送的参数
	var second = arguments[4];// 倒计时时间数,默认时间2秒，
	var fade_second = arguments[5];// 倒计时时间数,默认时间2
	if (type == undefined || type == "") {
		type == 0;
	}
	if (second == undefined || second == "") {
		second = 1000;
	}
	if (fade_second == undefined || second == "") {
		fade_second = 200;
	}
	if (content == undefined || content == "") {
		content = "确定要操作吗？";
	}
	var s = "<div class='pop_floor' ><div class='pop_box' style='left:7.5%; top:35%;'><div class='pop_info'>"
			+ content
			+ "</div><div class='do_btn'><a class='cancel' href='javascript:void(0);' onclick='re_false();'>取消</a><a class='sure' href='javascript:void(0);' onclick='re_true();'>确定</a></div></div><div class='cover_floor'></div></div>";
	var c = "<div class='pop_floor_1'><div class='pop_wrap' style='left:25%; top:34%;'><p class='text'>"
			+ content + "</p></div></div>";
	if (type == 1) {// alert定时隐藏
		jQuery("body").append(c);
		alert_timer_id = window.setInterval(function() {
			jQuery(".pop_floor").fadeOut(fade_second,
					runcallback(confirm_action));
			window.clearInterval(alert_timer_id);
		}, second);
	}
	if (type == 2) {// 确认并回调框
		jQuery("body").append(s);
	}
	jQuery(document).ready(function() {
		jQuery(".sure").click(function() {
			if (type == 1) {
				jQuery(".pop_floor").remove();
				runcallback(confirm_action);
			}
			if (type == 2) {
				jQuery(".pop_floor").remove();
				runcallback(confirm_action);
			}
		});
		jQuery(".cancel").click(function() {
			jQuery(".pop_floor").remove();
		});
	});
	function runcallback(callback) {
		if (confirm_action != undefined && confirm_action != "") {
			if (back_function_args == undefined || back_function_args == "") {
				callback();
			} else {
				callback(back_function_args);
			}
		}
	}
}
var wap_alert_timer_id;
function Wap_alert(){
	window.clearInterval(wap_alert_timer_id);
	jQuery(".pop_floor_1").remove();
	var content = arguments[0];// 提示内容
	if (content == undefined || content == "") {
		content = "";
	}
	var s = "<div class='pop_floor_1'><div class='pop_wrap' style='left:25%; top:34%;'><p class='text'>"
			+ content + "</p></div><div class='cover_floor'></div></div>";
	jQuery("body").append(s);
	wap_alert_timer_id = window.setInterval(function() {
		jQuery(".pop_floor_1").fadeOut(200, function() {
			jQuery(".pop_floor_1").remove();
			window.clearInterval(wap_alert_timer_id);
		});
	}, 2000);
}