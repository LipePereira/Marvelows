package com.lipedev.mervelows.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.lipedev.mervelows.R;
import com.lipedev.mervelows.adapters.ComicsRecyclerViewAdapter;
import com.lipedev.mervelows.database.AppDatabase;
import com.lipedev.mervelows.models.Comic;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFavoriteListFragmentInteractionListener}
 * interface.
 */
public class FavoriteComicsFragment extends Fragment {

    private List<Comic> comics; //Filtered
    private List<Comic> allComics; //Unfiltered

    private OnFavoriteListFragmentInteractionListener mListener;

    private String orderBy;

    private ProgressBar progressBar;

    GetComics getComicsTask;

    private ComicsRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoriteComicsFragment() {
    }

    public static FavoriteComicsFragment newInstance() {
        FavoriteComicsFragment fragment = new FavoriteComicsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (orderBy == null) orderBy = "name";
        TextInputEditText textInputEditTextSearch = getView().findViewById(R.id.textViewSearch);
        loadComicsFromDB(orderBy, textInputEditTextSearch.getText().toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_comics_list, container, false);

        progressBar = view.findViewById(R.id.progress);

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinner = view.findViewById(R.id.spinnerOrder);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    loadComicsFromDB("name", "");
                    orderBy = "name";
                } else {
                    loadComicsFromDB("date", "");
                    orderBy = "date";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextInputEditText textInputEditText = view.findViewById(R.id.textViewSearch);
        textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() { //Traditional OK to Search method
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String search = v.getText().toString();

                loadComicsFromDB(orderBy, search);
                return true;
            }
        });
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { //Quicker search as you type method
                loadComicsFromDB(orderBy, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Starts the async task to get data from the DBx0
     * @param order
     * @param search
     */
    private void loadComicsFromDB(String order, String search) {
        progressBar.setVisibility(View.VISIBLE);

        getComicsTask = new GetComics();
        getComicsTask.execute(order, search);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFavoriteListFragmentInteractionListener) {
            mListener = (OnFavoriteListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFavoriteListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFavoriteListFragmentInteractionListener {
        void onListFragmentInteraction(Comic comic);
    }

    public class GetComics extends AsyncTask<String , Void ,String> {
        String server_response;
        boolean isRunning;

        /**
         * Calls the API to get the JSON data
         *
         * @param strings
         *  [0] the URL to call
         *
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {

            AppDatabase db = AppDatabase.getInstance(getContext());

            if (strings[0].equals("name")) {
                comics = db.comicDao().getFavorites(true, strings[1]);
            } else {
                comics = db.comicDao().getFavoritesByDate(true, strings[1]);
            }

            for (Comic comic : comics) {
                allComics = new ArrayList<>();
                allComics.add(comic);
            }

            for (Comic comic : comics) {
                comic.setCreators(db.comicDao().getCreators(comic.getUid()));
                comic.setCharacters(db.comicDao().getCharacters(comic.getUid()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressBar.setVisibility(View.GONE);

            recyclerView = getView().findViewById(R.id.list);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            adapter = new ComicsRecyclerViewAdapter(comics, mListener);
            recyclerView.setAdapter(adapter);
        }
    }

}
