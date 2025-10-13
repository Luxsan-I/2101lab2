package com.university.bookstore.model;

import java.util.Arrays;
import java.util.List;

/**
 * Represents video content (movies, documentaries, educational videos).
 * Implements both Material and Media interfaces to demonstrate multiple inheritance.
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class VideoMaterial extends Material implements Media {
    
    private final String director;
    private final int duration;
    private final String format;
    private final double fileSize;
    private final MediaQuality quality;
    private final VideoType videoType;
    private final String rating;
    private final List<String> cast;
    private final boolean subtitlesAvailable;
    private final String aspectRatio;
    
    /**
     * Types of video content.
     */
    public enum VideoType {
        MOVIE("Movie"),
        DOCUMENTARY("Documentary"),
        EDUCATIONAL("Educational"),
        TV_SERIES("TV Series"),
        SHORT_FILM("Short Film"),
        TUTORIAL("Tutorial");
        
        private final String label;
        
        VideoType(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    /**
     * Creates a new VideoMaterial.
     * 
     * @param id unique identifier (UPC/EAN code)
     * @param title video title
     * @param director director or producer
     * @param price price in dollars
     * @param year release year
     * @param duration duration in minutes
     * @param format video format (MP4, AVI, MKV, etc.)
     * @param fileSize size in megabytes
     * @param quality video quality
     * @param videoType type of video
     * @param rating content rating (G, PG, PG-13, R, etc.)
     * @param cast list of main cast members
     * @param subtitlesAvailable whether subtitles are included
     * @param aspectRatio aspect ratio (16:9, 4:3, etc.)
     */
    public VideoMaterial(String id, String title, String director, double price,
                        int year, int duration, String format, double fileSize,
                        MediaQuality quality, VideoType videoType, String rating,
                        List<String> cast, boolean subtitlesAvailable, String aspectRatio) {
        super(id, title, price, year, 
              videoType == VideoType.DOCUMENTARY ? MaterialType.DOCUMENTARY : MaterialType.VIDEO);
        this.director = validateStringField(director, "Director");
        this.duration = validateDuration(duration);
        this.format = validateStringField(format, "Format");
        this.fileSize = validateFileSize(fileSize);
        this.quality = quality != null ? quality : MediaQuality.HD;
        this.videoType = videoType != null ? videoType : VideoType.MOVIE;
        this.rating = rating != null ? rating : "NR";
        this.cast = cast != null ? cast : Arrays.asList();
        this.subtitlesAvailable = subtitlesAvailable;
        this.aspectRatio = aspectRatio != null ? aspectRatio : "16:9";
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
        return director;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s (%d) - Directed by %s, %s, %d min, Rated %s, $%.2f",
            title, year, director, videoType, duration, rating, price);
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
        return fileSize > 4096;
    }
    
    @Override
    public MediaQuality getQuality() {
        return quality;
    }
    
    @Override
    public double getDiscountRate() {
        int currentYear = java.time.Year.now().getValue();
        if (year < currentYear - 5) {
            return 0.30;
        } else if (year < currentYear - 2) {
            return 0.15;
        }
        return 0.0;
    }
    
    /**
     * Checks if this is a feature-length video.
     * 
     * @return true if duration >= 60 minutes
     */
    public boolean isFeatureLength() {
        return duration >= 60;
    }
    
    /**
     * Gets bandwidth requirement for streaming in Mbps.
     * 
     * @return required bandwidth
     */
    public double getStreamingBandwidth() {
        switch (quality) {
            case LOW: return 1.5;
            case STANDARD: return 3.0;
            case HIGH: return 5.0;
            case HD: return 8.0;
            case ULTRA_HD: return 25.0;
            default: return 5.0;
        }
    }
    
    /**
     * Gets storage space required with compression.
     * 
     * @param compressionRatio compression ratio (0.5 = 50% compression)
     * @return compressed size in MB
     */
    public double getCompressedSize(double compressionRatio) {
        if (compressionRatio <= 0 || compressionRatio > 1) {
            throw new IllegalArgumentException("Compression ratio must be between 0 and 1");
        }
        return fileSize * compressionRatio;
    }
    
    public String getDirector() {
        return director;
    }
    
    public VideoType getVideoType() {
        return videoType;
    }
    
    public String getRating() {
        return rating;
    }
    
    public List<String> getCast() {
        return cast;
    }
    
    public boolean hasSubtitles() {
        return subtitlesAvailable;
    }
    
    public String getAspectRatio() {
        return aspectRatio;
    }
    
    @Override
    public String toString() {
        return String.format("VideoMaterial[ID=%s, Title='%s', Director='%s', Type=%s, Duration=%dmin, Quality=%s, Rating=%s, Price=$%.2f]",
            id, title, director, videoType, duration, quality, rating, price);
    }
}