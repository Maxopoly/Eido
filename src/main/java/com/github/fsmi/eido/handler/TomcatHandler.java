package com.github.fsmi.eido.handler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.apache.struts2.factory.StrutsActionProxyFactory;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import com.github.fsmi.eido.EidoConfig;

public class TomcatHandler {

	private Tomcat tomcat;
	private final Logger logger;
	private final File webCache;
	private String topLevelPath;
	private Context topContext;

	public TomcatHandler(Logger logger, EidoConfig config) {
		this.logger = logger;
		webCache = new File("webcache/");
		topLevelPath = config.getTopLevelPath();
		tomcat = new Tomcat();
		tomcat.setBaseDir("temp");
		tomcat.setPort(config.getHttpPort());
		tomcat.setHostname(config.getHttpHost());
		//force default connector to create
		tomcat.getConnector();
		setupWebFiles();
		setup();
	}

	private boolean setupWebFiles() {
		try {
			FileUtils.copyURLToFile(getClass().getResource("/struts.xml"), new File("struts.xml"));
		} catch (IOException e) {
			logger.error("Failed to deploy struts.xml config", e);
		}
		try {
			FileUtils.deleteDirectory(webCache);
		} catch (IOException e) {
			logger.error("Failed to delete existing web cache", e);
			return false;
		}
		// kinda hacky way to recursively extract files from a jar
		// Thanks to
		// https://stackoverflow.com/questions/1429172/how-to-list-the-files-inside-a-jar-file/28057735#28057735
		String inJarWebFolder = "/webbase/";
		URI uri;
		try {
			uri = TomcatHandler.class.getResource(inJarWebFolder).toURI();
		} catch (URISyntaxException e1) {
			logger.error("Resource path was invalid, you are doing something very wrong", e1);
			return false;
		}
		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());) {
			Path myPath;
			if ("jar".equals(uri.getScheme())) {
				myPath = fileSystem.getPath(inJarWebFolder);
			} else {
				// makes this work when started from elsewhere, like an IDE
				myPath = Paths.get(uri);
			}
			if (!deployDirectory(myPath, webCache)) {
				return false;
			}
		} catch (IOException e) {
			logger.error("Failed to deploy web files", e);
			return false;
		}
		topContext = tomcat.addContext("", webCache.getAbsolutePath());
		  ErrorPage ep = new ErrorPage();
		  ep.setErrorCode(500);
		  ep.setLocation("/error.html");
		  topContext.addErrorPage(ep);

		  topContext.addMimeMapping("ext", "type");
		return true;
	}

	private boolean deployDirectory(Path folder, File targetFolder) {
		targetFolder.mkdirs();
		try (Stream<Path> walk = Files.walk(folder, 1);) {
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path item = it.next();
				if (item.equals(folder)) {
					continue;
				}
				if (item.toString().endsWith("/")) {
					if (!deployDirectory(item, new File(targetFolder, item.getFileName().toString()))) {
						return false;
					}
					continue;
				}
				Path fileName = item.getFileName();
				FileUtils.copyURLToFile(item.toUri().toURL(), new File(targetFolder, fileName.toString()));
			}
		} catch (IOException e) {
			logger.error(String.format("Failed to deploy web files for path %s", folder), e);
			return false;
		}
		return true;
	}

	private void setup() {
		topContext.setDisplayName("eido");
		
		
		HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                PrintWriter writer = resp.getWriter();
                 
                writer.println("<html><title>Welcome</title><body>");
                writer.println("<h1>Have a Great Day!</h1>");
                writer.println("</body></html>");
            }
        };         
        tomcat.addServlet(topContext, "servlet", servlet);      
        topContext.addServletMappingDecoded("/eido",  "servlet");
		
		StrutsActionProxyFactory j;
		// add a filter and filterMapping
		Class<StrutsPrepareAndExecuteFilter> filterClass = StrutsPrepareAndExecuteFilter.class;
		FilterDef myFilterDef = new FilterDef();
		myFilterDef.setFilterClass(filterClass.getName());
		myFilterDef.setFilterName(filterClass.getSimpleName());
		myFilterDef.addInitParameter("listings", "true");
		myFilterDef.addInitParameter("config", "struts-default.xml,struts-plugin.xml,struts.xml");
		topContext.addFilterDef(myFilterDef);

		FilterMap myFilterMap = new FilterMap();
		myFilterMap.setFilterName(filterClass.getSimpleName());
		myFilterMap.addURLPattern("/*");
		topContext.addFilterMap(myFilterMap);
		
		
	}

	public void startWebServer() {
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			logger.error("Failed to start up Tomcat server", e);
		}
		logger.info("Successfully started Tomcat server, listening for requests...");
		tomcat.getServer().await();
	}

}
