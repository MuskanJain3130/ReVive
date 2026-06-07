package managedBeans;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import ejb.UserBeanLocal;
import entities.Addresses;
import entities.OrderDetails;
import entities.Orders;
import entities.Payments;
import entities.Products;
import entities.Users;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.json.JSONObject;

@Named("checkoutBean")
@ViewScoped
public class CheckoutBean implements Serializable {

    @EJB
    private UserBeanLocal userBean;

    @Inject
    private AuthManagedBean authBean;

    private String cartJson;
    private List<CartItem> parsedCartItems = new ArrayList<>();
    private double totalAmount = 0;

    private List<Addresses> userAddresses = new ArrayList<>();
    private Integer selectedAddressId;
    
    // New Address Form
    private Addresses newAddress = new Addresses();
    private boolean showNewAddressForm = false;

    // Razorpay Integration
    private String razorpayOrderId;
    private String razorpayKeyId = util.SmsService.getEnv("RAZORPAY_KEY_ID");
    private String razorpayKeySecret = util.SmsService.getEnv("RAZORPAY_KEY_SECRET");
    
    private String paymentMethod = "online"; // "online" or "cod"

    @PostConstruct
    public void init() {
        if (authBean.isLoggedIn()) {
            loadAddresses();
        }
    }

    public void loadAddresses() {
        userAddresses = userBean.getAddressesForUser(authBean.getCurrentUser().getUserid());
        if (!userAddresses.isEmpty()) {
            selectedAddressId = userAddresses.get(0).getAddressid();
        } else {
            showNewAddressForm = true;
        }
    }

