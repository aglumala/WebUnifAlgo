package org.algorithm.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.algorirthm.util.FileInput;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet class handles an uploaded plan and a file containing WSDL URLs and passes
 * them to the type equivalence engine that runs the equivalence algorithms
 */
public class WSDLProcessor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	 
	 private static String separator=File.separator;
	 public static final String WSDL_DIRECTORY=System.getProperty("user.dir")+separator+"content"+separator;
	
	@Override
	public ServletContext getServletContext() {
		return super.getServletContext();
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check that we have a file upload request
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out=response.getWriter();
		try {
			out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ProcessorServlet</title>");  
            out.println("</head>");
            out.println("<body  bgcolor=silver>");
            out.println("<h1 align=\"center\">Type Equivalence Results</h1>");  
            out.println("<table width=\"200\" border=\"1\" align=\"center\">");
            out.println("<tr><th scope=\"col\">Plan Action</th>"+
            "<th scope=\"col\">Matched Action</th>"+
            "<th scope=\"col\">WSDL URL</th>"+         
            "<th scope=\"col\">% Matches</th></tr>");
			if(isMultipart){
				// Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);
	
				// Parse the request
				List<FileItem> items = upload.parseRequest(request);
				
				// Process the uploaded items
				Iterator<FileItem> iter = items.iterator();
				int count=0;
				while (iter.hasNext()) {
				    FileItem item = iter.next();
				    count++;
				    if (!item.isFormField()) {    
				        processUploadedFile(item, out, count);
				    }
				}		
			}
			String plan="/home/anamulindwa/content/plan.txt";
			String file="/home/anamulindwa/content/wsdl.txt";
			
			//String plan="/home/lumala/algoContent/plan.txt";
			//String file="/home/lumala/algoContent/wsdl.txt";
			
			FileInput.setWSDLName(file);
			for (Iterator<String> iterator = FileInput.readInput().iterator(); iterator.hasNext();) {
                String wsdlFile=iterator.next();
                TypeEquivalanceThread test=new TypeEquivalanceThread(plan, wsdlFile, out);
            	Thread t=new Thread(test);
            	t.start();
				t.join();//the main thread waits for this thread to finish before it can output the response	
            }
			out.println("</table>");
            out.println("<h1 align=\"center\">Processing successfully finished...</h1>");
            out.println("</body>");
            out.println("</html>");
		} catch (FileUploadException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processUploadedFile(FileItem item, PrintWriter out, int count) {
		
	    File uploadedFile =null;
	    if(count==1){
	    	uploadedFile = new File("/home/lumala/Desktop/algoContent/plan.txt");
	    	
	    }else if(count==2){
	    	uploadedFile = new File("/home/lumala/Desktop/algoContent/wsdl.txt");
	    	
	    }
	    try {  	
			item.write(uploadedFile);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
