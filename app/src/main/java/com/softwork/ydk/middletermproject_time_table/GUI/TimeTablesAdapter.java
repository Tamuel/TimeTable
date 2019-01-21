package com.softwork.ydk.middletermproject_time_table.GUI;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.softwork.ydk.middletermproject_time_table.Data.TTDBProvider;
import com.softwork.ydk.middletermproject_time_table.R;

/**
 * Created by DongKyu on 2015-11-01.
 */
public class TimeTablesAdapter extends CursorAdapter {

    public TimeTablesAdapter(Context context, Cursor cur) {
        super(context, cur);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final TextView timeView = (TextView) view.findViewById(R.id.show_time_table_text_view);

        timeView.setText(cursor.getString(cursor.getColumnIndex(TTDBProvider.DB_DATA_NAME)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.time_tables_adapter_layout, parent, false);
        return v;
    }
}
