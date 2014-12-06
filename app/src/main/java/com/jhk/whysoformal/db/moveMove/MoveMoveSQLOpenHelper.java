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

package com.jhk.whysoformal.db.moveMove;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Ji Kim on 12/3/2014.
 */
public class MoveMoveSQLOpenHelper extends SQLiteOpenHelper {

    private static final String sDB_NAME = "MOVE_MOVE";
    private static final String sRESOURCE_ENTRIES_TABLE = "RESOURCE_ENTRIES";
    private static final String sRESOURCE_ENTRY_NAME_COLUMN = "RESOURCE_ENTRY_NAME";
    private static final String sRESOURCE_ENTRY_URL_COLUMN = "RESOURCE_URL_NAME";
    private static final String sRESOURCE_ENTRY_EMAIL_COLUMN = "RESOURCE_EMAIL_NAME";

    private static final String sCOMMON_RESOURCES_TABLE = "COMMON_RESOURCES";
    private static final String sCOMMON_RESOURCE_NAME_COLUMN = "COMMON_RESOURCE_NAME";
    private static final String sCOMMON_RESOURCE_URL_COLUMN = "COMMON_RESOURCE_URL";
    private static final String sCOMMON_RESOURCE_EMAIL_COLUMN = "COMMON_RESOURCE_EMAIL";
    private static final int sVERSION = 1;

    public MoveMoveSQLOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, sDB_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + sCOMMON_RESOURCES_TABLE + " (" + BaseColumns._ID + " integer primary key autoincrement, " + sCOMMON_RESOURCE_NAME_COLUMN +
                    " VARCHAR, " + sCOMMON_RESOURCE_URL_COLUMN + " VARCHAR, " + sCOMMON_RESOURCE_EMAIL_COLUMN + " VARCHAR)");
        db.execSQL("CREATE TABLE " + sRESOURCE_ENTRIES_TABLE + " (" + BaseColumns._ID + " integer primary key autoincrement, " + sRESOURCE_ENTRY_NAME_COLUMN +
                " VARCHAR, " + sRESOURCE_ENTRY_URL_COLUMN + " VARCHAR, " + sRESOURCE_ENTRY_EMAIL_COLUMN + " VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertResourceEntry() {
        ContentValues cv = new ContentValues();
        return getWritableDatabase().insert(sRESOURCE_ENTRIES_TABLE, null, cv);
    }
}
