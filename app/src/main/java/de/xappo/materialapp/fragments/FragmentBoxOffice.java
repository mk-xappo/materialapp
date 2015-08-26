package de.xappo.materialapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.xappo.materialapp.R;
import de.xappo.materialapp.adapters.AdapterBoxOffice;
import de.xappo.materialapp.extras.Constants;
import de.xappo.materialapp.logging.L;
import de.xappo.materialapp.materialapp.MyApplication;
import de.xappo.materialapp.network.VolleySingleton;
import de.xappo.materialapp.pojo.Movie;

import static de.xappo.materialapp.extras.UrlEndpoints.*;

import static de.xappo.materialapp.extras.Keys.EndpointBoxOffice.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentBoxOffice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBoxOffice extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private ArrayList<Movie> listMovies = new ArrayList<Movie>();
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private RecyclerView listMovieHits;

    private AdapterBoxOffice adapterBoxOffice;
    private TextView textViewVolleyEror;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentBoxOffice.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentBoxOffice newInstance(String param1, String param2) {
        FragmentBoxOffice fragment = new FragmentBoxOffice();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getRequestUrl(int limit)  {
        return URL_BOX_OFFICE
                + URL_CHAR_QUESTION
                + URL_PARAM_API_KEY + MyApplication.API_KEY_ROTTEN_TOMATOES
                + URL_CHAR_AMEPERSAND
                + URL_PARAM_LIMIT + limit;
    }

    public FragmentBoxOffice() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        Log.i(getClass().getName(), "onCreate()");
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sendJsonRequest();

    }

    private void sendJsonRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getRequestUrl(30),
                 new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                textViewVolleyEror.setVisibility(View.GONE);
                listMovies = parseJsonResponse(response);
                adapterBoxOffice.setMovieList(listMovies);
                Log.i(getClass().getName(), "onResponse()");
                //L.t(getActivity(), response.toString())
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);
            }
        });
        requestQueue.add(request);
    }

    private void handleVolleyError(VolleyError error) {
        textViewVolleyEror.setVisibility(View.VISIBLE);
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            textViewVolleyEror.setText(R.string.error_timeout);

        } else if (error instanceof AuthFailureError) {
            textViewVolleyEror.setText(R.string.error_auth_failure);
        } else if (error instanceof ServerError) {
            textViewVolleyEror.setText(R.string.error_auth_failure);
        } else if (error instanceof NetworkError) {
            textViewVolleyEror.setText(R.string.error_network);
        } else if (error instanceof ParseError) {
            textViewVolleyEror.setText(R.string.error_parser);
        }
    }

    private ArrayList<Movie> parseJsonResponse(JSONObject response) {
        ArrayList<Movie> listMovies = new ArrayList<Movie>();
        if (response != null && response.length() > 0) {
            try {
                JSONArray arrayMovies = response.getJSONArray(KEY_MOVIES);
                for (int i = 0; i < arrayMovies.length(); i++) {
                    long id = 0;
                    String title = Constants.NA;
                    String releaseDate = Constants.NA;
                    int audienceScore = -1;
                    String synopsis = Constants.NA;
                    String urlThumbnail = Constants.NA;

                    JSONObject currentMovie = arrayMovies.getJSONObject(i);
                    if (currentMovie.has(KEY_ID) && !currentMovie.isNull(KEY_ID)) {
                        id = currentMovie.getLong(KEY_ID);
                    }

                    if (currentMovie.has(KEY_TITLE) && !currentMovie.isNull(KEY_TITLE)) {
                        title = currentMovie.getString(KEY_TITLE);
                    }


                    if (currentMovie.has(KEY_RELEASE_DATES) && !currentMovie.isNull(KEY_RELEASE_DATES)) {
                        JSONObject objectReleaseDates = currentMovie.getJSONObject(KEY_RELEASE_DATES);
                        if (objectReleaseDates != null
                        && objectReleaseDates.has(KEY_THEATER)
                                && !objectReleaseDates.isNull(KEY_THEATER)) {
                            releaseDate = objectReleaseDates.getString(KEY_THEATER);
                        }
                    }
                    JSONObject objectRatings = currentMovie.getJSONObject(KEY_RATINGS);
                    if (objectRatings.has(KEY_AUDIENCE_SCORE) && !objectRatings.isNull(KEY_AUDIENCE_SCORE)) {
                        audienceScore = objectRatings.getInt(KEY_AUDIENCE_SCORE);
                    }
                    if (currentMovie.has(KEY_SYNOPSIS) && !currentMovie.isNull(KEY_SYNOPSIS)) {
                        synopsis = currentMovie.getString(KEY_SYNOPSIS);
                    }
                    if (currentMovie.has(KEY_POSTERS) && !currentMovie.isNull(KEY_POSTERS)) { {
                        JSONObject objectPosters = currentMovie.getJSONObject(KEY_POSTERS);
                        if (objectPosters != null
                                && objectPosters.has(KEY_THUMBNAIL)
                                && !objectPosters.isNull(KEY_THUMBNAIL)) {
                            urlThumbnail = objectPosters.getString(KEY_THUMBNAIL);
                        }
                    }}

                    Movie movie = new Movie();
                    movie.setId(id);
                    movie.setTitle(title);
                    Date date = null;
                    try {
                        date = dateFormat.parse(releaseDate);
                    } catch (ParseException e) {}
                    movie.setReleaseDateTheater(date);
                    movie.setAudienceScore(audienceScore);
                    movie.setSynopsis(synopsis);
                    movie.setUrlThumbnail(urlThumbnail);
                    Log.v(getClass().getName(), "parseJsonResponse movie: " + movie.toString());

                    if (id != -1 && !title.equals(Constants.NA)) {
                        listMovies.add(movie);
                    }
                }
                Log.v(getClass().getName(), "parseJsonResponse listMoviews: " + listMovies.toString());

            } catch (JSONException e) {

            }
        }
        return listMovies;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_box_office, container, false);
        textViewVolleyEror = (TextView) view.findViewById(R.id.textVolleyError);
        listMovieHits = (RecyclerView) view.findViewById(R.id.listMovieHits);
        listMovieHits.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterBoxOffice = new AdapterBoxOffice(getActivity());
        listMovieHits.setAdapter(adapterBoxOffice);
        return view;
    }


}
