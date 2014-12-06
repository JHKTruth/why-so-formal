/*
 * Copyright 2014 Ji Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jhk.whysoformal.activity.musiclist.fragment;

import com.jhk.whysoformal.R;
import com.jhk.whysoformal.algorithm.AudioShuffler;
import com.jhk.whysoformal.context.AudioContext;
import com.jhk.whysoformal.custom.view.graph.GraphStyleAttributes;
import com.jhk.whysoformal.custom.view.graph.GraphView;
import com.jhk.whysoformal.custom.view.graph.axis.BaseAxis;
import com.jhk.whysoformal.custom.view.graph.series.BaseSeries;
import com.jhk.whysoformal.service.MusicService;
import com.jhk.whysoformal.util.Constants;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public class MusicListFragment extends ListFragment
                                implements SearchView.OnQueryTextListener {

    private static String TAG = "MusicListFragment";
    
    private Callbacks mCallbacks;
    private PopupWindow mItemWeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.music_list);

        setHasOptionsMenu(true);

        AudioContext aContext = AudioContext.getInstance();
        Context appContext = getActivity().getApplicationContext();
        aContext.setApplicationContext(appContext);

        setListAdapter(new MusicListAdapter(appContext, refreshCursor()));
        getActivity().startService(new Intent(getActivity(), MusicService.class));
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getActionBar().setIcon(R.drawable.icon_music_player);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        
        ListView listView = (ListView) view.findViewById(android.R.id.list);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AudioContext.getInstance().pushPlayEntry(new AudioContext.PlayEntry(position, id));
        mCallbacks.onMusicSelected();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean retVal = true;

        switch (item.getItemId()) {
            case R.id.list_music_fragment_menu_item_search:
                getActivity().onSearchRequested();
                break;
            case R.id.list_music_fragment_menu_item_clear_search:
                updateSearchQuery(null);
                break;
            case R.id.list_music_fragment_menu_item_weight:
                showItemWeightPopUp();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return retVal;
    }

    private void showItemWeightPopUp() {
        if(mItemWeight == null) {
            mItemWeight = new PopupWindow(getListView());

            View view = getActivity().getLayoutInflater().inflate(R.layout.popup_weight_distribution, null);
            LinearLayout distributionLayout = (LinearLayout) view.findViewById(R.id.distributionLayout);
            Context ctx = mItemWeight.getContentView().getContext();

            BaseSeries data = new BaseSeries(getString(R.string.weight_distribution),
                                                new float[][]{new float[]{0.0f, 2.0f}, new float[]{1.0f, 1.0f}, new float[]{2.0f, 1.0f}, new float[]{3.0f, 1.0f}, new float[]{4.0f, 1.0f}},
                                                10,
                                                new GraphStyleAttributes(Color.BLUE, 20f, 3f));
            List<BaseSeries> entries = new LinkedList<BaseSeries>();
            entries.add(data);

            distributionLayout.addView(new GraphView(ctx, entries, getString(R.string.distribute_me),
                                        new BaseAxis(0, 4, 1, 10, GraphStyleAttributes.sDEFAULT_X_AXIS_LABEL_STYLE_ATTRIBUTES),
                                        new BaseAxis(0, 2, 1, 10, GraphStyleAttributes.sDEFAULT_Y_AXIS_LABEL_STYLE_ATTRIBUTES)));

            view.invalidate();
            Button okayButton = (Button) view.findViewById(R.id.weight_distribution_ok_button);
            okayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemWeight.dismiss();
                }
            });

            mItemWeight.setFocusable(true);
            mItemWeight.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
            mItemWeight.setHeight(600);
            mItemWeight.setContentView(view);
        }

        mItemWeight.showAtLocation(getListView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_list_music, menu);

        MenuItem searchItem = menu.findItem(R.id.list_music_fragment_menu_item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);

        //SearchableInfo searchInfo = searchManager.getSearchableInfo(getActivity().getComponentName());
        //searchView.setSearchableInfo(searchInfo);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        updateSearchQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateSearchQuery(String query) {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
                .putString(AudioContext.SEARCH_QUERY, query).commit();
        update();
    }

    public void update() {
        //Need to reset the cursor since the query could have been updated

        MusicListAdapter mlAdapter = (MusicListAdapter) getListAdapter();
        mlAdapter.changeCursor(refreshCursor());
        mlAdapter.notifyDataSetChanged();
    }

    private static class MusicListAdapter extends SimpleCursorAdapter {
        
        private static final String[] COLUMNS = new String[]{ MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns._ID};
        private static final int[] TEXT_VIEW_MAPPER = new int[]{R.id.list_music_item_display_name_text_view};
        
        public MusicListAdapter(Context context, Cursor cursor) {
            super(context, R.layout.fragment_music_list, cursor, COLUMNS, TEXT_VIEW_MAPPER, 0);
        }
        
    }

    public interface Callbacks {
        void onMusicSelected();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        mCallbacks = Callbacks.class.cast(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private Cursor refreshCursor() {
        AudioContext aContext = AudioContext.getInstance();

        String query = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(AudioContext.SEARCH_QUERY, null);

        //numberOfPartitions is the xAxis whereas the weight distribution is the yAxis. Allow user to drag the curve around, and get the value at 1, 2, 3, and 4 to denote the weight distribution
        //int numberOfPartitions = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt(AudioShuffler.NUMBER_OF_PARTITIONS, AudioShuffler.DEFAULT_NUMBER_OF_PARTITIONS);
        String weightDistributionString = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(AudioShuffler.WEIGHT_DISTRIBUTION, null);
        float[] weightDistribution;

        if(weightDistributionString == null) {
            weightDistribution = AudioShuffler.DEFAULT_WEIGHT_DISTRIBUTION;
        }else{
            String[] splitted = weightDistributionString.split(",");
            weightDistribution = new float[splitted.length];

            for(int i=0; i < splitted.length; i++) {
                weightDistribution[i] = Float.parseFloat(splitted[i]);
            }
        }

        Cursor cursor;

        if(query != null) {
            cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.MediaColumns.DISPLAY_NAME + " LIKE ?", new String[]{"%" + query + "%"}, MediaStore.MediaColumns.DATA + " " + Constants.DESC_ORDER);
        }else {
            cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.MediaColumns.DATA + " " + Constants.DESC_ORDER);
        }
        aContext.setCursor(cursor, weightDistribution);
        AudioContext.getInstance().clearPlayEntry();
        return cursor;
    }

    public static MusicListFragment newInstance() {
        return new MusicListFragment();
    }

}
