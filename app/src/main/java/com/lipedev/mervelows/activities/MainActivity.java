package com.lipedev.mervelows.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lipedev.mervelows.R;
import com.lipedev.mervelows.activities.DetailActivity;
import com.lipedev.mervelows.fragments.AboutFragment;
import com.lipedev.mervelows.fragments.AllComicsFragment;
import com.lipedev.mervelows.fragments.FavoriteComicsFragment;
import com.lipedev.mervelows.models.Comic;

public class MainActivity extends AppCompatActivity implements
        AllComicsFragment.OnListFragmentInteractionListener, FavoriteComicsFragment.OnFavoriteListFragmentInteractionListener {

    FrameLayout fragmentHolder;
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToAllComics();
                    return true;
                case R.id.navigation_favs:
                    switchToFavorites();
                    return true;
                case R.id.navigation_about:
                    switchToAbout();
                    return true;
            }
            return false;
        }
    };

    /**
     * Replaces the current fragment to the AllComicsFragment if needed.
     */
    private void switchToAllComics() {
        AllComicsFragment allComicsFragment = AllComicsFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, allComicsFragment);
        transaction.commit();
    }

    /**
     * Replaces the current fragment to the FavoritesFragment if needed.
     */
    private void switchToFavorites() {
        FavoriteComicsFragment favoriteComicsFragment = FavoriteComicsFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, favoriteComicsFragment);
        transaction.commit();
    }

    /**
     * Replaces the current fragment to the AboutFragment if needed.
     */
    private void switchToAbout() {
        AboutFragment aboutFragment = AboutFragment.newInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, aboutFragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHolder = findViewById(R.id.fragmentHolder);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onListFragmentInteraction(Comic comic) {
        Intent intent = new Intent(this, DetailActivity.class);

        intent.putExtra(DetailActivity.COMIC_EXTRA, comic);

        startActivity(intent);
    }
}
