package org.oucho.musicplayer.loaders;

import android.provider.MediaStore;

/**
 * Holds all of the sort orders for each list type.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public final class SortOrder {

    /**
     * Artist sort order entries.
     */
    public interface ArtistSortOrder {
        /* Artist sort order A-Z */
        String ARTIST_A_Z = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER;

        /* Artist sort order Z-A */
        String ARTIST_Z_A = ARTIST_A_Z + " DESC";

        /* Artist sort order number of songs */
        String ARTIST_NUMBER_OF_SONGS = MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                + " DESC";

        /* Artist sort order number of albums */
        String ARTIST_NUMBER_OF_ALBUMS = MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
                + " DESC";
    }

    /**
     * Album sort order entries.
     */
    public interface AlbumSortOrder {
        /* Album sort order A-Z */
        String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

        /* Album sort order Z-A */
        String ALBUM_Z_A = ALBUM_A_Z + " DESC";

        /* Album sort order songs */
        String ALBUM_NUMBER_OF_SONGS = MediaStore.Audio.Albums.NUMBER_OF_SONGS
                + " DESC";

        /* Album sort order artist */
        String ALBUM_ARTIST = MediaStore.Audio.Albums.ARTIST;

        /* Album sort order year */
        String ALBUM_YEAR = MediaStore.Audio.Albums.FIRST_YEAR + " DESC";

    }

    /**
     * Song sort order entries.
     */
    public interface SongSortOrder {
        /* Song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        /* Song sort order Z-A */
        String SONG_Z_A = SONG_A_Z + " DESC";

        /* Song sort order artist */
        String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;

        /* Song sort order album */
        String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;

        /* Song sort order year */
        String SONG_YEAR = MediaStore.Audio.Media.YEAR + " DESC";

        /* Song sort order duration */
        String SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC";

    }

    /**
     * Album song sort order entries.
     */
    public interface AlbumSongSortOrder {
        /* Album song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    }

    /**
     * Artist song sort order entries.
     */
    public interface ArtistSongSortOrder {
        /* Artist song sort order A-Z */
        String SONG_A_Z = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

    }

    /**
     * Artist album sort order entries.
     */
    public interface ArtistAlbumSortOrder {
        /* Artist album sort order A-Z */
        String ALBUM_A_Z = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER;

    }

}
