package com.university.bookstore.model;

import java.time.Year;
import java.util.Objects;

/**
 * Abstract base class representing any material in the store inventory.
 * Demonstrates abstraction and inheritance in OOP design.
 * 
 * <p>This class provides common properties and behavior for all materials
 * including books, magazines, audio, and video content.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public abstract class Material implements Comparable<Material> {
    
    protected static final int MIN_YEAR = 1450;
    
    protected final String id;
    protected final String title;
    protected final double price;
    protected final int year;
    protected final MaterialType type;
    
    /**
     * Enumeration of material types for polymorphic behavior.
     */
    public enum MaterialType {
        BOOK("Book"),
        MAGAZINE("Magazine"),
        AUDIO_BOOK("Audio Book"),
        VIDEO("Video"),
        MUSIC_ALBUM("Music Album"),
        PODCAST("Podcast"),
        DOCUMENTARY("Documentary"),
        E_BOOK("E-Book");
        
        private final String displayName;
        
        MaterialType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Protected constructor for subclasses.
     * 
     * @param id unique identifier for the material
     * @param title the material title
     * @param price the price in dollars
     * @param year the publication/release year
     * @param type the type of material
     */
    protected Material(String id, String title, double price, int year, MaterialType type) {
        this.id = validateId(id);
        this.title = validateStringField(title, "Title");
        this.price = validatePrice(price);
        this.year = validateYear(year);
        this.type = Objects.requireNonNull(type, "Material type cannot be null");
    }
    
    /**
     * Abstract method to get the creator/author/artist of the material.
     * Implementation varies by material type.
     * 
     * @return the creator's name
     */
    public abstract String getCreator();
    
    /**
     * Abstract method to get a formatted display string.
     * Each material type should provide its own formatting.
     * 
     * @return formatted display string
     */
    public abstract String getDisplayInfo();
    
    /**
     * Template method for calculating discounted price.
     * Subclasses can override getDiscountRate() to customize.
     * 
     * @return discounted price
     */
    public final double getDiscountedPrice() {
        return price * (1.0 - getDiscountRate());
    }
    
    /**
     * Hook method for discount rate. Default is no discount.
     * Subclasses can override to provide type-specific discounts.
     * 
     * @return discount rate between 0.0 and 1.0
     */
    public double getDiscountRate() {
        return 0.0;
    }
    
    protected String validateId(String id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        if (id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }
        return id.trim();
    }
    
    protected String validateStringField(String value, String fieldName) {
        if (value == null) {
            throw new NullPointerException(fieldName + " cannot be null");
        }
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }
    
    protected double validatePrice(double price) {
        if (price < 0.0) {
            throw new IllegalArgumentException(
                "Price cannot be negative. Provided: " + price);
        }
        if (Double.isNaN(price) || Double.isInfinite(price)) {
            throw new IllegalArgumentException(
                "Price must be a valid number. Provided: " + price);
        }
        return price;
    }
    
    protected int validateYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < MIN_YEAR || year > currentYear + 1) {
            throw new IllegalArgumentException(
                String.format("Year must be between %d and %d. Provided: %d",
                    MIN_YEAR, currentYear + 1, year));
        }
        return year;
    }
    
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getYear() {
        return year;
    }
    
    public MaterialType getType() {
        return type;
    }
    
    /**
     * Compares materials by title for natural ordering.
     */
    @Override
    public int compareTo(Material other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null Material");
        }
        int titleComparison = this.title.compareToIgnoreCase(other.title);
        if (titleComparison != 0) {
            return titleComparison;
        }
        return this.id.compareTo(other.id);
    }
    
    /**
     * Materials are equal if they have the same ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Material)) return false;
        Material other = (Material) obj;
        return id.equals(other.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s[ID=%s, Title='%s', Price=$%.2f, Year=%d]",
            type.getDisplayName(), id, title, price, year);
    }
}