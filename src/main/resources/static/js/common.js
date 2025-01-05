$(function() {

    $('#logoText').click(function(){
		window.location.href="/ui/";
	});

	TxtBoxUtils.start();
	PopBoxUtils.start();
	TableUtils.rowColour();
	
});

var CheckBoxUtils = {
		selectAll : function() {
			$('#selectAll').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('.selectItem').attr('checked', true);
					$('.tabCom tbody tr').each(function(){
						if($(this).find('.selectItem').length == 0){
							return true;
						}
						var oldColor = $(this).css('background-color');
						$(this).attr('oldColor',oldColor).css('background-color','#FFFFAA');
					});
				} else {
					$('.selectItem').attr('checked', false);
					$('.tabCom tbody tr').each(function(){
						if($(this).find('.selectItem').length == 0){
							return true;
						}
						var oldColor = $(this).attr('oldColor');
						$(this).removeAttr('oldColor').css('background-color', oldColor);
					});
				}
			});
		},
		
		selectSingle: function(){
			$('.selectItem').click(function(){
				var tr = $(this).parent().parent();
				if ($(this).attr('checked') == 'checked') {
					var oldColor = tr.css('background-color');
					tr.attr('oldColor',oldColor).css('background-color','#FFFFAA');
					if($('.selectItem:checked').length == $('.selectItem').length){
						$('#selectAll').attr('checked',true);
					}
				}else{
					var oldColor = tr.attr('oldColor');
					tr.removeAttr('oldColor').css('background-color', oldColor);
					if($('.selectItem:checked').length == 0){
						$('#selectAll').attr('checked',false);
					}
				}
				
			});
		}
	};

var TableUtils = {
		rowColour: function(){
			$('.tblCom tbody tr:odd').css({'background-color':'#F5FFE8'});
		},
		
		initialize : function(line) {
			var lastTr = $('.tblCom tbody tr:last');
			if (lastTr.find('.tabNoData').length > 0) {
				return;
			}
			var len = $('.tblCom tbody tr').length;
			if (len < line) {
				var ht = '<tr>';
				lastTr.find('td').each(function() {
					ht += '<td></td>';
				});
				ht += '</tr>';
				for (var i = 1; i <= line - len; i++) {
					$(ht).appendTo($('.tblCom tbody'));
				}
				$('.tblCom tbody tr:odd').css({'background-color':'#F5FFE8'});
			}
		}
};

var TxtBoxUtils = {

	start : function() {
		$('.txtField').live('focus', function() {
			$(this).css('background-color', '#FFD2D2');
		}).live('blur', function() {
			$(this).css('background-color', '#fff');
		});
	}

};

var TxtUtils = {
		showLongString: function(){
			var width;
			$('.longName').each(function(){
				width = $(this).parent().width();
				$(this).css('width', width - 10);
				$(this).attr('title',$.trim($(this).text()));
			});
		},
		
		loadingString: function(){
			var tw;
			if($('.tabCom').length > 0){
				tw = $('.tabCom').height();
			}else{
				tw = 450;
			}
			var s = '<div class="waitForData" style="height: ' + tw + 'px; line-height: ' + tw + 'px;"><label>l</label><label>o</label><label>a</label><label>d</label><label>i</label><label>n</label><label>g</label>...</div>';
			$('#tabContent').html(s);
			var index = 0;
			var len = $('.waitForData label').length;
			var obj = setInterval(function(){
				$('.waitForData label').eq(index++ % len).css({'font-size':'72pt','text-transform':'uppercase'}).siblings().css({'font-size':'36pt','text-transform':'lowercase'});
				if($('.waitForData').length == 0){
					clearInterval(obj);
				}
			},300);
		}
};

var PopBoxUtils = {

	start : function() {
		if ($(".popBox").length > 0) {
			$(".popBox").hide();
			$(".popClose").click(function() {
				$(".popBox").CloseDiv();
			});
		}
	}

};

var DateUtils = {

	formatNow : function() {
		var days = [ '日', '一', '二', '三', '四', '五', '六' ];
		var obj = new Date();
		var y = obj.getFullYear();
		var m = obj.getMonth() + 1;
		var d = obj.getDate();
		var i = obj.getDay();
		return y + '年' + m + '月' + d + '日' + ' 星期' + days[i];
	}

};

var Utils = {
	getScrollTop: function(){
		if($.browser.mozilla) {
			return document.documentElement.scrollTop;
		}else{
			return document.body.scrollTop;
		}
	}
};