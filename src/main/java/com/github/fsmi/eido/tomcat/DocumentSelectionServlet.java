package com.github.fsmi.eido.tomcat;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocumentSelectionServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().write("test");
		resp.getWriter().flush();
		resp.getWriter().close();
	}
}