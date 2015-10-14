package com.example.stu_nwad.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.stu_nwad.interfaces.UpdateHandler;
import com.example.stu_nwad.parsers.VersionParser;
import com.example.stu_nwad.syllabus.R;
import com.example.stu_nwad.syllabus.SyllabusVersion;

/**
 * Created by STU_nwad on 2015/10/14.
 * 用于检查更新，下载更新的apk
 */
public class UpdateHelper {

    private Context context;
    private UpdateHandler updateHandler;

    private VersionParser version_parser;
    private SyllabusVersion cur_version;

    public UpdateHelper(Context context, UpdateHandler updateHandler){
        this.context = context;
        this.version_parser = new VersionParser(context);
        this.updateHandler = updateHandler;
        cur_version = get_current_version(context);
    }

    public static SyllabusVersion get_current_version(Context context){
        try {
            PackageInfo pInfor = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int version_code = pInfor.versionCode;
            String version_name = pInfor.versionName;
            SyllabusVersion version = new SyllabusVersion();
            version.version_code = version_code;
            version.version_name = version_name;
//            Toast.makeText(context, "版本号: " + version_code + " 版本名字: " + version_name , Toast.LENGTH_SHORT).show();
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void check_for_update(){
        CheckUpdate task = new CheckUpdate(context.getString(R.string.version_control_address));
        task.execute();
    }

    /**
     * 用于检查是否有更新
     */
    class CheckUpdate extends AsyncTask<Void, Void, String>{

        private String address;

        public CheckUpdate(String address){
            this.address = address;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpCommunication.perform_get_call(address, 1000);
        }

        @Override
        protected void onPostExecute(String response){
            if (response.isEmpty()){
                Toast.makeText(context, "无法访问服务器", Toast.LENGTH_SHORT).show();
                updateHandler.deal_with_update(UpdateHandler.CONNECTION_ERROR, null);
                return;
            }

            SyllabusVersion version = version_parser.parse_version(response);
            if (version != null){
                // 网站上的比现在的版本更新
                if (version.version_code > cur_version.version_code){
                    updateHandler.deal_with_update(UpdateHandler.EXIST_UPDATE, version);
                }else{
                    updateHandler.deal_with_update(UpdateHandler.ALREADY_UPDATED, version);
                }
            }
        }
    }

}
