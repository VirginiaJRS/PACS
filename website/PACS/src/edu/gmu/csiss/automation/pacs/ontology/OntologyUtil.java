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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileManager;

import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class ImageOntologyUtil.java
 *@author Ziheng Sun
 *@time Oct 5, 2014 9:51:16 AM
 *Original aim is to support PACS.
 */
public class OntologyUtil {
	
	
	/**
	 * Create a ontology for an image
	 * @param imagefileurl
	 */
	public static ImageOntology createAOntologyForAnImage(String imagefileurl, OntModel om){
		Map kvs = BaseTool.getImageMetadataMap(imagefileurl);
		ImageOntology io = new ImageOntology(kvs, om);
		System.out.println("A new image ontology is created.");
		return io;
	}
	
	/**
	 * Save image ontology into database
	 * @param io
	 */
	public static void saveImageOntologyIntoDatabase(ImageOntology io){
		io.saveToDatabase();
	}
	/**
	 * add by Ziheng Sun
	 * @param v
	 * @return
	 */
	public static String turnCommonStrToOntURIPart(String v){
		return v.replaceAll(" ", "_").replaceAll(".", "_").replaceAll(":", "_");
	}
	
	/**
	 * Read ontology model from file
	 * @param ontoFile
	 * ontology file
	 * @return
	 * ontModel
	 */
	public static OntModel readOntModelFromFile(String ontoFile){
		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		try 		
		{
		    InputStream in = FileManager.get().open(ontoFile);
		    try 
		    {
		        ontoModel.read(in, null);
		    } 
		    catch (Exception e) 
		    {
		        e.printStackTrace();
		    }
		    System.out.println("Ontology " + ontoFile + " loaded.");
		} 
		catch (JenaException je) 
		{
		    System.err.println("ERROR" + je.getMessage());
		    je.printStackTrace();
		    throw new RuntimeException("Fail to load from ontology file: " + ontoFile);
		}
		return ontoModel;
	}
	
	/**
	 * 
	 * @param om
	 * @param filepath
	 * @throws IOException
	 */
	public static void saveOntologyModel(OntModel om, String filepath){
		File f = new File(filepath);
//		if(!f.exists()){
//			f.mkdirs();
//		}
		FileOutputStream fos = null;
		try {
			f.createNewFile();
			fos = new FileOutputStream(f);
			om.write(fos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Fail to write the ontology into file: " + filepath);
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
