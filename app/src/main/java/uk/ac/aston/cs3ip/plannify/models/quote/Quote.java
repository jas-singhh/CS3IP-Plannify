package uk.ac.aston.cs3ip.plannify.models.quote;

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
