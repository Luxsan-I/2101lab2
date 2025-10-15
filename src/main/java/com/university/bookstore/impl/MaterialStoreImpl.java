package com.university.bookstore.impl;

import com.university.bookstore.api.MaterialStore;
import com.university.bookstore.model.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Custom implementation of the {@link MaterialStore} interface backed by an {@link ArrayList}.
 * <p>
 * Provides comprehensive operations for managing a bookstore's inventory,
 * including adding, removing, searching, filtering, and computing statistics
 * across various subclasses of {@link Material}.
 *
 * <p>This implementation maintains an internal index for quick lookups
 * and supports polymorphic behavior across material types.
 *
 *     Luxsan Indran (221298286)
 *     luxsan@my.yorku.ca
 */
public class MaterialStoreImpl implements MaterialStore {

    private final List<Material> materials;
    private final Map<String, Material> materialIndex;

    /**
     * Default constructor — initializes an empty material inventory.
     */
    public MaterialStoreImpl() {
        this.materials = new ArrayList<>();
        this.materialIndex = new HashMap<>();
    }

    /**
     * Constructs a new {@code MaterialStoreImpl} preloaded with an initial collection of materials.
     *
     * @param initialMaterials a collection of {@link Material} objects to preload; may be {@code null}
     */
    public MaterialStoreImpl(Collection<Material> initialMaterials) {
        this();
        if (initialMaterials != null) {
            initialMaterials.forEach(this::addMaterial);
        }
    }

    /**
     * Adds a new material to the inventory if it does not already exist.
     *
     * @param material the {@link Material} to add
     * @return {@code true} if the material was added successfully; {@code false} if a duplicate ID exists
     * @throws NullPointerException if {@code material} is {@code null}
     */
    @Override
    public synchronized boolean addMaterial(Material material) {
        Objects.requireNonNull(material, "Material cannot be null");

        if (materialIndex.containsKey(material.getId())) {
            return false;
        }

        materials.add(material);
        materialIndex.put(material.getId(), material);
        return true;
    }

    /**
     * Removes a material from the inventory by its ID.
     *
     * @param id the unique identifier of the material
     * @return an {@link Optional} containing the removed {@link Material} if found; otherwise an empty {@link Optional}
     */
    @Override
    public synchronized Optional<Material> removeMaterial(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        Material removed = materialIndex.remove(id);
        if (removed != null) {
            materials.remove(removed);
            return Optional.of(removed);
        }
        return Optional.empty();
    }

