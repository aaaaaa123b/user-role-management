package com.example.application.filter;

import com.example.application.constant.MappingConstants;
import com.example.application.enumeration.Role;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;


@WebFilter(urlPatterns = MappingConstants.USERS_MAPPING, filterName = "2")
public class RoleUpdateFilter implements Filter {

    private static final Set<String> SECURE_METHODS = Set.of("PUT", "POST", "DELETE");

    private Gson gson;

    @Override
    public void init(FilterConfig filterConfig) {
        this.gson = new Gson();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        final String path = httpRequest.getServletPath() + httpRequest.getPathInfo();
        final String method = httpRequest.getMethod().toUpperCase();

        final boolean securedPath = path.matches(MappingConstants.Roles.ROLES_PATTERN)
                || path.matches(MappingConstants.Roles.ROLE_PATTERN);

        if (securedPath && SECURE_METHODS.contains(method)) {
            HttpSession session = httpRequest.getSession(false);

            if (session == null) {
                sendResponse(httpResponse, "Unauthorized request", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Set<Role> roles = (Set<Role>) session.getAttribute("roles");

            if (roles == null || !roles.contains(Role.ADMIN)) {
                sendResponse(httpResponse, "Unauthorized request", HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void sendResponse(HttpServletResponse response, Object o, int code) throws IOException {
        String user1 = gson.toJson(o);
        response.getWriter().write(user1);
        response.setStatus(code);
        response.setContentType("application/json");
    }
}