package net.mijlk.service;

import java.util.Date;

/**
 * Created by Farah on 26-11-2016.
 */

public class Recipe {

    private Long id;

    private String name;

    private String image;

    private String created;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
