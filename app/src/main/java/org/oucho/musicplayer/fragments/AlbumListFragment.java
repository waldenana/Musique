package org.oucho.musicplayer.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.oucho.musicplayer.MainActivity;
import org.oucho.musicplayer.R;
import org.oucho.musicplayer.adapters.AlbumListAdapter;
import org.oucho.musicplayer.adapters.BaseAdapter;
import org.oucho.musicplayer.dialog.AlbumEditorDialog;
import org.oucho.musicplayer.dialog.PlaylistPicker;
import org.oucho.musicplayer.loaders.AlbumLoader;
import org.oucho.musicplayer.loaders.SortOrder;
import org.oucho.musicplayer.model.Album;
import org.oucho.musicplayer.model.Playlist;
import org.oucho.musicplayer.utils.PlaylistsUtils;
import org.oucho.musicplayer.utils.PrefUtils;
import org.oucho.musicplayer.widgets.FastScroller;

import java.util.List;


public class AlbumListFragment extends BaseFragment {

    private AlbumListAdapter mAdapter;


    private final LoaderManager.LoaderCallbacks<List<Album>> mLoaderCallbacks = new LoaderCallbacks<List<Album>>() {

        @Override
        public Loader<List<Album>> onCreateLoader(int id, Bundle args) {

            AlbumLoader loader = new AlbumLoader(getActivity());
            loader.setSortOrder(PrefUtils.getInstance().getAlbumSortOrder());

            return loader;

        }

        @Override
        public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
            mAdapter.setData(data);

            PrefUtils prefUtils = PrefUtils.getInstance();
            String sortOrder = prefUtils.getAlbumSortOrder();

            mShowScrollerBubble = SortOrder.AlbumSortOrder.ALBUM_A_Z.equals(sortOrder);

            if (mFastScroller != null) {
                mFastScroller.setShowBubble(mShowScrollerBubble);
            }


        }

        @Override
        public void onLoaderReset(Loader<List<Album>> loader) {
            //  Auto-generated method stub

        }
    };


    private final AlbumEditorDialog.OnEditionSuccessListener mOnEditionSuccessListener = new AlbumEditorDialog.OnEditionSuccessListener() {
        @Override
        public void onEditionSuccess() {
            ((MainActivity) getActivity()).refresh();
        }
    };
    private final BaseAdapter.OnItemClickListener mOnItemClickListener = new BaseAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, View view) {
            Album album = mAdapter.getItem(position);

            switch (view.getId()) {
                case R.id.album_artwork:
                case R.id.album_info:
                    Fragment fragment = AlbumFragment.newInstance(album);
                    ((MainActivity) getActivity()).setFragment(fragment);
                    break;
                case R.id.menu_button:
                    showMenu(position, view);
                    break;

            }
        }
    };

    private boolean mShowScrollerBubble = true;
    private FastScroller mFastScroller;

    public AlbumListFragment() {
        // Required empty public constructor
    }

    public static AlbumListFragment newInstance() {

        return new AlbumListFragment();
    }

    private void showMenu(final int position, View v) {

        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.album_list_item, popup.getMenu());
        final Album album = mAdapter.getItem(position);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_edit_tags:
                        showEditorDialog(album);
                        return true;
                    case R.id.action_add_to_playlist:
                        showPlaylistPicker(album);
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private void showEditorDialog(Album album) {
        AlbumEditorDialog dialog = AlbumEditorDialog.newInstance(album);
        dialog.setOnEditionSuccessListener(mOnEditionSuccessListener);
        dialog.show(getChildFragmentManager(), "edit_album_tags");
    }

    private void showPlaylistPicker(final Album album) {
        PlaylistPicker picker = PlaylistPicker.newInstance();
        picker.setListener(new PlaylistPicker.OnPlaylistPickedListener() {
            @Override
            public void onPlaylistPicked(Playlist playlist) {
                PlaylistsUtils.addAlbumToPlaylist(getActivity().getContentResolver(), playlist.getId(), album.getId());
            }
        });
        picker.show(getChildFragmentManager(), "pick_playlist");

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    ActionBar actionBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_list, container, false);

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        Resources res = getActivity().getResources();

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float screenWidth = size.x;

        float itemWidth = res.getDimension(R.dimen.album_grid_item_width);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Math.round(screenWidth / itemWidth)));

        int artworkSize = res.getDimensionPixelSize(R.dimen.art_size);
        mAdapter = new AlbumListAdapter(getActivity(), artworkSize, artworkSize);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        mFastScroller = (FastScroller) rootView.findViewById(R.id.fastscroller);
        mFastScroller.setShowBubble(mShowScrollerBubble);
        mFastScroller.setSectionIndexer(mAdapter);
        mFastScroller.setRecyclerView(mRecyclerView);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);
    }

    @Override
    public void load() {
        getLoaderManager().restartLoader(0, null, mLoaderCallbacks);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.album_sort_by, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PrefUtils prefUtils = PrefUtils.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                prefUtils.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                load();
                break;
            case R.id.menu_sort_by_artist:
                prefUtils.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST);
                load();
                break;
            case R.id.menu_sort_by_year:
                prefUtils.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR);
                load();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){
            getActivity().setTitle("Album");
        }else  if (visible){
            getActivity().setTitle("Album");
        }
    }

}
