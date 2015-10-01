package com.example.stu_nwad.userecyclerview;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;


public class ClassParser {

    private ArrayList<Lesson> all_classes;

    public ArrayList<Lesson> getAllClasses(){
        return all_classes;
    }

    public static final String EMPTY_CLASS_STRING = "NONE";
    public static final String[] LABELS = {"一", "二", "三", "四", "五", "六", "日"};
    public static final String ERROR = "ERROR";

    Object[] objs = new Object[14 * 8];
    private Context context;

    public ClassParser(Context context){
        all_classes = new ArrayList<>();
        this.context = context;
        init();     // 生成初始化的数据，在特定位置上填上日期信息之类的
    }


    /**
     * 解析json数据
     * @param json_data 从服务器返回的代表课程信息的 json 数据
     * @return
     */
    public boolean parseJSON(String json_data){
        // 用response作为json传给 JSONTOkener
        JSONTokener jsonParser = new JSONTokener(json_data);
        all_classes.clear();
        try {
            JSONObject curriculum = (JSONObject) jsonParser.nextValue();
            // 判断有没有错误先
            if (curriculum.has(ERROR)){
                String error = curriculum.getString(ERROR);
                Toast.makeText(context, "啊哦，出现了这个错误呢:" + error, Toast.LENGTH_SHORT).show();
                return false;
            }
            // 得到所有课程的数组
            JSONArray classes = curriculum.getJSONArray("classes");
            Log.d(MainActivity.TAG, classes.length() + " classes");
            for (int i = 0; i < classes.length(); ++i) {
                // 得到每一节课
                JSONObject lesson = (JSONObject) classes.get(i);
                // 处理每一节课的信息
                String name = lesson.getString("name");
//                Log.d(MainActivity.TAG, name);
                String id = lesson.getString("id");
//                Log.d(MainActivity.TAG, id);
                String teacher = lesson.getString("teacher");
//                Log.d(MainActivity.TAG, teacher);
                String room = lesson.getString("room");
//                Log.d(MainActivity.TAG, room);
                String duration = lesson.getString("duration");
//                Log.d(MainActivity.TAG, duration);

                Lesson cls = new Lesson();
                cls.name = name;
                cls.id = id;
                cls.teacher = teacher;
                cls.room = room;
                cls.duration = duration;

                // 得到一周之内要上课的日期以及具体上课时间
                JSONObject days = lesson.getJSONObject("days");
                final int weekdays = 7;
                HashMap<String, String> lesson_days = new HashMap<>();
                for (int j = 0; j < weekdays; ++j) {
                    String key = "w" + j;
                    String isNull = days.getString(key);
                    if (!isNull.equals("None"))     // 去除没有的天数
                        lesson_days.put(key, days.getString(key));
//                    Log.d(MainActivity.TAG, key + ":" + days.getString(key));
                }
                cls.days = lesson_days;

                all_classes.add(cls);
            }
            return true;
        }catch (JSONException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
    }

    private void init(){
        Log.d(MainActivity.TAG, "start init()");

        for(int i = 0 ; i < objs.length ; ++i)
            objs[i] = EMPTY_CLASS_STRING;   // 初始化数据

        // 处理非课程的数据
        for (int i = 0 ; i < objs.length ; ++i){
            // 处理星期几这些日期
            if (i <= 7){
                if (i == 0)
                    objs[i] = "";   // 空白的一个格子
                else
                    objs[i] = LABELS[i - 1];    // 转化为中文的数字

            }else if (i % 8 == 0){
                // 处理第一列的 课的节数
                if (i / 8 <= 9) {   // 这里还是用数字表示
                    int num = i / 8;
                    objs[i] = num + "";
                }else{
                    // 用ABC代替
                    String label = "";
                    switch (i / 8){
                        case 10:
                            label = "0";
                            break;
                        case 11:
                            label = "A";
                            break;
                        case 12:
                            label = "B";
                            break;
                        case 13:
                            label = "C";
                            break;
                        default:
                            break;
                    }
                    objs[i] = label;
                }
            }else{
                objs[i] = EMPTY_CLASS_STRING; // 置为空
            }
        }
        Log.d(MainActivity.TAG, "end init()");
    }


    /**
     * 用解析得到的课程填充 objs
     */
    public void inflateTable(){
        Log.d(MainActivity.TAG, "before inflate class_table");
        // 填充课表数据
        for(int i = 0 ; i < all_classes.size() ; ++i){
            // 遍历key set
            Lesson lesson = all_classes.get(i);
            for (String key : lesson.days.keySet()){
                // key 的值是  w1 w2 这种格式
                String class_time = lesson.days.get(key);
//                Log.d(MainActivity.TAG, "class_time " + class_time);
                if (!class_time.equals(EMPTY_CLASS_STRING)){
                    // 添加到obj数组中
                    int offset = Integer.parseInt( key.substring(1));   // 得到 w1 中的数字部分
                    if (offset == 0)
                        offset = 7;     // 因为web api返回的数据 w0 是代表周日
                    boolean hasBeenAdded = false;
                    for(int count = 0 ; count < class_time.length() ; ++count){

                        char c = class_time.charAt(count);  // 得到数据
                        int row = -1;
                        switch (c){
                            case '0':
                                row = 10;
                                break;
                            case 'A':
                                row = 11;
                                break;
                            case 'B':
                                row = 12;
                                break;
                            case 'C':
                                row = 13;
                                break;
                            case '单':   // 跳过这个字符
                            case '双':
                                hasBeenAdded = false;
                                break;
                            default:
                                row = c - '0';
                                break;
                        }
                        int index = row * 8 + offset;
                        if (row == -1)   // 说明是单双周的情况
                            continue;
                        if (!hasBeenAdded) {     // 一节课添加一次即可
                            objs[index] = lesson;   // 将这节课添加到合适的位置
                            hasBeenAdded = true;
                        }else{
                            objs[index] = "同上";
                        }

                    }
                }
            }
        }
    }

}
