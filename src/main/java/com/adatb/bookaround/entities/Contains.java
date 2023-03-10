package com.adatb.bookaround.entities;

import com.adatb.bookaround.entities.compositepk.ContainsId;
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
@Table(name = "contains")
public class Contains implements Serializable {
    @EmbeddedId
    private ContainsId containsId;
}
