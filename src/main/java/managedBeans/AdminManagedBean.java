package managedBeans;

import client.AdminClient;
import client.UserClient;
import entities.Products;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Named("adminBean")
@SessionScoped   // was @RequestScoped — selectedProduct was lost on every AJAX round-trip
public class AdminManagedBean implements Serializable {

    private AdminClient adminClient;
    private UserClient userClient;
    private List<Products> allProducts = new ArrayList<>();
    private List<Products> filteredProducts;
    private Products selectedProduct;

    @PostConstruct
    public void init() {
        adminClient = new AdminClient();
        userClient = new UserClient();
        loadAllProducts();
    }

    public void loadAllProducts() {
        try {
            allProducts = userClient.getAllProducts(new GenericType<List<Products>>() {});
        } catch (Exception e) {
            allProducts = new ArrayList<>();
        }
    }

    /** Called by Approve button (AJAX) — updates list in-place */
    public void approve(int productId) {
        try {
            adminClient.approveProduct(String.valueOf(productId));
            // If the dialog's product is the one we just acted on, refresh it
            if (selectedProduct != null && selectedProduct.getProductid() == productId) {
                selectedProduct.setApprovalStatus("Approved");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loadAllProducts();
        }
    }

    /** Called by Reject button (AJAX) — updates list in-place */
    public void reject(int productId) {
        try {
            adminClient.rejectProduct(String.valueOf(productId));
            if (selectedProduct != null && selectedProduct.getProductid() == productId) {
                selectedProduct.setApprovalStatus("Rejected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loadAllProducts();
        }
    }

    /** Opens the detail dialog */
    public void selectProduct(Products product) {
        this.selectedProduct = product;
    }

    /** Returns the list of image filenames split on semicolon */
    public List<String> getImageList() {
        if (selectedProduct == null || selectedProduct.getImageUrl() == null
                || selectedProduct.getImageUrl().isBlank()
                || selectedProduct.getImageUrl().equals("placeholder.png")) {
            return new ArrayList<>();
        }
        return Arrays.asList(selectedProduct.getImageUrl().split(";"));
    }

    // Getters / setters
    public List<Products> getAllProducts() { return allProducts; }
    public List<Products> getFilteredProducts() { return filteredProducts; }
    public void setFilteredProducts(List<Products> filteredProducts) { this.filteredProducts = filteredProducts; }
    public Products getSelectedProduct() { return selectedProduct; }
    public void setSelectedProduct(Products selectedProduct) { this.selectedProduct = selectedProduct; }
}
