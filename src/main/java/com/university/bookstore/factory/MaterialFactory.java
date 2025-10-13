package com.university.bookstore.factory;

import java.util.Map;

import com.university.bookstore.model.AudioBook;
import com.university.bookstore.model.EBook;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;
import com.university.bookstore.model.PrintedBook;
import com.university.bookstore.model.VideoMaterial;

/**
 * Factory class for creating Material instances.
 * Demonstrates the Factory pattern by providing a centralized
 * way to create different types of materials without exposing
 * their constructors directly.
 * 
 * <p>This factory supports creating all material types including
 * the new EBook class, with proper validation and error handling.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class MaterialFactory {
    
    /**
     * Creates a Material instance based on the specified type and properties.
     * 
     * @param type the material type
     * @param properties map of properties required for the material
     * @return the created Material instance
     * @throws IllegalArgumentException if type is null or properties are invalid
     * @throws NullPointerException if required properties are missing
     */
    public static Material createMaterial(String type, Map<String, Object> properties) {
        if (type == null) {
            throw new NullPointerException("Material type cannot be null");
        }
        if (properties == null) {
            throw new NullPointerException("Properties cannot be null");
        }
        
        String normalizedType = type.trim().toUpperCase();
        
        switch (normalizedType) {
            case "BOOK":
            case "PRINTED_BOOK":
                return createPrintedBook(properties);
            case "MAGAZINE":
                return createMagazine(properties);
            case "AUDIO_BOOK":
            case "AUDIOBOOK":
                return createAudioBook(properties);
            case "VIDEO":
            case "VIDEO_MATERIAL":
                return createVideoMaterial(properties);
            case "EBOOK":
            case "E_BOOK":
                return createEBook(properties);
            default:
                throw new IllegalArgumentException("Unsupported material type: " + type);
        }
    }
    
    /**
     * Creates a PrintedBook instance.
     */
    private static PrintedBook createPrintedBook(Map<String, Object> properties) {
        String isbn = getRequiredString(properties, "isbn");
        String title = getRequiredString(properties, "title");
        String author = getRequiredString(properties, "author");
        double price = getRequiredDouble(properties, "price");
        int year = getRequiredInteger(properties, "year");
        int pages = getRequiredInteger(properties, "pages");
        String publisher = getOptionalString(properties, "publisher", "Unknown");
        boolean hardcover = getOptionalBoolean(properties, "hardcover", false);
        
        return new PrintedBook(isbn, title, author, price, year, pages, publisher, hardcover);
    }
    
    /**
     * Creates a Magazine instance.
     */
    private static Magazine createMagazine(Map<String, Object> properties) {
        String issn = getRequiredString(properties, "issn");
        String title = getRequiredString(properties, "title");
        String publisher = getRequiredString(properties, "publisher");
        double price = getRequiredDouble(properties, "price");
        int year = getRequiredInteger(properties, "year");
        int issue = getRequiredInteger(properties, "issue");
        String frequency = getOptionalString(properties, "frequency", "Monthly");
        String category = getOptionalString(properties, "category", "General");
        
        return new Magazine(issn, title, publisher, price, year, issue, frequency, category);
    }
    
    /**
     * Creates an AudioBook instance.
     * Note: This is a simplified version for demonstration purposes.
     */
    private static AudioBook createAudioBook(Map<String, Object> properties) {
        String isbn = getRequiredString(properties, "isbn");
        String title = getRequiredString(properties, "title");
        String author = getRequiredString(properties, "author");
        String narrator = getRequiredString(properties, "narrator");
        double price = getRequiredDouble(properties, "price");
        int year = getRequiredInteger(properties, "year");
        int duration = getRequiredInteger(properties, "duration");
        String format = getOptionalString(properties, "format", "MP3");
        double fileSize = getOptionalDouble(properties, "fileSize", 100.0);
        Media.MediaQuality quality = getRequiredMediaQuality(properties, "quality");
        String language = getOptionalString(properties, "language", "English");
        boolean unabridged = getOptionalBoolean(properties, "unabridged", true);
        
        return new AudioBook(isbn, title, author, narrator, price, year, duration, 
                           format, fileSize, quality, language, unabridged);
    }
    
    /**
     * Creates a VideoMaterial instance.
     * Note: This is a simplified version for demonstration purposes.
     */
    private static VideoMaterial createVideoMaterial(Map<String, Object> properties) {
        String id = getRequiredString(properties, "id");
        String title = getRequiredString(properties, "title");
        String director = getRequiredString(properties, "director");
        double price = getRequiredDouble(properties, "price");
        int year = getRequiredInteger(properties, "year");
        int duration = getRequiredInteger(properties, "duration");
        String format = getOptionalString(properties, "format", "MP4");
        double fileSize = getOptionalDouble(properties, "fileSize", 1000.0);
        Media.MediaQuality quality = getRequiredMediaQuality(properties, "quality");
        VideoMaterial.VideoType videoType = getRequiredVideoType(properties, "videoType");
        String rating = getOptionalString(properties, "rating", "PG");
        java.util.List<String> cast = getOptionalStringList(properties, "cast");
        boolean subtitlesAvailable = getOptionalBoolean(properties, "subtitlesAvailable", false);
        String aspectRatio = getOptionalString(properties, "aspectRatio", "16:9");
        
        return new VideoMaterial(id, title, director, price, year, duration, format, 
                               fileSize, quality, videoType, rating, cast, 
                               subtitlesAvailable, aspectRatio);
    }
    
    /**
     * Creates an EBook instance.
     */
    private static EBook createEBook(Map<String, Object> properties) {
        String id = getRequiredString(properties, "id");
        String title = getRequiredString(properties, "title");
        String author = getRequiredString(properties, "author");
        double price = getRequiredDouble(properties, "price");
        int year = getRequiredInteger(properties, "year");
        String fileFormat = getRequiredString(properties, "fileFormat");
        double fileSize = getRequiredDouble(properties, "fileSize");
        boolean drmEnabled = getRequiredBoolean(properties, "drmEnabled");
        int wordCount = getRequiredInteger(properties, "wordCount");
        Media.MediaQuality quality = getRequiredMediaQuality(properties, "quality");
        
        return new EBook(id, title, author, price, year, fileFormat, fileSize, 
                        drmEnabled, wordCount, quality);
    }
    
    // Helper methods for extracting and validating properties
    
    private static String getRequiredString(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Property '" + key + "' must be a String, got: " + value.getClass().getSimpleName());
        }
        String str = (String) value;
        if (str.trim().isEmpty()) {
            throw new IllegalArgumentException("Property '" + key + "' cannot be empty");
        }
        return str.trim();
    }
    
    private static double getRequiredDouble(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid number, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a number, got: " + value.getClass().getSimpleName());
    }
    
    private static int getRequiredInteger(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid integer, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be an integer, got: " + value.getClass().getSimpleName());
    }
    
    private static boolean getRequiredBoolean(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            String str = ((String) value).trim().toLowerCase();
            if ("true".equals(str) || "1".equals(str) || "yes".equals(str)) {
                return true;
            }
            if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
                return false;
            }
            throw new IllegalArgumentException("Property '" + key + "' must be a valid boolean, got: " + value);
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a boolean, got: " + value.getClass().getSimpleName());
    }
    
    private static Media.MediaQuality getRequiredMediaQuality(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof Media.MediaQuality) {
            return (Media.MediaQuality) value;
        }
        if (value instanceof String) {
            try {
                return Media.MediaQuality.valueOf(((String) value).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid MediaQuality, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a MediaQuality, got: " + value.getClass().getSimpleName());
    }
    
    private static String getOptionalString(Map<String, Object> properties, String key, String defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Property '" + key + "' must be a String, got: " + value.getClass().getSimpleName());
        }
        String str = (String) value;
        return str.trim().isEmpty() ? defaultValue : str.trim();
    }
    
    private static boolean getOptionalBoolean(Map<String, Object> properties, String key, boolean defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            String str = ((String) value).trim().toLowerCase();
            if ("true".equals(str) || "1".equals(str) || "yes".equals(str)) {
                return true;
            }
            if ("false".equals(str) || "0".equals(str) || "no".equals(str)) {
                return false;
            }
            throw new IllegalArgumentException("Property '" + key + "' must be a valid boolean, got: " + value);
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a boolean, got: " + value.getClass().getSimpleName());
    }
    
    private static double getOptionalDouble(Map<String, Object> properties, String key, double defaultValue) {
        Object value = properties.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid number, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a number, got: " + value.getClass().getSimpleName());
    }
    
    private static VideoMaterial.VideoType getRequiredVideoType(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            throw new NullPointerException("Required property '" + key + "' is missing");
        }
        if (value instanceof VideoMaterial.VideoType) {
            return (VideoMaterial.VideoType) value;
        }
        if (value instanceof String) {
            try {
                return VideoMaterial.VideoType.valueOf(((String) value).trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Property '" + key + "' must be a valid VideoType, got: " + value);
            }
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a VideoType, got: " + value.getClass().getSimpleName());
    }
    
    @SuppressWarnings("unchecked")
    private static java.util.List<String> getOptionalStringList(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value == null) {
            return java.util.Arrays.asList();
        }
        if (value instanceof java.util.List) {
            return (java.util.List<String>) value;
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.trim().isEmpty()) {
                return java.util.Arrays.asList();
            }
            return java.util.Arrays.asList(str.split(","));
        }
        throw new IllegalArgumentException("Property '" + key + "' must be a List<String> or String, got: " + value.getClass().getSimpleName());
    }
}
