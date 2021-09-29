package edu.utep.cs.cs4330.whatsnext;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

//--ATTRIBUTES--------------------------------------------------------------------------------------

    private MovieFinder mf = new MovieFinder();
    private EditText movieSearch;
    //Filter
    private EditText input_name;
    private EditText input_rating;
    private Switch input_watched;
    private Switch tv_shows;
    //Movie Information
    private MovieItem movie;
    private TextView title;
    private TextView description;
    private TextView rating;
    private TextView actors;
    private TextView reviews;
    private TextView services;
    private ImageView movie_poster;
    //UI's
    private View contentMain;
    private View movieDescription;
    private View contentList;
    private View errorScreen;
    //List & Database
    private MovieItemAdapter movieItemAdapter;
    private MovieItemAdapter filteredMovieItemAdapter;
    private MovieItemDatabase movieItemDatabase;

//--ON-CREATE---------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Gives access to the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        //Gives access to the Views, Buttons, and Texts of the xml files
        movieSearch = findViewById(R.id.movie_search);
        movieDescription = findViewById(R.id.movie_desc);
        errorScreen = findViewById(R.id.error_screen);
        contentList = findViewById(R.id.content_list);
        contentMain = findViewById(R.id.content_main);
        title = findViewById(R.id.title);
        movie_poster = findViewById(R.id.imageView);
        description = findViewById(R.id.description);
        rating = findViewById(R.id.rating);
        actors = findViewById(R.id.actors);
        reviews = findViewById(R.id.reviews);
        services = findViewById(R.id.services);
        tv_shows = findViewById(R.id.tv_show);
        //Registers the buttons
        registerClickListener(R.id.search);
        registerClickListener(R.id.favoritesList);
        registerClickListener(R.id.favorite);
        //Creates the ItemAdapter and Database for your personal list
        movieItemDatabase = new MovieItemDatabase(this);
        movieItemAdapter = new MovieItemAdapter(this, R.layout.content_items, movieItemDatabase.allItems());
        filteredMovieItemAdapter = new MovieItemAdapter(this, R.layout.content_items);
        movieItemAdapter.setItemClickListener(item -> movieItemDatabase.update(item));
        ListView listView = findViewById(R.id.content_list_view);
        listView.setAdapter(movieItemAdapter);
        registerForContextMenu(listView);
    }

//--CLICK-LISTENER----------------------------------------------------------------------------------

    //registerClickListener() and clicked() are used for the buttons
    private void registerClickListener(int id) {
        Button b = findViewById(id);
        b.setOnClickListener(v -> { try { clicked(id); } catch (InterruptedException e) { e.printStackTrace(); } });
    }
    //Determines the activity of the buttons in the layout files
    private void clicked(int id) throws InterruptedException {
        //Search Button
        if(id == R.id.search) {
            movieDescription.setVisibility(View.VISIBLE);
            contentList.setVisibility(View.INVISIBLE);
            contentMain.setVisibility(View.INVISIBLE);

            if(tv_shows.isChecked()) {
                movie = mf.getTVInformation("https://www.rottentomatoes.com/tv/" + movieSearch.getText().toString().replace(' ', '_'));
                movie.setReviews(new String[]{""}); movie.setServices(new String[]{""}); movie.setRating(0.0);
            } else { movie = mf.getMovieInformation("https://www.rottentomatoes.com/m/" + movieSearch.getText().toString().replace(' ', '_')); }
            TimeUnit.SECONDS.sleep(2);

            if (movie.getTitle() == null) { movieDescription.setVisibility(View.INVISIBLE); errorScreen.setVisibility(View.VISIBLE); }

            title.setText(movie.getTitle());
            description.setText(movie.getDescription());
            if(movie.getActors() != null) actors.setText(mf.convertArrayToString(movie.getActors()));
            if(movie.getReviews() != null && movie.getReviews().length != 1) reviews.setText(mf.convertArrayToString(movie.getReviews()));
            if(movie.getRating() != null && movie.getRating() != 0.0) rating.setText(String.format("%s/10.0", movie.getRating().toString()));
            if(movie.getServices() != null && movie.getServices().length != 1) services.setText(mf.convertArrayToString(movie.getServices()));
            Picasso.with(getApplicationContext()).load(movie.getImage()).fit().into(movie_poster);
        }
        //My Movies Button
        if(id == R.id.favoritesList) {
            contentList.setVisibility(View.VISIBLE);
            contentMain.setVisibility(View.INVISIBLE);
            movieDescription.setVisibility(View.INVISIBLE);
        }
        //Adds movie to your list
        if(id == R.id.favorite) {
            movieItemDatabase.addItem(movie);
            movieItemAdapter.add(movie);
            movieItemAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Added To Favorites", Toast.LENGTH_SHORT).show();
        }
    }

