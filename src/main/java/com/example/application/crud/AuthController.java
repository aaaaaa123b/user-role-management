package com.example.application.crud;

import com.example.application.model.User;
import com.example.application.service.UserService;
import com.example.application.service.impl.UserServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@WebServlet("/auth")
public class AuthController extends HttpServlet {

    private UserService userService;
    private Gson gson;

    @Override
    public void init() {
        this.userService = UserServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String authorizationHeader = req.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            Base64.Decoder decoder = Base64.getDecoder();
            String credentials = new String(decoder.decode(base64Credentials), StandardCharsets.UTF_8);
            String[] credentialParts = credentials.split(":", 2);

            String username = credentialParts[0];
            String password = credentialParts[1];

            Optional<User> optionalUser = userService.getUserByUsername(username);

            if (optionalUser.isEmpty()) {
                sendResponse(resp, "Invalid login or password", HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            User user = optionalUser.get();

            if (user.getPassword().equals(password)) {
                req.getSession().setAttribute("roles", user.getRoles());
                req.getSession().setAttribute("login", user.getLogin());
                sendResponse(resp, "Authorized successfully", HttpServletResponse.SC_OK);
            } else {
                sendResponse(resp, "Invalid login or password", HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private void sendResponse(HttpServletResponse response, Object o, int code) throws IOException {
        String user1 = gson.toJson(o);
        response.getWriter().write(user1);
        response.setStatus(code);
        response.setContentType("application/json");
    }
}
