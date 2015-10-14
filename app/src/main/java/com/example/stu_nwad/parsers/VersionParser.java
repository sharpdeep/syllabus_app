package com.example.stu_nwad.parsers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.stu_nwad.syllabus.SyllabusVersion;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by STU_nwad on 2015/10/14.
 * 解析远程服务器上关于 app 的版本信息
 */
public class VersionParser {

    private Context context;

    public VersionParser(Context context){
        this.context = context;
    }


    public  SyllabusVersion parse_version(String json_data){
        JSONTokener json_parser = new JSONTokener(json_data);
        try {
            JSONObject version_json = (JSONObject) json_parser.nextValue();
            // 版本号
            int version_code = version_json.getInt("versionCode");
            // 版本名
            String version_name = version_json.getString("versionName");
            // 描述
            String description = version_json.getString("versionDescription");
            // 发布者
            String version_publisher = version_json.getString("versionReleaser");
            // 发布日期
            long pub_date = version_json.getLong("versionDate");
            // 下载地址
            String download = version_json.getString("download_address");
            // 文件名
            String file_name = version_json.getString("apk_file_name");

            SyllabusVersion version = new SyllabusVersion(version_code, version_name, description);
            version.version_releaser = version_publisher;
            version.version_release_date = pub_date;
            version.dowload_address = download;
            version.apk_file_name = file_name;
            return version;
        } catch (JSONException e) {
            Toast.makeText(context, "版本信息解析失败", Toast.LENGTH_SHORT).show();
            Log.d("parse_version", e.toString());
            e.printStackTrace();
            return null;
        }
    }

}
