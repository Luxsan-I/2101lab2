package com.university.bookstore.model;

/**
 * Interface for materials with multimedia content.
 * Demonstrates interface segregation principle and multiple inheritance through interfaces.
 * 
 * <p>This interface defines behavior for audio and video materials,
 * including duration, format, and playback capabilities.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public interface Media {
    
    /**
     * Gets the duration of the media in minutes.
     * 
     * @return duration in minutes
     */
    int getDuration();
    
    /**
     * Gets the file format of the media.
     * 
     * @return format string (e.g., "MP3", "MP4", "WAV")
     */
    String getFormat();
    
    /**
     * Gets the file size in megabytes.
     * 
     * @return size in MB
     */
    double getFileSize();
    
    /**
     * Checks if the media requires an internet connection.
     * 
     * @return true if streaming required, false if downloadable
     */
    boolean isStreamingOnly();
    
    /**
     * Gets the quality setting for the media.
     * 
     * @return quality descriptor
     */
    MediaQuality getQuality();
    
    /**
     * Gets playback information as a formatted string.
     * 
     * @return playback details
     */
    default String getPlaybackInfo() {
        return String.format("Duration: %d min, Format: %s, Quality: %s, Size: %.1f MB",
            getDuration(), getFormat(), getQuality(), getFileSize());
    }
    
    /**
     * Calculates estimated download time based on connection speed.
     * 
     * @param mbps download speed in megabits per second
     * @return estimated time in seconds
     */
    default int estimateDownloadTime(double mbps) {
        if (isStreamingOnly()) {
            return 0;
        }
        double megabits = getFileSize() * 8;
        return (int) Math.ceil(megabits / mbps);
    }
    
    /**
     * Quality levels for media content.
     */
    enum MediaQuality {
        LOW("Low Quality", 64),
        STANDARD("Standard Quality", 128),
        HIGH("High Quality", 256),
        HD("HD Quality", 320),
        ULTRA_HD("Ultra HD", 512),
        PHYSICAL("Physical Media", 0);
        
        private final String description;
        private final int bitrate;
        
        MediaQuality(String description, int bitrate) {
            this.description = description;
            this.bitrate = bitrate;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getBitrate() {
            return bitrate;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}