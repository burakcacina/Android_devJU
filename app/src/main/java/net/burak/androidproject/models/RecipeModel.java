package net.burak.androidproject.models;

import java.util.List;

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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CreatorModel getCreator() {
        return creator;
    }

    public void setCreator(CreatorModel creator) {
        this.creator = creator;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public List<DirectionsModel> getDirections() {
        return directions;
    }

    public void setDirections(List<DirectionsModel> directions) {
        this.directions = directions;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}