//--CONTEXT-MENU------------------------------------------------------------------------------------

    //Initializes the context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_options, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo x = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Opens the movie_description.xml file of the clicked movie
        if(id == R.id.view) {
            movieDescription.setVisibility(View.VISIBLE);
            contentList.setVisibility(View.INVISIBLE);
            contentMain.setVisibility(View.INVISIBLE);
            title.setText(movieItemAdapter.getItem(x.position).getTitle());
            description.setText(movieItemAdapter.getItem(x.position).getDescription());
            if(movieItemAdapter.getItem(x.position).getRating() != 0.0) rating.setText(String.format("%s/10.0", movieItemAdapter.getItem(x.position).getRating().toString()));
            actors.setText(mf.convertArrayToString(movieItemAdapter.getItem(x.position).getActors()));
            reviews.setText(mf.convertArrayToString(movieItemAdapter.getItem(x.position).getReviews()));
            services.setText(mf.convertArrayToString(movieItemAdapter.getItem(x.position).getServices()));
            Picasso.with(getApplicationContext()).load(movieItemAdapter.getItem(x.position).getImage()).fit().into(movie_poster);
        }
        //Adds the selected movie to your personal list
        if(id == R.id.unFavorite) {
            movieItemDatabase.delete(movieItemAdapter.getItem(x.position).getId());
            movieItemAdapter.remove(movieItemAdapter.getItem(x.position));
            Toast.makeText(getApplicationContext(), "Removed From Favorites", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }

//--OPTIONS-MENU------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Opens a dialog that allows you to apply filters
        if(id == R.id.filter) { openDialog(); }
        //Resets the filter and displays all movies
        if(id == R.id.resetFilter) {
            filteredMovieItemAdapter.clear();
            ListView listView = findViewById(R.id.content_list_view);
            listView.setAdapter(movieItemAdapter);
            registerForContextMenu(listView);
            Toast.makeText(getApplicationContext(), "Filter Reset", Toast.LENGTH_SHORT).show();
        }
        //Clears your list of movies
        if (id == R.id.clearFavorites) {
            movieItemAdapter.clear();
            movieItemDatabase.deleteAll();
            movieItemAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "List Cleared", Toast.LENGTH_SHORT).show();
        }
        //Safely closes the app
        if (id == R.id.exit) { this.finishAffinity(); }

        return super.onOptionsItemSelected(item);
    }

//--DIALOG------------------------------------------------------------------------------------------

    private void openDialog() {
        final Dialog filter_screen = new Dialog(MainActivity.this);
        filter_screen.setContentView(R.layout.filter_items);
        filter_screen.setTitle("Filter");
        filter_screen.show();
        Button cancelButton = filter_screen.findViewById(R.id.cancelButton);
        Button applyButton = filter_screen.findViewById(R.id.applyButton);
        //Closes the dialog box
        cancelButton.setOnClickListener(view -> filter_screen.dismiss());
        //Applies the filter to the list then closes the dialog box
        applyButton.setOnClickListener(view -> {
            input_name = filter_screen.findViewById(R.id.addName);
            input_rating = filter_screen.findViewById(R.id.addRating);
            input_watched = filter_screen.findViewById(R.id.addWatched);
            performFiltering();
            filter_screen.dismiss();
        });
    }

