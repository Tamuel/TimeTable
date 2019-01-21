package com.softwork.ydk.middletermproject_time_table.GUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softwork.ydk.middletermproject_time_table.Data.TTDBProvider;
import com.softwork.ydk.middletermproject_time_table.R;
import com.softwork.ydk.middletermproject_time_table.Data.TTData;

import java.util.ArrayList;

/**
 * Created by DongKyu on 2015-10-29.
 */
public class TimeTableLinearLayout extends LinearLayout {
    private TTData.Day day;
    private ArrayList<Integer> lectureTimes;
    private ArrayList<String> lectureNames;
    private ArrayList<String> professorNames;
    private ArrayList<String> lectureRooms;

    private Boolean checkTimeStart;

    private Button[] timeTableButtons;

    public TimeTableLinearLayout(final Context context, TTData.Day _day) {
        super(context);
        lectureTimes = new ArrayList<Integer>();
        lectureNames = new ArrayList<String>();
        professorNames = new ArrayList<String>();
        lectureRooms = new ArrayList<String>();

        checkTimeStart = false;
        day = _day;

        /* Get Data From DB */
        ContentResolver cr = context.getContentResolver();
        String[] selArg = {day.getInt() + ""};
        Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                        + TTDBProvider.DB_LECTURE_TIME_TABLE + "/*"),
                null, TTDBProvider.LECTURE_TIME_DAY, selArg, " ORDER BY " + TTDBProvider.LECTURE_TIME_START_TIME + " asc");

        if(cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                lectureTimes.add(cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_START_TIME)));
                lectureTimes.add(cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_END_TIME)));

                String[] selNameArg = {cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_LECTURE_FK))};
                Cursor getLCur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                                + TTDBProvider.DB_LECTURE_TABLE + "/*"), null, TTDBProvider.LECTURE_ID, selNameArg, null);
                getLCur.moveToFirst();

                lectureNames.add(getLCur.getString(getLCur.getColumnIndex(TTDBProvider.LECTURE_NAME)));
                professorNames.add(getLCur.getString(getLCur.getColumnIndex(TTDBProvider.LECTURE_PROFESSOR)));
                lectureRooms.add(getLCur.getString(getLCur.getColumnIndex(TTDBProvider.LECTURE_ROOM)));
                getLCur.close();

                Log.e("HAVE LECTURE", day + " " + lectureTimes.get(lectureTimes.size() - 2));
                Log.e("HAVE LECTURE", day + " " + lectureTimes.get(lectureTimes.size() - 1));
            } while (cur.moveToNext());
            Log.e("HAVE LECTURE", day + " " + cur.getCount() + " OCC TIME : ");
        }

        cur.close();
        lectureTimes.add(TimeTableActivity.timeStepStrings.length);

        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0);
        layoutParams.weight = 1;
        this.setLayoutParams(layoutParams);
        this.setOrientation(LinearLayout.VERTICAL);

        timeTableButtons = new Button[TimeTableActivity.timeStepStrings.length];

        LayoutParams timeTableButtonLayoutParams = new LayoutParams(
                LayoutParams.FILL_PARENT,
                (int) getResources().getDimension(R.dimen.time_table_button_height),
                0);

        // 빈칸 넣기
        TextView blankTextView = new TextView(context);
        blankTextView.setHeight((int) (getResources().getDimension(R.dimen.time_table_button_height)));
        blankTextView.setBackgroundResource(R.drawable.time_table_button);
        this.addView(blankTextView);

        for(int a : lectureTimes)
        {
            Log.e("TIMES", "LT " + a);
        }

        // Make Time Table buttons
        for(int i = 0; i < TimeTableActivity.timeStepStrings.length; i++) {
            if(lectureTimes.get(0) == i && checkTimeStart) {
                checkTimeStart = false;
                lectureTimes.remove(0);
            }

            if (lectureTimes.get(0) == i && !checkTimeStart) { // 시간표 표시 구획
                checkTimeStart = true;
                final int temp = i;
                LayoutParams timeTableLayoutParams = new LayoutParams(
                        LayoutParams.FILL_PARENT,
                        (int) (getResources().getDimension(R.dimen.time_table_button_height)
                                * (lectureTimes.get(1) - lectureTimes.get(0))),
                        0);
                timeTableButtons[i] = new Button(context);
                timeTableButtons[i].setPadding(0, 0, 0, 0);
                timeTableButtons[i].setText(lectureNames.get(0) + "\n"
                        + professorNames.get(0) + "\n" + lectureRooms.get(0));
                timeTableButtons[i].setTextSize(getResources().getDimension(R.dimen.time_table_text_size));
                timeTableButtons[i].setTextColor(getResources().getColor(R.color.contentColor));
                timeTableButtons[i].setLayoutParams(timeTableLayoutParams);
                timeTableButtons[i].setBackgroundResource(R.drawable.exist_time_table_button);
                timeTableButtons[i].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setPositiveButton(getResources().getString(R.string.edit_lecture),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* Get Lecture DB */
                                    String[] selArg = {(temp) + ""};
                                    ContentResolver cr = context.getContentResolver();
                                    Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                                                    + TTDBProvider.DB_LECTURE_TIME_TABLE + "/*"),
                                            null, TTDBProvider.LECTURE_TIME_START_TIME, selArg, null);

                                    int lectureID = 0;
                                    int timeID = 0;
                                    int endTime = 0;
                                    if (cur.getCount() != 0) {
                                        cur.moveToFirst();
                                        do {
                                            if (cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_DAY)) == day.getInt()) {
                                                timeID = cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_ID));
                                                lectureID = cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_LECTURE_FK));
                                                endTime = cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_END_TIME));
                                                break;
                                            }
                                        } while (cur.moveToNext());
                                    }
                                    cur.close();

                                    Intent existLecture = new Intent(context, LectureActivity.class);
                                    existLecture.putExtra(TTData.GET_TIME_TABLE_START_TIME, temp);
                                    existLecture.putExtra(TTData.GET_TIME_TABLE_END_TIME, endTime - 1);
                                    existLecture.putExtra(TTData.GET_TIME_TABLE_DATE, day);
                                    existLecture.putExtra(TTData.GET_TIME_TABLE_ID, lectureID);
                                    existLecture.putExtra(TTData.GET_TIME_TABLE_TIME_ID, timeID);
                                    ((Activity) context).startActivity(existLecture);
                                    dialog.dismiss();     //닫기
                                }
                        });
                        alert.setNegativeButton(getResources().getString(R.string.lecture_note),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /* Get Lecture DB */
                                    String[] selArg = {(temp) + ""};
                                    ContentResolver cr = context.getContentResolver();
                                    Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                                                    + TTDBProvider.DB_LECTURE_TIME_TABLE + "/*"),
                                            null, TTDBProvider.LECTURE_TIME_START_TIME, selArg, null);
                                    int lectureID = 0;
                                    if (cur.getCount() != 0) {
                                        cur.moveToFirst();
                                        do {
                                            if (cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_DAY)) == day.getInt()) {
                                                lectureID = cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_LECTURE_FK));
                                                break;
                                            }
                                        } while (cur.moveToNext());
                                    }
                                    cur.close();

                                    Intent noteActivity = new Intent(context, AdviceNoteActivity.class);
                                    noteActivity.putExtra(TTData.GET_TIME_TABLE_ID, lectureID);
                                    context.startActivity(noteActivity);
                                    dialog.dismiss();     //닫기
                                }
                        });
                        alert.show();
                    }
                });

                this.addView(timeTableButtons[i]);
                lectureTimes.remove(0);
                lectureNames.remove(0);
                lectureRooms.remove(0);
                professorNames.remove(0);

            }
            else if(i < lectureTimes.get(0) && !checkTimeStart) { // 빈 구획

                final int temp = i;
                timeTableButtons[i] = new Button(context);
                timeTableButtons[i].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent newLecture = new Intent(context, LectureActivity.class);
                        newLecture.putExtra(TTData.GET_TIME_TABLE_START_TIME, temp);
                        newLecture.putExtra(TTData.GET_TIME_TABLE_DATE, day);
                        ((Activity) context).startActivity(newLecture);
                    }
                });

                timeTableButtons[i].setLayoutParams(timeTableButtonLayoutParams);
                timeTableButtons[i].setBackgroundResource(R.drawable.time_table_button);
                this.addView(timeTableButtons[i]);
            }
        }
    }

    public TTData.Day getDay() {
        return day;
    }

    public void setDay(TTData.Day day) {
        this.day = day;
    }
}
