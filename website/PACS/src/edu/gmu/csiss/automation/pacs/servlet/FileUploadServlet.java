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

import java.util.*;
import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.disk.*; 

/**
 * The servlet for uploading a file
 * @author Ziheng Sun
 */
@WebServlet(name = "FileUploadServlet", urlPatterns = {"/FileUploadServlet"})
public class FileUploadServlet extends HttpServlet {
    private String relativePath;
    private String filePath;    
    private String tempPath;  
    private String prefix_url;
    private String callback;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config); 
        relativePath = config.getInitParameter("filepath");
        tempPath = config.getInitParameter("temppath");
                        
        javax.servlet.ServletContext context = getServletContext();
                        
        filePath = context.getRealPath(relativePath);
        tempPath = context.getRealPath(tempPath);
    }
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.setContentType("text/plain;charset=gbk");
        
        res.setContentType("text/html; charset=utf-8");
        PrintWriter pw = res.getWriter();
        try{
            //initialize the prefix url
            if(prefix_url==null){
//                String hostname = req.getServerName ();
//                int port = req.getServerPort ();
//                prefix_url = "http://"+hostname+":"+port+"/igfds/"+relativePath+"/";
            	//updated by Ziheng - on 8/27/2015
            	//This method should be used everywhere.
                int num = req.getRequestURI().indexOf("/PACS");
                String prefix = req.getRequestURI().substring(0, num+"/PACS".length()); //in case there is something before PACS
                prefix_url = req.getScheme() + "://" +
                        req.getServerName() + 
                        ("http".equals(req.getScheme()) && req.getServerPort() == 80 || "https".equals(req.getScheme()) && req.getServerPort() == 443 ? "" : ":" + req.getServerPort() ) +
                        prefix+"/"+relativePath+"/";
            }
            pw.println("<!DOCTYPE html>");
            pw.println("<html>");
            String head = "<head>" + 
        "<title>File Uploading Response</title>" + 
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" + 
        "<script type=\"text/javascript\" src=\"js/TaskGridTool.js\"></script>"+
        "</head>";
            pw.println(head);
            pw.println("<body>");
            
            DiskFileItemFactory diskFactory = new DiskFileItemFactory();
            // threshold  2M 
            //extend to 2M - updated by ziheng - 9/25/2014
            diskFactory.setSizeThreshold(2 * 1024);
            // repository 
            diskFactory.setRepository(new File(tempPath));
                                 
            ServletFileUpload upload = new ServletFileUpload(diskFactory);
            // 2M
            upload.setSizeMax(2 * 1024 * 1024);
            // HTTP
            List fileItems = upload.parseRequest(req);
            Iterator iter = fileItems.iterator();
            while(iter.hasNext())
            {
                FileItem item = (FileItem)iter.next();
                if(item.isFormField())
                {
                    processFormField(item, pw);
                }else{
                    processUploadFile(item, pw);
                }
            }// end while()
            //add some buttons for further process
            pw.println("<input type=\"button\" id=\"bt\" value=\"load\" onclick=\"load();\">");
            pw.println("<input type=\"button\" id=\"close\" value=\"close window\" onclick=\"window.close();\">");
            pw.println("</body>");
            pw.println("</html>");
        }catch(Exception e){
            e.printStackTrace();
            pw.println("ERR:"+e.getClass().getName()+":"+e.getLocalizedMessage());
        }finally{
            pw.flush();
            pw.close();
        }
    }
    /**
     * Information of the fields except file fields
     * @param item
     * @param pw
     * @throws Exception 
     */
    private void processFormField(FileItem item, PrintWriter pw)
        throws Exception
    {
        String name = item.getFieldName();
        String value = item.getString();        
        System.out.println(name + " : " + value + "\r\n");
        if(name.equals("script")){
            pw.println("<script>");
            pw.println(value);
            pw.println("</script>");
        }
//        pw.println(name + " : " + value + "\r\n");
    }
    
    private void processUploadFile(FileItem item, PrintWriter pw)
        throws Exception
    {
        String filename = item.getName();       
        System.out.println( filename);
        int index = filename.lastIndexOf("\\");
        filename = filename.substring(index + 1, filename.length());
                        
        long fileSize = item.getSize();
        
        if("".equals(filename) && fileSize == 0)
        {           
            throw new RuntimeException("You didn't upload a file.");
            //return;
        }
        
        File uploadFile = new File(filePath + "/" + filename);
        item.write(uploadFile);
        pw.println("<a id=\"filelink\" href=\""+prefix_url+filename+"\" >Link</a> to the uploaded file : "+filename);
        System.out.println( fileSize + "\r\n");
    } 
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
