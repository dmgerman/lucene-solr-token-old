<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@ taglib uri="/WEB-INF/lucene-taglib.tld" prefix="JSP"%>
<%@ include file="header.jsp"%>
<%@ page import="java.util.*"%>

<% 
	String startRow = "0";
	String maxRows = "10";
	String query = request.getParameter("query");
	try{
		startRow = request.getParameter("startRow");
		maxRows = request.getParameter("maxRows");
	}
	catch(Exception e){
	}
%>
<table border=3>


	<JSP:Search id="rs" collection="E:/opt/lucene/index" criteria="<%= query %>" startRow="<%= startRow %>" maxRows="<%= maxRows %>">
	<%
		Set allFields = rs.getFields();
		int fieldSize = allFields.size();
		Iterator fieldIter = allFields.iterator();
		while(fieldIter.hasNext()){
			String nextField = (String) fieldIter.next();
			if(!nextField.equalsIgnoreCase("summary")){
			%>
				<tr><td><b><%= nextField %></b></td><td><%= rs.getField(nextField) %></td></tr>
			<%
			}else{
			%>
				<tr><td colspan="2"><b><%= nextField %></b></td></tr>
				<tr><td colspan="2"><%= rs.getField(nextField) %></td></tr>
			<%
			}
		}
	%>
	</JSP:Search>
<%
	if(new Integer(rs.hitCount).intValue() <= 0){
%>
	<tr>
		<td colspan=2>No results were found</td>
	</tr>
<%
	}
%>
	<tr>
		<td colspan=2><%= rs.hitCount %></td>
	</tr>
	</table>
	

<%@include file="footer.jsp"%>
