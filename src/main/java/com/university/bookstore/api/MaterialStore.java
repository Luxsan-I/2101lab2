package com.university.bookstore.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;

/**
 * Interface defining operations for a polymorphic material store.
 * Demonstrates interface segregation and dependency inversion principles.
 * 
 * <p>This interface extends the concept of a bookstore to handle
 * various types of materials using polymorphism.</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public interface MaterialStore {
    
    /**
     * Adds a material to the store inventory.
     * 
     * @param material the material to add
     * @return true if added successfully, false if duplicate ID
     */
    boolean addMaterial(Material material);
    
    /**
     * Removes a material by its ID.
     * 
     * @param id the material ID
     * @return the removed material, or Optional.empty() if not found
     */
    Optional<Material> removeMaterial(String id);
    
    /**
     * Finds a material by its ID.
     * 
     * @param id the material ID
     * @return the material, or Optional.empty() if not found
     */
    Optional<Material> findById(String id);
    
    /**
     * Searches materials by title (case-insensitive partial match).
     * 
     * @param title the title to search for
     * @return list of matching materials
     */
    List<Material> searchByTitle(String title);
    
    /**
     * Searches materials by creator (author/director/publisher).
     * 
     * @param creator the creator name
     * @return list of matching materials
     */
    List<Material> searchByCreator(String creator);
    
    /**
     * Gets all materials of a specific type.
     * Demonstrates polymorphic filtering.
     * 
     * @param type the material type
     * @return list of materials of the specified type
     */
    List<Material> getMaterialsByType(Material.MaterialType type);
    
    /**
     * Gets all materials that implement the Media interface.
     * Demonstrates interface-based polymorphism.
     * 
     * @return list of media materials
     */
    List<Media> getMediaMaterials();
    
    /**
     * Filters materials by a custom predicate.
     * Demonstrates functional programming with polymorphism.
     * 
     * @param predicate the filter condition
     * @return filtered list of materials
     */
    List<Material> filterMaterials(Predicate<Material> predicate);
    
    /**
     * Finds materials published in the last N years.
     * 
     * @param years the number of years to look back
     * @return list of recent materials
     */
    List<Material> findRecentMaterials(int years);
    
    /**
     * Finds materials by multiple creators (OR condition).
     * 
     * @param creators the creator names to search for
     * @return list of materials by any of the specified creators
     */
    List<Material> findByCreators(String... creators);
    
    /**
     * Finds materials with custom filtering using predicate.
     * 
     * @param condition the filter condition
     * @return filtered list of materials
     */
    List<Material> findWithPredicate(Predicate<Material> condition);
    
    /**
     * Gets materials sorted by custom comparator.
     * 
     * @param comparator the sorting comparator
     * @return sorted list of materials
     */
    List<Material> getSorted(java.util.Comparator<Material> comparator);
    
    /**
     * Gets materials within a price range.
     * 
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of materials in price range
     */
    List<Material> getMaterialsByPriceRange(double minPrice, double maxPrice);
    
    /**
     * Gets materials published/released in a specific year.
     * 
     * @param year the year
     * @return list of materials from that year
     */
    List<Material> getMaterialsByYear(int year);
    
    /**
     * Gets all materials sorted by title.
     * 
     * @return sorted list of all materials
     */
    List<Material> getAllMaterialsSorted();
    
    /**
     * Gets all materials (unsorted).
     * 
     * @return list of all materials
     */
    List<Material> getAllMaterials();
    
    /**
     * Calculates total inventory value.
     * 
     * @return sum of all material prices
     */
    double getTotalInventoryValue();
    
    /**
     * Calculates total discounted inventory value.
     * Uses polymorphic discount calculation.
     * 
     * @return sum of all discounted prices
     */
    double getTotalDiscountedValue();
    
    /**
     * Gets inventory statistics.
     * 
     * @return statistics object with counts and averages
     */
    InventoryStats getInventoryStats();
    
    /**
     * Clears all materials from the store.
     */
    void clearInventory();
    
    /**
     * Gets the number of materials in inventory.
     * 
     * @return material count
     */
    int size();
    
    /**
     * Checks if the inventory is empty.
     * 
     * @return true if no materials in inventory
     */
    boolean isEmpty();
    
    /**
     * Statistics class for inventory analysis.
     */
    class InventoryStats {
        private final int totalCount;
        private final double averagePrice;
        private final double medianPrice;
        private final int uniqueTypes;
        private final int mediaCount;
        private final int printCount;
        
        public InventoryStats(int totalCount, double averagePrice, double medianPrice,
                             int uniqueTypes, int mediaCount, int printCount) {
            this.totalCount = totalCount;
            this.averagePrice = averagePrice;
            this.medianPrice = medianPrice;
            this.uniqueTypes = uniqueTypes;
            this.mediaCount = mediaCount;
            this.printCount = printCount;
        }
        
        public int getTotalCount() { return totalCount; }
        public double getAveragePrice() { return averagePrice; }
        public double getMedianPrice() { return medianPrice; }
        public int getUniqueTypes() { return uniqueTypes; }
        public int getMediaCount() { return mediaCount; }
        public int getPrintCount() { return printCount; }
        
        @Override
        public String toString() {
            return String.format("InventoryStats[Total=%d, AvgPrice=$%.2f, MedianPrice=$%.2f, Types=%d, Media=%d, Print=%d]",
                totalCount, averagePrice, medianPrice, uniqueTypes, mediaCount, printCount);
        }
    }
}