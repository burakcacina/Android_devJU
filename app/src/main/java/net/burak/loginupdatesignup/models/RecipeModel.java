package net.burak.loginupdatesignup.models;

import java.util.List;

public class RecipeModel {
    private int id;
    private String movie;
    private String description;
    private int created;
    private String userid;
    private String name;
    private String image;
    private String username;
    private List<directions> directionsList;

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public int getid() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return movie;
    }
    public void setName(String name) {
        this.movie = name;
    }

    public int getCreated() { return created; }
    public void setCreated(int created) { this.created = created; }

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return username;
    }
    public void setUserName(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTagline() {
        return name;
    }
    public void setTagline(String name) {
        this.name = name;
    }

    public List<directions> getdirectionsList() {
        return directionsList;
    }

    public void setdirectionsList(List<directions> directionsList) { this.directionsList = directionsList; }

    public static class directions {
        private String description;
        private int order;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
    }
}

