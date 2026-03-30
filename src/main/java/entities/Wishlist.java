/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author muskanjain
 */
@Entity
@Table(name = "wishlist")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Wishlist.findAll", query = "SELECT w FROM Wishlist w"),
    @NamedQuery(name = "Wishlist.findByWishlistid", query = "SELECT w FROM Wishlist w WHERE w.wishlistid = :wishlistid"),
    @NamedQuery(name = "Wishlist.findByAddedAt", query = "SELECT w FROM Wishlist w WHERE w.added_at = :added_at")})
public class Wishlist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "wishlistid")
    private Integer wishlistid;
    @Column(name = "added_at", insertable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
@Temporal(TemporalType.TIMESTAMP)
private Date added_at;
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    @ManyToOne
    @JsonbTransient
    private Users userid;
    @JoinColumn(name = "productid", referencedColumnName = "productid")
    @ManyToOne
    @JsonbTransient
    private Products productid;

    public Wishlist() {
    }

    public Wishlist(Integer wishlistid) {
        this.wishlistid = wishlistid;
    }

    public Wishlist(Integer wishlistid, Date addedAt) {
        this.wishlistid = wishlistid;
//        this.addedAt = addedAt;
    }

    public Integer getWishlistid() {
        return wishlistid;
    }

    public void setWishlistid(Integer wishlistid) {
        this.wishlistid = wishlistid;
    }

//    public Date getAddedAt() {
//        return addedAt;
//    }
//
//    public void setAddedAt(Date addedAt) {
//        this.addedAt = addedAt;
//    }

    public Users getUserid() {
        return userid;
    }

    public void setUserid(Users userid) {
        this.userid = userid;
    }

    public Products getProductid() {
        return productid;
    }

    public void setProductid(Products productid) {
        this.productid = productid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wishlistid != null ? wishlistid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wishlist)) {
            return false;
        }
        Wishlist other = (Wishlist) object;
        if ((this.wishlistid == null && other.wishlistid != null) || (this.wishlistid != null && !this.wishlistid.equals(other.wishlistid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Wishlist[ wishlistid=" + wishlistid + " ]";
    }
    
}
