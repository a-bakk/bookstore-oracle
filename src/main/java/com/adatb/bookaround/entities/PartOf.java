package com.adatb.bookaround.entities;

import com.adatb.bookaround.entities.compositepk.PartOfId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "partof")
public class PartOf implements Serializable {
    @EmbeddedId
    private PartOfId partOfId;
    @Column
    private Date addedAt;
}
