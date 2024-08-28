package com.example.TenantAdmin.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class TokenUtils {
    private static final SimpleDateFormat EXPIRATION_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
    public static UUID generateTokenWithExpiration() {
        // Generate a UUID token
        UUID token = UUID.randomUUID();
        String tokenString = token.toString();

        // Calculate expiration date (1 hour from now)
        Date expirationDate = new Date(System.currentTimeMillis() + 3600 * 1000);

        // Format expiration date into a fixed-length string (e.g., 12 characters)
        // Example: 202408141652
        String expirationDateString = EXPIRATION_DATE_FORMAT.format(expirationDate);

        // Adjust token length to fit the combined format within 36 characters
        String shortenedToken = tokenString.substring(0, 36 - expirationDateString.length() - 1); // -1 for hyphen

        // Combine UUID and expiration date into a single string
        String combinedString = shortenedToken + "-" + expirationDateString;

        // Convert the combined string to UUID (if it fits the UUID format)
        // Note: This may not create a valid UUID format. If you need a UUID, you'll need to handle the format differently.
        UUID tokenWithExpiration;
        try {
            tokenWithExpiration = UUID.fromString(combinedString + "00000000-0000-0000-0000-000000000000".substring(combinedString.length()));
        } catch (IllegalArgumentException e) {
            // Handle exception if combinedString does not fit UUID format
            throw new IllegalArgumentException("Combined string does not fit UUID format", e);
        }

        return tokenWithExpiration;
    }


    public static boolean isTokenExpired(String tokenWithExpiration) {
        // Ensure the token format is valid and has at least 12 characters for the expiration date
        if (tokenWithExpiration.length() < 12) {
            return true; // Invalid token format or too short
        }

        try {
            // Extract the expiration date string from the last 12 characters of the token
            String expirationDateString = tokenWithExpiration.substring(tokenWithExpiration.length() - 12);
            System.out.println("expirationDateString: " + expirationDateString);

            // Parse the expiration date string
            Date expirationDate = EXPIRATION_DATE_FORMAT.parse(expirationDateString);

            // Print for debugging
            System.out.println("Parsed Expiration Date: " + expirationDate);
            System.out.println("Current Date: " + new Date());

            // Check if the current time is past the expiration date
            return new Date().after(expirationDate);
        } catch (ParseException e) {
            // Handle invalid date format
            return true; // Treat as expired if there's an issue with parsing
        }
    }

}