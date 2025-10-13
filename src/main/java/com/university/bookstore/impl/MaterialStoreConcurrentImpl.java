package com.university.bookstore.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.Magazine;
import com.university.bookstore.model.Material;
import com.university.bookstore.model.Media;

/**
 * Thread-safe implementation of MaterialStore using ConcurrentHashMap for primary indexing
 * and immutable snapshots for secondary indexes with StampedLock for consistency.
 * 
 * <p>This implementation provides:
 * - Lock-free reads for primary operations (findById)
 * - Thread-safe writes with minimal locking
 * - Immutable snapshots for secondary indexes
 * - High performance under concurrent access</p>
 * 
 * @author Navid Mohaghegh
 * @version 2.0
 * @since 2024-09-15
 */
public class MaterialStoreConcurrentImpl implements MaterialStore {
    
    // Primary index using ConcurrentHashMap for thread-safe, lock-free reads
    private final ConcurrentHashMap<String, Material> primaryIndex;
    
    // Secondary indexes with immutable snapshots
    private volatile Map<String, List<Material>> titleIndex;
    private volatile Map<String, List<Material>> creatorIndex;
    private volatile Map<Material.MaterialType, List<Material>> typeIndex;
    private volatile List<Material> allMaterials;
    
    // Track if indexes are dirty to avoid unnecessary rebuilds
    private volatile boolean indexesDirty = true;
    
    // StampedLock for managing secondary index updates
    private final StampedLock indexLock = new StampedLock();
    
    // Statistics tracking
    private volatile int totalCount = 0;
    private volatile double totalValue = 0.0;
    private volatile double totalDiscountedValue = 0.0;
    
    /**
     * Creates a new empty concurrent material store.
     */
    public MaterialStoreConcurrentImpl() {
        this.primaryIndex = new ConcurrentHashMap<>();
        this.titleIndex = Collections.emptyMap();
        this.creatorIndex = Collections.emptyMap();
        this.typeIndex = Collections.emptyMap();
        this.allMaterials = Collections.emptyList();
    }
    
    /**
     * Creates a concurrent material store with initial materials.
     * 
     * @param initialMaterials materials to add initially
     */
    public MaterialStoreConcurrentImpl(Collection<Material> initialMaterials) {
        this();
        if (initialMaterials != null) {
            for (Material material : initialMaterials) {
                if (material != null) {
                    addMaterialInternal(material);
                }
            }
            // Update secondary indexes once after adding all materials
            updateSecondaryIndexes();
        }
    }
    
    /**
     * Internal method to add material without triggering index updates during construction.
     */
    private void addMaterialInternal(Material material) {
        primaryIndex.put(material.getId(), material);
        updateStatistics(material, true);
    }
    
    /**
     * Batch add materials for better performance.
     * Only updates secondary indexes once after all materials are added.
     */
    public void addMaterialsBatch(Collection<Material> materials) {
        if (materials == null || materials.isEmpty()) {
            return;
        }
        
        // Add all materials to primary index first
        for (Material material : materials) {
            if (material != null) {
                primaryIndex.put(material.getId(), material);
                updateStatistics(material, true);
            }
        }
        
        // Update secondary indexes once after all materials are added
        this.indexesDirty = true;
        updateSecondaryIndexes();
    }
    
    @Override
    public boolean addMaterial(Material material) {
        if (material == null) {
            throw new NullPointerException("Cannot add null material");
        }
        
        // Use synchronized block to ensure atomicity of the entire operation
        synchronized (this) {
            // Use putIfAbsent for atomic check-and-insert
            Material existing = primaryIndex.putIfAbsent(material.getId(), material);
            if (existing != null) {
                return false; // Material already exists
            }
            
            // Update statistics atomically - only when material was actually added
            updateStatistics(material, true);
            this.indexesDirty = true;
        }
        
        return true;
    }
    
    @Override
    public Optional<Material> removeMaterial(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        synchronized (this) {
            // Remove from primary index atomically
            Material removed = primaryIndex.remove(id);
            if (removed == null) {
                return Optional.empty();
            }
            
            // Update statistics atomically
            updateStatistics(removed, false);
            this.indexesDirty = true;
            
            return Optional.of(removed);
        }
    }
    
