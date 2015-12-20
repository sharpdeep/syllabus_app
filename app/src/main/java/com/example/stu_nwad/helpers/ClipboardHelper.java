package com.example.stu_nwad.helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by smallfly on 2015/11/6.
 * 用于方便地操作系统剪贴板
 */
public class ClipboardHelper {

    public static void setContent(Context context, String content){
        ClipboardManager cbm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("", content);
        cbm.setPrimaryClip(clipData);
    }


}
