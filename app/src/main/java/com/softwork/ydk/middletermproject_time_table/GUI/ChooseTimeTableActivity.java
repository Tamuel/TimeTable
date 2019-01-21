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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.softwork.ydk.middletermproject_time_table.Data.TTDBProvider;
import com.softwork.ydk.middletermproject_time_table.Data.TTData;
import com.softwork.ydk.middletermproject_time_table.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChooseTimeTableActivity extends Activity implements AdapterView.OnItemClickListener{
    private ListView timeTablesListView;

    private ArrayList<Integer> timeTableIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_time_table);
        timeTableIDs = new ArrayList<Integer>();

        timeTablesListView = (ListView) findViewById(R.id.time_tables_list_view);
        timeTablesListView.setOnItemClickListener(this);

        makeTimeTableList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent data = new Intent();
        ContentResolver cr = getContentResolver();
        String[] selArg = {timeTableIDs.get(position) + ""};
        Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                        + TTDBProvider.DB_DATA_TABLE + "/*"),
                null, TTDBProvider.DB_DATA_ID, selArg, null);

        cur.moveToFirst();

        data.putExtra(TTData.GET_TIME_TABLE_ID, timeTableIDs.get(position));
        data.putExtra(TTData.GET_TIME_TABLE_NAME, cur.getString(cur.getColumnIndex(TTDBProvider.DB_DATA_NAME)));
        setResult(TTData.RESULT_OK, data);
        finish();
    }

    public void makeTimeTableList() {
        timeTableIDs.clear();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(TTDBProvider.DB_DATA_CONTENT_URI,
                null, null, null, " ORDER BY " + TTDBProvider.DB_DATA_DATE + " asc");

        if (cur == null) {
            ((new Toast(this)).makeText(this,
                    "Cannot get Content Resolver", Toast.LENGTH_SHORT)).show();
            return;
        }

        cur.moveToFirst();

        do {
            timeTableIDs.add(cur.getInt(cur.getColumnIndex(TTDBProvider.DB_DATA_ID)));
        } while(cur.moveToNext());

        TimeTablesAdapter adapt = new TimeTablesAdapter(this, cur);

        timeTablesListView.setAdapter(adapt);
    }

    public void onButtonClick(View v) {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.add_new_time_table_dialog,(ViewGroup) findViewById(R.id.layout_root));

        AlertDialog.Builder aDialog = new AlertDialog.Builder(ChooseTimeTableActivity.this);
        aDialog.setView(layout);

        aDialog.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ContentResolver cr = getContentResolver();
                ContentValues newTimeTable = new ContentValues();

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

                newTimeTable.put(TTDBProvider.DB_DATA_NAME,
                        ((EditText)layout.getTouchables().get(0)).getText().toString());
                newTimeTable.put(TTDBProvider.DB_DATA_DATE,
                        simpleDateFormat.format(cal.getTime()));

                Uri newLectureUri = cr.insert(TTDBProvider.DB_DATA_CONTENT_URI, newTimeTable);
                Log.e("NEW TIME TABLE URI!!!!", newLectureUri.toString());
                makeTimeTableList();
            }
        });
        aDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog ad = aDialog.create();
        ad.show();
    }
}
