package com.se.aiconomy.server.common.utils;

/**
 * Utility class for file-related operations.
 */
public class FileUtils {
    /**
     * Gets the file extension from a file path.
     *
     * @param filePath the file path
     * @return the file extension in lower case, or an empty string if none exists
     */
    public static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";  // No extension found
        }
        return filePath.substring(dotIndex + 1).toLowerCase();  // Return extension in lower case
    }
}