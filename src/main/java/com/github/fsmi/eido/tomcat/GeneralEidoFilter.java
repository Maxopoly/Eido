package com.github.fsmi.eido.tomcat;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class GeneralEidoFilter implements Filter {
 
    @Override
    public void init(FilterConfig filterConfig) {
        // ...
    }
 
    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) 
      throws IOException, ServletException {
 
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("myHeader", "myHeaderValue");
        chain.doFilter(request, httpResponse);
    }
 
    @Override
    public void destroy() {
        // ...
    }
}
