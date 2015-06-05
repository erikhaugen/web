
<%@page import="com.livescribe.framework.lsconfiguration.Env"%>
<%@page import="com.livescribe.admin.config.AppProperties"%>
<%
    boolean debug = false;
    if (AppProperties.getIntance().getRunningEnvironment() != Env.PRODUCTION) {
        String debugstr = request.getParameter("debug");
        if (debugstr != null && debugstr.length() > 0)
            AppProperties.getIntance().setExtendedProperty("page.debuginfo", debugstr);
        else
            debugstr = AppProperties.getIntance().getExtendedProperty("page.debuginfo");
        
        if (debugstr != null && debugstr.length() > 0)
            debug = Boolean.parseBoolean(debugstr);
    }
    
    if (debug) {
%>
<div>
    <table border="1">
        <tr>
            <td colspan="2" align="center"><b>REQUEST INFORMATION</b>
            </td>
        </tr>
        <tr>
            <td>Handling Server</td>
            <td><%=AppProperties.getIntance().getHostname()%></td>
        </tr>
        <tr>
            <td>JSESSIONID</td>
            <td><%=request.getSession().getId()%></td>
        </tr>
    </table>
</div>
<%
    }
%>
