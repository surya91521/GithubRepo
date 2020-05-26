package com.example.git;

public class Repo {

   private String image;
    private String name;
    private String full;
    private String count;

    public Repo(String image,String name, String full ,String count) {
        this.image = image;
        this.name = name;
        this.full = full;
        this.count =count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }
}