    public void parseCart() {
        if (cartJson == null || cartJson.trim().isEmpty() || "[]".equals(cartJson)) {
            System.out.println("[ReVive Checkout] Cart is empty.");
            return;
        }

        try (JsonReader reader = Json.createReader(new StringReader(cartJson))) {
            JsonArray jsonArray = reader.readArray();
            parsedCartItems.clear();
            totalAmount = 0;

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.getJsonObject(i);
                CartItem item = new CartItem();
                item.setId(obj.getInt("id", 0)); // Note: Depending on frontend cart storage, product ID may be stored differently. 
                                                 // Ensure frontend cart stores `productid`!
                
                // Workaround: if 'productid' is in the JSON
                if (obj.containsKey("productid")) {
                    item.setId(obj.getInt("productid"));
                }
                
                item.setTitle(obj.getString("title", "Unknown"));
                item.setPrice(obj.getJsonNumber("price").doubleValue());
                item.setQuantity(obj.getInt("quantity", 1));
                
                parsedCartItems.add(item);
                totalAmount += (item.getPrice() * item.getQuantity());
            }
        } catch (Exception e) {
            System.err.println("[ReVive Checkout] Error parsing cart: " + e.getMessage());
        }
    }

    public void saveNewAddress() {
        try {
            if (newAddress.getIsDefault() == null) {
                newAddress.setIsDefault(false);
            }
            userBean.addAddress(newAddress, authBean.getCurrentUser().getUserid());
            
            // Reload and select the new address
            loadAddresses();
            selectedAddressId = newAddress.getAddressid();
            
            // Reset form
            newAddress = new Addresses();
            showNewAddressForm = false;
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Address added securely."));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to add address."));
        }
    }

    public void initiateRazorpayOrder() {
        try {
            // Razorpay uses subunit (paise/cents), so multiply by 100
            int amountInSubunits = (int) (totalAmount * 100);

            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInSubunits);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
            
            Order rzpOrder = razorpay.orders.create(orderRequest);
            this.razorpayOrderId = rzpOrder.get("id");
            
            // Note: In JSF, you'd typically invoke a JS script here to open the Razorpay modal
            // But we will use a commandButton to trigger the JS directly from the view.
            
        } catch (RazorpayException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Payment Error", "Failed to connect to Razorpay. Check API Keys."));
            System.err.println("Razorpay Error: " + e.getMessage());
        }
    }

    // Called from JS after Razorpay Success OR directly for COD
    public String completeOrder(String paymentId) {
        try {
            // Safety check: ensure cart is parsed
            if (parsedCartItems == null || parsedCartItems.isEmpty()) {
                System.out.println("[ReVive] parsedCartItems empty, attempting re-parse. JSON: " + cartJson);
                parseCart();
            }

            if (parsedCartItems.isEmpty()) {
                throw new Exception("Cart is still empty after re-parse. Cannot complete order.");
            }

            if (selectedAddressId == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please select an address."));
                return null;
            }

            Orders newOrder = new Orders();
            newOrder.setOrderdate(new Date());
            newOrder.setStatus("Placed");
            newOrder.setBuyerid(authBean.getCurrentUser());
            // UserBean will find the managed address
            Addresses addrPlaceholder = new Addresses();
            addrPlaceholder.setAddressid(selectedAddressId);
            newOrder.setAddressid(addrPlaceholder);

            List<OrderDetails> detailsList = new ArrayList<>();
            for (CartItem ci : parsedCartItems) {
                OrderDetails od = new OrderDetails();
                // UserBean will find the managed product
                Products prodPlaceholder = new Products();
                prodPlaceholder.setProductid(ci.getId());
                od.setProductid(prodPlaceholder);
                
                od.setQuantity(ci.getQuantity());
                od.setPrice(ci.getPrice());
                detailsList.add(od);
            }

            Payments payment = new Payments();
            payment.setAmount(totalAmount);
            payment.setPaymentDate(new Date());
            
            if ("online".equals(paymentMethod)) {
                payment.setPaymentMode("Razorpay");
                payment.setPaymentStatus("Paid");
            } else {
                payment.setPaymentMode("COD");
                payment.setPaymentStatus("Pending");
            }

            userBean.processCheckout(newOrder, detailsList, payment);

            // Send confirmation email asynchronously
            if (authBean.getCurrentUser() != null && authBean.getCurrentUser().getEmail() != null) {
                sendEmailNotification(authBean.getCurrentUser().getEmail(), String.valueOf(newOrder.getOrderid()), totalAmount);
            }

            // Send confirmation SMS
            if (authBean.getCurrentUser() != null && authBean.getCurrentUser().getPhone() != null && !authBean.getCurrentUser().getPhone().trim().isEmpty()) {
                sendSmsNotification(authBean.getCurrentUser().getPhone(), String.valueOf(newOrder.getOrderid()), totalAmount);
            }

            // Clear the local cart via JS on the next page
            return "success?faces-redirect=true";

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Checkout Failed", e.getMessage()));
            return null;
        }
    }

    private void sendEmailNotification(String toEmail, String orderId, double totalAmount) {
        System.out.println("[ReVive Mail] Sending order confirmation email to: " + toEmail);
        try {
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", "localhost");
            props.put("mail.smtp.port", "25");
            
            jakarta.mail.Session mailSession = jakarta.mail.Session.getInstance(props, null);
            jakarta.mail.internet.MimeMessage message = new jakarta.mail.internet.MimeMessage(mailSession);
            message.setFrom(new jakarta.mail.internet.InternetAddress("noreply@revive.com", "ReVive Marketplace"));
            message.addRecipient(jakarta.mail.Message.RecipientType.TO, new jakarta.mail.internet.InternetAddress(toEmail));
            message.setSubject("Order Confirmation - ReVive", "UTF-8");
            
            String htmlContent = "<h3>Dear Customer,</h3>" +
                    "<p>Thank you for shopping with ReVive Marketplace!</p>" +
                    "<p>Your order <strong>#" + orderId + "</strong> has been successfully placed via <strong>Cash on Delivery (COD)</strong>.</p>" +
                    "<p><strong>Total Amount to be Paid:</strong> INR " + totalAmount + "</p>" +
                    "<p>We will pack and ship your items shortly.</p>" +
                    "<br/>" +
                    "<p>Best regards,<br/>ReVive Team</p>";
            
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Print beautiful simulated email box to the console for testing
            System.out.println("\n+-----------------------------------------------------------------------+");
            System.out.println("|                     [SIMULATED EMAIL NOTIFICATION]                     |");
            System.out.println("+-----------------------------------------------------------------------+");
            System.out.println("| From:    noreply@revive.com (ReVive Marketplace)                      |");
            System.out.println("| To:      " + String.format("%-60s", toEmail) + " |");
            System.out.println("| Subject: Order Confirmation - ReVive                                  |");
            System.out.println("+-----------------------------------------------------------------------+");
            System.out.println("| Body (HTML):                                                          |");
            for (String line : htmlContent.split("<br/>|</p>|<h3>|</h3>")) {
                String cleanLine = line.replaceAll("<[^>]*>", "").trim();
                if (!cleanLine.isEmpty()) {
                    System.out.println("|   " + String.format("%-67s", cleanLine) + " |");
                }
            }
            System.out.println("+-----------------------------------------------------------------------+\n");

            new Thread(() -> {
                try {
                    jakarta.mail.Transport.send(message);
                    System.out.println("[ReVive Mail] Email sent successfully to " + toEmail);
                } catch (Exception e) {
                    System.out.println("[ReVive Mail] SMTP delivery failed (this is expected if local SMTP server is not running). Details: " + e.getMessage());
                }
            }).start();
            
        } catch (Exception e) {
            System.out.println("[ReVive Mail] Error creating email: " + e.getMessage());
        }
    }

    private void sendSmsNotification(String phoneNumber, String orderId, double totalAmount) {
        StringBuilder invoice = new StringBuilder();
        invoice.append("🛍️ *ReVive Marketplace* 🛍️\n");
        invoice.append("*Invoice for Order #").append(orderId).append("*\n\n");

        String customerName = authBean.getCurrentUser().getUsername();
        Addresses selectedAddress = null;
        if (userAddresses != null) {
            for (Addresses a : userAddresses) {
                if (a.getAddressid().equals(selectedAddressId)) {
                    selectedAddress = a;
                    break;
                }
            }
        }

        invoice.append("👤 *Customer:* ").append(customerName).append("\n");
        if (selectedAddress != null) {
            invoice.append("📍 *Delivery Address:* ").append(selectedAddress.getStreet())
                   .append(", ").append(selectedAddress.getCity())
                   .append(" - ").append(selectedAddress.getZipcode()).append("\n\n");
        } else {
            invoice.append("\n");
        }

        invoice.append("📦 *Order Details:*\n");
        if (parsedCartItems != null) {
            for (CartItem ci : parsedCartItems) {
                invoice.append("- ").append(ci.getQuantity()).append("x ").append(ci.getTitle())
                       .append(" (INR ").append(ci.getPrice()).append(")\n");
            }
        }

        String paymentStr = "online".equals(paymentMethod) ? "Razorpay (Paid)" : "Cash on Delivery (COD)";
        invoice.append("\n💳 *Payment Mode:* ").append(paymentStr).append("\n");
        invoice.append("💰 *Total Amount:* *INR ").append(totalAmount).append("*\n\n");
        invoice.append("Thank you for shopping sustainably with ReVive! ♻️");

        util.SmsService.sendWhatsApp(phoneNumber, invoice.toString());
    }

    public void toggleAddressForm() {
        this.showNewAddressForm = !this.showNewAddressForm;
    }

    // Getters and Setters
    public String getCartJson() { return cartJson; }
    public void setCartJson(String cartJson) { this.cartJson = cartJson; }
    public List<CartItem> getParsedCartItems() { return parsedCartItems; }
    public double getTotalAmount() { return totalAmount; }
    public List<Addresses> getUserAddresses() { return userAddresses; }
    public Integer getSelectedAddressId() { return selectedAddressId; }
    public void setSelectedAddressId(Integer selectedAddressId) { this.selectedAddressId = selectedAddressId; }
    public Addresses getNewAddress() { return newAddress; }
    public void setNewAddress(Addresses newAddress) { this.newAddress = newAddress; }
    public boolean isShowNewAddressForm() { return showNewAddressForm; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public String getRazorpayKeyId() { return razorpayKeyId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // Inner class to represent parsed cart items easily
    public static class CartItem {
        private int id;
        private String title;
        private double price;
        private int quantity;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
