package managedBeans;

import client.AdminClient;
import client.UserClient;
import entities.Products;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
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

    @EJB
    private ejb.AdminBeanLocal adminEJB;

    private AdminClient adminClient;
    private UserClient userClient;
    private List<Products> allProducts = new ArrayList<>();
    private List<Products> filteredProducts;
    private Products selectedProduct;

    private List<entities.ReturnRequests> returnRequests = new ArrayList<>();
    private List<entities.Refunds> refunds = new ArrayList<>();
    private List<entities.Orders> allOrders = new ArrayList<>();
    
    private List<entities.ReturnRequests> filteredReturnRequests;
    private List<entities.Refunds> filteredRefunds;
    private List<entities.Orders> filteredOrders;

    @PostConstruct
    public void init() {
        adminClient = new AdminClient();
        userClient = new UserClient();
        loadAllProducts();
        loadReturnsAndRefunds();
    }

    public void loadAllProducts() {
        try {
            allProducts = userClient.getAllProducts(new GenericType<List<Products>>() {});
        } catch (Exception e) {
            allProducts = new ArrayList<>();
        }
    }

    public void loadReturnsAndRefunds() {
        try {
            returnRequests = adminEJB.getAllReturnRequests();
        } catch (Exception e) {
            e.printStackTrace();
            returnRequests = new ArrayList<>();
        }
        try {
            refunds = adminEJB.getAllRefunds();
        } catch (Exception e) {
            e.printStackTrace();
            refunds = new ArrayList<>();
        }
        try {
            allOrders = adminEJB.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            allOrders = new ArrayList<>();
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

    public void approveReturn(int returnId) {
        try {
            adminEJB.handleReturn(returnId, true);
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO, "Success", "Return request approved."));
        } catch (Exception e) {
            e.printStackTrace();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "Failed to approve return: " + e.getMessage()));
        } finally {
            loadReturnsAndRefunds();
        }
    }

    public void rejectReturn(int returnId) {
        try {
            adminEJB.handleReturn(returnId, false);
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO, "Success", "Return request rejected."));
        } catch (Exception e) {
            e.printStackTrace();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "Failed to reject return: " + e.getMessage()));
        } finally {
            loadReturnsAndRefunds();
        }
    }

    public void processRefund(int refundId) {
        try {
            adminEJB.processRefunds(refundId);
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO, "Success", "Refund marked as processed successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "Failed to process refund: " + e.getMessage()));
        } finally {
            loadReturnsAndRefunds();
        }
    }

    public void updateOrderStatus(int orderId, String status) {
        try {
            adminEJB.updateOrderStatus(orderId, status);
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_INFO, "Success", "Order status updated successfully."));
        } catch (Exception e) {
            e.printStackTrace();
            jakarta.faces.context.FacesContext.getCurrentInstance().addMessage(null,
                new jakarta.faces.application.FacesMessage(jakarta.faces.application.FacesMessage.SEVERITY_ERROR, "Error", "Failed to update order status: " + e.getMessage()));
        } finally {
            loadReturnsAndRefunds();
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

    // Statistics getters
    public long getTotalProductsCount() {
        return allProducts != null ? allProducts.size() : 0;
    }

    public long getApprovedProductsCount() {
        return allProducts != null ? allProducts.stream().filter(p -> "Approved".equals(p.getApprovalStatus())).count() : 0;
    }

    public long getPendingProductsCount() {
        return allProducts != null ? allProducts.stream().filter(p -> "Pending".equals(p.getApprovalStatus())).count() : 0;
    }

    public long getRejectedProductsCount() {
        return allProducts != null ? allProducts.stream().filter(p -> "Rejected".equals(p.getApprovalStatus())).count() : 0;
    }

    // Getters / setters
    public List<Products> getAllProducts() { return allProducts; }
    public List<Products> getFilteredProducts() { return filteredProducts; }
    public void setFilteredProducts(List<Products> filteredProducts) { this.filteredProducts = filteredProducts; }
    public Products getSelectedProduct() { return selectedProduct; }
    public void setSelectedProduct(Products selectedProduct) { this.selectedProduct = selectedProduct; }

    public List<entities.ReturnRequests> getReturnRequests() { return returnRequests; }
    public void setReturnRequests(List<entities.ReturnRequests> returnRequests) { this.returnRequests = returnRequests; }

    public List<entities.Refunds> getRefunds() { return refunds; }
    public void setRefunds(List<entities.Refunds> refunds) { this.refunds = refunds; }

    public List<entities.Orders> getAllOrders() { return allOrders; }
    public void setAllOrders(List<entities.Orders> allOrders) { this.allOrders = allOrders; }

    public List<entities.ReturnRequests> getFilteredReturnRequests() { return filteredReturnRequests; }
    public void setFilteredReturnRequests(List<entities.ReturnRequests> filteredReturnRequests) { this.filteredReturnRequests = filteredReturnRequests; }

    public List<entities.Refunds> getFilteredRefunds() { return filteredRefunds; }
    public void setFilteredRefunds(List<entities.Refunds> filteredRefunds) { this.filteredRefunds = filteredRefunds; }

    public List<entities.Orders> getFilteredOrders() { return filteredOrders; }
    public void setFilteredOrders(List<entities.Orders> filteredOrders) { this.filteredOrders = filteredOrders; }
}
