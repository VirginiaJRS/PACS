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
package edu.gmu.csiss.automation.pacs.ontology;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class ParameterRuleOntology.java
 *@author Ziheng Sun
 *@time Oct 5, 2015 4:09:21 PM
 *Original aim is to support PACS.
 */
public class ParameterRuleOntology {
	
	public static final String owlpath = "http://www3.csiss.gmu.edu/pacs/ontology/parameterrule.owl";

	public static final String topper_ontology = "http://www3.csiss.gmu.edu/pacs/ontology/parameterrule.owl",
			CLASSIFICATION_METHOD = topper_ontology + "#Classification_Method",
			INPUT_PARAMETER = topper_ontology + "#Input_Parameter",
			PARAMETER_CONFIGURATION = topper_ontology + "#Parameter_Configuration",
			PARAMETER_PAIR = topper_ontology + "#Parameter_Pair",
			PARAMETER_VALUE = topper_ontology + "#Parameter_Value",
			SELECTION_RULE = topper_ontology + "#Selection_Rule",
			TARGET_IMAGE = topper_ontology + "#Target_Image",
			INCLUDE = topper_ontology + "#include",
			HASCONFIGURATION = topper_ontology + "#has_configuration",
			DESIGNEDFOR = topper_ontology + "#designed_for",
			USEDIN = topper_ontology + "#used_in",
			HASPARAMETER = topper_ontology + "#has_parameter",
			HASPAIR = topper_ontology + "#has_pair",
			HASVALUE = topper_ontology + "#has_value",
			VALUE = topper_ontology  + "#value",
			PARAMNAME = topper_ontology + "#param_name";
	String imageurl, methoduri, tempontpath;
	Map parammap;
	OntModel om;
	
	public ParameterRuleOntology(OntModel om){
		this.om = om;
	}
	
