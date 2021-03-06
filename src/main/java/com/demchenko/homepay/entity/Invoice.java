package com.demchenko.homepay.entity;

import com.demchenko.homepay.entity.basic.NamedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice extends NamedEntity {

    private InvoiceType invoiceType;

    @ManyToOne(fetch = FetchType.EAGER)
    private Estate estate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice",
            cascade = CascadeType.ALL)
    private Set<Payment> paymentSet;
}
