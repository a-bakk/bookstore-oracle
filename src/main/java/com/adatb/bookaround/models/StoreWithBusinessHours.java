package com.adatb.bookaround.models;

import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoreWithBusinessHours {
    private Store store;
    private ArrayList<BusinessHours> businessHours = new ArrayList<>();

    // required for openingTime-closingTime
    public boolean listContainsDayOfWeek(Short dayOfWeek) {
        for (BusinessHours businessHour : this.getBusinessHours()) {
            if (Objects.equals(businessHour.getDayOfWeek(), dayOfWeek)) {
                return true;
            }
        }
        return false;
    }
}
