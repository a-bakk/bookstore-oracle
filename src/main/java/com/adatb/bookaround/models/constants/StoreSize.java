package com.adatb.bookaround.models.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StoreSize {
    LARGE_STORE("nagy áruház"),
    MEDIUM_STORE("közepes áruház"),
    SMALL_STORE("kicsi áruház");
    private final String value;
}
