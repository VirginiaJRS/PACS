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

import edu.gmu.csiss.automation.pacs.ontology.Onto2Database;
import edu.gmu.csiss.automation.pacs.ontology.ParameterRuleOntology;
import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class RegisterNewRuleTool.java
 *@author Ziheng Sun
 *@time Oct 5, 2019 4:41:54 PM
 *Original aim is to support PACS.
 */
public class RegisterNewRuleTool {
	/**
	 * Register new rule into the database
	 * @param imgurl
	 * @param methoduri
	 * @param parammap
	 */
	public static void register(String imgurl, String methoduri, Map parammap){
		ParameterRuleOntology pro = new ParameterRuleOntology(imgurl, methoduri, parammap);
		pro.saveToDatabase();
	}
	
}
