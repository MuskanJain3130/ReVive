package managedBeans;

import client.AdminClient;
import client.UserClient;
import entities.Products;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Named("adminBean")
@RequestScoped
public class AdminManagedBean implements Serializable {

    private AdminClient adminClient;
    private UserClient userClient;
    private List<Products> allProducts = new ArrayList<>();
    private List<Products> filteredProducts;

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

    public String approve(int productId) {
        try {
            adminClient.approveProduct(String.valueOf(productId));
            return "admin?faces-redirect=true";
        } catch (Exception e) {
            return null;
        }
    }

    public String reject(int productId) {
        try {
            adminClient.rejectProduct(String.valueOf(productId));
            return "admin?faces-redirect=true";
        } catch (Exception e) {
            return null;
        }
    }

    public List<Products> getAllProducts() {
        return allProducts;
    }

    public List<Products> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<Products> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }
}
