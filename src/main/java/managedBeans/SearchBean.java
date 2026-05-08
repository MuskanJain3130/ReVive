package managedBeans;

import client.UserClient;
import entities.Products;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Named("searchBean")
@ViewScoped
public class SearchBean implements Serializable {

    private UserClient userClient;
    private List<Products> allProducts = new ArrayList<>();
    private List<Products> filteredProducts = new ArrayList<>();

    // Filter properties
    private String query;
    private Double minPrice;
    private Double maxPrice;
    private List<String> selectedConditions;
    private String sortOption;

    @PostConstruct
    public void init() {
        userClient = new UserClient();
        loadAllProducts();
    }

    private void loadAllProducts() {
        try {
            allProducts = userClient.getAllApprovedProducts(new GenericType<List<Products>>() {});
        } catch (Exception e) {
            allProducts = new ArrayList<>();
            System.err.println("[ReVive] Error loading products in SearchBean: " + e.getMessage());
        }
    }

    // Called by viewAction on load, and by AJAX when filters change
    public void applyFilters() {
        if (allProducts == null) {
            filteredProducts = new ArrayList<>();
            return;
        }

        // 1. Filter by Query
        List<Products> stream = new ArrayList<>(allProducts);
        
        if (query != null && !query.trim().isEmpty()) {
            String q = query.toLowerCase().trim();
            stream = stream.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(q) || 
                                 (p.getDescription() != null && p.getDescription().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        // 2. Filter by Price
        if (minPrice != null) {
            stream = stream.stream().filter(p -> p.getPrice() >= minPrice).collect(Collectors.toList());
        }
        if (maxPrice != null && maxPrice > 0) {
            stream = stream.stream().filter(p -> p.getPrice() <= maxPrice).collect(Collectors.toList());
        }

        // 3. Filter by Condition
        if (selectedConditions != null && !selectedConditions.isEmpty()) {
            stream = stream.stream()
                    .filter(p -> selectedConditions.contains(p.getProductCondition()))
                    .collect(Collectors.toList());
        }

        // 4. Sort
        if (sortOption != null && !sortOption.trim().isEmpty()) {
            switch (sortOption) {
                case "priceAsc":
                    stream.sort(Comparator.comparing(Products::getPrice));
                    break;
                case "priceDesc":
                    stream.sort(Comparator.comparing(Products::getPrice).reversed());
                    break;
                case "newest":
                    stream.sort((p1, p2) -> {
                        if (p1.getCreatedAt() == null) return 1;
                        if (p2.getCreatedAt() == null) return -1;
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt()); // Descending
                    });
                    break;
                default:
                    // Relevance / Default order
                    break;
            }
        }

        filteredProducts = stream;
    }
    
    // Clear all filters except the query
    public void clearFilters() {
        minPrice = null;
        maxPrice = null;
        if (selectedConditions != null) selectedConditions.clear();
        sortOption = "relevance";
        applyFilters();
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    public List<String> getSelectedConditions() { return selectedConditions; }
    public void setSelectedConditions(List<String> selectedConditions) { this.selectedConditions = selectedConditions; }
    public String getSortOption() { return sortOption; }
    public void setSortOption(String sortOption) { this.sortOption = sortOption; }
    public List<Products> getFilteredProducts() { return filteredProducts; }
}
