package seatsio.accounts.subaccounts;

import com.google.gson.annotations.SerializedName;

public class ChartValidationSettings {

    @SerializedName("VALIDATE_DUPLICATE_LABELS")
    public ChartValidationLevel validateDuplicateLabels;

    @SerializedName("VALIDATE_OBJECTS_WITHOUT_CATEGORIES")
    public ChartValidationLevel validateObjectsWithoutCategories;

    @SerializedName("VALIDATE_UNLABELED_OBJECTS")
    public ChartValidationLevel validateUnlabeledObjects;
}
