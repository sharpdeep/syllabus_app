package com.example.stu_nwad.syllabus;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.stu_nwad.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;


public class ClassParser {

    private ArrayList<Lesson> all_classes;
    public ArrayList<Lesson> weekend_classes;  // 存放周末的课程

    public static final String EMPTY_CLASS_STRING = "";
    public static final String[] LABELS = {"一", "二", "三", "四", "五", "六", "日"};
    public static final String ERROR = "ERROR";

    public static final int ROWS = 14;
    public static final int COLUMNS = 6;    // 包含了 一个 空单元 以及 星期一到星期五
    public Object[] weekdays_syllabus_data;  // 用于适配 课表的 view 的数据

    private Context context;

    public ClassParser(Context context){
        weekdays_syllabus_data = new Object[ROWS * COLUMNS];
        all_classes = new ArrayList<>();
        weekend_classes = new ArrayList<>();
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

                String credit = lesson.getString("credit");

                Lesson cls = new Lesson();
                cls.name = name;
                cls.id = id;
                cls.teacher = teacher;
                cls.room = room;
                cls.duration = duration;
                cls.credit = credit;

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

        for(int i = 0 ; i < weekdays_syllabus_data.length ; ++i)
            weekdays_syllabus_data[i] = EMPTY_CLASS_STRING;   // 初始化数据

        // 处理非课程的数据
        for (int i = 0 ; i < weekdays_syllabus_data.length ; ++i){
            // 处理星期几这些日期
            if (i <= 5){    // 一个空白格子，外加 周一到周五
                if (i == 0)
                    weekdays_syllabus_data[i] = "";   // 空白的一个格子
                else
                    weekdays_syllabus_data[i] = LABELS[i - 1];    // 转化为中文的数字

            }else if (i % COLUMNS == 0){
                // 处理第一列的 课的节数
                // 表明目前第i个元素位于 i / COLUMNS 行的第一个位置
                if (i / COLUMNS <= 9) {   // 这里还是用数字表示
                    int num = i / COLUMNS;
                    weekdays_syllabus_data[i] = num + "";  // i.e. 123..ABC
                }else{
                    // 用ABC代替
                    String label = "";
                    switch (i / COLUMNS){
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
                    weekdays_syllabus_data[i] = label;
                }
            }//else{
//                weekdays_syllabus_data[i] = EMPTY_CLASS_STRING; // 置为空
//            }
        }
        Log.d(MainActivity.TAG, "end init()");
    }


    /**
     * 用解析得到的课程填充 weekdays_syllabus_data
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
                    if (offset == 0 || offset == 6) {     // 忽略周六周日的课
//                        offset = 7;     // 因为web api返回的数据 w0 是代表周日
                        weekend_classes.add(all_classes.get(i));    // 添加周末的课程到此
                        continue;
                    }
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
                        int index = row * COLUMNS + offset;
                        if (row == -1)   // 说明是单双周的情况
                            continue;
                        if (!hasBeenAdded) {     // 一节课添加一次即可
                            weekdays_syllabus_data[index] = lesson;   // 将这节课添加到合适的位置
                            hasBeenAdded = true;
                        }else{
                            weekdays_syllabus_data[index] = "同上";
                        }

                    }
                }
            }
        }
    }

}
