package com.example.vehicle.rental.util;

public class Contants {

    public enum RoleCheck {
        ADMIN("admin"),
        CUSTOMER("customer");

        private final String role;

        RoleCheck(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }
    }

    public static final String SECRET_KEY = "sk_test_51RwljzLyy5IXJa2t0JqKYqEnKwrMlwF5xEuk6jgg4807pkePucQBHJFezfhSPEnZoyIvdSLuS58dkbegdqW6Qb6G00jM0saq4P";



}
