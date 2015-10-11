package com.example.stu_nwad.syllabus;

import java.util.Calendar;

/**
 * Created by STU_nwad on 2015/10/10.
 */
public class Homework {
    public String publisher;
    public String content;
    public long pub_time;   // 发布时间, 这个是秒
    public String hand_in_time;     // 上交时间

    public String transfer_time(){
        Calendar calendar =  Calendar.getInstance();
        calendar.setTimeInMillis(pub_time * 1000);  // 这个是毫秒
        // 月份是从 0 开始的
        return calendar.get(Calendar.YEAR) + "-" +  (calendar.get(Calendar.MONTH) + 1 ) + "-" + calendar.get(Calendar.DAY_OF_MONTH)
                + " " + calendar.get(Calendar.HOUR_OF_DAY) + "点";
    }

    @Override
    public String toString(){
        return "发布者 " + publisher + "\n发布时间 " + transfer_time() + "\n" + "上交时间 " + hand_in_time + "\n内容:\n" + content;
    }

}
