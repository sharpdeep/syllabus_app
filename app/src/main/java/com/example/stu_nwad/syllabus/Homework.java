package com.example.stu_nwad.syllabus;

/**
 * Created by STU_nwad on 2015/10/10.
 */
public class Homework {
    public String publisher;
    public String content;
    public long pub_time;   // 发布时间
    public String hand_in_time;     // 上交时间

    @Override
    public String toString(){
        return publisher + " " + pub_time + " " + hand_in_time + " " + content;
    }

}
