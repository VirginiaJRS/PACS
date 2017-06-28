/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
#******************************************************************************
*/
package edu.gmu.csiss.automation.pacs.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.soap.SOAPException;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;

import edu.gmu.csiss.automation.pacs.ontology.ImageOntology;
import edu.gmu.csiss.automation.pacs.ontology.OntologyUtil;


/**
 *Class BaseTool.java
 *@author ziheng
 *@time Aug 6, 2019 2:49:10 PM
 *Original aim is to support PACS.
 */
public class BaseTool {
	
	private static String _classpath = null;
	/**
	 * Judge whether an object is null
	 * @param obj
	 * @return
	 * true or false
	 */
	public static boolean isNull(Object obj){
		boolean isnull=false;
		if(obj==null || obj == "" || "".equals(obj)){
			isnull = true;
		}
		return isnull;
	}
	/**
	 * Escape the reserved characters
	 * @param msg
	 * @return
	 */
	public String escape(String msg){
		msg = msg.replaceAll("\\'", "");
		return msg;
	}
	/**
	 * Get max number between two numbers
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getMax(int a, int b) {
		  return (a>b?a:b);
	}
	/**
	 * Get classpath
	 * @return
	 * class path
	 */
	public static String getClassPath(){
		if(isNull(_classpath)){
			String dir = new BaseTool().getClass().getClassLoader()
			.getResource("").getPath();
//			dir = dir.replaceAll("\\%20", " "); //commented by Ziheng on 8/29/2015
			try {
				dir = URLDecoder.decode(dir,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			_classpath = dir;
		}
		return _classpath;
	}
	/**
	 * Read the string from a file
	 * @param path
	 * @return
	 */
	public static String readStringFromFile(String path){
		StringBuffer strLine = new StringBuffer();
		try{
			  // Open the file that is the first 
			  // command line parameter
			  FileInputStream fstream = new FileInputStream(path);
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  FileReader fr = new FileReader(path);
			  BufferedReader br = new BufferedReader(fr);
			  String str = null;
			  //Read File Line By Line
			  while ((str = br.readLine()) != null)   {
			  // Print the content on the console
				  strLine.append(str).append("\n");
//				  System.out.println (strLine);
			  }
			  //Close the input stream
			  in.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
	    }
		return strLine.toString().trim();
	}
	/**
	 * Parse string from input stream
	 * @param in
	 * @return
	 */
	public String parseStringFromInputStream(InputStream in){
	        String output = null;
	        try{
	                // WORKAROUND cut the parameter name "request" of the stream
	                BufferedReader br = new BufferedReader(new 
	                                InputStreamReader(in,"UTF-8"));
	                StringWriter sw = new StringWriter();
	                int k;
	                while ((k = br.read()) != -1) {
	                        sw.write(k);
	                }
	                output = sw.toString();
	
	        }catch(Exception e){
	                e.printStackTrace();
	        }finally{
	                try{
	                        in.close();
	                }catch(Exception e1){
	                        e1.printStackTrace();
	                }
	        }
	        return output;
	}
	/**
	 * Read document from string
	 * @param xmlstring
	 * @return
	 */
	public Document readDocumentFromString(String xmlstring){
        Document doc = null;
        try{
                doc  = DocumentHelper.parseText(xmlstring.trim());
        }catch(Exception e){
//                e.printStackTrace();
                throw new RuntimeException("Fail to read document from string:"+xmlstring);
        }
        return doc;
	}
	/**
	 * Read element from string
	 * @param xmlstring
	 * @return
	 */
	public  Element readElementFromString(String xmlstring){
	        Element ele = null;
	        try{
	                Document doc  = DocumentHelper.parseText(xmlstring.trim());
	                ele = doc.getRootElement();
	        }catch(Exception e){
	//                e.printStackTrace();
	                throw new RuntimeException("Fail to read element from string:"+xmlstring);
	        }
	        return ele;
	}
	/**
     * Convert string to input stream
     * @param str
     * @return
     * @throws IOException 
     */
    public InputStream convertString2InputStream(String str) throws IOException{
        InputStream stream = IOUtils.toInputStream(str, "UTF-8");
        return stream;
    }
	
    /**
     * Get the DATETIME format of current time
     * @return
     * DATETIME
     */
    public String getCurrentMySQLDatetime(){
    	java.util.Date dt = new java.util.Date();
    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String currentTime = sdf.format(dt);
    	return currentTime;
    }
	/**
	 * 
	 * @return
	 */
    public static long getTimeMilliseconds(){
    	return java.lang.System.currentTimeMillis();
    }
	/**
	 * send a HTTP POST request
	 * @param param
	 * @param input_url
	 * @return
	 */
	public static  String POST(String param,String input_url){
        try {
                URL url = new URL(input_url);	      
                HttpURLConnection con =(HttpURLConnection)url.openConnection();
                con.setDoOutput(true); 
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/xml");
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setUseCaches(false);

                PrintWriter xmlOut = new PrintWriter(con.getOutputStream());
                xmlOut.write(param);   
                xmlOut.flush();
                BufferedReader response = new BufferedReader(new InputStreamReader(con.getInputStream())); 
                String result = "";
                String line;
                while((line = response.readLine())!=null){
                    result += "\n" + line;
                }
                return result.toString();  
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	/**
	 * Get image metadata
	 * @param imgurl
	 * @return
	 */
	public static String getImageMetadata(String imgurl){
		return getOnlineFileContent(imgurl);
	}
	/**
	 * Read online file content
	 * @param txturl
	 * @return
	 */
	public static String getOnlineFileContent(String txturl){
		StringBuilder  stringBuilder = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader( new FileReader (txturl));
			String line = null;
		    String ls = System.getProperty("line.separator");
		    while( ( line = reader.readLine() ) != null ) {
		        stringBuilder.append( line );
		        stringBuilder.append( ls );
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return stringBuilder.toString();
	}
	/**
	 * Turn metadata into a map
	 * @param metadata
	 * @return
	 */
	public static Map turnMetadataIntoKeyValueMap(String metadata, String imgurl){
		Map metadatamap = new HashMap();
		//extract image name
		int index = imgurl.lastIndexOf("/");
		String imgname = imgurl.substring(index);
		metadatamap.put(ImageOntology.TITLE, imgname);
		//extract metadata information
		String[] lines = metadata.split("\n");
		int bandcount = 0;
		String ulpoint = null, lrpoint = null;
		for(int i=0;i<lines.length;i++){
			String line = lines[i];
			if(line.startsWith("Driver:")){
				//format
				String format = OntologyUtil.turnCommonStrToOntURIPart(line.substring("Driver:".length()).trim());
				metadatamap.put(ImageOntology.FORMAT, ImageOntology.topper_ontology + "#" +format);
			}else if(line.startsWith("Size is")){
				//size
				String size = line.substring("Size is".length());
				metadatamap.put(ImageOntology.SIZE, size);
			}else if(line.startsWith("Coordinate System is ")){
				//projection
				String projection = line.substring("Coordinate System is ".length()).trim();
				if(projection.equals("`'")){
					metadatamap.put(ImageOntology.PROJECTION, ImageOntology.NONPROJECTION);
				}else{
					metadatamap.put(ImageOntology.PROJECTION, ImageOntology.topper_ontology + "#" + OntologyUtil.turnCommonStrToOntURIPart(projection));
				}
			}else if(line.startsWith("Upper Left")){
				ulpoint = line.substring("Upper Left".length()).trim();
			}else if(line.startsWith("Lower Right")){
				lrpoint = line.substring("Lower Right".length());
			}else if(line.startsWith("Band")){
				bandcount++;
			}else if(line.startsWith("Description: ")){
				String desc = line.substring("Description: ".length());
				metadatamap.put(ImageOntology.DESCRIPTION, desc);
			}else if(line.startsWith("Volume: ")){
				String vol = line.substring("Volume: ".length());
				metadatamap.put(ImageOntology.VOLUME, vol);
			}else if(line.startsWith("Resolution: ")){
				String res = line.substring("Resolution: ".length());
				metadatamap.put(ImageOntology.RESOLUTION, res);
			}
		}
		//add spatial extent
		metadatamap.put(ImageOntology.SPATIAL_EXTENT, ulpoint.substring(1, ulpoint.length()-1)+", " + lrpoint.substring(1, lrpoint.length()-1));
		//add bandcount
		metadatamap.put(ImageOntology.BANDCOUNT, bandcount);
		
//		Driver: JPEG/JPEG JFIF
//		Files: /usr/local/apache-tomcat-7.0.39-9006/webapps/GeoprocessingWS/temp/warning-nudity-brazilian-butt-bombshell-andressa-urach.jpg
//		Size is 970, 1491
//		Coordinate System is `'
//		Image Structure Metadata:
//		  COMPRESSION=JPEG
//		  INTERLEAVE=PIXEL
//		  SOURCE_COLOR_SPACE=YCbCr
//		Corner Coordinates:
//		Upper Left  (    0.0,    0.0)
//		Lower Left  (    0.0, 1491.0)
//		Upper Right (  970.0,    0.0)
//		Lower Right (  970.0, 1491.0)
//		Center      (  485.0,  745.5)
//		Band 1 Block=970x1 Type=Byte, ColorInterp=Red
//		  Overviews: 485x746, 243x373, 122x187
//		  Image Structure Metadata:
//		    COMPRESSION=JPEG
//		Band 2 Block=970x1 Type=Byte, ColorInterp=Green
//		  Overviews: 485x746, 243x373, 122x187
//		  Image Structure Metadata:
//		    COMPRESSION=JPEG
//		Band 3 Block=970x1 Type=Byte, ColorInterp=Blue
//		  Overviews: 485x746, 243x373, 122x187
//		  Image Structure Metadata:
//		    COMPRESSION=JPEG
		return metadatamap;
	}
	/**
	 * Write string to file
	 * @param content
	 * @param filepath
	 */
	public static void writeString2File(String content, String filepath){
		PrintWriter out;
		try {
			out = new PrintWriter(filepath);
			out.println(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param parammap
	 * @return
	 */
	public static String turnParamMap2Str(Map parammap){
		Iterator it = parammap.keySet().iterator();
		StringBuffer parammapstr = new StringBuffer();
		int i=0;
		while(it.hasNext()){
			String k =  (String) it.next();
			String v = (String) parammap.get(k);
			if(i!=0){
				parammapstr.append("$PP$");
			}
			parammapstr.append(k).append("$KV$").append(v);
		}
		return parammapstr.toString();
	}
	
	public static Map getImageMetadataMap(String imgurl){
		String txturl = BaseTool.getImageMetadata(imgurl);
		String txt = BaseTool.getOnlineFileContent(txturl);
		
		Map kvs = BaseTool.turnMetadataIntoKeyValueMap(txt, imgurl);
		return kvs;
	}
	/**
	 * Main Entry
	 * @param args
	 */
	public static final void main(String[] args){
		BaseTool tool = new BaseTool();
		
	}
}
