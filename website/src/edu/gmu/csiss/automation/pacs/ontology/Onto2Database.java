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


import java.io.*;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;


import com.hp.hpl.jena.db.*;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.*;

import edu.gmu.csiss.automation.pacs.utils.BaseTool;
/**
 * Ontology interact with MySQL 
 * @author Ziheng Sun
 * @time Oct 5 2014
 */
public class Onto2Database{
	public static String strDriver; //path of driver class
    public static String strURL; // URL of database
    public static String strUser; //database user id
    public static String strPassWord ; //database password
    public static String strDB =  "MySQL"; //database type
    
	static{
		try {
			Properties p = new Properties();			
			FileInputStream ferr = new FileInputStream(BaseTool.getClassPath() + "/database.properties");
			p.load(ferr);
			ferr.close();
			strDriver = p.getProperty("driver");
			strURL = p.getProperty("database_url");
			strUser = p.getProperty("user");
			strPassWord = p.getProperty("password");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/* Create connection to mysql */
	public  static IDBConnection connectDB(String DB_URL, String DB_USER,
	 String DB_PASSWD, String DB_NAME) {
	    return new DBConnection(DB_URL, DB_USER, DB_PASSWD, DB_NAME);
	} 
	 
	/* Read ontology from file and save it into database */
	public  static OntModel createDBModelFromFile(IDBConnection con, String name,
	   String filePath) {
	    ModelMaker maker = ModelFactory.createModelRDBMaker(con);
	    Model base = maker.createModel(name);
	    OntModel newmodel = ModelFactory.createOntologyModel(getModelSpec(maker), base);
	    newmodel.read(filePath);
	    newmodel.commit();
	    return newmodel;
	}

	/* get ontology from database */
	public static  OntModel getModelFromDB(IDBConnection con, String name) {
	    ModelMaker maker = ModelFactory.createModelRDBMaker(con);
	    Model base = maker.getModel(name);
	    OntModel newmodel = ModelFactory.createOntologyModel(getModelSpec(maker), base);
	    return newmodel;
	}
	
	public static  OntModelSpec getModelSpec(ModelMaker maker) {
	    OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
	    spec.setImportModelMaker(maker);
	    return spec;
	}
	public static void test2(){
		try {
	         Class.forName(strDriver);
	     } catch (ClassNotFoundException e) {
	         e.printStackTrace();
	     }
	  
	     
	     IDBConnection con = connectDB(strURL,strUser, strPassWord, strDB);
	     System.out.println(con);
	     OntModel model = getModelFromDB(con, "expert");
	     SimpleReadOntology(model);
	}

	/* Read ontology class from database */
	public static void SimpleReadOntology(OntModel model) {
		//model.listIndividuals();
		
	     for (Iterator i = model.listClasses(); i.hasNext();) {
	         OntClass c = (OntClass) i.next();
	         System.out.println("class ：---- \n"+c.getURI());
	         Iterator i2 = model.listIndividuals(c);
	         for(;i2.hasNext();){
	        	 Individual in = (Individual)i2.next();
	        	 System.out.println("---- individual：---\n" + in.getURI());
	         }
	     }
	}
	/**
	 * Generate ontology model from database
	 * @return
	 */
	public static OntModel getOntModelFromDB(){
		OntModel model = null;
		try {
	         Class.forName(strDriver);
	         IDBConnection con = connectDB(strURL,strUser, strPassWord, strDB);
		     System.out.println(con);
		     model = getModelFromDB(con, "expert");
		     //con.close();
	     } catch (ClassNotFoundException e) {
	         e.printStackTrace();
	     }
//		catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//		}
	     return model;
	}
	public static void saveOntModel2DataBase(OntModel om){
		try{
            om.commit();
            //om.close();
      } catch(RDFRDBException e){
    	    System.out.println("Exceptions occur in RDF Database module..."+e.getLocalizedMessage());
      }
	}
	public static void closeOntModel2DataBaseConnection(OntModel om){
		om.close();
	}
	public static void cleanDB(){
		try{
            // create a database connection
            IDBConnection conn = new DBConnection ( strURL, strUser, strPassWord, strDB );
            //load database driver
            Class.forName(strDriver); 
           conn.cleanDB();
       	    // close database connection
            conn.close();
      } catch(ClassNotFoundException e) {
            System.out.println("Driver is not available...");
      }catch (SQLException e) {
    	  	e.printStackTrace();
      }catch(RDFRDBException e){
    	    System.out.println("Exceptions occur in RDF Database module...");
      }
	}
	public static void saveOwlFile2DataBase(String owlpath){
		try{
                // create a connection
                IDBConnection conn = new DBConnection ( strURL, strUser, strPassWord, strDB );
                // load driver, need exception control
                Class.forName(strDriver); 
                // generate a modelmaker by factory
                ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
                // create a default model and give it a name: MyOntology
                Model defModel = maker.createModel("expert");
                // prepare the ontology file which need to be stored in database. Create input file stream.
                FileInputStream inputSreamfile = null;
                InputStreamReader in = null;
	            File file = new File(owlpath);
	            inputSreamfile = new FileInputStream(file);
	            in = new InputStreamReader(inputSreamfile,"UTF-8");
	            // Read file
	            defModel.read(in,null);
	            // Close input stream reader
	            in.close();
	            // Execute data transformation, save ontology into database
	            defModel.commit();
	       	    // close database connection
	            conn.close();
	      } catch(ClassNotFoundException e) {
                System.out.println("Driver is not available...");
	      }catch (FileNotFoundException e) {
	           	e.printStackTrace();
	           	System.out.println("Ontology File is not available...");
	      } catch (UnsupportedEncodingException e) {
	          	e.printStackTrace();
	      }catch (IOException e) {
	    	  	e.printStackTrace();
	      }catch (SQLException e) {
	    	  	e.printStackTrace();
	      }catch(RDFRDBException e){
	    	    System.out.println("Exceptions occur in RDF Database module...");
	      }
	}
     public static void test(){

         try{
            // create a database connection
              IDBConnection conn = new DBConnection ( strURL, strUser, strPassWord, strDB );
            
              // load database driver class, need exception control
              try{
                   Class.forName(strDriver); 
              }catch(ClassNotFoundException e) {
                   System.out.println("Driver is not available...");
              }
              
              // use database connection parameter to create a model maker
              ModelMaker maker = ModelFactory.createModelRDBMaker(conn);
              
              // create a default model, name it as myontology.
              Model defModel = maker.createModel("MyOntology");
 
              // prepare the ontoloyg file needed to save into database, create a input file stream.
              FileInputStream inputSreamfile = null;
              try {
                   File file = new File("D:/Tomcat 6.0/webapps/ontologies/tasks/AtomicTaskGrounding20127716243940.owl");
                   inputSreamfile = new FileInputStream(file);
              } catch (FileNotFoundException e) {
                   e.printStackTrace();
                   System.out.println("Ontology File is not available...");
              }
              
              InputStreamReader in = null;
              try {
                   in = new InputStreamReader(inputSreamfile,"UTF-8");
              } catch (UnsupportedEncodingException e) {
                   e.printStackTrace();
              }
              
            // read file
            defModel.read(in,null);
            
            // close input stream reader
            try {
                   in.close();
              } catch (IOException e) {
                   e.printStackTrace();
              }
            
              // perform data transformation, save ontology data into database
            defModel.commit();
              
            // close database connection
              try {
                   conn.close();
              } catch (SQLException e) {
                   e.printStackTrace();
              }
 
         }catch(RDFRDBException e){
              System.out.println("Exceptions occur...");
         }
     }
     
     public static void main(String[] args){
//    	 Onto2Database od = new Onto2Database();
//    	 od.cleanDB();
    	 
    	 Onto2Database.cleanDB();
    	 //od.test2();
//    	 od.saveOwlFile2DataBase("D:/Tomcat 6.0/webapps/ontologies/tasks/Provenance1347702373408.owl");
//    	 OntModel m = od.getOntModelFromDB();
//    	 String DATASET = "http://geopw.whu.edu.cn:8090/ontologies/tasks/provenance.owl#DataSet";
//    	 m.createIndividual("http://geopw.whu.edu.cn:8090/ontologies/tasks/provenance.owl#Test1",m.getResource(DATASET));
    	// od.saveOntModel2DataBase(m);
//    	 m = od.getOntModelFromDB();
//    	 System.out.println(m.getResource("http://geopw.whu.edu.cn:8090/ontologies/tasks/provenance.owl#Test1"));
    	 //m.removeAll();
    	 //m.commit();
    	 //od.test2();
     }
} //code over