//--FILTER------------------------------------------------------------------------------------------

    protected void performFiltering() {
        String filter_name = input_name.getText().toString();
        Double filter_rating = 11.11;
        if(!(input_rating.getText().toString().isEmpty())) { filter_rating = Double.parseDouble(input_rating.getText().toString()); }
        boolean filter_watched = input_watched.isChecked();

        //Checks if a name was inputed, and filters based on the input
        if (filter_name.length() > 0) {
            if (filteredMovieItemAdapter.getCount() == 0) {
                for (int i = 0; i < movieItemAdapter.getCount(); i++) {
                    if ((movieItemAdapter.getItem(i).getTitle().toUpperCase()).contains(filter_name.toUpperCase())) { filteredMovieItemAdapter.add(movieItemAdapter.getItem(i)); }
                }
            } else {
                for (int i = 0; i < filteredMovieItemAdapter.getCount(); i++) {
                    if (!(filteredMovieItemAdapter.getItem(i).getTitle().toUpperCase().contains(filter_name.toUpperCase()))) { filteredMovieItemAdapter.remove(filteredMovieItemAdapter.getItem(i)); }
                }
            }
        }
        //Checks if a rating was inputed, and filters based on the input
        if (filter_rating != 11.11) {
            //If no name was picked, add movies with a proper rating
            if (filteredMovieItemAdapter.getCount() == 0) {
                for (int i = 0; i < movieItemAdapter.getCount(); i++) {
                    if (movieItemAdapter.getItem(i).getRating() >= filter_rating) { filteredMovieItemAdapter.add(movieItemAdapter.getItem(i)); }
                }
            //If a name was picked, remove movies with lower then input rating
            } else {
                for (int i = 0; i < filteredMovieItemAdapter.getCount(); i++) {
                    if (filteredMovieItemAdapter.getItem(i).getRating() < filter_rating) { filteredMovieItemAdapter.remove(filteredMovieItemAdapter.getItem(i)); }
                }
            }
        }
        //Checks if the watched switch was clicked, and filters based on the input
        if (filter_watched) {
            //If no name or rating was picked, adds movies that have been watched
            if (filteredMovieItemAdapter.getCount() == 0) {
                for (int i = 0; i < movieItemAdapter.getCount(); i++) {
                    if ((movieItemAdapter.getItem(i).getWatched())) { filteredMovieItemAdapter.add(movieItemAdapter.getItem(i)); }
                }
            //If a name/rating was picked, removes movies that havent been watched
            } else {
                for (int i = 0; i < filteredMovieItemAdapter.getCount(); i++) {
                    if (!(filteredMovieItemAdapter.getItem(i).getWatched())) { filteredMovieItemAdapter.remove(filteredMovieItemAdapter.getItem(i)); }
                }
            }
        //Checks if the watched switched was not clicked, and filters based on the input
        } else {
            //If no name or rating was picked, adds movies that have not been watched
            if (filteredMovieItemAdapter.getCount() == 0) {
                for (int i = 0; i < movieItemAdapter.getCount(); i++) {
                    if (!(movieItemAdapter.getItem(i).getWatched())) { filteredMovieItemAdapter.add(movieItemAdapter.getItem(i)); }
                }
            //If a name/rating was picked, removes movies that have been watched
            } else {
                for (int i = 0; i < filteredMovieItemAdapter.getCount(); i++) {
                    if (filteredMovieItemAdapter.getItem(i).getWatched()) { filteredMovieItemAdapter.remove(filteredMovieItemAdapter.getItem(i)); }
                }
            }
        }
        //If no filters were picked, don't display the new filtered list
        if (filteredMovieItemAdapter.getCount() != 0) {
            ListView listView = findViewById(R.id.content_list_view);
            listView.setAdapter(filteredMovieItemAdapter);
            registerForContextMenu(listView);
        }
    }
}