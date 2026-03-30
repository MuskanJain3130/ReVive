package managedBeans;

import client.UserClient;
import entities.Products;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import jakarta.ws.rs.core.GenericType;
import org.primefaces.model.file.UploadedFile;

@Named("productBean")
@RequestScoped
public class ProductManagedBean implements Serializable {

    @Inject
    private AuthManagedBean authBean;

    private UserClient userClient;
    private Products newProduct = new Products();
    private List<Products> myProducts = new ArrayList<>();
    private List<Products> approvedProducts = new ArrayList<>();
    private UploadedFile file;

    @PostConstruct
    public void init() {
        userClient = new UserClient();
        loadProducts();
    }

    public void loadProducts() {
        try {
            approvedProducts = userClient.getAllApprovedProducts(new GenericType<List<Products>>() {});
        } catch (Exception e) {
            approvedProducts = new ArrayList<>();
        }

        if (authBean.isLoggedIn() && authBean.getCurrentUser() != null) {
            try {
                myProducts = userClient.myProducts(new GenericType<List<Products>>() {}, String.valueOf(authBean.getCurrentUser().getUserid()));
            } catch (Exception e) {
                myProducts = new ArrayList<>();
            }
        }
    }

    public String addProduct() {
        if (authBean.isLoggedIn() && authBean.getCurrentUser() != null) {
            
            // Handle File Upload
            if (file != null && file.getFileName() != null && !file.getFileName().isEmpty()) {
                try {
                    String extension = file.getFileName().substring(file.getFileName().lastIndexOf("."));
                    String newFileName = UUID.randomUUID().toString() + extension;
                    
                    // Path to project webapp/images folder
                    String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
                    File folder = new File(path);
                    if (!folder.exists()) folder.mkdirs();
                    
                    File savedFile = new File(folder, newFileName);
                    try (InputStream input = file.getInputStream();
                         FileOutputStream output = new FileOutputStream(savedFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = input.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                    }
                    newProduct.setImageUrl(newFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            userClient.addProduct(newProduct, String.valueOf(authBean.getCurrentUser().getUserid()));
            newProduct = new Products(); // Reset
            return "seller?faces-redirect=true";
        }
        return null;
    }

    // Getters and Setters
    public Products getNewProduct() { return newProduct; }
    public void setNewProduct(Products newProduct) { this.newProduct = newProduct; }
    public List<Products> getMyProducts() { return myProducts; }
    public List<Products> getApprovedProducts() { return approvedProducts; }
    public UploadedFile getFile() { return file; }
    public void setFile(UploadedFile file) { this.file = file; }
}
