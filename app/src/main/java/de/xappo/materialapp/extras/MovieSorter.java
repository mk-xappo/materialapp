package de.xappo.materialapp.extras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.xappo.materialapp.pojo.Movie;

/**
 * Created by knoppik on 26.08.2015.
 */
public class MovieSorter {
    public void sortMoviesByName(ArrayList<Movie> movies) {
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
    }

    public void sortMoviesByDate(ArrayList<Movie> movies) {
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                return lhs.getReleaseDateTheater().compareTo(rhs.getReleaseDateTheater());
            }
        });
    }

    public void sortMoviesByRatings(ArrayList<Movie> movies) {
        Collections.sort(movies, new Comparator<Movie>() {
            @Override
            public int compare(Movie lhs, Movie rhs) {
                int ratingLhs = lhs.getAudienceScore();
                int ratingRhs = rhs.getAudienceScore();
                if (ratingLhs < ratingRhs) {
                    return 1;
                } else if (ratingLhs > ratingRhs) {
                    return -1;
                } else {
                    return 0;
                }


            }
        });
    }
}
