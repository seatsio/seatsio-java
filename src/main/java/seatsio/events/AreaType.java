package seatsio.events;

import com.google.gson.annotations.SerializedName;

public enum AreaType {

    @SerializedName("generalAdmission")
    GENERAL_ADMISSION,

    @SerializedName("fixedOccupancy")
    FIXED_OCCUPANCY,

    @SerializedName("variableOccupancy")
    VARIABLE_OCCUPANCY
}

