package com.lipedev.mervelows.activities;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lipedev.mervelows.R;
import com.lipedev.mervelows.database.AppDatabase;
import com.lipedev.mervelows.models.Character;
import com.lipedev.mervelows.models.Comic;
import com.lipedev.mervelows.models.Creator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    public static final String COMIC_EXTRA = "DetailActivity.COMIC_EXTRA";

    private Comic comic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        comic = (Comic) getIntent().getSerializableExtra(COMIC_EXTRA);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavorites();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViews();

        new CheckIfAlreadyOnDB().execute(); // Checks if current comic is already on DB for favorite purposes.
    }

    private void setupViews() {
        ImageView imageView = findViewById(R.id.imageView);
        Picasso.get().load(comic.getImagePath()
                + "/detail."
                + comic.getImageExtension())
                .fit()
                .centerInside()
                .into(imageView);


        TextView textViewTitle = findViewById(R.id.textViewTitle);
        textViewTitle.setText(comic.getTitle());

        TextView textViewDescription = findViewById(R.id.textViewDescription);
        textViewDescription.setText(comic.getDescription());

        TextView textViewDate = findViewById(R.id.textViewDate);
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        textViewDate.setText(format.format(comic.getLaunchDate()));

        TextView textViewPrice = findViewById(R.id.textViewPrice);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale(Locale.getDefault().getLanguage(), "US"));
        textViewPrice.setText(formatter.format(comic.getPrice()));

        LinearLayout linearLayoutChars = findViewById(R.id.characterList);
        for (Character character : comic.getCharacters()) {
            TextView textViewChar = (TextView) getLayoutInflater().inflate(R.layout.listitem_character, null);
            textViewChar.setText(character.getName());
            linearLayoutChars.addView(textViewChar);
        }

        LinearLayout linearLayoutCreators = findViewById(R.id.creatorsList);
        for (Creator creator : comic.getCreators()) {
            TextView textViewChar = (TextView) getLayoutInflater().inflate(R.layout.listitem_character, null);
            textViewChar.setText(creator.getName() + " (" + creator.getRole() + ")");
            linearLayoutCreators.addView(textViewChar);
        }

        checkFavoriteState();
    }

    private void checkFavoriteState() {
        TextView textViewFavorite = findViewById(R.id.textViewFavorited);
        if (comic.isFavorite()) {
            textViewFavorite.setVisibility(View.VISIBLE);
        } else {
            textViewFavorite.setVisibility(View.GONE);
        }
    }

    private void addToFavorites() {
        if (comic.isFavorite()) {
            new SaveAsFavorite().execute("false");
        } else {
            new SaveAsFavorite().execute("true");
        }
    }

    public class CheckIfAlreadyOnDB extends AsyncTask<String , Void ,String> {

        /**
         * saves current comic as a favorite
         *
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {

            AppDatabase db = AppDatabase.getInstance(DetailActivity.this);

            Comic comicFromDB = db.comicDao().findByMarvelId(comic.getMarvelId());
            if (comicFromDB != null) {
                Log.i("FromDB", comicFromDB.toString());
                comic.setUid(comicFromDB.getUid());
                comic.setFavorite(comicFromDB.isFavorite());
                Log.i("Taken", comic.toString());
            }

            return comic.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            checkFavoriteState();
        }
    }

    public class SaveAsFavorite extends AsyncTask<String , Void ,String> {

        /**
         * saves current comic as a favorite
         *
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {

            if (strings[0].equals("true")) {
                comic.setFavorite(true);
            } else {
                comic.setFavorite(false);
            }

            AppDatabase db = AppDatabase.getInstance(DetailActivity.this);

            Log.i("Saving", comic.toString());

            if (comic.getUid() > 0) {
                db.comicDao().update(comic);
                Log.i("Saving", "Update");
            } else {
                Log.i("Saving", "Create");

                // Save comic to db with favorite flag set to true
                long insertedID = db.comicDao().insert(comic);

                Log.i("Long", insertedID +"");

                for (Creator creator : comic.getCreators()) { //also save all creators
                    creator.setComicUid((int) insertedID);
                    db.comicDao().insertCreator(creator);
                }

                for (Character character : comic.getCharacters()) { //also save characters
                    character.setComicUid((int) insertedID);
                    db.comicDao().insertCharacter(character);
                }
            }

            return comic.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Snackbar.make(findViewById(R.id.container), comic.isFavorite() ? R.string.set_as_favorite : R.string.unset_as_favorite, BaseTransientBottomBar.LENGTH_SHORT)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addToFavorites();
                        }
                    }).show();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkFavoriteState();
                }
            });

        }
    }

}
