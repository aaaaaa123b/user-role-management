package com.example.application.filter;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebFilter(urlPatterns = "/*", filterName = "0")
public class LoggingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(LoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        Iterator<String> iterator = req.getHeaderNames().asIterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            logger.info(str + " : " + req.getHeader(str));
        }

        String body = req.getReader().lines().collect(Collectors.joining());
        logger.info(body);
        request.setAttribute("body", body);
        chain.doFilter(request, response);

    }

}
