package com.softwork.ydk.middletermproject_time_table.GUI;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
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
public class NoteCursorAdapter extends CursorAdapter{

    public NoteCursorAdapter(Context context, Cursor cur) {
        super(context, cur);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        final TextView timeView = (TextView) view.findViewById(R.id.show_time_text_view);
        final TextView contentView = (TextView) view.findViewById(R.id.show_content_text_view);
        final Button deleteButton = (Button) view.findViewById(R.id.note_delete_button);
        final ContentResolver cr = context.getContentResolver();
        final Context con = context;
        final int noteID = cursor.getInt(cursor.getColumnIndex(TTDBProvider.LECTURE_NOTE_ID));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cr.delete(TTDBProvider.NOTE_TABLE_CONTENT_URI, TTDBProvider.LECTURE_NOTE_ID
                        + " = '" + noteID + "'", null);
                ((AdviceNoteActivity)con).makeNoteList();
            }
        });

        timeView.setText(cursor.getString(cursor.getColumnIndex(TTDBProvider.LECTURE_NOTE_TIME)));
        contentView.setText(cursor.getString(cursor.getColumnIndex(TTDBProvider.LECTURE_NOTE_CONTENT)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.note_adapter_layout, parent, false);
        return v;
    }
}
