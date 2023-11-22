package com.example.application.constant;

import java.util.regex.Pattern;

public class MappingConstants {

    private MappingConstants() {
    }

    public static final String USERS_MAPPING = "/users/*";

    public static class Users {

        private Users() {
        }

        public static final String USER_PATTERN = "/users/(\\d+)";
        public static final Pattern USER_COMPILED_PATTERN = Pattern.compile(USER_PATTERN);

        public static final String USERS_PATTERN = "/users";
        public static final Pattern USERS_COMPILED_PATTERN = Pattern.compile(USERS_PATTERN);
    }

    public static class Roles {

        private Roles() {
        }

        public static final String ROLES_PATTERN = "/users/(\\d+)/roles";
        public static final String ROLE_PATTERN = "/users/(\\d+)/roles/([A-Z]+)";

        public static final Pattern ROLES_COMPILED_PATTERN = Pattern.compile(ROLES_PATTERN);
        public static final Pattern ROLE_COMPILED_PATTERN = Pattern.compile(ROLE_PATTERN);
    }
}
