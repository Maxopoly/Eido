package com.github.fsmi.eido.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import com.github.fsmi.eido.EidoConfig;
import com.github.fsmi.eido.tomcat.GeneralEidoFilter;
import com.github.fsmi.eido.tomcat.DocumentSelectionServlet;

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
		setupWebFiles();
		setup();
	}

	private boolean setupWebFiles() {
		try {
			FileUtils.deleteDirectory(webCache);
		} catch (IOException e) {
			logger.error("Failed to delete existing web cache", e);
			return false;
		}
		// kinda hacky way to recursively extract files from a jar
		// Thanks to
		// https://stackoverflow.com/questions/1429172/how-to-list-the-files-inside-a-jar-file/28057735#28057735
		String webFolder = "/webbase/";
		URI uri;
		try {
			uri = TomcatHandler.class.getResource(webFolder).toURI();
		} catch (URISyntaxException e1) {
			logger.error("Resource path was invalid, you are doing something very wrong");
			return false;
		}
		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());){
			Path myPath;
			if ("jar".equals(uri.getScheme())) {
				myPath = fileSystem.getPath(webFolder);
			} else {
				// makes this work when started from elsewhere, like an IDE
				myPath = Paths.get(uri);
			}
			try (Stream<Path> walk = Files.walk(myPath, 1);) {
				for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
					Path item = it.next();
					if (item.toString().endsWith("/")) {
						// directory
						continue;
					}
					String targetPath = item.toString().substring(webFolder.length());
					FileUtils.copyURLToFile(item.toUri().toURL(), new File(webCache, targetPath));
				}
			}
		} catch (IOException e) {
			logger.error("Failed to deploy web files", e);
			return false;
		}
		topContext = tomcat.addContext(topLevelPath, webCache.getAbsolutePath());
		return true;
	}

	private void setup() {
		// add a servlet
		Class<DocumentSelectionServlet> servletClass = DocumentSelectionServlet.class;
		Tomcat.addServlet(topContext, servletClass.getSimpleName(), servletClass.getName());
		topContext.addServletMappingDecoded(topLevelPath, servletClass.getSimpleName());

		// add a filter and filterMapping
		Class<GeneralEidoFilter> filterClass = GeneralEidoFilter.class;
		FilterDef myFilterDef = new FilterDef();
		myFilterDef.setFilterClass(filterClass.getName());
		myFilterDef.setFilterName(filterClass.getSimpleName());
		topContext.addFilterDef(myFilterDef);

		FilterMap myFilterMap = new FilterMap();
		myFilterMap.setFilterName(filterClass.getSimpleName());
		myFilterMap.addURLPattern(topLevelPath + "*");
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