    @Override
    public Optional<Material> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        // Lock-free read from ConcurrentHashMap - NO secondary index needed!
        return Optional.ofNullable(primaryIndex.get(id));
    }
    
    @Override
    public List<Material> searchByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        Map<String, List<Material>> currentTitleIndex = titleIndex;
        List<Material> results = currentTitleIndex.get(title.trim().toLowerCase());
        
        return results != null ? new ArrayList<>(results) : new ArrayList<>();
    }
    
    @Override
    public List<Material> searchByCreator(String creator) {
        if (creator == null || creator.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        Map<String, List<Material>> currentCreatorIndex = creatorIndex;
        List<Material> results = currentCreatorIndex.get(creator.trim().toLowerCase());
        
        return results != null ? new ArrayList<>(results) : new ArrayList<>();
    }
    
    @Override
    public List<Material> getMaterialsByType(Material.MaterialType type) {
        if (type == null) {
            return new ArrayList<>();
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        Map<Material.MaterialType, List<Material>> currentTypeIndex = typeIndex;
        List<Material> results = currentTypeIndex.get(type);
        
        return results != null ? new ArrayList<>(results) : new ArrayList<>();
    }
    
    @Override
    public List<Media> getMediaMaterials() {
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(material -> material instanceof Media)
                .map(material -> (Media) material)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> filterMaterials(Predicate<Material> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> findRecentMaterials(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years cannot be negative: " + years);
        }
        
        int currentYear = java.time.Year.now().getValue();
        int cutoffYear = currentYear - years;
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(material -> material.getYear() >= cutoffYear)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> findByCreators(String... creators) {
        if (creators == null || creators.length == 0) {
            return new ArrayList<>();
        }
        
        Set<String> creatorSet = java.util.Arrays.stream(creators)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(material -> creatorSet.contains(material.getCreator().toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> findWithPredicate(Predicate<Material> condition) {
        if (condition == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getSorted(Comparator<Material> comparator) {
        if (comparator == null) {
            throw new NullPointerException("Comparator cannot be null");
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getMaterialsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new IllegalArgumentException(
                String.format("Invalid price range: min=%.2f, max=%.2f", minPrice, maxPrice));
        }
        
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(material -> material.getPrice() >= minPrice && material.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getMaterialsByYear(int year) {
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .filter(material -> material.getYear() == year)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getAllMaterialsSorted() {
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        return currentAllMaterials.stream()
                .sorted()
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> getAllMaterials() {
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        return new ArrayList<>(allMaterials);
    }
    
    @Override
    public double getTotalInventoryValue() {
        // Lock-free read of volatile field
        return totalValue;
    }
    
    @Override
    public double getTotalDiscountedValue() {
        // Lock-free read of volatile field
        return totalDiscountedValue;
    }
    
    @Override
    public InventoryStats getInventoryStats() {
        // Ensure indexes are built (lazy loading)
        ensureSecondaryIndexes();
        
        // Lock-free read from immutable snapshot
        List<Material> currentAllMaterials = allMaterials;
        
        if (currentAllMaterials.isEmpty()) {
            return new InventoryStats(0, 0.0, 0.0, 0, 0, 0);
        }
        
        // Calculate statistics
        double averagePrice = currentAllMaterials.stream()
                .mapToDouble(Material::getPrice)
                .average()
                .orElse(0.0);
        
        List<Double> prices = currentAllMaterials.stream()
                .map(Material::getPrice)
                .sorted()
                .collect(Collectors.toList());
        
        double medianPrice = prices.isEmpty() ? 0.0 :
            prices.size() % 2 == 0 ?
                (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2.0 :
                prices.get(prices.size() / 2);
        
        Set<Material.MaterialType> uniqueTypes = currentAllMaterials.stream()
                .map(Material::getType)
                .collect(Collectors.toSet());
        
        long mediaCount = currentAllMaterials.stream()
                .filter(material -> material instanceof Media)
                .count();
        
        long printCount = currentAllMaterials.stream()
                .filter(material -> material instanceof Magazine || 
                                  (material.getClass().getSimpleName().equals("PrintedBook")))
                .count();
        
        return new InventoryStats(
            currentAllMaterials.size(),
            averagePrice,
            medianPrice,
            uniqueTypes.size(),
            (int) mediaCount,
            (int) printCount
        );
    }
    
    @Override
    public void clearInventory() {
        // Clear primary index
        primaryIndex.clear();
        
        // Reset statistics
        totalCount = 0;
        totalValue = 0.0;
        totalDiscountedValue = 0.0;
        
        // Update secondary indexes under write lock
        updateSecondaryIndexes();
    }
    
    @Override
    public int size() {
        // Lock-free read of volatile field
        return totalCount;
    }
    
    @Override
    public boolean isEmpty() {
        // Lock-free read of volatile field
        return totalCount == 0;
    }
    
    /**
     * Updates statistics atomically when materials are added or removed.
     */
    private void updateStatistics(Material material, boolean isAdd) {
        double multiplier = isAdd ? 1.0 : -1.0;
        
        // Update statistics directly since we're already in a synchronized block
        totalCount += (int) multiplier;
        totalValue += material.getPrice() * multiplier;
        totalDiscountedValue += material.getDiscountedPrice() * multiplier;
    }
    
    /**
     * Updates secondary indexes under write lock to ensure consistency.
     * Creates new immutable snapshots for lock-free reads.
     */
    private void invalidateSecondaryIndexes() {
        // Mark indexes as dirty - they'll be rebuilt on next access
        this.indexesDirty = true;
    }
    
    private void ensureSecondaryIndexes() {
        // Fast path: check if indexes are already built and up-to-date
        if (!indexesDirty && allMaterials != null) {
            return;
        }
        
        // Try optimistic read first to avoid write lock if possible
        long stamp = indexLock.tryOptimisticRead();
        if (!indexesDirty && allMaterials != null) {
            if (indexLock.validate(stamp)) {
                return; // Indexes are already built and up-to-date
            }
        }
        
        // If optimistic read failed, get write lock and rebuild
        updateSecondaryIndexes();
    }
    
    private void updateSecondaryIndexes() {
        long stamp = indexLock.writeLock();
        try {
            // Double-check pattern to avoid unnecessary rebuilds
            if (!indexesDirty && allMaterials != null) {
                return;
            }
            
            // Create new immutable snapshots
            List<Material> newAllMaterials = new ArrayList<>(primaryIndex.values());
            
            // Build indexes efficiently - use single stream for better performance
            Map<String, List<Material>> newTitleIndex = newAllMaterials.stream()
                    .collect(Collectors.groupingBy(
                        material -> material.getTitle().toLowerCase(),
                        Collectors.toList()
                    ));
            
            Map<String, List<Material>> newCreatorIndex = newAllMaterials.stream()
                    .collect(Collectors.groupingBy(
                        material -> material.getCreator().toLowerCase(),
                        Collectors.toList()
                    ));
            
            Map<Material.MaterialType, List<Material>> newTypeIndex = newAllMaterials.stream()
                    .collect(Collectors.groupingBy(
                        Material::getType,
                        Collectors.toList()
                    ));
            
            // Make collections immutable and assign to volatile fields
            this.allMaterials = Collections.unmodifiableList(newAllMaterials);
            this.titleIndex = Collections.unmodifiableMap(newTitleIndex);
            this.creatorIndex = Collections.unmodifiableMap(newCreatorIndex);
            this.typeIndex = Collections.unmodifiableMap(newTypeIndex);
            
            // Mark indexes as clean
            this.indexesDirty = false;
            
        } finally {
            indexLock.unlockWrite(stamp);
        }
    }
    
    /**
     * Gets the current size of the primary index (for debugging/monitoring).
     * 
     * @return size of primary index
     */
    public int getPrimaryIndexSize() {
        return primaryIndex.size();
    }
    
    /**
     * Gets the current size of the title index (for debugging/monitoring).
     * 
     * @return size of title index
     */
    public int getTitleIndexSize() {
        return titleIndex.size();
    }
    
    /**
     * Gets the current size of the creator index (for debugging/monitoring).
     * 
     * @return size of creator index
     */
    public int getCreatorIndexSize() {
        return creatorIndex.size();
    }
}
