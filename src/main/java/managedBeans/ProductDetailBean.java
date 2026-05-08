package managedBeans;

import client.UserClient;
import entities.OrderDetails;
import entities.Products;
import entities.Reviews;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Named("productDetailBean")
@ViewScoped
public class ProductDetailBean implements Serializable {

    @Inject
    private AuthManagedBean authBean;

    private UserClient userClient;
    private int productId;
    private Products product;
    private List<Reviews> reviews = new ArrayList<>();
    private OrderDetails userOrderDetail;
    private Reviews newReview = new Reviews();
    
    // Extracted images for the carousel
    private List<String> images = new ArrayList<>();

    @PostConstruct
    public void init() {
        userClient = new UserClient();
    }

    public void loadProduct() {
        if (productId <= 0) {
            return;
        }
        try {
            product = userClient.getProductById(Products.class, String.valueOf(productId));
            
            // Extract images
            images.clear();
            if (product != null && product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                String[] split = product.getImageUrl().split(";");
                for (String s : split) {
                    if (!s.trim().isEmpty()) {
                        images.add(s.trim());
                    }
                }
            }
            if (images.isEmpty()) {
                images.add("placeholder.png");
            }
            
            // Fetch reviews
            reviews = userClient.getReviewsForProduct(new GenericType<List<Reviews>>() {}, String.valueOf(productId));
            
            // Check if user bought it (for "Write a Review" form)
            if (authBean.isLoggedIn() && authBean.getCurrentUser() != null) {
                userOrderDetail = userClient.getOrderDetailsForUserProduct(OrderDetails.class, 
                        String.valueOf(authBean.getCurrentUser().getUserid()), 
                        String.valueOf(productId));
            }
            
        } catch (Exception e) {
            System.err.println("[ReVive] Error loading product details: " + e.getMessage());
            product = null;
        }
    }

    public void submitReview() {
        if (userOrderDetail == null || !authBean.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must have purchased this product to review it."));
            return;
        }
        try {
            userClient.addReview(newReview, String.valueOf(userOrderDetail.getOrderdetailid()), String.valueOf(authBean.getCurrentUser().getUserid()));
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Review added successfully!"));
            
            // Reset and reload
            newReview = new Reviews();
            reviews = userClient.getReviewsForProduct(new GenericType<List<Reviews>>() {}, String.valueOf(productId));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add review."));
        }
    }
    
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }
    
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        double sum = 0;
        for (Reviews r : reviews) {
            sum += r.getRating();
        }
        return sum / reviews.size();
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public Products getProduct() { return product; }
    public List<Reviews> getReviews() { return reviews; }
    public OrderDetails getUserOrderDetail() { return userOrderDetail; }
    public Reviews getNewReview() { return newReview; }
    public void setNewReview(Reviews newReview) { this.newReview = newReview; }
    public List<String> getImages() { return images; }
    
    public boolean isCanReview() {
        return userOrderDetail != null;
    }
}
