package com.example.aki.javaq;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by MinaFujisawa on 2017/06/06.
 */

public class ProgressFragment extends Fragment {
    private TextView mActiveStreakTextView;
    private TextView mLongestStreakTextView;
    private TextView mMonTextView;
    private TextView mTueTextView;
    private TextView mWedTextView;
    private TextView mThuTextView;
    private TextView mFriTextView;
    private TextView mSatTextView;
    private TextView mSunTextView;

    private DayOfWeek dayOfWeek;

    private SharedPreferences mAcStreakShearedPref;
    private SharedPreferences.Editor editor;

    public static final String EXTRA_SECTION_POSITON = "aki.javaq_extra_section_position";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dayOfWeek = new DayOfWeek();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_fragment, container, false);
        mMonTextView = (TextView) view.findViewById(R.id.weekly_mon);
        mTueTextView = (TextView) view.findViewById(R.id.weekly_tue);
        mWedTextView = (TextView) view.findViewById(R.id.weekly_wed);
        mThuTextView = (TextView) view.findViewById(R.id.weekly_thu);
        mFriTextView = (TextView) view.findViewById(R.id.weekly_fri);
        mSatTextView = (TextView) view.findViewById(R.id.weekly_sta);
        mSunTextView = (TextView) view.findViewById(R.id.weekly_sun);

        mActiveStreakTextView = (TextView) view.findViewById(R.id.active_streak);
        mAcStreakShearedPref = getActivity().getSharedPreferences(QuizResultFragment.SHEARED_PREF_ACTIVE, Context.MODE_PRIVATE);
        editor = mAcStreakShearedPref.edit();
        long lastCheckedMillis = mAcStreakShearedPref.getLong(QuizResultFragment.SHEARED_PREF_ACTIVE_TIME_STAMP, 0);

        // set active streak
        if(isUsedYesterday(lastCheckedMillis)){
            editor.clear().commit();
        }
        int activeDays = mAcStreakShearedPref.getInt(QuizResultFragment.SHEARED_PREF_ACTIVE_DAYS, 0);
        mActiveStreakTextView.setText(String.valueOf(activeDays));

        return view;
    }

    // for active streak
    private boolean isUsedYesterday(long lastCheckedMillis){
        long now = System.currentTimeMillis();
        // tomorrow at midnight
        Calendar date = new GregorianCalendar();
        date.setTime(new Date(lastCheckedMillis));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 2); //next day
        long tomorrowMidnight = date.getTimeInMillis();
        if(tomorrowMidnight < now){
            return true;
        } else {
            return false;
        }
    }



    private void setWeeklyProgressColor(String day) {
        switch (day) {
            case "Monday":
                mMonTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Tuesday":
                mTueTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Wednesday":
                mWedTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Thursday":
                mThuTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Friday":
                mFriTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Saturday":
                mSatTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
            case "Sunday":
                mSunTextView.setBackgroundColor(getResources().getColor(R.color.main_color));
                mMonTextView.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }


}


