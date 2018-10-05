package com.lipedev.mervelows.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lipedev.mervelows.R;
import com.lipedev.mervelows.adapters.ComicsRecyclerViewAdapter;
import com.lipedev.mervelows.models.Character;
import com.lipedev.mervelows.models.Comic;
import com.lipedev.mervelows.models.Creator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AllComicsFragment extends Fragment {

    private List<Comic> comics;
    private OnListFragmentInteractionListener mListener;

    private ProgressBar progressBar;

    GetComics getComicsTask;

    long curOffset = 0;

    private ComicsRecyclerViewAdapter adapter;
    private String lastSearch;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllComicsFragment() {
    }

    public static AllComicsFragment newInstance() {
        AllComicsFragment fragment = new AllComicsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comics_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);

        progressBar = view.findViewById(R.id.progress);

        Context context = view.getContext();

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        comics = new ArrayList<>();
        adapter = new ComicsRecyclerViewAdapter(comics, mListener);
        recyclerView.setAdapter(adapter);

        setupInfiniteScroll(recyclerView);

        return view;
    }

    private void setupInfiniteScroll(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {
                    TextInputEditText textInputEditTextSearch = getView().findViewById(R.id.textViewSearch);
                    loadComicsFromAPI(textInputEditTextSearch.getText().toString(), false);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText textInputEditTextSearch = view.findViewById(R.id.textViewSearch);
        textInputEditTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadComicsFromAPI(v.getText().toString(), true);
                    return true;
                }
                return false;
            }
        });

        lastSearch = "";
        loadComicsFromAPI(textInputEditTextSearch.getText().toString(), true);
    }

    /**
     * Builds the URL and sends it to the async task
     * @param search
     */
    private void loadComicsFromAPI(String search, boolean freshSearch) {
        progressBar.setVisibility(View.VISIBLE);

        long time = new Date().getTime();

        String hash = getHashedKeys(time);

        if (freshSearch) { //New search/First open, so the offset is reset to 0, AKA start from the beginning again
            curOffset = 0;
        }

        String url = String.format(getString(R.string.marvel_api_url__format),
                getString(R.string.marvel_api_url) + getString(R.string.api_resource__comics),
                time + "",
                getString(R.string.public_key),
                hash,
                "&limit=50&offset=" + (curOffset * 50) + ""
        );

        if (freshSearch) {
            if (!search.isEmpty()) {
                url += "&titleStartsWith=" + search;
            }
        } else {
            if (!lastSearch.isEmpty()) {
                url += "&titleStartsWith=" + lastSearch;
            }
        }
        lastSearch = search;

        // Only starts a new load task if the previous one is finished
        if (getComicsTask == null) {
            getComicsTask = new GetComics();
            getComicsTask.execute(url);
        }else if (!getComicsTask.isRunning) {
            if (freshSearch) {
                comics.clear();
                adapter.notifyDataSetChanged();
            }

            getComicsTask = new GetComics();
            getComicsTask.execute(url);
        }
    }

    /**
     *
     * @return the MD5 hash of timestamp + privatekey + publickey
     */
    private String getHashedKeys(long time) {
        String toHash = time + getString(R.string.private_key) + getString(R.string.public_key);

        byte[] toHashBytes = new byte[0];
        try {
            toHashBytes = toHash.getBytes("UTF-8");
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(toHashBytes);

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < digestBytes.length; ++i) {
                stringBuffer.append(Integer.toHexString((digestBytes[i] & 0xFF) | 0x100).substring(1,3));
            }
            return stringBuffer.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
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

            URL url;
            HttpURLConnection urlConnection = null;

            isRunning = true;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                    Log.i("Status", "Finished");
                    Log.i("Comics", server_response);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressBar.setVisibility(View.GONE);

            if (server_response != null) {
                //Populate Recycler

                curOffset++; // only now, that we have all current 50 comics we are ready to request the next 50

                try {
                    JSONObject jsonObject = new JSONObject(server_response);
                    populateRecycler(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //Show Error Message
                Snackbar.make(getActivity().findViewById(R.id.container), R.string.network_error, BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .setAction(R.string.try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TextInputEditText textInputEditTextSearch = getView().findViewById(R.id.textViewSearch);
                                loadComicsFromAPI(textInputEditTextSearch.getText().toString(), true);

                            }
                        }).show();
            }

            isRunning = false; // Task Done
        }
    }

    private void populateRecycler(JSONObject jsonObject) {
        try {
            JSONArray results = jsonObject.getJSONObject("data").getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject comicJson = results.getJSONObject(i);
                Comic comic = new Comic(
                        comicJson.getString("title"),
                        comicJson.getString("description"),
                        comicJson.getLong("id"),
                        processLaunchDate(comicJson.getJSONArray("dates")),
                        processPrices(comicJson.getJSONArray("prices")),
                        processCreators(comicJson.getJSONObject("creators").getJSONArray("items")),
                        processCharacters(comicJson.getJSONObject("characters").getJSONArray("items")),
                        comicJson.getJSONObject("thumbnail").getString("path"),
                        comicJson.getJSONObject("thumbnail").getString("extension"),
                        false
                );
                if (comics.indexOf(comic) == -1) {
                    comics.add(comic);
                    adapter.notifyItemInserted(comics.size()-1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of Strings representing the characters of the comic.
     *
     * @param chars the JSONArray containing creators data
     * @return the List of creators
     */
    private List<Character> processCharacters(JSONArray chars) {
        List<Character> charList = new ArrayList<>();
        for (int i = 0; i < chars.length(); i++) {
            try {
                JSONObject chara = chars.getJSONObject(i);
                Character character = new Character(chara.getString("name"));
                charList.add(character);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return charList;
    }

    /**
     * Returns a list of pairs representing the creators of the comic. The pair is as follows:
     * First Item is the CreatorName
     * Second Item is the CreatorRole
     *
     * @param creators the JSONArray containing creators data
     * @return the List of creators
     */
    private List<Creator> processCreators(JSONArray creators) {
        List<Creator> creatorsList = new ArrayList<>();
        for (int i = 0; i < creators.length(); i++) {
            try {
                JSONObject creator = creators.getJSONObject(i);
                Creator creatorModel = new Creator(creator.getString("name"), creator.getString("role"));
                creatorsList.add(creatorModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return creatorsList;
    }

    /**
     * Returns the first price for the comic available in the prices array
     *
     * @param prices the jsonArray of relevant prices
     * @return the price
     */
    private double processPrices(JSONArray prices) {
        for (int i = 0; i < prices.length(); i++) {
            try {
                if (prices.getJSONObject(i).getString("type").equals("printPrice")) {
                    return prices.getJSONObject(i).getDouble("price");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * Returns the onsaleDate for the comic
     *
     * @param dates the jsonArray of relevant dates
     * @return the date
     */
    private Date processLaunchDate(JSONArray dates) {
        for (int i = 0; i < dates.length(); i++) {
            try {
                JSONObject date = dates.getJSONObject(i);

                if (date.getString("type").equals("onsaleDate")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ");

                    String dateString = date.getString("date");

                    return sdf.parse(dateString);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Date();
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
