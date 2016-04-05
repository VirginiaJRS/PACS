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
 * PACS Utility Functions
 * @author Ziheng Sun
 * @date 2015.8.6
 */
pacs.util = {
		
		init: function(){
			pacs.util.initServer();
			pacs.util.initPage();
		},
		
		initPage: function(){
			$("#loading").hide();
		},
		
		initHeight: function(){
			//use css to solve this problem
//			var winheight = $(document).height();
//			
		},
		
		/**
		 * Initialize the server environment variables
		 */
		 initServer:function(){
//			serverURL = window.location.hostname;
//			serverPort = window.location.port;
			console.log(window.location.protocol+"\n"+window.location.host+"\n"+window.location.pathname);
		},
		/**
		 * Get servlet URL
		 * @returns
		 */
		getServletURL:function(no){
			var path = window.location.pathname;
			var i = path.indexOf("/PACS");
			var prepath = path.substring(0,i);
			console.log("prefix path: " + prepath);
			var arr = [window.location.protocol, '//', window.location.host, prepath ];
			if(no==1){
				arr.push("/PACS/ParameterlessAutomaticClassificationServlet");
			}
			console.log(arr);
			return arr.join("");
		},
		/**
		 * Send post request to the servlet
		 * Asynchronous by Default
		 * @param url
		 * @param success
		 * @param fail
		 * @param always
		 */
		sendPostRequest: function(url, data, success, fail, always){
			var posting = $.ajax({
//				contentType: "application/x-www-form-urlencoded", //this is by default
				type: "POST",
				url: url, 
				data: data, 
				success: success, //success(result,status,xhr)
				error: fail, //error(xhr,status,error)
				complete: always //(xhr,status)
			});
		},
		
		sendAsynRequest: function(){
			
		}
		
}
