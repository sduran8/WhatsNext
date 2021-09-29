package edu.utep.cs.cs4330.whatsnext;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class MovieItemAdapter extends ArrayAdapter<MovieItem> {

//--DATABASE----------------------------------------------------------------------------------------

    public interface ItemClickListener { void itemClicked(MovieItem item);}
    private ItemClickListener listener;
    public void setItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

//--CONSTRUCTORS------------------------------------------------------------------------------------

    public MovieItemAdapter(Context context, int resourceId) {
        super(context, resourceId);
    }
    public MovieItemAdapter(Context context, int resourceId, List<MovieItem> items) { super(context, resourceId, items); }

//--METHODS-----------------------------------------------------------------------------------------

    //Adds the item to the content_list for display
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_items, parent, false);
            CheckBox checkBox = convertView.findViewById(R.id.watched);
            checkBox.setOnClickListener(view -> {
                CheckBox cb = (CheckBox) view;
                MovieItem item = (MovieItem) cb.getTag();
                item.setWatched(cb.isChecked());
                if (listener != null) { listener.itemClicked(item); }
            });
        }
        MovieItem product = getItem(position);
        TextView textView = convertView.findViewById(R.id.movie_list);
        if (product.getRating() != 0.0) {
            textView.setText(Html.fromHtml("<b>Title: </b>" + product.getTitle() +
                                                  "<br/> <b>Rating: </b>" + product.getRating()));
        } else {
            textView.setText(Html.fromHtml("<b>Title: </b>" + product.getTitle() +
                                                  "<br/> <b>Rating: </b>" + "None"));
        }
        CheckBox checkBox = convertView.findViewById(R.id.watched);
        checkBox.setChecked(product.getWatched());
        checkBox.setTag(product);
        return convertView;
    }
}