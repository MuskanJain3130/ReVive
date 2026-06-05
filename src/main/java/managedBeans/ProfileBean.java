package managedBeans;

import ejb.UserBeanLocal;
import entities.OrderDetails;
import entities.Orders;
import entities.Users;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("profileBean")
@ViewScoped
public class ProfileBean implements Serializable {

    @EJB
    private UserBeanLocal userBean;

    @Inject
    private AuthManagedBean authBean;

    private Users userProfile;
    private List<Orders> myOrders;
    private List<OrderDetails> mySales;

    @PostConstruct
    public void init() {
        if (authBean.isLoggedIn()) {
            userProfile = authBean.getCurrentUser();
            loadActivity();
        }
    }

    public void loadActivity() {
        if (userProfile != null) {
            myOrders = userBean.getOrdersForBuyer(userProfile.getUserid());
            if (authBean.isSeller()) {
                mySales = userBean.getSalesForSeller(userProfile.getUserid());
            }
        }
    }

    public void updateProfile() {
        try {
            userBean.updateUserInfo(userProfile);
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Profile updated successfully."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to update profile."));
        }
    }

    public List<Orders> getMyOrders() { return myOrders; }
    public List<OrderDetails> getMySales() { return mySales; }
    public Users getUserProfile() { return userProfile; }
    public void setUserProfile(Users userProfile) { this.userProfile = userProfile; }
    
    private Orders selectedOrder;
    public Orders getSelectedOrder() { return selectedOrder; }
    public void setSelectedOrder(Orders selectedOrder) { this.selectedOrder = selectedOrder; }
    
    private OrderDetails selectedDetail;
    private String returnReason;

    public void selectDetailForReturn(OrderDetails detail) {
        this.selectedDetail = detail;
        this.returnReason = "";
    }

    public void submitReturnRequest() {
        if (selectedDetail == null) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No product selected for return."));
            return;
        }
        if (returnReason == null || returnReason.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please provide a reason for return."));
            return;
        }
        try {
            entities.ReturnRequests rr = new entities.ReturnRequests();
            rr.setOrderdetailid(selectedDetail);
            rr.setReason(returnReason);
            rr.setStatus("Requested");
            rr.setRequestedAt(new java.util.Date());
            
            userBean.requestReturn(rr);
            
            // Reload activities
            loadActivity();
            
            // Re-sync selectedOrder if present to update the details modal immediately
            if (selectedOrder != null) {
                for (Orders o : myOrders) {
                    if (o.getOrderid().equals(selectedOrder.getOrderid())) {
                        selectedOrder = o;
                        break;
                    }
                }
            }
            
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Return request submitted successfully."));
            
            org.primefaces.PrimeFaces.current().executeScript("PF('returnDialog').hide();");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, 
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to submit return request: " + e.getMessage()));
        }
    }

    public OrderDetails getSelectedDetail() { return selectedDetail; }
    public void setSelectedDetail(OrderDetails selectedDetail) { this.selectedDetail = selectedDetail; }
    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String returnReason) { this.returnReason = returnReason; }
}
