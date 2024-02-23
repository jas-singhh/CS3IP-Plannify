package uk.ac.aston.cs3ip.plannify.enums;

import androidx.annotation.NonNull;

public enum EnumFilterId {
    // These Enum provides a better and safer way of performing requests based
    // on the provided filters.

    MEAL_TYPE("type"),
    CUISINES("cuisine"),
    DIETS("diet"),

    MAX_READY_TIME("maxReadyTime");


    private final String filterId;

    EnumFilterId(String filterId) {
        this.filterId = filterId;
    }

    public String getFilterId() {
        return filterId;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getFilterId();
    }
}
