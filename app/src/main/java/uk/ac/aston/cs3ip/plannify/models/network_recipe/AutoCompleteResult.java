package uk.ac.aston.cs3ip.plannify.models.network_recipe;

import java.io.Serializable;

public class AutoCompleteResult implements Serializable {

    private int id;
    private String title;
    private String imageType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
