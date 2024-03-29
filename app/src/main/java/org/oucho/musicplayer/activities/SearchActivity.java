package org.oucho.musicplayer.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.oucho.musicplayer.MainActivity;
import org.oucho.musicplayer.R;
import org.oucho.musicplayer.images.ArtistImageCache;
import org.oucho.musicplayer.images.ArtworkCache;
import org.oucho.musicplayer.loaders.AlbumLoader;
import org.oucho.musicplayer.loaders.ArtistLoader;
import org.oucho.musicplayer.loaders.BaseLoader;
import org.oucho.musicplayer.loaders.SongLoader;
import org.oucho.musicplayer.model.Album;
import org.oucho.musicplayer.model.Artist;
import org.oucho.musicplayer.model.Song;
import org.oucho.musicplayer.widgets.FastScroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SearchActivity extends BaseActivity {

    private static final String FILTER = "filter";


    private boolean mAlbumListLoaded = false;
    private boolean mArtistListLoaded = false;
    private boolean mSongListLoaded = false;
    private View mEmptyView;
    private SearchAdapter mAdapter;

    private final LoaderManager.LoaderCallbacks<List<Album>> mAlbumLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<List<Album>>() {

        @Override
        public Loader<List<Album>> onCreateLoader(int id, Bundle args) {

            AlbumLoader loader = new AlbumLoader(SearchActivity.this, null);

            setLoaderFilter(args, loader);

            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
            mAlbumListLoaded = true;
            mAdapter.setAlbumList(data);

        }

        @Override
        public void onLoaderReset(Loader<List<Album>> loader) {

        }
    };



    private final LoaderManager.LoaderCallbacks<List<Artist>> mArtistLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<List<Artist>>() {

        @Override
        public void onLoadFinished(Loader<List<Artist>> loader, List<Artist> data) {
            mArtistListLoaded = true;
            mAdapter.setArtistList(data);

        }

        @Override
        public void onLoaderReset(Loader<List<Artist>> loader) {

        }

        @Override
        public Loader<List<Artist>> onCreateLoader(int id, Bundle args) {

            ArtistLoader loader = new ArtistLoader(SearchActivity.this);

            setLoaderFilter(args, loader);

            return loader;
        }
    };



    private final LoaderManager.LoaderCallbacks<List<Song>> mSongLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<List<Song>>() {
        @Override
        public void onLoaderReset(Loader<List<Song>> loader) {
            //  Auto-generated method stub
        }

        @Override
        public void onLoadFinished(Loader<List<Song>> loader, List<Song> songList) {
            mSongListLoaded = true;
            mAdapter.setSongList(songList);
            Log.e("test", "" + mAdapter.getItemCount());
        }

        @Override
        public Loader<List<Song>> onCreateLoader(int id, Bundle args) {
            SongLoader loader = new SongLoader(SearchActivity.this);

            setLoaderFilter(args, loader);

            return loader;
        }
    };
    private int mThumbSize;



    private final OnQueryTextListener searchQueryListener = new OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            //  Auto-generated method stub
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            refresh(newText);

            return true;
        }

    };
    private RecyclerView mRecyclerView;


    private final RecyclerView.AdapterDataObserver mEmptyObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            if (mAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }

        }
    };



    private static void setLoaderFilter(Bundle args, BaseLoader loader) {
        String filter;
        if (args != null) {
            filter = args.getString(FILTER);
        } else {
            filter = "";
        }
        loader.setFilter(filter);
    }



    /* *********************************************************************************************
     * Création de l'activité
     * ********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String couleur = BaseActivity.getColor(this);

        ActionBar actionBar = getSupportActionBar();

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#" + couleur));
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);

        SearchView searchView = new SearchView(actionBar.getThemedContext());
        searchView.setIconifiedByDefault(false);
        actionBar.setCustomView(searchView);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setElevation(0);

        searchView.setOnQueryTextListener(searchQueryListener);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mThumbSize = getResources().getDimensionPixelSize(R.dimen.art_thumbnail_search_size);
        mEmptyView = findViewById(R.id.empty_view);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_view);
        mAdapter = new SearchAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.registerAdapterDataObserver(mEmptyObserver);

        getSupportLoaderManager().initLoader(0, null, mAlbumLoaderCallbacks);
        getSupportLoaderManager().initLoader(1, null, mArtistLoaderCallbacks);
        getSupportLoaderManager().initLoader(2, null, mSongLoaderCallbacks);

        FastScroller mFastScroller = (FastScroller) findViewById(R.id.fastscroller);
        assert mFastScroller != null;
        mFastScroller.setRecyclerView(mRecyclerView);
    }




    private void refresh(String newText) {
        Bundle args = null;
        if (newText != null) {
            args = new Bundle();
            args.putString(FILTER, newText);
        }

        mAlbumListLoaded = false;
        mArtistListLoaded = false;
        mSongListLoaded = false;
        getSupportLoaderManager().restartLoader(0, args, mAlbumLoaderCallbacks);
        getSupportLoaderManager().restartLoader(1, args, mArtistLoaderCallbacks);
        getSupportLoaderManager().restartLoader(2, args, mSongLoaderCallbacks);
    }





    private void returnToMain(String action, Bundle data) {
        Intent i = new Intent(action);
        if (data != null) {
            i.putExtras(data);
        }
        setResult(RESULT_OK, i);
        finish();
    }





    /* *********************************************************************************************
     * Vue liste album
     * ********************************************************************************************/

    class AlbumViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        final ImageView vArtwork;
        final TextView vName;
        final TextView vArtist;


        public AlbumViewHolder(View itemView) {
            super(itemView);
            vArtwork = (ImageView) itemView.findViewById(R.id.album_artwork);
            vName = (TextView) itemView.findViewById(R.id.album_name);
            vArtist = (TextView) itemView.findViewById(R.id.artist_name);
            vArtwork.setOnClickListener(this);
            itemView.findViewById(R.id.album_info).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Album album = (Album) mAdapter.getItem(position);

            switch (v.getId()) {
                case R.id.album_info:
                    Log.d("album", "album id " + album.getId() + " " + album.getAlbumName());
                    Bundle data = new Bundle();
                    data.putLong(MainActivity.ALBUM_ID, album.getId());
                    data.putString(MainActivity.ALBUM_NAME, album.getAlbumName());
                    data.putString(MainActivity.ALBUM_ARTIST, album.getArtistName());
                    data.putInt(MainActivity.ALBUM_YEAR, album.getYear());
                    data.putInt(MainActivity.ALBUM_TRACK_COUNT, album.getTrackCount());
                    returnToMain(MainActivity.ACTION_SHOW_ALBUM, data);
                    break;
            }
        }
    }



    /* *********************************************************************************************
     * Vue liste artiste
     * ********************************************************************************************/

    class ArtistViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        final TextView vName;
        final TextView vAlbumCount;
        final ImageView vArtistImage;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            vName = (TextView) itemView.findViewById(R.id.artist_name);
            vAlbumCount = (TextView) itemView.findViewById(R.id.album_count);
            vArtistImage = (ImageView) itemView.findViewById(R.id.artwork);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Artist artist = (Artist) mAdapter.getItem(position);

            Bundle data = new Bundle();
            data.putLong(MainActivity.ARTIST_ARTIST_ID, artist.getId());
            data.putString(MainActivity.ARTIST_ARTIST_NAME, artist.getName());
            data.putInt(MainActivity.ARTIST_ALBUM_COUNT, artist.getAlbumCount());
            data.putInt(MainActivity.ARTIST_TRACK_COUNT, artist.getTrackCount());
            returnToMain(MainActivity.ACTION_SHOW_ARTIST, data);
        }
    }



    /* *********************************************************************************************
     * Vue liste morceau
     * ********************************************************************************************/

    class SongViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        final TextView vTitle;
        final TextView vArtist;
        final ImageView vArtwork;

        public SongViewHolder(View itemView) {
            super(itemView);
            vTitle = (TextView) itemView.findViewById(R.id.title);
            vArtist = (TextView) itemView.findViewById(R.id.artist);
            vArtwork = (ImageView) itemView.findViewById(R.id.artwork);
            itemView.findViewById(R.id.item_view).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Song song = (Song) mAdapter.getItem(position);
            switch (v.getId()) {
                case R.id.item_view:
                    selectSong(song);
                    break;
            }
        }

        private void selectSong(Song song) {
            Bundle data = songToBundle(song);

            returnToMain(MainActivity.ACTION_PLAY_SONG, data);
        }

        private Bundle songToBundle(Song song) {
            Bundle data = new Bundle();
            data.putLong(MainActivity.SONG_ID, song.getId());
            data.putString(MainActivity.SONG_TITLE, song.getTitle());
            data.putString(MainActivity.SONG_ARTIST, song.getArtist());
            data.putString(MainActivity.SONG_ALBUM, song.getAlbum());
            data.putLong(MainActivity.SONG_ALBUM_ID, song.getAlbumId());
            data.putInt(MainActivity.SONG_TRACK_NUMBER, song.getTrackNumber());
            return data;
        }
    }


    class SectionViewHolder extends RecyclerView.ViewHolder {

        final TextView vSection;

        public SectionViewHolder(View itemView) {
            super(itemView);
            vSection = (TextView) itemView;
        }
    }

    class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ALBUM = 1;
        private static final int ARTIST = 2;
        private static final int SONG = 3;
        private static final int SECTION_ALBUMS = 4;
        private static final int SECTION_ARTISTS = 5;
        private static final int SECTION_SONGS = 6;


        private final List<Album> mAlbumList = Collections.synchronizedList(new ArrayList<Album>());
        private final List<Artist> mArtistList = Collections.synchronizedList(new ArrayList<Artist>());
        private final List<Song> mSongList = Collections.synchronizedList(new ArrayList<Song>());


        public void setAlbumList(List<Album> albumList) {
            mAlbumList.clear();
            mAlbumList.addAll(albumList);
            refreshIfNecessary();
        }

        private void refreshIfNecessary() {
            if (mAlbumListLoaded && mArtistListLoaded && mSongListLoaded) {
                notifyDataSetChanged();
            }
        }

        public void setArtistList(List<Artist> artistList) {
            mArtistList.clear();
            mArtistList.addAll(artistList);
            refreshIfNecessary();
        }

        public void setSongList(List<Song> songList) {
            mSongList.clear();
            mSongList.addAll(songList);
            refreshIfNecessary();
        }

        public Object getItem(int position) {
            int albumRows = mAlbumList.size() > 0 ? mAlbumList.size() + 1 : 0;

            if (albumRows > position && position != 0) {

                return mAlbumList.get(position - 1);

            }
            int artistRows = mArtistList.size() > 0 ? mArtistList.size() + 1 : 0;
            if (albumRows + artistRows > position && position - albumRows != 0) {
                return mArtistList.get(position - albumRows - 1);
            }
            int songRows = mSongList.size() > 0 ? mSongList.size() + 1 : 0;
            if (albumRows + artistRows + songRows > position && position - albumRows - artistRows != 0) {
                return mSongList.get(position - albumRows - artistRows - 1);
            }
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {


            View itemView;
            RecyclerView.ViewHolder viewHolder;

            switch (type) {
                case ALBUM:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_album_list_item, parent, false);

                    viewHolder = new AlbumViewHolder(itemView);
                    return viewHolder;
                case ARTIST:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_artist_list_item, parent, false);

                    viewHolder = new ArtistViewHolder(itemView);
                    return viewHolder;
                case SONG:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_song_list_item, parent, false);

                    viewHolder = new SongViewHolder(itemView);
                    return viewHolder;

                case SECTION_ALBUMS:
                case SECTION_ARTISTS:
                case SECTION_SONGS:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
                    viewHolder = new SectionViewHolder(itemView);
                    return viewHolder;
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int type = getItemViewType(position);
            int albumRows = mAlbumList.size() > 0 ? mAlbumList.size() + 1 : 0;


            int artistRows = mArtistList.size() > 0 ? mArtistList.size() + 1 : 0;

            switch (type) {
                case ALBUM:
                    Album album = mAlbumList.get(position - 1);
                    ((AlbumViewHolder) viewHolder).vName.setText(album.getAlbumName());
                    ((AlbumViewHolder) viewHolder).vArtist.setText(album.getArtistName());

                    ((AlbumViewHolder) viewHolder).vArtwork.setTag(position);

                    ArtworkCache.getInstance().loadBitmap(album.getId(), ((AlbumViewHolder) viewHolder).vArtwork, mThumbSize, mThumbSize);

                    break;
                case ARTIST:
                    Artist artist = mArtistList.get(position - albumRows - 1);
                    ((ArtistViewHolder) viewHolder).vName.setText(artist.getName());
                    ((ArtistViewHolder) viewHolder).vAlbumCount.setText(getResources()
                            .getQuantityString(R.plurals.albums_count,
                                    artist.getAlbumCount(), artist.getAlbumCount()));

                    ((ArtistViewHolder) viewHolder).vArtistImage.setTag(position);

                    ArtistImageCache.getInstance().loadBitmap(artist.getName(), ((ArtistViewHolder) viewHolder).vArtistImage, mThumbSize, mThumbSize);
                    break;
                case SONG:

                    Song song = mSongList.get(position - albumRows - artistRows - 1);

                    ((SongViewHolder) viewHolder).vTitle.setText(song.getTitle());
                    ((SongViewHolder) viewHolder).vArtist.setText(song.getArtist());

                    ((SongViewHolder) viewHolder).vArtwork.setTag(position);

                    ArtworkCache.getInstance().loadBitmap(song.getAlbumId(), ((SongViewHolder) viewHolder).vArtwork, mThumbSize, mThumbSize);
                    break;
                case SECTION_ALBUMS:
                    ((SectionViewHolder) viewHolder).vSection.setText(R.string.albums);

                    break;
                case SECTION_ARTISTS:
                    ((SectionViewHolder) viewHolder).vSection.setText(R.string.artists);

                    break;
                case SECTION_SONGS:
                    ((SectionViewHolder) viewHolder).vSection.setText(R.string.titles);

                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            int albumRows = mAlbumList.size() > 0 ? mAlbumList.size() + 1 : 0;

            if (albumRows > position) {
                if (position == 0) {
                    return SECTION_ALBUMS;
                }
                return ALBUM;
            }
            int artistRows = mArtistList.size() > 0 ? mArtistList.size() + 1 : 0;
            if (albumRows + artistRows > position) {
                if (position - albumRows == 0) {
                    return SECTION_ARTISTS;
                }
                return ARTIST;
            }
            int songRows = mSongList.size() > 0 ? mSongList.size() + 1 : 0;
            if (albumRows + artistRows + songRows > position) {
                if (position - albumRows - artistRows == 0) {
                    return SECTION_SONGS;
                }
                return SONG;
            }
            return 0;
        }

        @Override
        public int getItemCount() {

            int count = 0;
            if (mAlbumList.size() > 0) {
                count += mAlbumList.size() + 1;
            }
            if (mArtistList.size() > 0) {
                count += mArtistList.size() + 1;
            }
            if (mSongList.size() > 0) {
                count += mSongList.size() + 1;
            }
            return count;

        }
    }
}
