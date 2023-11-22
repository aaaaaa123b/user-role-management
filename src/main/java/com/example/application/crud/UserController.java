package com.example.application.crud;

import com.example.application.constant.MappingConstants;
import com.example.application.dto.CreateUserDTO;
import com.example.application.dto.UpdateRolesDTO;
import com.example.application.dto.UpdateUserDTO;
import com.example.application.enumeration.Role;
import com.example.application.model.*;
import com.example.application.service.UserService;
import com.example.application.service.impl.UserServiceImpl;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

@WebServlet(urlPatterns = MappingConstants.USERS_MAPPING)
public class UserController extends HttpServlet {

    private UserService userService;
    private Gson gson;

    @Override
    public void init() {
        this.userService = UserServiceImpl.getInstance();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) pathInfo = "";

        final String path = request.getServletPath() + pathInfo;

        if (path.matches(MappingConstants.Users.USER_PATTERN)) {
            Matcher matcher = MappingConstants.Users.USER_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                int userID = Integer.parseInt(matcher.group(1));
                User user = userService.findById(userID);

                if (user == null) {
                    sendResponse(response, "User not found", HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                sendResponse(response, user, HttpServletResponse.SC_OK);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        if (path.matches(MappingConstants.Users.USERS_PATTERN)) {
            List<User> users = userService.findAll();
            sendResponse(response, users, HttpServletResponse.SC_OK);
            return;
        }

        if (path.matches(MappingConstants.Roles.ROLES_PATTERN)) {
            Matcher matcher = MappingConstants.Roles.ROLES_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                int userID = Integer.parseInt(matcher.group(1));
                User user = userService.findById(userID);
                Set<Role> roles = user.getRoles();
                sendResponse(response, roles, HttpServletResponse.SC_OK);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String path = request.getServletPath() + request.getPathInfo();

        if (path.matches(MappingConstants.Users.USER_PATTERN)) {
            Matcher matcher = MappingConstants.Users.USER_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                int userID = Integer.parseInt(matcher.group(1));

                UpdateUserDTO updateUser = getFromRequest(request, UpdateUserDTO.class);
                User user = userService.findById(userID);

                user.setLogin(updateUser.getLogin());
                user.setPassword(updateUser.getPassword());

                sendResponse(response, user, HttpServletResponse.SC_OK);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        if (path.matches(MappingConstants.Roles.ROLES_PATTERN)) {
            Matcher matcher = MappingConstants.Roles.ROLES_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                int userID = Integer.parseInt(matcher.group(1));

                UpdateRolesDTO updatedRoles = getFromRequest(request, UpdateRolesDTO.class);
                User user = userService.findById(userID);

                user.setRoles(updatedRoles.getRoles());

                sendResponse(response, user, HttpServletResponse.SC_OK);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String path = request.getServletPath() + request.getPathInfo();

        if (path.matches(MappingConstants.Users.USER_PATTERN)) {
            Matcher matcher = MappingConstants.Users.USER_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                int userID = Integer.parseInt(matcher.group(1));
                userService.deleteUser(userID);
                sendResponse(response);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        if (path.matches(MappingConstants.Roles.ROLE_PATTERN)) {
            Matcher matcher = MappingConstants.Roles.ROLE_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                int userID = Integer.parseInt(matcher.group(1));
                Role role = Role.valueOf(matcher.group(2));

                User user = userService.findById(userID);

                if (user.getRoles().size() < 2) {
                    sendResponse(response, "Cannot remove the last role", HttpServletResponse.SC_CONFLICT);
                    return;
                }

                user.getRoles().remove(role);
                user = userService.updateUser(userID, user);

                sendResponse(response, user.getRoles(), HttpServletResponse.SC_OK);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final String path = request.getServletPath();

        if (path.matches(MappingConstants.Users.USERS_PATTERN)) {
            Matcher matcher = MappingConstants.Users.USERS_COMPILED_PATTERN.matcher(path);

            if (matcher.find()) {
                CreateUserDTO dto = getFromRequest(request, CreateUserDTO.class);

                User createdUser = new User();

                HashSet<Role> roles = new HashSet<>();
                roles.add(Role.USER);
                createdUser.setRoles(roles);

                createdUser.setLogin(dto.getLogin());
                createdUser.setPassword(dto.getPassword());

                userService.addUser(createdUser);

                sendResponse(response, createdUser, HttpServletResponse.SC_CREATED);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
    }

    private void sendResponse(HttpServletResponse response, Object o, int code) throws IOException {
        String user1 = gson.toJson(o);
        response.getWriter().write(user1);
        response.setStatus(code);
        response.setContentType("application/json");
    }

    private void sendResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
    }

    private <T> T getFromRequest(HttpServletRequest request, Class<T> clazz) {
        String res = request.getAttribute("body").toString();
        return gson.fromJson(res, clazz);
    }
}
