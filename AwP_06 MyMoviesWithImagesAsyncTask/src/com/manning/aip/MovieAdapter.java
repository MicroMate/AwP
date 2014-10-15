package com.manning.aip;

import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

public class MovieAdapter extends ArrayAdapter<String> {

   private HashMap<Integer, Boolean> movieCollection =
            new HashMap<Integer, Boolean>();

   private String[] movieIconUrls;

   public MovieAdapter(Context context) {
      super(context, R.layout.movie_item, android.R.id.text1, context
               .getResources().getStringArray(R.array.movies));

      movieIconUrls =
               context.getResources().getStringArray(R.array.movie_thumbs);
   }

   public void toggleMovie(int position) {
      if (!isInCollection(position)) {
         movieCollection.put(position, true);
      } else {
         movieCollection.put(position, false);
      }
   }

   public boolean isInCollection(int position) {
      return movieCollection.get(position) == Boolean.TRUE;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      View listItem = super.getView(position, convertView, parent);

      CheckedTextView checkMark =
               (CheckedTextView) listItem.findViewById(android.R.id.text1);
      checkMark.setChecked(isInCollection(position));

      ImageView imageView = (ImageView) listItem.findViewById(R.id.movie_icon);
      imageView.setImageDrawable(null);
      imageView.setTag(position);
      String imageUrl = this.movieIconUrls[position];

      // Od wersji 3.0 HoneyComb 
      // tak jest tworzony tylko jeden watek, zdjecia sa pobierane szeregowo
      //new DownloadTask(position, imageView).execute(imageUrl);

      // dla równoległego wykonywania watku PULI WATKÓW
      // nalezy uruchamiac tą metodą
      new DownloadTask(position, imageView).executeOnExecutor(
               AsyncTask.THREAD_POOL_EXECUTOR, imageUrl);


      return listItem;
   }
}
