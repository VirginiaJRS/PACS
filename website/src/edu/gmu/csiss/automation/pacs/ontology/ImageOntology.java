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
import com.hp.hpl.jena.sparql.function.library.langeq;

import edu.gmu.csiss.automation.pacs.utils.BaseTool;

/**
 *Class ImageOntology.java
 *@author Ziheng Sun
 *@time Oct 5, 2014 3:52:25 PM
 *Original aim is to support PACS.
 */
public class ImageOntology {
	
	public static final String owlpath = "http://www3.csiss.gmu.edu/pacs/ontology/image.owl";
	
	public static final String topper_ontology = "http://www3.csiss.gmu.edu/pacs/ontology/image.owl",
			IMAGE = topper_ontology + "#Image",
			AUTHOR = topper_ontology + "#Author",
			BAND = topper_ontology + "#Band",
			BANDINFO = topper_ontology + "#Band_Info",
			CAMERA = topper_ontology + "#Camera",
			FILE_LOCATION = topper_ontology + "#File_Location",
			FORMAT = topper_ontology + "#Format",
			POINT = topper_ontology + "#Point",
			PROJECTION = topper_ontology + "#Projection",
			RESOLUTION = topper_ontology + "#Resolution",
			SIZE = topper_ontology + "#Size",
			SPATIAL_EXTENT = topper_ontology + "#Spatial_Extent",
			TAKEN_DATE = topper_ontology + "#Taken_Date",
			VOLUME = topper_ontology + "#Volume",
			DESCRIPTION = topper_ontology + "#description",
			TITLE = topper_ontology + "#title",
			CREATEDBY = topper_ontology + "#createdBy",
			HASFORMAT = topper_ontology + "#hasFormat",
			HASTAKENDATE = topper_ontology + "#hasTakenDate",
			DATE = topper_ontology + "#date'",
			VALUE = topper_ontology + "#value",
			HASVOLUME = topper_ontology + "#hasVolume",
			HASPROJECTION = topper_ontology + "#hasProjection",
			WIDTH = topper_ontology + "#width",
			HEIGHT = topper_ontology + "#height",
			HASSIZE = topper_ontology + "#hasSize",
			HASBANDINFO = topper_ontology + "#hasBandInfo",
			TAKENBY = topper_ontology + "#takenBy",
			STOREDAT = topper_ontology + "#storedAt",
			HASSPATIALEXTENT = topper_ontology + "#hasSpatialExtent",
			HASRESOLUTION = topper_ontology + "#hasResolution",
			HASBAND = topper_ontology + "#hasBand",
			BANDCOUNT = topper_ontology + "#band_count",
			NONPROJECTION = topper_ontology + "#Non_Projection";
	
	String tempontpath;
	OntModel om;
	
	private String ontURI = null;
	
	
	public String getOntURI() {
		return ontURI;
	}


	public void setOntURI(String ontURI) {
		this.ontURI = ontURI;
	}
	
	public ImageOntology(Map imagemetadata){
		tempontpath = BaseTool.getClassPath() + "tempont.owl";
		OntModel om = OntologyUtil.readOntModelFromFile(tempontpath);
		createOntology(imagemetadata, om);
	}


