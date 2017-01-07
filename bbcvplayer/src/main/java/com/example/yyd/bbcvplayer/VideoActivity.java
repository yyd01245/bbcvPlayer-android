package com.example.yyd.bbcvplayer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.libsdl.app.SDLActivity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import static android.provider.MediaStore.Video;


public class VideoActivity extends AppCompatActivity {

    private static final int MSG_FIND_FILE_OVER = 1;

    private static ArrayList<String> listvideo = null;
    private ListView m_listVideo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent data = getIntent();
        listvideo = data.getStringArrayListExtra("local_video");

        setContentView(R.layout.activity_video);
        m_listVideo = (ListView)findViewById(R.id.vidoe_listView);
        //getVideoList();
        m_listVideo.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listvideo));
        SetListViewClickListener();

    }
    private void getVideoList(){
        listvideo = new ArrayList<String>();
        String str[] = {Video.Media._ID,
                Video.Media.DISPLAY_NAME,
                Video.Media.DATA};
        Cursor cursor = VideoActivity.this.getContentResolver().query(
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,str,
                Video.Media.INTERNAL_CONTENT_URI,str,
                null,null,null);
        while (cursor.moveToNext()){
            System.out.println(cursor.getString(0));  // id
            System.out.println(cursor.getString(1));  //  name
            System.out.println(cursor.getString(2));  // 绝对路径
            listvideo.add(cursor.getString(2));
        }
        cursor = VideoActivity.this.getContentResolver().query(
                Video.Media.EXTERNAL_CONTENT_URI,str,
                null,null,null);
        while (cursor.moveToNext()){
            System.out.println(cursor.getString(0));  // id
            System.out.println(cursor.getString(1));  //  name
            System.out.println(cursor.getString(2));  // 绝对路径
            listvideo.add(cursor.getString(2));
        }


        //getMediaData();


	
    }

    private android.os.Handler msgHandle = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_FILE_OVER:

                    break;
                default :
                    break;
            }
           // super.handleMessage(msg);
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            VideoActivity.getMediaData();
            msgHandle.sendEmptyMessage(MSG_FIND_FILE_OVER);
        }
    };
    private static void getMediaData(){
//        ArrayList<String> allVideoList = null;// 视频信息集合
//        allVideoList = new ArrayList<String>();
        getVideoFile(listvideo, Environment.getExternalStorageDirectory());// 获得视频文件

    }

    private static void getVideoFile(final ArrayList<String> list, File file) {// 获得视频文件
      
            file.listFiles(new FileFilter() {
      
                @Override  
                public boolean accept(File file) {  
                    // sdCard找到视频名称  
                    String name = file.getName();  
      
                    int i = name.indexOf('.');  
                    if (i != -1) {  
                        name = name.substring(i);  
                        if (name.equalsIgnoreCase(".mp4")  
                                || name.equalsIgnoreCase(".3gp")  
                                || name.equalsIgnoreCase(".wmv")  
                                || name.equalsIgnoreCase(".ts")  
                                || name.equalsIgnoreCase(".rmvb")  
                                || name.equalsIgnoreCase(".mov")  
                                || name.equalsIgnoreCase(".m4v")  
                                || name.equalsIgnoreCase(".avi")  
                                || name.equalsIgnoreCase(".m3u8")  
                                || name.equalsIgnoreCase(".3gpp")  
                                || name.equalsIgnoreCase(".3gpp2")  
                                || name.equalsIgnoreCase(".mkv")  
                                || name.equalsIgnoreCase(".flv")  
                                || name.equalsIgnoreCase(".divx")  
                                || name.equalsIgnoreCase(".f4v")  
                                || name.equalsIgnoreCase(".rm")  
                                || name.equalsIgnoreCase(".asf")  
                                || name.equalsIgnoreCase(".ram")  
                                || name.equalsIgnoreCase(".mpg")  
                                || name.equalsIgnoreCase(".v8")  
                                || name.equalsIgnoreCase(".swf")  
                                || name.equalsIgnoreCase(".m2v")  
                                || name.equalsIgnoreCase(".asx")  
                                || name.equalsIgnoreCase(".ra")  
                                || name.equalsIgnoreCase(".ndivx")  
                                || name.equalsIgnoreCase(".xvid")) {  
                            String fpath =  file.getAbsolutePath();
                            //file.getName();
                            list.add(fpath);
                            return true;  
                        }  
                    } else if (file.isDirectory()) {  
                        getVideoFile(list, file);  
                    }  
                    return false;  
                }  
            });  
        }  

    private void SetListViewClickListener(){
        m_listVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                ListView listView = (ListView) arg0;
               String file =  listView.getItemAtPosition(arg2).toString();
                //open file use libcore
                openVideo(file);
            }
        });
    }

    private void openVideo(String file){
        Intent intent = new Intent();
        intent.putExtra("url",file);
        Uri uri = Uri.parse(file);
        intent.setData(uri);
        intent.setClass(this,SDLActivity.class);
        startActivity(intent);
        finish();
    }

}
