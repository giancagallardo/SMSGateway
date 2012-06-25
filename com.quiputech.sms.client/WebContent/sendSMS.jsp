<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Send Single SMS</title>
</head>
<body>

<form method="post" action="SendSMSController">
<h3>Send single SMS</h3>
<table>
	<tr>
		<td>Token</td>
		<td>12345678</td>
	</tr>
	<tr>
		<td>From:</td>
		<td>
			<input name="from" type="text" />
		</td>
	</tr>
	<tr>
		<td>To:</td>
		<td>
			<input name="to" type="text" />
		</td>
	</tr>
	<tr>
		<td>Message:</td>
		<td>
			<textarea rows="4" cols="50" name="message">
			</textarea>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td><input type="submit" /></td>
	</tr>
</table>
</form>

</body>
</html>