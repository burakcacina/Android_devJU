package net.burak.androidproject.models;

/**
 * Created by Cube on 2/6/2017.
 */

public class CommentModel {

    private int id;
    private String text;
    private int grade;
    private Commenter commenter;
    private String image;
    private int created;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int created) {
        this.grade = grade;
    }

    public Commenter getCommenter() {
        return commenter;
    }

    public void setCommenter(Commenter commenter) {
        this.commenter = commenter;
    }

    public static class Commenter {
        private String id;
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
