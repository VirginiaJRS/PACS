/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
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
 *@time Nov 4, 2019 5:35:14 PM
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
