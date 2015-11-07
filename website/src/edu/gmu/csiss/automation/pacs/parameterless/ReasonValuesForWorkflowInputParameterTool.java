/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
# Copyright (C) 2015 CSISS, GMU (http://csiss.gmu.edu), Ziheng Sun (szhwhu@gmail.com)
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
package edu.gmu.csiss.automation.pacs.parameterless;

import java.util.Map;

import edu.gmu.csiss.automation.pacs.reasoner.ImageParameterOntologyReasoner;
import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class ReasonValuesForWorkflowInputParameterTool.java
 *@author Ziheng Sun
 *@time Nov 4, 2015 5:35:14 PM
 *Original aim is to support PACS.
 */
public class ReasonValuesForWorkflowInputParameterTool {
	
	/**
	 * Reason input values for an image
	 * @param imgurl
	 * @return
	 */
	public static Map reasonInputValuesForAnImage(String imgurl){
		//get details of the image
		Map kvs = BaseTool.getImageMetadataMap(imgurl);
		//input the image to a reasoner to find the similarest image in the ontology database
		Map parammap = ImageParameterOntologyReasoner.reason(kvs);
		//output the generated values
		return parammap;
	}
	
}
