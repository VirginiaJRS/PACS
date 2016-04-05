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

import edu.gmu.csiss.automation.pacs.ontology.Onto2Database;
import edu.gmu.csiss.automation.pacs.ontology.ParameterRuleOntology;
import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class RegisterNewRuleTool.java
 *@author Ziheng Sun
 *@time Oct 5, 2014 4:41:54 PM
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
