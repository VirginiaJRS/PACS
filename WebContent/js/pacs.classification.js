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
 * Classification Function
 * @author Ziheng Sun
 * @date 2015.08.27
 */
pacs.classification = {
		/**
		 * Success
		 * @param result
		 * @param status
		 * @param xhr
		 */
		onsuccess: function(result,status,xhr){
			console.log("The request is processed.");
			if(result){
				result = result.trim();
			}
			if(result.indexOf("ERR")==0){
				$("#contentdiv").html("<p><img src=\"images/wrong.png\" class=\"noticeimage\">Something went wrong and the classification is failed. Try another time or contact <a href=\"mailto:zsun@gmu.edu\">the Webmaster</a>.</p><p>"+status+"</p><p>"+result+"</p>");
			}else{
				$("#contentdiv").html("<p><img src=\"images/check.png\" class=\"noticeimage\">Your target image has been successfully classified. The link to the result is <a href=\""+result+"\" class=\"orderid\">"+result+"</a>. </p>" +
						"<p>The result is a ESRI ArcGIS Shapefile with its supporting documents. In the shapefile, the property table of each vector includes a property named \"class\". The value of column class is the final classification result of the vector.</p>" +
						"<p>Unzip the shapefile package and open it by a GIS tool. Most of the current GIS systems support reading and displaying ESRI shapefile format. </p>" +
						"<p>If you want to classify another image, click <a href=\"index.html\">here</a>.</p>");
			}
			
		},
		/**
		 * Failure
		 * @param xhr
		 * @param status
		 * @param error
		 */
		onfailure: function(xhr, status, error){
			if(error){
				error = error.trim();
			}
			$("#contentdiv").html("<p><img src=\"images/wrong.png\" class=\"noticeimage\">Something went wrong and the classification is failed. Try another time or contact <a href=\"mailto:zsun@gmu.edu\">the Webmaster</a>.</p><p>"+status+"</p><p>"+error+"</p>");
		},
		/**
		 * Always
		 */
		onalways: function(){
			$("#loading").hide();
		},
		/**
		 * Classify an image into meaningful features
		 * @param imgurl
		 */
		classify: function(imgurl){
			//invoke the workflow 
			$("#loading").show();
			var req = imgurl;
			pacs.util.sendPostRequest(pacs.util.getServletURL(1), req, pacs.classification.onsuccess, pacs.classification.onfailure);
		}
		
};