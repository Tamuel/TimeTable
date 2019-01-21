package com.softwork.ydk.middletermproject_time_table.GUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.softwork.ydk.middletermproject_time_table.Data.TTDBProvider;
import com.softwork.ydk.middletermproject_time_table.Data.TTData;
import com.softwork.ydk.middletermproject_time_table.R;

import java.util.ArrayList;

public class LectureActivity extends Activity implements AdapterView.OnItemSelectedListener{
    private EditText lectureNameEditText;
    private EditText lectureProfessorEditText;
    private EditText lectureMajorEditText;
    private EditText lectureRoomEditText;
    private EditText lectureContentEditText;

    private Spinner selectExistLectureSpinner;
    private Spinner startTimeSpinner;
    private Spinner endTimeSpinner;
    private Spinner dateSpinner;

    private ArrayList<String> times;
    private String[] dates;
    private ArrayList<String> lectureNames;
    private ArrayList<Integer> lectureIDs;

    private int startTime;
    private int endTime;
    private TTData.Day day;
    private int existLectureID;
    private int existTimeID;

    private boolean isNewLecture;
    private boolean removeLecture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture);

        removeLecture = false;
        isNewLecture = true;
        lectureNames = new ArrayList<String>();
        lectureIDs = new ArrayList<Integer>();

        Intent getData = getIntent();
        startTime = getData.getIntExtra(TTData.GET_TIME_TABLE_START_TIME, 0);
        endTime = getData.getIntExtra(TTData.GET_TIME_TABLE_END_TIME, 2);
        if(endTime != 2) endTime -= startTime;
        existLectureID = getData.getIntExtra(TTData.GET_TIME_TABLE_ID, 0);
        existTimeID = getData.getIntExtra(TTData.GET_TIME_TABLE_TIME_ID, 0);
        day = (TTData.Day) getData.getSerializableExtra(TTData.GET_TIME_TABLE_DATE);

        lectureNameEditText = (EditText) findViewById(R.id.lecture_name_edit_text);
        lectureProfessorEditText = (EditText) findViewById(R.id.professor_edit_text);
        lectureMajorEditText = (EditText) findViewById(R.id.lecture_major_edit_text);
        lectureRoomEditText = (EditText) findViewById(R.id.lecture_room_edit_text);
        lectureContentEditText = (EditText) findViewById(R.id.lecture_content_edit_text);

        times = new ArrayList<String>();

        dates = getResources().getStringArray(R.array.week_day);
        for(String temp : getResources().getStringArray(R.array.time_table_hours))
            times.add(temp);

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this,
                R.layout.time_table_spinner_dropdown_item, times);

        startTimeSpinner = (Spinner) findViewById(R.id.start_time_spinner);
        startTimeSpinner.setPrompt(getResources().getString(R.string.start_time));
        startTimeSpinner.setAdapter(timeAdapter);
        startTimeSpinner.setOnItemSelectedListener(this);
        startTimeSpinner.setSelection(startTime);

        ArrayAdapter<String> timeAdapter2 = new ArrayAdapter<String>(this,
                R.layout.time_table_spinner_dropdown_item, times.subList(startTime + 1, times.size()));

        endTimeSpinner = (Spinner) findViewById(R.id.end_time_spinner);
        endTimeSpinner.setPrompt(getResources().getString(R.string.end_time));
        endTimeSpinner.setAdapter(timeAdapter2);
        endTimeSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(this,
                R.layout.time_table_spinner_dropdown_item, dates);
        dateSpinner = (Spinner) findViewById(R.id.date_spinner);
        dateSpinner.setPrompt(getResources().getString(R.string.date));
        dateSpinner.setAdapter(dateAdapter);
        dateSpinner.setSelection(day.getInt());
        dateSpinner.setOnItemSelectedListener(this);



        lectureNames.add(getResources().getString(R.string.exist_lecture));
        lectureIDs.add(0);

        ContentResolver cr = getContentResolver();
        Cursor cur;

        cur = cr.query(TTDBProvider.LECTURE_TABLE_CONTENT_URI, null, null, null, " ORDER BY " + TTDBProvider.LECTURE_ID + " asc");
        if (cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                lectureNames.add(cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_NAME)));
                lectureIDs.add(cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_ID)));
            } while (cur.moveToNext());
        }
        cur.close();


        ArrayAdapter<String> lectureAdapter = new ArrayAdapter<String>(this,
                R.layout.time_table_spinner_dropdown_item, lectureNames);
        selectExistLectureSpinner = (Spinner) findViewById(R.id.select_exist_lecture_spinner);
        selectExistLectureSpinner.setAdapter(lectureAdapter);
        selectExistLectureSpinner.setOnItemSelectedListener(this);
        selectExistLectureSpinner.setSelection(lectureIDs.indexOf(existLectureID));
        if(existLectureID != 0) { // 시간표에서 강의를 선택했을 경우
            selectExistLectureSpinner.setEnabled(false);
            Button button = (Button) findViewById(R.id.make_new_lecture_button); // 버튼 텍스트를 강의 수정으로 바꿔줌
            button.setText(getResources().getString(R.string.edit_lecture));

            String[] selArg = {existLectureID + ""};
            cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                            + TTDBProvider.DB_LECTURE_TIME_TABLE + "/*"), null,
                    TTDBProvider.LECTURE_TIME_LECTURE_FK, selArg, null);

            Button button2 = (Button) findViewById(R.id.lecture_cancel_button); // 버튼 텍스트를 삭제로 바꿔줌
            if(cur.getCount() == 1) {
                removeLecture = true;
                button2.setText(getResources().getString(R.string.delete_whole_lecture));
            } else {
                button2.setText(getResources().getString(R.string.delete_lecture));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.start_time_spinner:
                ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this,
                        R.layout.time_table_spinner_dropdown_item, times.subList(position + 1, times.size()));
                endTimeSpinner.setAdapter(timeAdapter);

                startTime = position;
                int gap = times.size() - position - 2;
                if(gap > endTime)
                    endTimeSpinner.setSelection(endTime);
                else
                    endTimeSpinner.setSelection(gap);
                break;

            case R.id.select_exist_lecture_spinner:
                if(position != 0)
                    getExistLectureData(lectureIDs.get(position));
                else
                    resetEditTexts();
                break;

            case R.id.end_time_spinner:
                endTime = position;
                break;

            case R.id.date_spinner:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.make_new_lecture_button:
                    if (existLectureID != 0) { // 기존에 있던 시간표를 눌렀을 경우
                        updateLecture(existTimeID, existLectureID);
                        finish();
                    } else  if(canEditLecture()) {
                        if (isNewLecture) {
                            addNewLecture();
                            finish();
                        } else {
                            addNewTime(lectureIDs.get(selectExistLectureSpinner.getSelectedItemPosition()));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.overlap_lecture_Exist), Toast.LENGTH_SHORT).show();
                    }
                break;

            case R.id.lecture_cancel_button:
                if(removeLecture == true) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(getResources().getString(R.string.delete_whole_lecture_answer));
                    alert.setPositiveButton(getResources().getString(R.string.delete),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteLectureTime();
                                    dialog.dismiss();     //닫기
                                    finish();
                                }
                            });
                    alert.setNegativeButton(getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                    alert.show();
                }
                else if(existLectureID != 0) {
                    deleteLectureTime();
                    finish();
                } else {
                    finish();
                }
                break;
        }
    }

    /**
     * 겹치는 시간이 있는지 판별
     * */
    public boolean canEditLecture() {
        ArrayList<Integer> getTime = new ArrayList<Integer>();

        String[] selArg = {day.getInt() + ""};
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                        + TTDBProvider.DB_LECTURE_TIME_TABLE + "/*"),
                null, TTDBProvider.LECTURE_TIME_DAY, selArg, " ORDER BY " + TTDBProvider.LECTURE_TIME_START_TIME + " asc");

        if(cur.getCount() != 0) {
            cur.moveToFirst();
            do {
                getTime.add(cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_START_TIME)));
                getTime.add(cur.getInt(cur.getColumnIndex(TTDBProvider.LECTURE_TIME_END_TIME)));
            } while (cur.moveToNext());
        }

        cur.close();

        for(int i = 0; i < getTime.size(); i += 2) {
            if(getTime.get(i) <= startTime && getTime.get(i + 1) > startTime ||
                    getTime.get(i) <= (endTime + startTime) && getTime.get(i + 1) > (endTime + startTime)) {
                return false;
            }
        }

        return true;
    }

    public void deleteLectureTime() {
        ContentResolver cr = getContentResolver();
        cr.delete(TTDBProvider.TIME_TABLE_CONTENT_URI, TTDBProvider.LECTURE_TIME_ID + " = '" +
        existTimeID + "'", null);

        String[] selArg = {existLectureID + ""};
        Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                        + TTDBProvider.DB_LECTURE_TIME_TABLE + "/*"), null,
                TTDBProvider.LECTURE_TIME_LECTURE_FK, selArg, null);

        Log.e("남은 강의수", cur.getCount() + "");
        if(cur.getCount() == 0) { // 강의에 할당된 시간이 하나도 없으면 강의 삭제
            cr.delete(TTDBProvider.NOTE_TABLE_CONTENT_URI, TTDBProvider.LECTURE_NOTE_LECTURE_FK+ " = '" +
                    existLectureID + "'", null);

            cr.delete(TTDBProvider.LECTURE_TABLE_CONTENT_URI, TTDBProvider.LECTURE_ID + " = '" +
                    existLectureID + "'", null);
        }

        cur.close();
    }

    public void resetEditTexts() {
        isNewLecture = true;

        lectureNameEditText.setText("");
        lectureProfessorEditText.setText("");
        lectureMajorEditText.setText("");
        lectureRoomEditText.setText("");
        lectureContentEditText.setText("");

        lectureNameEditText.setEnabled(true);
        lectureProfessorEditText.setEnabled(true);
        lectureMajorEditText.setEnabled(true);
        lectureRoomEditText.setEnabled(true);
        lectureContentEditText.setEnabled(true);
    }

    public void getExistLectureData(int position) {
        isNewLecture = false;

        /* Get Lecture DB */
        String[] selArg = {position + ""};
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(Uri.parse("content://" + TTDBProvider.AUTHORITY + "/"
                        + TTDBProvider.DB_LECTURE_TABLE + "/*"),
                null, TTDBProvider.LECTURE_ID, selArg, null);
        cur.moveToFirst();

        lectureNameEditText.setText(cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_NAME)));
        lectureProfessorEditText.setText(cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_PROFESSOR)));
        lectureMajorEditText.setText(cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_MAJOR)));
        lectureRoomEditText.setText(cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_ROOM)));
        lectureContentEditText.setText(cur.getString(cur.getColumnIndex(TTDBProvider.LECTURE_INFORMATION)));

        if(existLectureID == 0) {
            lectureNameEditText.setEnabled(false);
            lectureProfessorEditText.setEnabled(false);
            lectureMajorEditText.setEnabled(false);
            lectureRoomEditText.setEnabled(false);
            lectureContentEditText.setEnabled(false);
        }
        cur.close();
    }

    public void addNewLecture() {
        ContentResolver cr = getContentResolver();
        ContentValues newLecture = new ContentValues();

        newLecture.put(TTDBProvider.LECTURE_NAME,
                lectureNameEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_PROFESSOR,
                lectureProfessorEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_MAJOR,
                lectureMajorEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_ROOM,
                lectureRoomEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_INFORMATION,
                lectureContentEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_DB_FK,
                TTData.TIME_TABLE_ID);

        Uri newLectureUri = cr.insert(TTDBProvider.LECTURE_TABLE_CONTENT_URI, newLecture);
        Log.e("NEW LECTURE URI!!!!", newLectureUri.toString());

        addNewTime(Integer.parseInt(newLectureUri.getPathSegments().get(1)));
    }

    public void addNewTime(int lectureID) {
        ContentResolver cr = getContentResolver();
        ContentValues newTime = new ContentValues();

        newTime.put(TTDBProvider.LECTURE_TIME_DAY,
                dateSpinner.getSelectedItemPosition());
        newTime.put(TTDBProvider.LECTURE_TIME_START_TIME,
                startTimeSpinner.getSelectedItemPosition());
        newTime.put(TTDBProvider.LECTURE_TIME_END_TIME,
                endTimeSpinner.getSelectedItemPosition()
                        + startTimeSpinner.getSelectedItemPosition() + 1);
        newTime.put(TTDBProvider.LECTURE_TIME_LECTURE_FK, lectureID);

        Uri newTimeUri = cr.insert(TTDBProvider.TIME_TABLE_CONTENT_URI, newTime);
        Log.e("EXIST TIME URI!!!!", newTimeUri.toString());
    }

    public void updateLecture(int timeID, int lectureID) {
        ContentResolver cr = getContentResolver();

        ContentValues newLecture = new ContentValues();

        newLecture.put(TTDBProvider.LECTURE_NAME,
                lectureNameEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_PROFESSOR,
                lectureProfessorEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_MAJOR,
                lectureMajorEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_ROOM,
                lectureRoomEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_INFORMATION,
                lectureContentEditText.getText().toString());
        newLecture.put(TTDBProvider.LECTURE_DB_FK,
                TTData.TIME_TABLE_ID);

        cr.update(TTDBProvider.LECTURE_TABLE_CONTENT_URI, newLecture,
                TTDBProvider.LECTURE_ID + " = '" + lectureID + "'", null);

        ContentValues newTime = new ContentValues();

        newTime.put(TTDBProvider.LECTURE_TIME_DAY,
                dateSpinner.getSelectedItemPosition());
        newTime.put(TTDBProvider.LECTURE_TIME_START_TIME,
                startTimeSpinner.getSelectedItemPosition());
        newTime.put(TTDBProvider.LECTURE_TIME_END_TIME,
                endTimeSpinner.getSelectedItemPosition()
                        + startTimeSpinner.getSelectedItemPosition() + 1);
        newTime.put(TTDBProvider.LECTURE_TIME_LECTURE_FK, lectureID);

        cr.update(TTDBProvider.TIME_TABLE_CONTENT_URI, newTime,
                TTDBProvider.LECTURE_TIME_ID + " = '" + timeID + "'", null);
    }
}
