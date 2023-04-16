package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerWithOrderCount {
    private Customer customer;
    private Long numberOfOrders;
}
