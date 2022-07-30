package com.example.temp2;

public class recipe {
    String id;
    String title;
    String desc;
    String author;
    String ingredient;
    String imageURL;
    String category;
    String steps;
    Integer like;

    public recipe() {

    }

    public recipe(String id, String title, String desc , String author, String ingredient, String imageURL, String category, String steps, Integer like) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.author = author;
        this.ingredient = ingredient;
        this.imageURL = imageURL;
        this.category = category;
        this.steps = steps;
        this.like = like;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
