package com.university.bookstore.model;

/**
 * Represents an audio book that implements both Material and Media interfaces.
 * Demonstrates multiple inheritance through interfaces.
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class AudioBook extends Material implements Media {
    
    private final String isbn;
    private final String author;
    private final String narrator;
    private final int duration;
    private final String format;
    private final double fileSize;
    private final boolean streamingOnly;
    private final MediaQuality quality;
    private final String language;
    private final boolean unabridged;
    
    /**
     * Creates a new AudioBook with full specifications.
     * 
     * @param isbn ISBN identifier
     * @param title book title
     * @param author original author
     * @param narrator voice narrator
     * @param price price in dollars
     * @param year publication year
     * @param duration duration in minutes
     * @param format audio format (MP3, M4B, etc.)
     * @param fileSize size in megabytes
     * @param quality audio quality level
     * @param language narration language
     * @param unabridged true if unabridged version
     */
    public AudioBook(String isbn, String title, String author, String narrator,
                     double price, int year, int duration, String format,
                     double fileSize, MediaQuality quality, String language,
                     boolean unabridged) {
        super(validateIsbn(isbn), title, price, year, MaterialType.AUDIO_BOOK);
        this.isbn = this.id;
        this.author = validateStringField(author, "Author");
        this.narrator = validateStringField(narrator, "Narrator");
        this.duration = validateDuration(duration);
        this.format = validateStringField(format, "Format");
        this.fileSize = validateFileSize(fileSize);
        this.streamingOnly = false;
        this.quality = quality != null ? quality : MediaQuality.STANDARD;
        this.language = validateStringField(language, "Language");
        this.unabridged = unabridged;
    }
    
    private static String validateIsbn(String isbn) {
        if (isbn == null) {
            throw new NullPointerException("ISBN cannot be null");
        }
        String cleaned = isbn.replaceAll("-", "").trim();
        if (cleaned.length() < 10 || cleaned.length() > 13) {
            throw new IllegalArgumentException("Invalid ISBN: " + isbn);
        }
        return cleaned;
    }
    
    private int validateDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException(
                "Duration must be positive. Provided: " + duration);
        }
        return duration;
    }
    
    private double validateFileSize(double fileSize) {
        if (fileSize <= 0) {
            throw new IllegalArgumentException(
                "File size must be positive. Provided: " + fileSize);
        }
        return fileSize;
    }
    
    @Override
    public String getCreator() {
        return author + " (Narrated by " + narrator + ")";
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s by %s, narrated by %s - %s, %d hours %d minutes, $%.2f",
            title, author, narrator,
            unabridged ? "Unabridged" : "Abridged",
            duration / 60, duration % 60, price);
    }
    
    @Override
    public int getDuration() {
        return duration;
    }
    
    @Override
    public String getFormat() {
        return format;
    }
    
    @Override
    public double getFileSize() {
        return fileSize;
    }
    
    @Override
    public boolean isStreamingOnly() {
        return streamingOnly;
    }
    
    @Override
    public MediaQuality getQuality() {
        return quality;
    }
    
    @Override
    public double getDiscountRate() {
        if (!unabridged) {
            return 0.10;
        }
        return 0.0;
    }
    
    /**
     * Calculates listening sessions based on daily listening time.
     * 
     * @param minutesPerDay daily listening time
     * @return number of days to complete
     */
    public int calculateListeningSessions(int minutesPerDay) {
        if (minutesPerDay <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) duration / minutesPerDay);
    }
    
    /**
     * Gets playback speed adjusted duration.
     * 
     * @param speed playback speed (1.0 = normal, 1.5 = 1.5x, etc.)
     * @return adjusted duration in minutes
     */
    public int getAdjustedDuration(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }
        return (int) Math.ceil(duration / speed);
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getNarrator() {
        return narrator;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public boolean isUnabridged() {
        return unabridged;
    }
    
    @Override
    public String toString() {
        return String.format("AudioBook[ISBN=%s, Title='%s', Author='%s', Narrator='%s', Duration=%dmin, Format=%s, %s, Price=$%.2f]",
            isbn, title, author, narrator, duration, format,
            unabridged ? "Unabridged" : "Abridged", price);
    }
}