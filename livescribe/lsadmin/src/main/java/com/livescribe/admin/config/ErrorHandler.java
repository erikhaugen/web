package com.livescribe.admin.config;

import java.io.*;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;

public class ErrorHandler extends HttpServlet {

    private static final Logger log = Logger.getLogger(ErrorHandler.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Method to handle GET method request.
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Analyze the servlet exception
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        if (servletName == null) {
            servletName = "Unknown";
        }
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        // Set response content type
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        String title = "Error/Exception Information";
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
        out.println(docType + "<html>\n" + "<head><title>" + title + "</title></head>\n" + "<body bgcolor=\"#f0f0f0\">\n");

        out.println("<h2>System has error!!!</h2>");
        out.println("Servlet Name : " + servletName + "<br/>");
        out.println("The request URI: " + requestUri + "<br/>");
        out.println("The status code : " + statusCode + "<br/>");
        out.println("The error message: " + errorMessage + "<br/>");
        out.println("<br/>");
        log.error("----------------- Error information -----------------");
        log.error("Servlet Name : " + servletName);
        log.error("The request URI: " + requestUri);
        log.error("The status code : " + statusCode);
        log.error("The error message: " + errorMessage);
        
        String stackTrace = null;
        if (throwable != null) {
            out.println("Exception Type : " + throwable.getClass().getName() + "<br/>");
            out.println("The exception message: " + throwable.getMessage() + "<br/>");
            log.error(throwable.getMessage(), throwable);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
    			PrintStream pw = new PrintStream(os);
    			throwable.printStackTrace(pw);
    			stackTrace = os.toString();
    			stackTrace = stackTrace.replaceAll("\n", "<br/>&emsp;");
        } else {
            out.println("Exception Type : Unknown<br/>");
            out.println("The exception message: Unknown<br/>");
        }
        out.println("<br/>");
        out.println("Please find more error information in log file.<br/>");
        out.println("Error happened at " + new Date().toString() + ".<br/>");
        out.println("<br/>");
        out.println("Please inform the error to webteam@livescribe.com.");
        
        if (stackTrace != null) {
			out.println("<hr/>");
	        out.println("<h3>Stace trace</h3>");
	        out.println(stackTrace);
        }
        
        out.println("</body>");
        out.println("</html>");
    }

    // Method to handle POST method request.
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
