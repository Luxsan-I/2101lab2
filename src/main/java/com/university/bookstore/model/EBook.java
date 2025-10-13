package com.university.bookstore.model;

import java.util.Objects;
import java.util.Set;

/**
 * Represents an electronic book (e-book) in the bookstore system.
 * Extends Material and implements Media interface to demonstrate
 * multiple inheritance through interfaces.
 * 
 * <p>EBooks support various file formats, DRM protection, and
 * provide reading time estimation based on word count.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class EBook extends Material implements Media {
    
    private static final Set<String> VALID_FORMATS = Set.of("PDF", "EPUB", "MOBI");
    private static final int WORDS_PER_MINUTE = 250;
    private static final double DRM_FREE_DISCOUNT = 0.15;
    
    private final String author;
    private final String fileFormat;
    private final double fileSize; // in MB
    private final boolean drmEnabled;
    private final int wordCount;
    private final MediaQuality quality;
    
    /**
     * Constructs a new EBook with validation.
     * 
     * @param id unique identifier
     * @param title the book title
     * @param author the author name
     * @param price the price in dollars
     * @param year the publication year
     * @param fileFormat the file format (PDF, EPUB, or MOBI)
     * @param fileSize the file size in MB
     * @param drmEnabled whether DRM is enabled
     * @param wordCount the number of words in the book
     * @param quality the media quality
     * @throws IllegalArgumentException if validation fails
     */
    public EBook(String id, String title, String author, double price, int year,
                 String fileFormat, double fileSize, boolean drmEnabled, 
                 int wordCount, MediaQuality quality) {
        super(id, title, price, year, MaterialType.E_BOOK);
        this.author = validateStringField(author, "Author");
        this.fileFormat = validateFileFormat(fileFormat);
        this.fileSize = validateFileSize(fileSize);
        this.drmEnabled = drmEnabled;
        this.wordCount = validateWordCount(wordCount);
        this.quality = Objects.requireNonNull(quality, "Media quality cannot be null");
    }
    
    /**
     * Validates that the file format is supported.
     * 
     * @param format the file format to validate
     * @return the validated format
     * @throws IllegalArgumentException if format is not supported
     */
    private String validateFileFormat(String format) {
        if (format == null) {
            throw new NullPointerException("File format cannot be null");
        }
        String upperFormat = format.trim().toUpperCase();
        if (!VALID_FORMATS.contains(upperFormat)) {
            throw new IllegalArgumentException(
                String.format("Unsupported file format: %s. Supported formats: %s", 
                    format, VALID_FORMATS));
        }
        return upperFormat;
    }
    
    /**
     * Validates that the file size is positive.
     * 
     * @param size the file size to validate
     * @return the validated size
     * @throws IllegalArgumentException if size is not positive
     */
    private double validateFileSize(double size) {
        if (size <= 0.0) {
            throw new IllegalArgumentException(
                String.format("File size must be positive. Provided: %.2f MB", size));
        }
        if (Double.isNaN(size) || Double.isInfinite(size)) {
            throw new IllegalArgumentException(
                String.format("File size must be a valid number. Provided: %.2f MB", size));
        }
        return size;
    }
    
    /**
     * Validates that the word count is non-negative.
     * 
     * @param count the word count to validate
     * @return the validated count
     * @throws IllegalArgumentException if count is negative
     */
    private int validateWordCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException(
                String.format("Word count cannot be negative. Provided: %d", count));
        }
        return count;
    }
    
    @Override
    public String getCreator() {
        return author;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("EBook: %s by %s (%s, %.1f MB, %s, %d words)", 
            getTitle(), author, fileFormat, fileSize, 
            drmEnabled ? "DRM" : "DRM-free", wordCount);
    }
    
    @Override
    public double getDiscountRate() {
        // DRM-free books get a 15% discount
        return drmEnabled ? 0.0 : DRM_FREE_DISCOUNT;
    }
    
    /**
     * Calculates estimated reading time in minutes based on average reading speed.
     * 
     * @return estimated reading time in minutes
     */
    public int getReadingTimeMinutes() {
        return wordCount / WORDS_PER_MINUTE;
    }
    
    /**
     * Gets the file format of the ebook.
     * 
     * @return the file format
     */
    public String getFileFormat() {
        return fileFormat;
    }
    
    /**
     * Gets the file size in MB.
     * 
     * @return the file size
     */
    public double getFileSize() {
        return fileSize;
    }
    
    /**
     * Checks if DRM is enabled.
     * 
     * @return true if DRM is enabled
     */
    public boolean isDrmEnabled() {
        return drmEnabled;
    }
    
    /**
     * Gets the word count of the book.
     * 
     * @return the word count
     */
    public int getWordCount() {
        return wordCount;
    }
    
    @Override
    public MediaQuality getQuality() {
        return quality;
    }
    
    public String getDescription() {
        return String.format("Digital %s format book by %s. %s, %.1f MB. Estimated reading time: %d minutes.",
            fileFormat, author, 
            drmEnabled ? "DRM protected" : "DRM-free", 
            fileSize, 
            getReadingTimeMinutes());
    }
    
    // Media interface implementation
    @Override
    public int getDuration() {
        return getReadingTimeMinutes();
    }
    
    @Override
    public String getFormat() {
        return fileFormat;
    }
    
    @Override
    public boolean isStreamingOnly() {
        return false; // E-books are downloadable, not streaming
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EBook)) return false;
        if (!super.equals(obj)) return false;
        
        EBook other = (EBook) obj;
        return Double.compare(other.fileSize, fileSize) == 0 &&
               drmEnabled == other.drmEnabled &&
               wordCount == other.wordCount &&
               Objects.equals(author, other.author) &&
               Objects.equals(fileFormat, other.fileFormat) &&
               quality == other.quality;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), author, fileFormat, fileSize, 
                           drmEnabled, wordCount, quality);
    }
    
    @Override
    public String toString() {
        return String.format("EBook[ID=%s, Title='%s', Author='%s', Price=$%.2f, Year=%d, " +
                           "Format=%s, Size=%.1fMB, DRM=%s, Words=%d, Quality=%s]",
            getId(), getTitle(), author, getPrice(), getYear(), 
            fileFormat, fileSize, drmEnabled, wordCount, quality);
    }
}
