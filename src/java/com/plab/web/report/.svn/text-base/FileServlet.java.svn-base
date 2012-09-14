package com.plab.web.report;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



import java.util.*;
import java.util.logging.*;

/**
 * Servlet implementation class FileServlet
 */
public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem>  items = upload.parseRequest(request);
			for(FileItem item: items){
				if (item.isFormField()) {
					String name = item.getFieldName();
				    String value = item.getString();
			    } else {
			    	String fieldName = item.getFieldName();
			        String fileName = item.getName();
			        String contentType = item.getContentType();
			        boolean isInMemory = item.isInMemory();
			        long sizeInBytes = item.getSize();
			        InputStreamReader in = new InputStreamReader(item.getInputStream());
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        byte[] buffer = new byte[1024];
			        int read = 0;
			        while( (read = in.read()) != -1){
			        	out.write(buffer, 0, read);
			        }
			        byte[] data = out.toByteArray();
			        System.out.println("FILE LENGTH " + data.length);
			    }
			}
		} catch (FileUploadException e) {
			Logger.getLogger(ReportingServlet.class.getName()).log(Level.SEVERE, null, e);
		}
		
	}

	
	

}
