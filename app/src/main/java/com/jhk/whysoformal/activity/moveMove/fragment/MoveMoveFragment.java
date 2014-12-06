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

package com.jhk.whysoformal.activity.moveMove.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.jhk.whysoformal.R;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public class MoveMoveFragment extends ListFragment {

    private static final String TAG = "MoveMoveFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        setListAdapter(new MoveMoveListAdapter(getActivity().getApplicationContext(), refreshCursor()));
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getActionBar().setIcon(R.drawable.move_move);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
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

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Cursor refreshCursor() {
        Cursor cursor = null;

        //content resolver is when one application wishes to access an another application's DB. since it is
        //contained by the app, create an SQLHelper and return the cursor

        return cursor;
    }

    public static MoveMoveFragment newInstance() {
        return new MoveMoveFragment();
    }

    private static class MoveMoveListAdapter extends SimpleCursorAdapter {
        private static final String sRESOURCE_NAME = "RESOURCE_NAME";
        private static final String sEMAIL_ADDRESS = "EMAIL_ADDRESS";
        private static final String sCHANGE_RESOURCE_URL = "CHANGE_RESOURCE_URL";
        private static final String sCHANGE_RESOURCE_EMAIL = "CHANGE_RESOURCE_EMAIL";

        private static final String[] sRESOURCE_NAMES = new String[]{"American Express, Bank of America, Capital One, Chase, Wells Fargo"};
        private static final String[] sCOLUMNS = new String[]{ sRESOURCE_NAME, sEMAIL_ADDRESS, sCHANGE_RESOURCE_URL, sCHANGE_RESOURCE_EMAIL};
        private static final int[] sVIEW_MAPPER = new int[]{R.id.moveMoveResourceName, R.id.moveMoveEmail, R.id.moveMoveChangeResourceURL, R.id.changeResourceEmail};

        public MoveMoveListAdapter(Context context, Cursor cursor) {
            super(context, R.layout.fragment_move_move_list, cursor, sCOLUMNS, sVIEW_MAPPER, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = super.newView(context, cursor, parent);

            ArrayAdapter<String> resourceNames = new ArrayAdapter<String>(context, R.layout.fragment_move_move_list, sRESOURCE_NAMES);
            AutoCompleteTextView resourceNamesACTV = (AutoCompleteTextView) view.findViewById(R.id.moveMoveResourceName);
            resourceNamesACTV.setAdapter(resourceNames);

            return view;
        }

    }

}
