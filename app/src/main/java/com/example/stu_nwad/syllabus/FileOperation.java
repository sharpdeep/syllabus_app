package com.example.stu_nwad.syllabus;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 处理文件的存储与读取
 */
public class FileOperation {

    public static boolean hasFile(File file){
        if (file.exists())
            return true;
        return false;
    }

    public static boolean hasFile(Context context, String filename){
        File file = new File(context.getFilesDir(), filename);
        Log.d(MainActivity.TAG, file.toString());
        return hasFile(file);
    }

    /**
     * 保存最新的用户名和密码
     */
    public static boolean save_user(Context context, String user_file, String password_file, String username, String passwd){
        boolean flag;
        flag = save_to_file(context, user_file, username);
        if (flag){
            flag = save_to_file(context, password_file, passwd);
        }
        if (flag){
            // 保存成功
            Log.d(MainActivity.TAG, "成功保存用户");
        }else{
            Log.d(MainActivity.TAG, "保存用户失败");
        }
        return flag;
    }

    public static String[] load_user(Context context, String user_file, String password_file){
        if (hasFile(context, user_file) && hasFile(context, password_file)){
            String[] user = new String[2];
            user[0] = read_from_file(context, user_file);
            user[1] = read_from_file(context, password_file);
            return user;
        }
        return null;
    }

    public static boolean save_to_file(Context context, String filename, String data){
        try{
            FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            out.write(data.getBytes("UTF-8"));
            out.flush();
            out.close();
            return true;
        }catch (FileNotFoundException e){
            Log.d(MainActivity.TAG, e.toString());
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String read_from_file(Context context, String filename){
        try {
            FileInputStream inStream = context.openFileInput(filename);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length= -1 ;
            while( (length=inStream.read(buffer)) != -1)   {
                stream.write(buffer, 0, length);  // 写入字节流中
            }
            stream.close();
            inStream.close();
            String data = stream.toString();
//            Toast.makeText(MainActivity.this, "读取" + filename, Toast.LENGTH_SHORT).show();
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
