package edu.utep.cs.cs4330.whatsnext;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

public class MovieFinder {

//--ATTRIBUTES--------------------------------------------------------------------------------------

    public static String strSeparator = "\n-----\n";
    private String response;
    private String[] html;
    private String[] html2;
    private MovieItem movie = new MovieItem();
    private JSONObject movie_info;

//--THREADS------------------------------------------------------------------------------------------

    //Gets the movie information from its url
    protected MovieItem getMovieInformation(String url) throws InterruptedException {
        new Thread(() -> {
            StringBuilder content = new StringBuilder();
            String reader = "";
            try {
                URL web_url = new URL(url);
                HttpURLConnection httpConn = (HttpURLConnection) web_url.openConnection();
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                if (HttpURLConnection.HTTP_OK == httpConn.getResponseCode()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    while ((reader = input.readLine()) != null) { content.append(reader); }
                }
                response = content.toString();
                html = response.split("<");
                html2 = response.split("root.RottenTomatoes.context.");
                for (String s : html) {
                    //Gets the Title, Image, and Description from the HTML
                    if (s.contains("og:title")) { movie.setTitle(s.substring(34, s.length() - 14)); }
                    if (s.contains("og:image")) { movie.setImage(s.substring(34, s.length() - 14)); }
                    if (s.contains("og:description")) { movie.setDescription(s.substring(40, s.length() - 14)); }
                    //Use the Json provided to get the actors and reviews
                    if (s.contains("@context")) {
                        movie_info = new JSONObject(s.substring(50, s.length() - 16));
                        String[] actors = {movie_info.getJSONArray("actors").getJSONObject(0).get("name").toString(),
                                movie_info.getJSONArray("actors").getJSONObject(1).get("name").toString(),
                                movie_info.getJSONArray("actors").getJSONObject(2).get("name").toString()};
                        movie.setActors(actors);
                        String[] reviews = {movie_info.getJSONArray("review").getJSONObject(0).get("reviewBody").toString(),
                                movie_info.getJSONArray("review").getJSONObject(1).get("reviewBody").toString(),
                                movie_info.getJSONArray("review").getJSONObject(2).get("reviewBody").toString()};
                        movie.setReviews(reviews);
                    }
                }
                //Gets the Rating from the HTML (Required a different split)
                for (String s : html2) {
                    if (s.contains("scoreInfo")) {
                        String[] ratings = s.split("\"");
                        for (String s2 : ratings) {
                            if (s2.contains(".")) { movie.setRating(Double.parseDouble(s2)); break; }
                        }
                    }
                    //Gets the services the movie is available on (Required multiple splits)
                    if (s.contains("whereToWatch")) {
                        s = s.substring(s.indexOf('['), s.lastIndexOf(']'));
                        s = s.substring(s.indexOf('{'), s.lastIndexOf('}') + 1);
                        String[] availible = s.split("\"");
                                             //Fandango,     Netflix,       Hulu,          Amazon,       Disney,        ESPN,           Apple
                        String[] services = {availible[2], availible[4], availible[6], availible[10], availible[12], availible[14], availible[16]};
                        checkIfStreamable(services);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
        return movie;
    }
    //Gets the television information from its url
    protected MovieItem getTVInformation(String url) throws InterruptedException {
        new Thread(() -> {
            StringBuilder content = new StringBuilder();
            String reader = "";
            try {
                URL web_url = new URL(url);
                HttpURLConnection httpConn = (HttpURLConnection) web_url.openConnection();
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                if (HttpURLConnection.HTTP_OK == httpConn.getResponseCode()) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    while ((reader = input.readLine()) != null) { content.append(reader); }
                }
                response = content.toString();
                html = response.split("<");
                html2 = response.split("root.RottenTomatoes.context.");
                for (String s : html) {
                    //Gets the Title, Image, and Description from the HTML
                    if (s.contains("og:title")) { movie.setTitle(s.substring(34, s.length() - 14)); }
                    if (s.contains("og:image")) { movie.setImage(s.substring(34, s.length() - 14)); }
                    if (s.contains("og:description")) { movie.setDescription(s.substring(49, s.length() - 14)); }
                    //Use the Json provided to get the actors
                    if (s.contains("@context")) {
                        movie_info = new JSONObject(s.substring(50, s.length() - 16));
                        String[] actors = {movie_info.getJSONArray("actor").getJSONObject(0).get("name").toString(),
                                movie_info.getJSONArray("actor").getJSONObject(1).get("name").toString(),
                                movie_info.getJSONArray("actor").getJSONObject(2).get("name").toString()};
                        movie.setActors(actors);
                    }
                }
                //Gets the Rating from the HTML (Required a different split)
                for (String s : html2) {
                    if (s.contains("scoreInfo")) {
                        String[] ratings = s.split("\"");
                        for (String s2 : ratings) {
                            if (s2.contains(".")) { movie.setRating(Double.parseDouble(s2.substring(1, s2.length() - 1))); break; }
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
        return movie;
    }

//--HELPER-METHODS----------------------------------------------------------------------------------

    //Converts an array of strings to one string
    public String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i < array.length; i++) {
            str = str + array[i];
            if(i < array.length-1){ str = str+strSeparator; }
        }
        return str;
    }
    //Converts a string to an array
    public String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }
    //Converts the service array to an presentable array
    public void checkIfStreamable(String[] array) {
        String checker = "";
        if(array[0].contains("true")) { checker = checker+"Fandango"+strSeparator; }
        if(array[1].contains("true")) { checker = checker+"Netflix"+strSeparator; }
        if(array[2].contains("true")) { checker = checker+"Hulu"+strSeparator; }
        if(array[3].contains("true")) { checker = checker+"Amazon Prime"+strSeparator; }
        if(array[4].contains("true")) { checker = checker+"Disney Plus"+strSeparator; }
        if(array[5].contains("true")) { checker = checker+"ESPN"+strSeparator; }
        if(array[6].contains("true")) { checker = checker+"Apple TV"+strSeparator; }
        movie.setServices(convertStringToArray(checker));
    }
}