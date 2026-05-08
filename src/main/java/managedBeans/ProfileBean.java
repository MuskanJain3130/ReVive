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
}
