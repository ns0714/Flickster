package com.flickster.utils;

/**
 * Created by Amod on 3/9/17.
 */

public class MovieUtil {

    public static int getItemTypeRatingBased(double value) {
        return value > MovieConstants.POPULAR_RATING_BASE ?
                MovieConstants.POPULAR_MOVIE_TAG : MovieConstants.MOVIE_TAG;
    }
}
