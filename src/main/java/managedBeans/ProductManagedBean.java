package managedBeans;

import client.UserClient;
import entities.Products;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.ws.rs.core.GenericType;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

@Named("productBean")
@ViewScoped
public class ProductManagedBean implements Serializable {

    @Inject
    private AuthManagedBean authBean;

    private UserClient userClient;
    private Products newProduct = new Products();
    private List<Products> myProducts = new ArrayList<>();
    private List<Products> approvedProducts = new ArrayList<>();
    private List<Products> filteredProducts = new ArrayList<>();
    private String searchKeyword;
    private UploadedFiles files;  // multiple file upload

    @PostConstruct
    public void init() {
        userClient = new UserClient();
        loadProducts();
    }

    public void loadProducts() {
        try {
            approvedProducts = userClient.getAllApprovedProducts(new GenericType<List<Products>>() {});
            filterProducts();
        } catch (Exception e) {
            approvedProducts = new ArrayList<>();
            filteredProducts = new ArrayList<>();
        }

        if (authBean.isLoggedIn() && authBean.getCurrentUser() != null) {
            try {
                myProducts = userClient.myProducts(new GenericType<List<Products>>() {}, String.valueOf(authBean.getCurrentUser().getUserid()));
            } catch (Exception e) {
                myProducts = new ArrayList<>();
            }
        }
    }

    public List<String> completeSearch(String query) {
        String q = query.toLowerCase();
        return approvedProducts.stream()
                .filter(p -> p.getTitle().toLowerCase().contains(q))
                .map(Products::getTitle)
                .distinct()
                .collect(Collectors.toList());
    }

    public void filterProducts() {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            filteredProducts = approvedProducts.stream()
                    .filter(p -> p.getQuantity() != null && p.getQuantity() > 0)
                    .collect(Collectors.toList());
        } else {
            String q = searchKeyword.toLowerCase().trim();
            filteredProducts = approvedProducts.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(q) && p.getQuantity() != null && p.getQuantity() > 0)
                    .collect(Collectors.toList());
        }
    }

    public String addProduct() {
        if (authBean.isLoggedIn() && authBean.getCurrentUser() != null) {

            System.out.println("[ReVive] addProduct() called by user: " + authBean.getCurrentUser().getUsername());
            System.out.println("[ReVive] files bean = " + files);
            System.out.println("[ReVive] files list = " + (files != null ? files.getFiles() : "null"));

            // Handle multiple file uploads — join with semicolon, max 10
            if (files != null && files.getFiles() != null && !files.getFiles().isEmpty()) {
                System.out.println("[ReVive] Number of files received: " + files.getFiles().size());
                List<String> savedNames = new ArrayList<>();
                int count = 0;
                for (UploadedFile file : files.getFiles()) {
                    if (count >= 10) break;
                    if (file == null || file.getFileName() == null || file.getFileName().isEmpty()) {
                        System.out.println("[ReVive] Skipping null/empty file at index " + count);
                        continue;
                    }
                    System.out.println("[ReVive] Processing file: " + file.getFileName() + " size=" + file.getSize());
                    try {
                        String ext = file.getFileName().substring(file.getFileName().lastIndexOf("."));
                        String newFileName = UUID.randomUUID().toString() + ext;

                        // Resolve the deployed web application's images path dynamically
                        String path = jakarta.faces.context.FacesContext.getCurrentInstance()
                                .getExternalContext()
                                .getRealPath("/images");
                        System.out.println("[ReVive] Dynamic real path for /images: " + path);

                        if (path == null) {
                            // Fallback to hardcoded workspace path if real path is not available
                            path = "d:\\Code\\ReVive_Java_Project\\ReVive\\src\\main\\webapp\\images";
                            System.out.println("[ReVive] Real path is null, falling back to: " + path);
                        }

                        File folder = new File(path);
                        if (!folder.exists()) {
                            boolean created = folder.mkdirs();
                            System.out.println("[ReVive] Created folder: " + folder.getAbsolutePath() + " -> " + created);
                        }

                        File savedFile = new File(folder, newFileName);
                        try (InputStream input = file.getInputStream();
                             FileOutputStream output = new FileOutputStream(savedFile)) {
                            byte[] buffer = new byte[8192];
                            int length;
                            while ((length = input.read(buffer)) > 0) {
                                output.write(buffer, 0, length);
                            }
                        }
                        System.out.println("[ReVive] Saved file to deployed dir: " + savedFile.getAbsolutePath() + " exists=" + savedFile.exists() + " size=" + savedFile.length());

                        // Try to duplicate the file in the workspace source directory so it persists across cleans/rebuilds
                        try {
                            File realPathDir = new File(path);
                            File parent = realPathDir.getParentFile();
                            if (parent != null) {
                                File grandParent = parent.getParentFile();
                                if (grandParent != null) {
                                    File projectRoot = grandParent.getParentFile();
                                    if (projectRoot != null) {
                                        File srcImagesDir = new File(projectRoot, "src/main/webapp/images");
                                        if (!srcImagesDir.exists() || !srcImagesDir.isDirectory()) {
                                            // Fallback to local workspace layout
                                            srcImagesDir = new File("d:\\Code\\ReVive_Java_Project\\ReVive\\src\\main\\webapp\\images");
                                        }
                                        if (srcImagesDir.exists() && srcImagesDir.isDirectory()) {
                                            File srcSavedFile = new File(srcImagesDir, newFileName);
                                            java.nio.file.Files.copy(savedFile.toPath(), srcSavedFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                            System.out.println("[ReVive] Saved duplicate to source dir: " + srcSavedFile.getAbsolutePath());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("[ReVive] Could not write duplicate copy to source directory: " + e.getMessage());
                        }

                        savedNames.add(newFileName);
                        count++;
                    } catch (Exception e) {
                        System.out.println("[ReVive] ERROR saving file: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                if (!savedNames.isEmpty()) {
                    newProduct.setImageUrl(String.join(";", savedNames));
                    System.out.println("[ReVive] imageUrl set to: " + newProduct.getImageUrl());
                }
            } else {
                System.out.println("[ReVive] No files uploaded (files is null or empty)");
            }

            // Guard: image_url column is NOT NULL
            if (newProduct.getImageUrl() == null || newProduct.getImageUrl().isEmpty()) {
                newProduct.setImageUrl("placeholder.png");
                System.out.println("[ReVive] No image uploaded, using placeholder.png");
            }

            System.out.println("[ReVive] Saving product: " + newProduct.getTitle() + " imageUrl=" + newProduct.getImageUrl());
            userClient.addProduct(newProduct, String.valueOf(authBean.getCurrentUser().getUserid()));
            newProduct = new Products();
            return "seller?faces-redirect=true";
        }
        return null;
    }

    // Getters and Setters
    public Products getNewProduct() { return newProduct; }
    public void setNewProduct(Products newProduct) { this.newProduct = newProduct; }
    public List<Products> getMyProducts() { return myProducts; }
    public List<Products> getApprovedProducts() { return approvedProducts; }
    public List<Products> getFilteredProducts() { return filteredProducts; }
    public String getSearchKeyword() { return searchKeyword; }
    public void setSearchKeyword(String searchKeyword) { this.searchKeyword = searchKeyword; }
    public UploadedFiles getFiles() { return files; }
    public void setFiles(UploadedFiles files) { this.files = files; }
}
