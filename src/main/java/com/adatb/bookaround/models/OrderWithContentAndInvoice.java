package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.Invoice;
import com.adatb.bookaround.entities.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderWithContentAndInvoice {
    private List<BookWithAuthorsAndGenres> books = new ArrayList<>();
    private String contentAsString;
    private Order order;
    private Invoice invoice;
}
