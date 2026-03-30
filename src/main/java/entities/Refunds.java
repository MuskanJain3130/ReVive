/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

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
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author muskanjain
 */
@Entity
@Table(name = "refunds")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Refunds.findAll", query = "SELECT r FROM Refunds r"),
    @NamedQuery(name = "Refunds.findByRefundid", query = "SELECT r FROM Refunds r WHERE r.refundid = :refundid"),
    @NamedQuery(name = "Refunds.findByAmount", query = "SELECT r FROM Refunds r WHERE r.amount = :amount"),
    @NamedQuery(name = "Refunds.findByStatus", query = "SELECT r FROM Refunds r WHERE r.status = :status"),
    @NamedQuery(name = "Refunds.findByProcessedAt", query = "SELECT r FROM Refunds r WHERE r.processedAt = :processedAt")})
public class Refunds implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "refundid")
    private Integer refundid;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "amount")
    private Double amount;
    @Size(max = 9)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Column(name = "processed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processedAt;
    @JoinColumn(name = "returnid", referencedColumnName = "returnid")
    @ManyToOne
    private ReturnRequests returnid;

    public Refunds() {
    }

    public Refunds(Integer refundid) {
        this.refundid = refundid;
    }

    public Refunds(Integer refundid, Date processedAt) {
        this.refundid = refundid;
        this.processedAt = processedAt;
    }

    public Integer getRefundid() {
        return refundid;
    }

    public void setRefundid(Integer refundid) {
        this.refundid = refundid;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public ReturnRequests getReturnid() {
        return returnid;
    }

    public void setReturnid(ReturnRequests returnid) {
        this.returnid = returnid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (refundid != null ? refundid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Refunds)) {
            return false;
        }
        Refunds other = (Refunds) object;
        if ((this.refundid == null && other.refundid != null) || (this.refundid != null && !this.refundid.equals(other.refundid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Refunds[ refundid=" + refundid + " ]";
    }
    
}