	public ImageOntology(Map imagemetadata, OntModel om){
		createOntology(imagemetadata, om);
	}
	/**
	 * Create ontology
	 * @param imagemetadata
	 * @param om
	 */
	private void createOntology(Map imagemetadata, OntModel om){		
		String imageid = String.valueOf(BaseTool.getTimeMilliseconds());
		String ontouri = topper_ontology + "#ImageOnt_" + imageid;
		
		Ontology ot = om.createOntology(ontouri);
		ot.addImport(om.createResource(ImageOntology.owlpath)); 
		//create an individual of class Image
		OntClass imgclass = om.getOntClass(IMAGE);
		String imguri = topper_ontology + "#Image_" + imageid;
		Individual imginv = om.createIndividual(imguri, imgclass);
		//create properties for the image if they exist in the imagemetadata map
		OntClass authorclass = om.getOntClass(AUTHOR);
		OntClass formatclass = om.getOntClass(FORMAT);
		OntClass takendateclass = om.getOntClass(TAKEN_DATE);
		OntClass volclass = om.getOntClass(VOLUME);
		OntClass sizeclass = om.getOntClass(SIZE);
		OntClass bandinfoclass = om.getOntClass(BANDINFO);
		OntClass locationclass = om.getOntClass(FILE_LOCATION);
		OntClass extentclass = om.getOntClass(SPATIAL_EXTENT);
		
		Property createdbyp = om.getProperty(CREATEDBY);
		Property hasformatp = om.getProperty(HASFORMAT);
		Property hastakendatep = om.getProperty(HASTAKENDATE);
		Property datep = om.getProperty(DATE);
		Property valuep = om.getProperty(VALUE);
		Property hasvolp = om.getProperty(HASVOLUME);
		Property hasprojp = om.getProperty(HASPROJECTION);
		Property widthp = om.getProperty(WIDTH);
		Property heightp = om.getProperty(HEIGHT);
		Property hassizep = om.getProperty(HASSIZE);
		Property hasbandinfop = om.getProperty(HASBANDINFO);
		Property takendbyp = om.getProperty(TAKENBY);
		Property storedatp = om.getProperty(STOREDAT);
		Property hasextentp = om.getProperty(HASSPATIALEXTENT);
		Property titlep = om.getProperty(TITLE);
		
		Iterator it = imagemetadata.keySet().iterator();
		while(it.hasNext()){
			String k = (String)it.next();
			String v = (String)imagemetadata.get(k);
			if(k.equals(AUTHOR)){
				String name = OntologyUtil.turnCommonStrToOntURIPart(v);
				String authoruri = topper_ontology + "#" + name;
				Individual authorinv = om.createIndividual(authoruri, authorclass);
				imginv.addProperty(createdbyp, authorinv);
			}else if(k.equals(FORMAT)){
				String format = OntologyUtil.turnCommonStrToOntURIPart(v);
				String formaturi = topper_ontology + "#" + format;
				Individual formatinv = om.createIndividual(formaturi, formatclass);
				imginv.addProperty(hasformatp, formatinv);
			}else if(k.equals(TAKEN_DATE)){
				String takendateuri = topper_ontology + "#TakenDate_" + imageid;
				Individual takendateinv = om.createIndividual(takendateuri, takendateclass);
				takendateinv.addProperty(datep, v);
				imginv.addProperty(hastakendatep, takendateinv);
			}else if(k.equals(VOLUME)){
				String voluri = topper_ontology + "#Vol_" + imageid;
				Individual volinv = om.createIndividual(voluri, volclass);
				volinv.addProperty(valuep, v);
				imginv.addProperty(hasvolp, volinv);
			}else if(k.equals(PROJECTION)){
				String projuri = topper_ontology + "#" + OntologyUtil.turnCommonStrToOntURIPart(v);
				Resource projr = om.getResource(projuri);
				imginv.addProperty(hasprojp, projr);
			}else if(k.equals(SIZE)){
				String[] wh = v.split(" ");
				String sizeuri = topper_ontology + "#Size_" + imageid;
				Individual sizeinv = om.createIndividual(sizeuri, sizeclass);
				sizeinv.addProperty(widthp, wh[0]);
				sizeinv.addProperty(heightp, wh[1]);
				imginv.addProperty(hassizep, sizeinv);
			}else if(k.equals(BANDINFO)){
				String bandinfouri = topper_ontology + "#BandInfo_" + imageid ;
				Individual bandinfoinv = om.createIndividual(bandinfouri, bandinfoclass);
				//record the information of every band 
				imginv.addProperty(hasbandinfop, bandinfoinv);
			}else if(k.equals(CAMERA)){
				String camerauri = topper_ontology + "#" + OntologyUtil.turnCommonStrToOntURIPart(v);
				Resource camerar = om.getResource(camerauri);
				imginv.addProperty(takendbyp, camerar);
			}else if(k.equals(FILE_LOCATION)){
				String locationuri = topper_ontology + "#Location_" + imageid;
				Individual locationinv = om.createIndividual(locationuri, locationclass);
				imginv.addProperty(storedatp, locationinv);
			}else if(k.equals(SPATIAL_EXTENT)){
				String extenturi = topper_ontology + "#Extent_" + imageid;
				Individual extentinv = om.createIndividual(extenturi, extentclass);
				imginv.addProperty(hasextentp, extentinv);
			}else if(k.equals(TITLE)){
				String imgname = OntologyUtil.turnCommonStrToOntURIPart(v);
				imginv.addProperty(titlep, imgname);
			}
		}
		this.om = om;
	}
	
	/**
	 * Save ontology to database
	 */
	public void saveToDatabase(){
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
