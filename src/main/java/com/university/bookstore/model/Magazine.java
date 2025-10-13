package com.university.bookstore.model;

/**
 * Represents a magazine in the store inventory.
 * Demonstrates inheritance from Material base class.
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class Magazine extends Material {
    
    private final String issn;
    private final String publisher;
    private final int issueNumber;
    private final String frequency;
    private final String category;
    
    /**
     * Frequency of magazine publication.
     */
    public enum PublicationFrequency {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        BIWEEKLY("Bi-weekly"),
        MONTHLY("Monthly"),
        QUARTERLY("Quarterly"),
        ANNUAL("Annual");
        
        private final String label;
        
        PublicationFrequency(String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return label;
        }
    }
    
    /**
     * Creates a new Magazine.
     * 
     * @param issn International Standard Serial Number
     * @param title magazine title
     * @param publisher publishing company
     * @param price price per issue
     * @param year publication year
     * @param issueNumber issue number
     * @param frequency publication frequency
     * @param category magazine category/genre
     */
    public Magazine(String issn, String title, String publisher, double price,
                    int year, int issueNumber, String frequency, String category) {
        super(validateIssn(issn), title, price, year, MaterialType.MAGAZINE);
        this.issn = this.id;
        this.publisher = validateStringField(publisher, "Publisher");
        this.issueNumber = validateIssueNumber(issueNumber);
        this.frequency = validateStringField(frequency, "Frequency");
        this.category = validateStringField(category, "Category");
    }
    
    private static String validateIssn(String issn) {
        if (issn == null) {
            throw new NullPointerException("ISSN cannot be null");
        }
        
        String cleaned = issn.replaceAll("-", "").trim();
        
        if (cleaned.length() != 8) {
            throw new IllegalArgumentException(
                "ISSN must be 8 characters. Provided: " + issn);
        }
        
        return cleaned;
    }
    
    private int validateIssueNumber(int issueNumber) {
        if (issueNumber <= 0) {
            throw new IllegalArgumentException(
                "Issue number must be positive. Provided: " + issueNumber);
        }
        return issueNumber;
    }
    
    @Override
    public String getCreator() {
        return publisher;
    }
    
    @Override
    public String getDisplayInfo() {
        return String.format("%s - Issue #%d (%s %d) - %s, $%.2f",
            title, issueNumber, frequency, year, category, price);
    }
    
    @Override
    public double getDiscountRate() {
        int currentYear = java.time.Year.now().getValue();
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        
        if (year < currentYear || (year == currentYear && issueNumber < currentMonth - 2)) {
            return 0.25;
        }
        return 0.0;
    }
    
    /**
     * Calculates annual subscription cost with discount.
     * 
     * @return annual subscription price
     */
    public double calculateAnnualSubscription() {
        int issuesPerYear = getIssuesPerYear();
        double fullPrice = price * issuesPerYear;
        return fullPrice * 0.85;
    }
    
    private int getIssuesPerYear() {
        switch (frequency.toUpperCase()) {
            case "DAILY": return 365;
            case "WEEKLY": return 52;
            case "BI-WEEKLY": return 26;
            case "MONTHLY": return 12;
            case "QUARTERLY": return 4;
            case "ANNUAL": return 1;
            default: return 12;
        }
    }
    
    public String getIssn() {
        return issn;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public int getIssueNumber() {
        return issueNumber;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public String getCategory() {
        return category;
    }
    
    @Override
    public String toString() {
        return String.format("Magazine[ISSN=%s, Title='%s', Publisher='%s', Issue=%d, %s, Category='%s', Price=$%.2f, Year=%d]",
            issn, title, publisher, issueNumber, frequency, category, price, year);
    }
}