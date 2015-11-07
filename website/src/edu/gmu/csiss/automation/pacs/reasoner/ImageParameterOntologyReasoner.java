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
package edu.gmu.csiss.automation.pacs.reasoner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import edu.gmu.csiss.automation.pacs.ontology.ImageOntology;
import edu.gmu.csiss.automation.pacs.ontology.Onto2Database;
import edu.gmu.csiss.automation.pacs.ontology.ParameterRuleOntology;
import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class ImageOntologyReasoner.java
 *According to the inputted image metadata maps, reason a parameter map based on ontology database.
 *@author Ziheng Sun
 *@time Oct 5, 2015 9:51:57 AM
 *Original aim is to support PACS.
 */
public class ImageParameterOntologyReasoner {
	
	/**
	 * Reason a parameter map from the metadata of an image
	 * @param targetkvs
	 * @return
	 */
	public static Map reason(Map targetkvs){
		//get the ontmodel from database
		OntModel om = Onto2Database.getOntModelFromDB();
		//list all the rules
		//list all the images which have parameter rules
		OntClass ruleclass = om.getOntClass(ParameterRuleOntology.SELECTION_RULE);
		
		Property designedforp = om.getProperty(ParameterRuleOntology.DESIGNEDFOR);
		Property includep = om.getProperty(ParameterRuleOntology.INCLUDE);
		Property createdbyp = om.getProperty(ImageOntology.CREATEDBY);
		Property hasconfp = om.getProperty(ParameterRuleOntology.HASCONFIGURATION);
		Property haspairp = om.getProperty(ParameterRuleOntology.HASPAIR);
		Property hasparamp = om.getProperty(ParameterRuleOntology.HASPARAMETER);
		Property hasvaluep = om.getProperty(ParameterRuleOntology.HASVALUE);
		Property paramnamep = om.getProperty(ParameterRuleOntology.PARAMNAME);
		Property valuep = om.getProperty(ParameterRuleOntology.VALUE);
		Property takenbyp = om.getProperty(ImageOntology.TAKENBY);
		Property hasprojp = om.getProperty(ImageOntology.HASPROJECTION);
		Property hasresolutionp = om.getProperty(ImageOntology.HASRESOLUTION);
		Property imagevaluep = om.getProperty(ImageOntology.VALUE);
		Property descp = om.getProperty(ImageOntology.DESCRIPTION);
		Property titlep = om.getProperty(ImageOntology.TITLE);
		Property hassizep = om.getProperty(ImageOntology.HASSIZE);
		Property widthp = om.getProperty(ImageOntology.WIDTH);
		Property heightp = om.getProperty(ImageOntology.HEIGHT);
		Property hasvolumep = om.getProperty(ImageOntology.HASVOLUME);
		Property hasformatp = om.getProperty(ImageOntology.HASFORMAT);
		Property hasbandinfop = om.getProperty(ImageOntology.HASBANDINFO);
		Property hasbandp = om.getProperty(ImageOntology.HASBAND);
		Property bandcountp = om.getProperty(ImageOntology.BANDCOUNT);
		
//		List rules = new ArrayList();
		ExtendedIterator<Individual> invit = om.listIndividuals(ruleclass);
		Individual max_simi_rule = null;
		double max_sim_index = -999;
		while(invit.hasNext()){
			Individual ruleinv = (Individual)invit.next();
			Resource targetimgr = ruleinv.getProperty(designedforp).getResource();
			//for single image - calculate the similarity
			Resource imgr = targetimgr.getPropertyResourceValue(includep);
			//compare the properties between each image and the target image
			//calculate the similarity of each property. Notice the equation for different
			//property is different too.  Some comparisons need refer to WordNet.
			//Compare the overall similarity index values, and figure out the rule
			//whose image has the most similar property values with the target value.
			//Return the rule's parameter value map.
			Iterator keyit = targetkvs.keySet().iterator();
			List<Integer> distancearray = new ArrayList();
			List<Double> weightarray = new ArrayList();
			while(keyit.hasNext()){
				String k = (String) keyit.next();
				String v = (String) targetkvs.get(k);
				
				int dis = 10; //distance is between 0 and 10, default is 10. 10 is the maximum distance number.
				double weight = 1.0; //weight ranges from 0 to 1. If a property is less important, its weight will be lower. 
				if(k.equals(ImageOntology.AUTHOR)){
					Resource authorr = imgr.getPropertyResourceValue(createdbyp);
					//the similarity distance between two authors
					if(authorr.getURI().equals(v)){
						dis = 0;
					}else{
						dis = 2; //semantic connections that can reach from one author to another
						//reproject 2 to 0~10 range - the same process in the following properties
						dis = 10;
					}
					weight = 0.2;
				}else if(k.equals(ImageOntology.CAMERA)){
					Resource camerar = imgr.getPropertyResourceValue(takenbyp);
					if(camerar.getURI().equals(v)){
						dis = 0;
					}else {
						dis = 10;
					}
					weight = 0.2;
				}else if(k.equals(ImageOntology.SPATIAL_EXTENT)){
					//calculate the ratio between the overlapped part and the complete area 
					//not finished
					dis = 5;
					
					weight = 1;
				}else if(k.equals(ImageOntology.DESCRIPTION)){
					//based on wordnet
					String sen1 = v;
					String sen2 = imgr.getProperty(descp).getString();
					dis = WordNetReasoner.calculateDistanceBetweenTwoSentence(sen1, sen2);
					if(dis>10){
						dis = 10; //maximum is 10
					}
					weight = 0.6;
				}else if(k.equals(ImageOntology.TITLE)){
					//based on wordnet and literal similarity algorithm
					String name1 = v;
					String name2 = imgr.getProperty(titlep).getString();
					dis = WordNetReasoner.calculateDistanceBetweenTwoName(name1, name2);
					if(dis>10){
						dis = 10;
					}
					weight = 0.8;
				}else if(k.equals(ImageOntology.PROJECTION)){
					Resource projr = imgr.getPropertyResourceValue(hasprojp);
					if(projr.getURI().equals(v)){
						dis = 0;
					}else{
						dis = 10;
					}
					weight = 1;
				}else if(k.equals(ImageOntology.RESOLUTION)){
					Resource resolutionr = imgr.getPropertyResourceValue(hasresolutionp);
					String res = resolutionr.getProperty(imagevaluep).getString();
					if(res.equals(v)){
						dis = 0;
					}else{
						dis = 10;
					}
					weight = 1;
				}else if(k.equals(ImageOntology.SIZE)){
					//calculate the actual pixel width/height
					Resource sizer = imgr.getProperty(hassizep).getResource();
					String width = sizer.getProperty(widthp).getString();
					String height = sizer.getProperty(heightp).getString();
					String[] newwh = v.split(" ");
					int area1 = Integer.parseInt(width)*Integer.parseInt(height);
					int area2 = Integer.parseInt(newwh[0])*Integer.parseInt(newwh[1]);
					double ratio = 1- Math.abs(area1-area2)/BaseTool.getMax(area1, area2);
					dis = (int)ratio*10;
					weight = 0.7;
				}else if(k.equals(ImageOntology.VOLUME)){
					//get the volume number of the ontology image
					Resource volr = imgr.getPropertyResourceValue(hasvolumep);
					String vol = volr.getProperty(imagevaluep).getString();
					int vol1 = Integer.parseInt(vol);
					int vol2 = Integer.parseInt(v);
					double ratio = 1 - Math.abs(vol1-vol2)/BaseTool.getMax(vol1, vol2);
					dis = (int)ratio*10;
					weight = 0.5;
				}else if(k.equals(ImageOntology.FORMAT)){
					Resource formatr = imgr.getPropertyResourceValue(hasformatp);
					if(formatr.getURI().equals(v)){
						dis = 0;
					}else{
						dis = 10;
					}
					weight = 0.8;
				}else if(k.equals(ImageOntology.BANDINFO)){
					//this is an important comparison
					Resource bandinfor = imgr.getPropertyResourceValue(hasbandinfop);
					//right now , we only compare the count of bands. 
					//The other important information like band width, band range, will be considered in the future development.
//					StmtIterator sit = bandinfor.listProperties(hasbandp);
//					while(sit.hasNext()){
//						
//						Statement st = sit.next();
//						
//					}
					String bandcount = bandinfor.getProperty(bandcountp).getString();
					if(Integer.parseInt(bandcount)==Integer.parseInt(v)){
						dis = 0;
					}else{
						dis = 10;
					}
					weight = 1;
				}else{
					System.out.println("This property is ignored because there is no matched property in the ontology database.");
					continue;
				}
				distancearray.add(dis);
			}
			//calculate the overall distance
			double overalldis = 0.0;
			for(int i=0;i<distancearray.size();i++){
				overalldis += distancearray.get(i)*weightarray.get(i);
			}
			//overall similarity
			double overallsimilarity = 1/overalldis;
			//sort out the rule with maximum similarity
			if(max_sim_index == -999||max_sim_index < overallsimilarity){
				max_sim_index = overalldis;
				max_simi_rule = ruleinv;
			}
		}
		//get the parameter configuration of the rule with the highest similarity
		Resource confinv = max_simi_rule.getPropertyResourceValue(hasconfp);
		StmtIterator sit = confinv.listProperties(haspairp);
		Map parammap = new HashMap();
		while(sit.hasNext()){
			Statement sm = sit.next();
			Resource apair = sm.getResource();
			Resource param = apair.getPropertyResourceValue(hasparamp);
			String paranname = param.getProperty(paramnamep).getString();
			Resource val = apair.getPropertyResourceValue(hasvaluep);
			String value = val.getProperty(valuep).getString();
			parammap.put(param, value);
		}
		return parammap;
	}
	
}
