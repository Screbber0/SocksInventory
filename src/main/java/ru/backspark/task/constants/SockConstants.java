package ru.backspark.task.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SockConstants {

    COLOR("color"),
    COTTON_PERCENTAGE("cottonPercentage"),
    QUANTITY("quantity");

    private final String value;
}
