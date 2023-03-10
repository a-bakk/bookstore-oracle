package com.adatb.bookaround.entities;

import com.adatb.bookaround.entities.compositepk.StockId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stock")
public class Stock implements Serializable {
    @EmbeddedId
    private StockId stockId;
    @Column
    private Integer count;
}