	/**
	 * Construction function
	 * @param imageuri
	 * @param methoduri
	 * @param parammap
	 */
	public ParameterRuleOntology(String imageuri, String methoduri, Map parammap){
		this.imageurl = imageuri;
		this.methoduri = methoduri;
		this.parammap = parammap;
		createOntology();
	}
	/**
	 * Create ontology
	 */
	private void createOntology(){
		tempontpath = BaseTool.getClassPath() + "tempont.owl";
		OntModel om = OntologyUtil.readOntModelFromFile(tempontpath);
		this.createOntology(om);
	}
	/**
	 * Create ontology
	 * @param om
	 */
	private void createOntology(OntModel om){
		String ruleid = String.valueOf(BaseTool.getTimeMilliseconds());
		String ontologyuri = topper_ontology + "#Onto_" + ruleid;
		//add imports to the owl
		Ontology ot = om.createOntology(ontologyuri);
		ot.addImport(om.createResource(ImageOntology.owlpath)); //not sure if image.owl is necessary to be imported here
		ot.addImport(om.createResource(ParameterRuleOntology.owlpath));
		//Create an ontology for an image
		ImageOntology io = OntologyUtil.createAOntologyForAnImage(this.imageurl, om);
		//create an parameter rule individual
		OntClass ruleclass = om.getOntClass(SELECTION_RULE);
		String ruleuri = topper_ontology + "#Rule_" + ruleid;  //use time stamp
		Individual newrule = om.createIndividual(ruleuri, ruleclass);
		
		//create a targetimage individual
		OntClass targetimageclass = om.getOntClass(TARGET_IMAGE);
		String targetimageuri = topper_ontology + "#TargetImage_" + ruleid;
		Individual newtargetimage = om.createIndividual(targetimageuri, targetimageclass);
		//connect the target image with the image uri
		Property includep = om.getProperty(INCLUDE);
		Resource imager = om.getResource(io.getOntURI());
		newtargetimage.addProperty(includep, imager);
		Property designedp = om.getProperty(DESIGNEDFOR);
		newrule.addProperty(designedp, newtargetimage);
		
		//create a parameter configuration individual
		OntClass pconfclass = om.getOntClass(PARAMETER_CONFIGURATION);
		String pconfuri = topper_ontology + "#ParameterConfiguration_" + ruleid;
		Individual newpconf = om.createIndividual(pconfuri, pconfclass);
		Property hasconfp = om.getProperty(HASCONFIGURATION);
		newrule.addProperty(hasconfp, newpconf);
		
		//create a connection between rule and method
		Property usedin = om.getProperty(USEDIN);
		Resource methodr = om.getResource(CLASSIFICATION_METHOD);
		newrule.addProperty(usedin, methodr);
		
		OntClass paramclass = om.getOntClass(INPUT_PARAMETER);
		OntClass pairclass = om.getOntClass(PARAMETER_PAIR);
		OntClass valueclass = om.getOntClass(PARAMETER_VALUE);
		Property hasparamp = om.getProperty(HASPARAMETER);
		Property haspairp = om.getProperty(HASPAIR);
		Property hasvalue = om.getProperty(HASVALUE);
		Property valuep = om.getProperty(VALUE);
		Property paramnamep = om.getProperty(PARAMNAME);
		//add parameter pairs
		Iterator it = parammap.keySet().iterator();
		int pnumber = 0;
		while(it.hasNext()){
			String p = (String)it.next(); //parameter naem
			String v = (String)parammap.get(p); //parameter value
			//create a new parameter pair individual
			String pairuri = topper_ontology + "#Pair_" + ruleid +"_"+ pnumber;
			Individual pairinv = om.createIndividual(pairuri, pairclass);
			//create a new input parameter
			String puri = topper_ontology + "#Param_" + ruleid +"_"+ pnumber;
			Individual pinv = om.createIndividual(puri, paramclass);
			pinv.addProperty(paramnamep, p);
			pairinv.addProperty(hasparamp, pinv);
			//create a new parameter value
			String vuri = topper_ontology + "#Value_" + ruleid + "_"+ pnumber;
			Individual vinv = om.createIndividual(vuri, valueclass);
			vinv.addProperty(valuep, v);
			pairinv.addProperty(hasvalue, vinv);
			//add the new pair to parameter configuration
			newpconf.addProperty(haspairp, pairinv);
			pnumber++;
		}
		//save ontology into a file
		OntologyUtil.saveOntologyModel(om, tempontpath);
		this.om = om;
//		OntClass paper = om.getOntClass( ImageOntology.IMAGE );
//		Individual p1 = om.createIndividual( imgonturi, paper );
//		Individual imgo = om.getResource(imgonturi);
		
	}
	/**
	 * Save ontology to database
	 */
	public void saveToDatabase(){
		//write to a file
//		String filepath = BaseTool.getClassPath() + "tempontology.owl";
//		BaseTool.writeString2File(this.toOWL(), filepath);
		//save the ontology file to database
		if(om==null){
			throw new RuntimeException("The OntModel object is null and cannot be written into a file.");
		}
		FileWriter out = null;
		try {
		  // XML format - long and verbose
		 String filepath = BaseTool.getClassPath() +  "temp.xml" ;
		  out = new FileWriter( filepath);
		  om.write( out, "RDF/XML-ABBREV" );
		  Onto2Database.saveOwlFile2DataBase(filepath);
		  System.out.println("The ontology is saved to the database. \nRegistration is over.");
//		  // OR Turtle format - compact and more readable
//		  // use this variant if you're not sure which to use!
//		  out = new FileWriter( "mymodel.ttl" );
//		  om.write( out, "Turtle" );
		}catch(Exception e){
			throw new RuntimeException("Fail to write the ontology model into a file.");
		}
		finally {
		  if (out != null) {
		    try {
		    	out.close();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    	
		    }
		  }
		}
		
	}
	/**
	 * Turn ontology to owl
	 * @return
	 */
	public String toOWL(){
		if(om==null){
			throw new RuntimeException("The OntModel object is null and cannot be written into a file.");
		}
		FileWriter out = null;
		try {
		  // XML format - long and verbose
		  out = new FileWriter( BaseTool.getClassPath() +  "temp.xml" );
		  om.write( out, "RDF/XML-ABBREV" );
		  
//		  // OR Turtle format - compact and more readable
//		  // use this variant if you're not sure which to use!
//		  out = new FileWriter( "mymodel.ttl" );
//		  om.write( out, "Turtle" );
		}catch(Exception e){
			throw new RuntimeException("Fail to write the ontology model into a file.");
		}
		finally {
		  if (out != null) {
		    try {
		    	out.close();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    	
		    }
		  }
		}
		return null;
	}
	
}
