package com.adatb.bookaround.models.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OnStockStatus {
    ON_STOCK("készleten"),
    FEW_REMAINING("öt darabnál kevesebb van"),
    NONE("elfogyott");
    private final String value;
}
