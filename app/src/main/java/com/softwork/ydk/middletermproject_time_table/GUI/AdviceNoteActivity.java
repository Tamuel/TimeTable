package com.softwork.ydk.middletermproject_time_table.GUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.softwork.ydk.middletermproject_time_table.Data.TTDBProvider;
import com.softwork.ydk.middletermproject_time_table.Data.TTData;
import com.softwork.ydk.middletermproject_time_table.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdviceNoteActivity extends Activity {
    private ListView listView;

    private int lectureID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice_note);

        Intent getData = getIntent();
        lectureID = getData.getIntExtra(TTData.GET_TIME_TABLE_ID, 0);

        listView = (ListView) findViewById(R.id.advice_note_list_view);

        makeNoteList();
    }

    public void makeNoteList() {
        ContentResolver cr = getContentResolver();
        String[] selArg = {lectureID + ""};
        Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                        + TTDBProvider.DB_LECTURE_NOTE_TABLE + "/*"),
                null, TTDBProvider.LECTURE_NOTE_LECTURE_FK, selArg, " ORDER BY " + TTDBProvider.LECTURE_NOTE_TIME + " desc");

        if (cur == null) {
            ((new Toast(this)).makeText(this,
                    "Cannot get Content Resolver", Toast.LENGTH_SHORT)).show();
            return;
        }

        Log.e("NOTE NUM", cur.getCount() + "");

        cur.moveToFirst();
        NoteCursorAdapter adapt = new NoteCursorAdapter(this, cur);

        listView.setAdapter(adapt);
    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.make_new_note_button:
                Context mContext = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.add_new_note_dialog,(ViewGroup) findViewById(R.id.layout_root));

                AlertDialog.Builder aDialog = new AlertDialog.Builder(AdviceNoteActivity.this);
                aDialog.setView(layout);

                aDialog.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ContentResolver cr = getContentResolver();
                        ContentValues newNote = new ContentValues();

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

                        newNote.put(TTDBProvider.LECTURE_NOTE_CONTENT,
                                ((EditText)layout.getTouchables().get(0)).getText().toString());
                        newNote.put(TTDBProvider.LECTURE_NOTE_TIME,
                                simpleDateFormat.format(cal.getTime()));
                        newNote.put(TTDBProvider.LECTURE_NOTE_LECTURE_FK,
                                lectureID + "");

                        Uri newLectureUri = cr.insert(TTDBProvider.NOTE_TABLE_CONTENT_URI, newNote);
                        Log.e("NEW LECTURE URI!!!!", newLectureUri.toString());
                        makeNoteList();
                    }
                });
                aDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog ad = aDialog.create();
                ad.show();
                break;

            case R.id.note_delete_button:
                break;
        }
    }
}