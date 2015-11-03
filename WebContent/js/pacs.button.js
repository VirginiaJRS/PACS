/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
# Copyright (C) 2015 CSISS, GMU (http://csiss.gmu.edu),
# Ziheng Sun (szhwhu@gmail.com)
#
# This source is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 2 of the License, or (at your option)
# any later version.
#
# This code is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#
# A copy of the GNU General Public License is available on the World Wide Web
# at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by writing
# to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
# MA 02111-1307, USA.
#
#******************************************************************************
*/
/**
 * Button.js
 * @author Ziheng Sun
 * @date 2015.8.11
 * 
 */
pacs.button = {
		/**
		 * Initialize the click listener function of the Query button
		 */
		init: function(){
			//for uploading image
			if($("#upload").length){
				$("#upload").click(function(){
					pacs.upload.loadLocalImage();
				});
			}
			//for classification
			if($("#classify").length){
				$("#classify").click(function(){
					if(pacs.button.parameterCheck4()){
						pacs.button.submit4();
						pacs.button.disableAllButtons();
					}
				});
			}
			//for the submit button in regser.html
			if($("#regser").length){
				$("#regser").click(function(){
					if(pacs.button.parameterCheck2()){
						pacs.button.submit2();
					}
				});
			}
			if($("#unregser").length){
				$("#unregser").click(function(){
					if(pacs.button.parameterCheck3()){
						pacs.button.submit3();
					}
				});
			}
		},
		/**
		 * Disable buttons and inputs
		 */
		disableAllButtons: function(){
			$("#classify").attr("disabled","disabled");
			$("#reset").attr("disabled","disabled");
			$("#upload").attr("disabled","disabled");
			$("#imgurl").attr("disabled","disabled");
		},
		/**
		 * Check the correctness of the inputs
		 * @reutrns {Boolean}
		 */
		parameterCheck4: function(){
			var o=true;
			if($("#imgurl").val().length==0){
				alert("The image URL is null.");
				o = false;
			}
			return o;
		},
		/**
		 * Check the correctness of the inputted parameter values
		 * @returns {Boolean}
		 */
		parameterCheck3:function(){
			var o = true;
			if($("#wsdluri2").val().length==0){
				alert("WSDL URI cannot be null.");
				o = false;
			}
			return o;
		},
		/**
		 * Check the correctness of the inputted parameter values
		 * @returns {Boolean}
		 */
		parameterCheck2:function(){
			var o = true;
			if($("#wsdluri").val().length==0){
				alert("WSDL URI cannot be null.");
				o = false;
			}
			return o;
		},
		/**
		 * Check the correctness of the inputted parameter values
		 * @returns {Boolean}
		 */
		parameterCheck: function(){
			var o = true;
			if($("#category").val().length==0){
				alert("Product category cannot be null.");
				o = false;
			}else if($("#proj").val().length==0){
				alert("Projection cann't be null.");
				o = false;
			}else if($("#north").val().length==0){
				alert("North cann't be null.");
				o = false;
			}else if($("#south").val().length==0){
				alert("South cann't be null.");
				o = false;
			}else if($("#east").val().length==0){
				alert("East cann't be null.");
				o = false;
			}else if($("#west").val().length==0){
				alert("West cann't be null.");
				o = false;
			}else if($("#bdtv").val().length==0){
				alert("Begin time cann't be null.")
				o = false;
			}else if($("#edtv").val().length==0){
				alert("End time cann't  be null.");
				o = false;
			}else if($("#mailaddress").val().length==0){
				alert("Mail cann't  be null.");
				o = false;
			}
			return o;
		},
		/**
		 * Recover the page construction which may be destroyed by the replacement of the content in the PACS Div.
		 */
		pageRecover: function(){
			$('html, body').animate({ scrollTop: 0 }, 'fast');
			
		},
		/**
		 * When the request is process, display the response message.
		 * @param result
		 * @param status
		 * @param xhr
		 */
		done: function(result,status,xhr){
			console.log("The request is processed.");
			if(result){
				result = result.trim();
			}
			$("#contentdiv").html("<p><img src=\"images/check.png\" class=\"noticeimage\">Congratulations! Your request has been received by the server and will be processed as soon as possible. The order id of your request is <font class=\"orderid\">"+result+"</font>. The results will be sent to the E-mail address you provided on the last page. You can also check the status of the processing <a href=\"\">here</a> by entering the order number.</p><table class=\"blankunderneathcover\"></table>");
			pacs.button.pageRecover();
		},
		
		fail: function(xhr, status, error){
			console.log("The request is failed.");
			if(error){
				error = error.trim();
			}
			$("#contentdiv").html("<p><img src=\"images/wrong.png\" class=\"noticeimage\">Something went wrong and the order is failed to be placed. Try another time or contact <a href=\"mailto:zsun@gmu.edu\">the Webmaster</a>.</p><p>"+status+"</p><table class=\"blankunderneathcover\"></table>");
			pacs.button.pageRecover();
		},
		
		submit: function(){
			//compose the request
			var request = ["category=", $("#category").val(),"&boundingbbox=",$("#east").val(),",",$("#south").val(),",",$("#west").val(),",",$("#north").val(),"&proj=",$("#proj").val(),"&begintime=",$("#bdtv").val(),"&endtime=",$("#edtv").val(),"&mail=",$("#mailaddress").val()];
			pacs.util.sendPostRequest(pacs.util.getServletURL(1), request.join(""),pacs.button.done, pacs.button.fail);
		},
		
		submit2: function(){
			pacs.register.registerWSDL($("#wsdluri").val());
		},
		
		submit3: function(){
			pacs.register.unregisterWSDL($("#wsdluri2").val());
		},
		
		submit4: function(){
			pacs.classification.classify($("#imgurl").val());
		}
};