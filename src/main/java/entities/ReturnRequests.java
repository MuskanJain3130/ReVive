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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author muskanjain
 */
@Entity
@Table(name = "return_requests")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ReturnRequests.findAll", query = "SELECT r FROM ReturnRequests r"),
    @NamedQuery(name = "ReturnRequests.findByReturnid", query = "SELECT r FROM ReturnRequests r WHERE r.returnid = :returnid"),
    @NamedQuery(name = "ReturnRequests.findByStatus", query = "SELECT r FROM ReturnRequests r WHERE r.status = :status"),
    @NamedQuery(name = "ReturnRequests.findByRequestedAt", query = "SELECT r FROM ReturnRequests r WHERE r.requestedAt = :requestedAt")})
public class ReturnRequests implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "returnid")
    private Integer returnid;
    @Lob
    @Size(max = 65535)
    @Column(name = "reason")
    private String reason;
    @Size(max = 9)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requested_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestedAt;
    @JoinColumn(name = "orderdetailid", referencedColumnName = "orderdetailid")
    @ManyToOne
    @JsonbTransient
    private OrderDetails orderdetailid;
    @OneToMany(mappedBy = "returnid")
    private Collection<Refunds> refundsCollection;

    public ReturnRequests() {
    }

    public ReturnRequests(Integer returnid) {
        this.returnid = returnid;
    }

    public ReturnRequests(Integer returnid, Date requestedAt) {
        this.returnid = returnid;
        this.requestedAt = requestedAt;
    }

    public Integer getReturnid() {
        return returnid;
    }

    public void setReturnid(Integer returnid) {
        this.returnid = returnid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Date requestedAt) {
        this.requestedAt = requestedAt;
    }

    public OrderDetails getOrderdetailid() {
        return orderdetailid;
    }

    public void setOrderdetailid(OrderDetails orderdetailid) {
        this.orderdetailid = orderdetailid;
    }

    @XmlTransient
    public Collection<Refunds> getRefundsCollection() {
        return refundsCollection;
    }

    public void setRefundsCollection(Collection<Refunds> refundsCollection) {
        this.refundsCollection = refundsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (returnid != null ? returnid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReturnRequests)) {
            return false;
        }
        ReturnRequests other = (ReturnRequests) object;
        if ((this.returnid == null && other.returnid != null) || (this.returnid != null && !this.returnid.equals(other.returnid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ReturnRequests[ returnid=" + returnid + " ]";
    }
    
}
