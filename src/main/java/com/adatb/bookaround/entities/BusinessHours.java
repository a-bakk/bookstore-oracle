package com.adatb.bookaround.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "business_hours")
public class BusinessHours implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "business_hours_seq")
    @SequenceGenerator(name = "business_hours_seq", sequenceName = "business_hours_seq", allocationSize = 1)
    @Column(name = "hours_id")
    private Long hoursId;
    @Column
    private Short dayOfWeek;
    @Column
    private String openingTime;
    @Column
    private String closingTime;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "store_id", referencedColumnName = "store_id")
    private Store store;
}
