package com.example.application.filter;

import com.example.application.constant.MappingConstants;
import com.example.application.enumeration.Role;
import com.example.application.model.User;
import com.example.application.service.UserService;
import com.example.application.service.impl.UserServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@WebFilter(urlPatterns = {MappingConstants.USERS_MAPPING}, filterName = "1")
public class SessionSynchronizationFilter implements Filter {

    private static final Logger logger = Logger.getLogger(SessionSynchronizationFilter.class.getName());

    public UserService userService;
    private Gson gson;

    @Override
    public void init(FilterConfig filterConfig) {
        this.userService = UserServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        final HttpSession session = req.getSession(false);

        if (session == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Set<Role> cachedRoles = (Set<Role>) session.getAttribute("roles");
        String login = (String) session.getAttribute("login");

        Optional<User> optionalUser = userService.getUserByUsername(login);

        if (optionalUser.isEmpty()) {
            logger.info("User has been removed during active session. Session has been invalidated.");
            session.invalidate();
            sendResponse(res, "User does not exist", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Set<Role> persistedRoles = optionalUser.get().getRoles();

        if (cachedRoles.size() == persistedRoles.size() && persistedRoles.containsAll(cachedRoles)) {
            logger.warning("Cached roles are in actual state (like persisted roles)");
            filterChain.doFilter(request, response);
            return;
        }

        final String logMessage = "Cached roles are in invalid state. Cached roles are %s while actual roles are %s".formatted(
                cachedRoles,
                persistedRoles
        );
        logger.warning(logMessage);

        cachedRoles.clear();
        cachedRoles.addAll(persistedRoles);

        filterChain.doFilter(request, response);
    }

    private void sendResponse(HttpServletResponse response, Object o, int code) throws IOException {
        String user1 = gson.toJson(o);
        response.getWriter().write(user1);
        response.setStatus(code);
        response.setContentType("application/json");
    }
}