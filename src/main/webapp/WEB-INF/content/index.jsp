<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en" ng-app="app">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Klausurenverkauf - FSMI</title>
</head>
<body>

	<h2>Products</h2>

	<div *ngFor="let document of documents">

		<h3>{{ product.id }}</h3>


	</div>



	<script src="/external.js"></script>
	<script src="/application.js"></script>
	<script src="/lib/angular/angular.min.js"></script>
	<script src="/lib/angular/angular-route.min.js"></script>
	<script src="/app.js"></script>
	<script src="/config.js"></script>
	<script src="/services/DataService.js"></script>
	<script src="/controllers/AppController.js"></script>
	<script src="/controllers/HomeController.js"></script>
	<script src="/controllers/ApacheProjectsController.js"></script>
</body>
</html>
