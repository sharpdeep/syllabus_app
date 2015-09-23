package com.example.stu_nwad.userecyclerview;

import java.util.HashMap;

/**
 * Created by STU_nwad on 2015/9/23.
 */
public class Lesson {
    public String name;
    public String id;
    public String teacher;
    public String room;
    public String duration;
    public HashMap<String, String> days;

    @Override
    public String toString(){
        // 去掉课程的[课程号]
        int s_index = name.indexOf(']');
        if (s_index != -1){
            name = name.substring(s_index + 1);
        }
        return name + "[" + room + "]" /* + days.toString() */;
    }
}
