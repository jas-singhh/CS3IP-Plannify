package uk.ac.aston.cs3mdd.mealplanner.quote_model;

import java.io.Serializable;

public class Quote implements Serializable {

    private String category;
    private String quote;
    private String author;

    public String getCategory() {
        return category;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }
}
