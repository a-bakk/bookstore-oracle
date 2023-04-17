package com.adatb.bookaround.models.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public enum BookstoreDate {
    JANUARY_2023(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), "Január"),
    FEBRUARY_2023(LocalDate.of(2023, 2, 1), LocalDate.of(2023, 1, 28), "Február"),
    MARCH_2023(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 31), "Március"),
    APRIL_2023(LocalDate.of(2023, 4, 1), LocalDate.of(2023, 4, 30), "Április"),
    MAY_2023(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 5, 31), "Május"),
    JUNE_2023(LocalDate.of(2023, 6, 1), LocalDate.of(2023, 6, 30), "Június"),
    JULY_2023(LocalDate.of(2023, 7, 1), LocalDate.of(2023, 7, 31), "Július"),
    AUGUST_2023(LocalDate.of(2023, 8, 1), LocalDate.of(2023, 8, 31), "Augusztus"),
    SEPTEMBER_2023(LocalDate.of(2023, 9, 1), LocalDate.of(2023, 9, 30), "Szeptember"),
    OCTOBER_2023(LocalDate.of(2023, 10, 1), LocalDate.of(2023, 10, 31), "Október"),
    NOVEMBER_2023(LocalDate.of(2023, 11, 1), LocalDate.of(2023, 11, 30), "November"),
    DECEMBER_2023(LocalDate.of(2023, 12, 1), LocalDate.of(2023, 12, 31), "December");

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String value;
}
