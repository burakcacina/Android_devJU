package net.burak.androidproject.models;

import lombok.Data;

import java.util.List;

@Data
public class RecipeModel {
    private Integer id;
    private String name;
    private String description;

    private CreatorModel creator;

    private String image;
    private Long created;

    private List<DirectionsModel> directions;

    private transient boolean fav = false;
    private transient Integer pageNumber = null;

}

