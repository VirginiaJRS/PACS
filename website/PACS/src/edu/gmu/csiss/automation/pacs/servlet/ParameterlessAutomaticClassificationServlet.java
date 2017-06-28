/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
#******************************************************************************
*/
package edu.gmu.csiss.automation.pacs.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gmu.csiss.automation.pacs.reasoner.ImageParameterOntologyReasoner;
import edu.gmu.csiss.automation.pacs.utils.BaseTool;
import edu.gmu.csiss.automation.pacs.utils.SysDir;

/**
 * Servlet implementation class ParameterlessAutomaticClassificationServlet
 * @author Ziheng Sun
 * @date 2019.8.28
 */
public class ParameterlessAutomaticClassificationServlet extends HttpServlet {
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
     * Do request
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
              String poststr = parseStringFromInputStream(request.getInputStream());
              System.out.println(poststr);
              if(poststr==null){
                  throw new RuntimeException("Incorrect Inputs.");
              }
              String imgurl = poststr.trim();
              //get the metadata of the image
              Map kvmap = BaseTool.getImageMetadataMap(imgurl);
              //get parameter configuration for the image
              Map parammap = ImageParameterOntologyReasoner.reason(kvmap);
              String parammapstr = BaseTool.turnParamMap2Str(parammap);
              String req = "$PARAMETERLESSAUTOCLASS$"+imgurl+"$IP$" + parammapstr;
              /**
               * The internal processing logic is described and implemented by a BPEL jar 
               * (http://www3.csiss.gmu.edu/igfds/workflow/pacs.jar).
               * The jar can be unzipped to two files (.bpel, .wsdl)
               * The BPEL file describes the processing workflow and the WSDL file describes
               * the input and output of the workflow.
               */
              String resp = BaseTool.POST(req, SysDir.executionservletaddress);
              if(resp==null||resp.trim().startsWith("Sorry")){
            	  throw new RuntimeException("Fail to execute the parameterless automatic classification workflow due to :" + resp);
              }
              out.println(resp.trim());
        }catch(Exception e){
	          e.printStackTrace();
	          out.println("ERR:"+e.getClass().getName()+":"+e.getLocalizedMessage());
        } finally {
              out.close();
        }
		
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

}
