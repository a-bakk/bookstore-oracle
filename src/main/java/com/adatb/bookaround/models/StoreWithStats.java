package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.constants.StoreSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreWithStats {
    private Store store;
    private Long numberOfBooks;
    private StoreSize storeSize = StoreSize.SMALL_STORE;
}
