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
 * Uploading image to the server
 * @author Ziheng Sun
 * @date 2015.8.27
 */
pacs.upload = {
		
		loadLocalImage: function(){
            var winUp = window.open("","","width=400,height=150"); 
            var docUp = winUp.document;
            docUp.open();
            docUp.writeln("<html><head><title>Image Uploader</title>");
            docUp.writeln("<body  style=\"text-align: center;\">");
    //        docUp.writeln("<script type=\"text/javascript\" src=\"js/TaskGridTool.js\"><\/script>");
            var script = "      function load(){" +
    "        var fl = document.getElementById('filelink');" +
    //"        var oRequest = new XMLHttpRequest();" +
    "        var sURL = fl.href;" +
    "        var winMain=window.opener;" +
    "        var imgurlele = winMain.document.getElementById('imgurl');"+
    "        imgurlele.value = sURL;"+
    "        window.close();" +
    "}";
            var cont = "<form name=\"uploadForm\" method=\"POST\"        enctype=\"MULTIPART/FORM-DATA\"        action=\"FileUpload\">                  \n" +
    "            <!--User Name:<input type=\"text\" name=\"username\" size=\"30\"/><br>--><br>\n" +

    "            Upload File: <input type=\"file\" name=\"file1\"><br><br>\n" +
    "            <input type=\"hidden\" name=\"script\" value=\""+script+"\">"+
    "        <input type=\"submit\" name=\"submit\" value=\"submit\"> &nbsp; <input type=\"reset\" name=\"reset\" value=\"reset\">\n" +
    "      </form>";
            docUp.writeln(cont);
            docUp.writeln("<script>"+"function ret(){var winMain=window.opener;}"+"<\/script></body>");
        }
		
};