package edu.utep.cs.cs4330.whatsnext;

public class MovieItem {

//--ATTRIBUTES--------------------------------------------------------------------------------------

    private int id;
    private String title;
    private String image;
    private String description;
    private String[] actors;
    private String[] reviews;
    private Double rating;
    private boolean watched;
    private String[] services;

//--CONSTRUCTORS------------------------------------------------------------------------------------

    public MovieItem(int id, String title, String image, String description, String[] actors, String[] reviews, Double rating, boolean watched, String[] services) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.actors = actors;
        this.reviews = reviews;
        this.rating = rating;
        this.watched = watched;
        this.services = services;
    }
    public MovieItem(String title, String image, String description, String[] actors, String[] reviews, Double rating, boolean watched, String[] services) {
        this(0, title, image, description, actors, reviews, rating, watched, services);
    }
    public MovieItem(String title, String image, String description, String[] actors, String[] reviews, Double rating, String[] services) {
        this(title, image, description, actors, reviews, rating, false, services);
    }
    public MovieItem() { }

//--SETTERS-----------------------------------------------------------------------------------------

    public void setId(int id) {this.id = id; }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setImage(String image) { this.image = image; }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setActors(String[] actors) { this.actors = actors; }
    public void setReviews(String[] reviews) { this.reviews = reviews; }
    public void setRating(Double rating) { this.rating = rating; }
    public void setWatched(boolean watched) { this.watched = watched; }
    public void setServices(String[] services) { this.services = services; }

//--GETTERS-----------------------------------------------------------------------------------------

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getDescription() {
        return description;
    }
    public String[] getActors() { return actors; }
    public String[] getReviews() { return reviews; }
    public Double getRating() { return rating; }
    public boolean getWatched() { return watched; }
    public String[] getServices() { return services; }
}