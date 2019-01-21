package com.softwork.ydk.middletermproject_time_table.GUI;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softwork.ydk.middletermproject_time_table.R;
import com.softwork.ydk.middletermproject_time_table.Data.TTDBProvider;
import com.softwork.ydk.middletermproject_time_table.Data.TTData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeTableActivity extends Activity {
    static public ArrayList<String> weekDayStrings;
    static public String[] timeStepStrings;

    private Button[] weekDayButtons;

    private LinearLayout timeTableLayout;
    private LinearLayout weekDayLayout;
    private TimeTableTimeLinearLayout timeTableTimeLayout;
    private TimeTableLinearLayout[] timeTableLayouts; // Index 0 for show time step


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(TTDBProvider.DB_DATA_CONTENT_URI,
                null, null, null, null);

        if(cur.getCount() == 0) {
            Intent getData = new Intent(TimeTableActivity.this, MakeTimeTableActivity.class);
            startActivityForResult(getData, TTData.GET_TIME_TABLE_CODE);
        }
        else {
            cur.moveToFirst();
            String timeTableName = cur.getString(cur.getColumnIndex(TTDBProvider.DB_DATA_NAME));
            TextView tableName = (TextView) findViewById(R.id.time_table_name_text_view);
            tableName.setText(timeTableName);
            TTData.TIME_TABLE_NAME = timeTableName;
            TTData.TIME_TABLE_ID = cur.getInt(cur.getColumnIndex(TTDBProvider.DB_DATA_ID));
        }

        cur.close();

        weekDayLayout = (LinearLayout) findViewById(R.id.week_layout);
        timeTableLayout = (LinearLayout) findViewById(R.id.timeTableOuterLayout);

        weekDayStrings = new ArrayList<String>();
        weekDayStrings.add("");
        for(String temp : getResources().getStringArray(R.array.week_day))
            weekDayStrings.add(temp);
        timeStepStrings = getResources().getStringArray(R.array.time_table_hours);

        weekDayButtons = new Button[weekDayStrings.size()];
        timeTableTimeLayout = new TimeTableTimeLinearLayout(this);
        timeTableLayouts = new TimeTableLinearLayout[weekDayStrings.size() - 1];

        // Make week day buttons
        for(int i = 0; i < weekDayStrings.size(); i++) {
            weekDayButtons[i] = new Button(this);
            weekDayButtons[i].setText(weekDayStrings.get(i));
            weekDayButtons[i].setTextSize(getResources().getDimension(R.dimen.day_text_size));
            weekDayButtons[i].setTextColor(getResources().getColor(R.color.tableTimeStepColor));
            weekDayButtons[i].setBackgroundResource(R.drawable.day_button);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    (int) getResources().getDimension(R.dimen.time_table_button_height),
                    0);
            layoutParams.weight = 1;
            weekDayButtons[i].setLayoutParams(layoutParams);
            weekDayLayout.addView(weekDayButtons[i]);
        }

        makeTimeTableLayouts();
    }

    public void makeTimeTableLayouts() {
        timeTableLayout.removeAllViews();
        timeTableLayout.addView(timeTableTimeLayout);
        for(int i = 0; i < weekDayStrings.size() - 1; i++) {
            timeTableLayouts[i] = new TimeTableLinearLayout(this, TTData.Day.values()[i]);
            timeTableLayout.addView(timeTableLayouts[i]);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        makeTimeTableLayouts();
    }

    public void onButtonClick(View v) {
//        switch (v.getId()) {
//            case R.id.change_time_table_button:
//                Intent changeTimeTable = new Intent(TimeTableActivity.this, ChooseTimeTableActivity.class);
//                startActivityForResult(changeTimeTable, TTData.GET_ANOTHER_TIME_TABLE);
//                break;
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TTData.GET_TIME_TABLE_CODE) {
            if(resultCode == TTData.RESULT_OK) {
                TextView tableName = (TextView) findViewById(R.id.time_table_name_text_view);
                TTData.TIME_TABLE_NAME = data.getStringExtra(TTData.GET_TIME_TABLE_NAME);
                tableName.setText(TTData.TIME_TABLE_NAME);

                ContentResolver cr = getContentResolver();
                ContentValues content = new ContentValues();

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

                content.put(TTDBProvider.DB_DATA_NAME, TTData.TIME_TABLE_NAME);
                content.put(TTDBProvider.DB_DATA_DATE, simpleDateFormat.format(cal.getTime()));
                content.put(TTDBProvider.DB_DATA_SEMESTER, "3학년 2학기");

                Uri newDBUri = cr.insert(TTDBProvider.DB_DATA_CONTENT_URI, content);

                TTData.TIME_TABLE_ID = Integer.parseInt(newDBUri.getPathSegments().get(1));
            }
        } else if(requestCode == TTData.GET_ANOTHER_TIME_TABLE) {
            if(resultCode == TTData.RESULT_OK) {
                TextView tableName = (TextView) findViewById(R.id.time_table_name_text_view);
                TTData.TIME_TABLE_NAME = data.getStringExtra(TTData.GET_TIME_TABLE_NAME);
                tableName.setText(TTData.TIME_TABLE_NAME);
                TTData.TIME_TABLE_ID = data.getIntExtra(TTData.GET_TIME_TABLE_ID, 0);
                makeTimeTableLayouts();
            }
        }
    }
}
