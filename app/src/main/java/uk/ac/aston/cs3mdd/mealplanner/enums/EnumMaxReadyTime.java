package uk.ac.aston.cs3mdd.mealplanner.enums;

import androidx.annotation.NonNull;

public enum EnumMaxReadyTime {
    // These Enum provides a better and safer way of performing requests based
    // on the provided filters.

    UP_TO_15M("Up to 15m"),
    UP_TO_30M("Up to 30m"),
    UP_TO_1H("Up to 1h"),

    UP_TO_2H("Up to 2h"),
    MORE_THAN_2H("More than 2h");


    private final String maxReadyTime;

    EnumMaxReadyTime(String maxReadyTime) {
        this.maxReadyTime = maxReadyTime;
    }

    public String getMaxReadyTime() {
        return maxReadyTime;
    }

    /**
     * Returns the Enum corresponding to the given value.
     *
     * @param value value to check the Enum for.
     * @return the Enum corresponding to the given value or null if there is none.
     */
    public static EnumMaxReadyTime fromValue(String value) {
        if (value == null) return null;

        for (EnumMaxReadyTime myEnum: EnumMaxReadyTime.values()) {
            if (myEnum.getMaxReadyTime().equals(value))
                return myEnum;
        }

        return null;
    }

    /**
     * Returns the max ready time in minutes from the current selection.
     *
     * @return max ready time in minutes based on the provided selection.
     */
    public int getMaxReadyTimeInMinutesFromEnum() {
        switch(this){
            case UP_TO_15M:
                return 15;
            case UP_TO_30M:
                return 30;
            case UP_TO_1H:
                return 60;
            case UP_TO_2H:
                return 120;
            case MORE_THAN_2H:
                return 300;
        }

        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getMaxReadyTime();
    }
}
