package uk.ac.aston.cs3mdd.mealplanner.room;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.AnalyzedInstruction;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.ExtendedIngredient;
import uk.ac.aston.cs3mdd.mealplanner.models.api_recipe.Nutrition;

/**
 * This class provides type converters to tell Room how to
 * store custom data types.
 */
public class RoomDataTypeConverter {

    /** For Local Date */
    // reference: https://stackoverflow.com/questions/54927913/room-localdatetime-typeconverter

    @TypeConverter
    public String fromLocalDate(LocalDate value) {
        if (value == null) {
            return (null);
        }

        return value.toString();
    }

    @TypeConverter
    public LocalDate toLocalDate(String value) {
        if (value == null) {
            return (null);
        }

        return LocalDate.parse(value);
    }

    /** For list of String */
    @TypeConverter
    public String fromStringList(List<String> value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public List<String> toStringList(String value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, type);
    }

    /** For list of Ingredients */
    @TypeConverter
    public String fromExtendedIngredientsList(List<ExtendedIngredient> value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ExtendedIngredient>>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public List<ExtendedIngredient> toExtendedIngredientsList(String value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ExtendedIngredient>>() {}.getType();
        return gson.fromJson(value, type);
    }

    /** For list of analyzed instructions */
    @TypeConverter
    public String fromAnalyzedInstructionsList(List<AnalyzedInstruction> value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<AnalyzedInstruction>>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public List<AnalyzedInstruction> toAnalyzedInstructionsList(String value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<AnalyzedInstruction>>() {}.getType();
        return gson.fromJson(value, type);
    }

    /** For list of nutrients */
    @TypeConverter
    public String fromNutrition(Nutrition value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Nutrition>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public Nutrition toNutrition(String value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Nutrition>() {}.getType();
        return gson.fromJson(value, type);
    }

    /** For list of occasions */
    @TypeConverter
    public String fromOccasionsList(List<Object> value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Object>>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public List<Object> toOccasionsList(String value) {
        if (value == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Object>>() {}.getType();
        return gson.fromJson(value, type);
    }

//
//    /** For map of String and Nutrient */
//    @TypeConverter
//    public String fromTotalNutrientsMap(Map<String, Nutrient> value) {
//        if (value == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<Map<String, Nutrient>>() {}.getType();
//        return gson.toJson(value, type);
//    }
//
//    @TypeConverter
//    public Map<String, Nutrient> toTotalNutrientsMap(String value) {
//        if (value == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<Map<String, Nutrient>>() {}.getType();
//        return gson.fromJson(value, type);
//    }
//
//    /** For list of Digest */
//    @TypeConverter
//    public String fromDigestList(List<Digest> value) {
//        if (value == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<Digest>>() {}.getType();
//        return gson.toJson(value, type);
//    }
//
//    @TypeConverter
//    public List<Digest> toDigestList(String value) {
//        if (value == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<Digest>>() {}.getType();
//        return gson.fromJson(value, type);
//    }
//
//    /** For map of EnumImageType and Image */
//    @TypeConverter
//    public String fromImagesMap(Map<EnumImageType, Image> value) {
//        if (value == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<Map<EnumImageType, Image>>() {}.getType();
//        return gson.toJson(value, type);
//    }
//
//    @TypeConverter
//    public Map<EnumImageType, Image> toImagesMap(String value) {
//        if (value == null) {
//            return (null);
//        }
//        Gson gson = new Gson();
//        Type type = new TypeToken<Map<EnumImageType, Image>>() {}.getType();
//        return gson.fromJson(value, type);
//    }
}
