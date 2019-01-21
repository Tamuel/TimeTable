package com.softwork.ydk.middletermproject_time_table.GUI;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softwork.ydk.middletermproject_time_table.R;

/**
 * Created by DongKyu on 2015-10-29.
 */
public class TimeTableTimeLinearLayout extends LinearLayout {
    private Button[] timeStepButtons;

    public TimeTableTimeLinearLayout(Context context) {
        super(context);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0);
        layoutParams.weight = 1;
        this.setLayoutParams(layoutParams);
        this.setOrientation(LinearLayout.VERTICAL);


        timeStepButtons = new Button[TimeTableActivity.timeStepStrings.length];

        LinearLayout.LayoutParams timeTableButtonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                (int) getResources().getDimension(R.dimen.time_table_button_height),
                0);

        for(int i = 0; i < TimeTableActivity.timeStepStrings.length; i++) {
            if(i == 0) {
                TextView blankTextView = new TextView(context);
                blankTextView.setHeight((int) (getResources().getDimension(R.dimen.time_table_button_height) / 2));
                blankTextView.setBackgroundColor(Color.WHITE);
                this.addView(blankTextView);
            }

            timeStepButtons[i] = new Button(context);
            timeStepButtons[i].setText(TimeTableActivity.timeStepStrings[i]);
            timeStepButtons[i].setBackgroundColor(Color.WHITE);
            timeStepButtons[i].setTextColor(getResources().getColor(R.color.tableTimeStepColor));
            timeStepButtons[i].setTextSize(getResources().getDimension(R.dimen.time_step_text_size));
            timeStepButtons[i].setLayoutParams(timeTableButtonLayoutParams);
            this.addView(timeStepButtons[i]);
        }
    }
}