    /**
     * Finds a material by its unique identifier.
     *
     * @param id the material ID
     * @return an {@link Optional} containing the matching material, or empty if not found
     */
    @Override
    public Optional<Material> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(materialIndex.get(id));
    }

    /**
     * Searches for materials whose titles contain the given keyword.
     *
     * @param title the title or keyword to search for
     * @return a list of matching materials; empty list if none found
     */
    @Override
    public List<Material> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            return List.of();
        }

        String keyword = title.trim().toLowerCase();
        return materials.stream()
                .filter(m -> m.getTitle().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Searches for materials created by a specific creator or matching keyword.
     *
     * @param creator the creator name or partial match
     * @return list of materials matching the search criteria
     */
    @Override
    public List<Material> searchByCreator(String creator) {
        if (creator == null || creator.isBlank()) {
            return List.of();
        }

        String keyword = creator.trim().toLowerCase();
        return materials.stream()
                .filter(m -> m.getCreator().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all materials of a specific {@link Material.MaterialType}.
     *
     * @param type the material type
     * @return list of materials matching the given type
     */
    @Override
    public List<Material> getMaterialsByType(Material.MaterialType type) {
        if (type == null) {
            return List.of();
        }

        return materials.stream()
                .filter(m -> m.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all materials that are instances of {@link Media}.
     *
     * @return list of all media materials in the inventory
     */
    @Override
    public List<Media> getMediaMaterials() {
        return materials.stream()
                .filter(Media.class::isInstance)
                .map(Media.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Filters materials according to a provided predicate condition.
     *
     * @param predicate the filtering condition
     * @return list of materials that satisfy the predicate
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    @Override
    public List<Material> filterMaterials(Predicate<Material> predicate) {
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        return materials.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all materials with a price between {@code min} and {@code max}.
     *
     * @param min minimum price (inclusive)
     * @param max maximum price (inclusive)
     * @return list of materials within the price range
     */
    @Override
    public List<Material> getMaterialsByPriceRange(double min, double max) {
        if (min < 0 || max < 0 || min > max) {
            return List.of();
        }

        return materials.stream()
                .filter(m -> m.getPrice() >= min && m.getPrice() <= max)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves materials released in a specific year.
     *
     * @param year the release year
     * @return list of materials released in that year
     */
    @Override
    public List<Material> getMaterialsByYear(int year) {
        return materials.stream()
                .filter(m -> m.getYear() == year)
                .collect(Collectors.toList());
    }

    /**
     * Returns all materials sorted by their natural ordering (via {@link Comparable}).
     *
     * @return sorted list of all materials
     */
    @Override
    public List<Material> getAllMaterialsSorted() {
        List<Material> copy = new ArrayList<>(materials);
        Collections.sort(copy);
        return copy;
    }

    /**
     * Returns a shallow copy of the full inventory.
     *
     * @return list containing all materials
     */
    @Override
    public List<Material> getAllMaterials() {
        return new ArrayList<>(materials);
    }

    /**
     * Calculates the total value of all materials in the inventory.
     *
     * @return the total monetary value
     */
    @Override
    public double getTotalInventoryValue() {
        return materials.stream()
                .mapToDouble(Material::getPrice)
                .sum();
    }

    /**
     * Calculates the total discounted value of all materials after applying active discounts.
     *
     * @return total discounted value
     */
    @Override
    public double getTotalDiscountedValue() {
        return materials.stream()
                .mapToDouble(Material::getDiscountedPrice)
                .sum();
    }

    /**
     * Computes statistical summaries about the current inventory.
     *
     * @return an {@link InventoryStats} object containing statistical values
     */
    @Override
    public InventoryStats getInventoryStats() {
        if (materials.isEmpty()) {
            return new InventoryStats(0, 0, 0, 0, 0, 0);
        }

        List<Double> prices = materials.stream()
                .map(Material::getPrice)
                .sorted()
                .collect(Collectors.toList());

        double avgPrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double median = prices.size() % 2 == 0
                ? (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2
                : prices.get(prices.size() / 2);

        int distinctTypes = (int) materials.stream()
                .map(Material::getType)
                .distinct()
                .count();

        long mediaCount = materials.stream().filter(Media.class::isInstance).count();
        long printedCount = materials.stream()
                .filter(m -> m instanceof PrintedBook || m instanceof Magazine)
                .count();

        return new InventoryStats(
                materials.size(),
                avgPrice,
                median,
                distinctTypes,
                (int) mediaCount,
                (int) printedCount
        );
    }

    /**
     * Clears all materials from the inventory.
     */
    @Override
    public synchronized void clearInventory() {
        materials.clear();
        materialIndex.clear();
    }

    /**
     * Returns the total number of materials currently stored.
     *
     * @return number of materials
     */
    @Override
    public int size() {
        return materials.size();
    }

    /**
     * Checks whether the inventory is currently empty.
     *
     * @return {@code true} if no materials exist; {@code false} otherwise
     */
    @Override
    public boolean isEmpty() {
        return materials.isEmpty();
    }

    /**
     * Retrieves materials released within the past {@code years} years.
     *
     * @param years number of recent years to include
     * @return list of recent materials
     * @throws IllegalArgumentException if {@code years} is negative
     */
    @Override
    public List<Material> findRecentMaterials(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years must be non-negative");
        }

        int currentYear = java.time.Year.now().getValue();
        int cutoff = currentYear - years;

        return materials.stream()
                .filter(m -> m.getYear() >= cutoff)
                .collect(Collectors.toList());
    }

    /**
     * Finds all materials whose creators match any of the provided names.
     *
     * @param creators array of creator names to match
     * @return list of materials created by any of the specified creators
     */
    @Override
    public List<Material> findByCreators(String... creators) {
        if (creators == null || creators.length == 0) {
            return List.of();
        }

        Set<String> creatorSet = Arrays.stream(creators)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        if (creatorSet.isEmpty()) {
            return List.of();
        }

        return materials.stream()
                .filter(m -> creatorSet.contains(m.getCreator()))
                .collect(Collectors.toList());
    }

    /**
     * Filters materials using a provided predicate (functionally identical to {@link #filterMaterials(Predicate)}).
     *
     * @param condition the filtering condition
     * @return list of materials satisfying the given predicate
     * @throws NullPointerException if {@code condition} is {@code null}
     */
    @Override
    public List<Material> findWithPredicate(Predicate<Material> condition) {
        Objects.requireNonNull(condition, "Predicate cannot be null");
        return materials.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    /**
     * Returns all materials sorted according to a custom comparator.
     *
     * @param comparator the sorting logic
     * @return sorted list of materials
     * @throws NullPointerException if {@code comparator} is {@code null}
     */
    @Override
    public List<Material> getSorted(Comparator<Material> comparator) {
        Objects.requireNonNull(comparator, "Comparator cannot be null");
        return materials.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves formatted display information for all stored materials.
     *
     * @return list of formatted strings from {@link Material#getDisplayInfo()}
     */
    public List<String> getAllDisplayInfo() {
        return materials.stream()
                .map(Material::getDisplayInfo)
                .collect(Collectors.toList());
    }

    /**
     * Groups materials in the inventory by their {@link Material.MaterialType}.
     *
     * @return map of material types to their corresponding lists of materials
     */
    public Map<Material.MaterialType, List<Material>> groupByType() {
        return materials.stream()
                .collect(Collectors.groupingBy(Material::getType));
    }

    /**
     * Retrieves all materials that currently have an active discount applied.
     *
     * @return list of discounted materials
     */
    public List<Material> getDiscountedMaterials() {
        return materials.stream()
                .filter(m -> m.getDiscountRate() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the total amount saved across all discounted materials.
     *
     * @return total discount amount in currency
     */
    public double getTotalDiscountAmount() {
        return materials.stream()
                .mapToDouble(m -> m.getPrice() * m.getDiscountRate())
                .sum();
    }

    /**
     * Returns a formatted summary of the store, including inventory size, type count, and total value.
     *
     * @return formatted summary string
     */
    @Override
    public String toString() {
        return String.format(
                "MaterialStoreImpl[Count=%d, Types=%d, TotalValue=$%.2f]",
                size(),
                groupByType().size(),
                getTotalInventoryValue()
        );
    }
}
