package org.oucho.musicplayer.loaders;

import android.content.Context;

import org.oucho.musicplayer.model.db.FavoritesDbHelper;
import org.oucho.musicplayer.model.Song;

import java.util.List;


public class FavoritesLoader extends BaseLoader<List<Song>> {

    private int mLimit = -1;
    public FavoritesLoader(Context context) {
        super(context);
    }

    @SuppressWarnings("UnusedParameters")
    public FavoritesLoader(Context context, int limit) {
        super(context);
        mLimit = 3;
    }

    @Override
    public List<Song> loadInBackground() {
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(getContext());
        List<Song> favorites = dbHelper.read(mLimit);
        dbHelper.close();
        return favorites;
    }
}
