package com.example.yyd.bbcvplayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bbcvplayercore.libcore.LibCore;
import org.libsdl.app.SDLActivity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private  LibCore player;

    private Button videoBtn = null;
    private Button audioBtn = null;
    private Button picturBtn = null;
    private Button customBtn = null;

    private final int CLICK_INTERNET = 1;
    private final int CLICK_VIDEO_LOCAL = 2;
    private final int CLICK_CUSTOM = 3;

    private static int mSerachfile = 0;

    private static final int MSG_FIND_FILE_OVER = 1;

    private static ArrayList<String> listvideo = null;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        return super.dispatchKeyEvent(event);
    }

    private android.os.Handler msgHandle = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FIND_FILE_OVER:
                    // search local file over
                    mSerachfile = 1;
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
            MainActivity.getMediaData();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listvideo = new ArrayList<String>();
        Thread thr = new Thread(null, mRunnable, "Service_Service");
        thr.start();

        setContentView(R.layout.activity_main);
        testfunc();
        InitBtn();
        Button bt = (Button)findViewById(R.id.buttinbegin);
        bt.setOnClickListener(this);
        bt.setTag(CLICK_INTERNET);

        videoBtn.setOnClickListener(this);
        videoBtn.setTag(CLICK_VIDEO_LOCAL);

        customBtn.setOnClickListener(this);
        customBtn.setTag(CLICK_CUSTOM);
        SetLinstener();
    }

    private Dialog popMenu(){
        return new android.app.AlertDialog.Builder(MainActivity.this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(R.string.wait_loadvideo)
                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked Yes so do some stuff */
                    }
                })
                .create();
    }

    @Override
    public void onClick(View v){
        String st = player.version();
        int tag = (Integer)v.getTag();
        switch (tag){
            case CLICK_INTERNET:
            {
                //  boolean te= player.nativeNew();
                Intent intent = new Intent();
                intent.putExtra("url","udp://@:55555");
                intent.setClass(this,SDLActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case CLICK_VIDEO_LOCAL:
            {
                //this.msgHandle.obtainMessage()
                if(mSerachfile != 1){
                    //pop menu not ready
                    Dialog dialog =  popMenu();
                    dialog.show();
                    break;
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("local_video",listvideo);
                intent.setClass(this,VideoActivity.class);

                startActivity(intent);
                finish();
                break;
            }
            case CLICK_CUSTOM:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.button_title);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                final EditText et = new EditText(MainActivity.this);
                et.setText(R.string.default_url);
                builder.setView(et);
                builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = et.getText().toString();
                        if(input.equals("")){
                            Toast.makeText(getApplicationContext(),R.string.warning_string + input,Toast.LENGTH_LONG).show();

                        }else{
                            Intent intent = new Intent();
                            intent.putExtra("url",input);
                            intent.setClass(MainActivity.this,SDLActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                builder.setNegativeButton(R.string.button_cansel,null);
                AlertDialog dialog = builder.create();
                dialog.show();

//                Window wind = dialog.getWindow();
//                wind.setContentView(R.layout.dialog_input);
//                EditText et = (EditText)wind.findViewById(R.id.input_text);
//                et.setText(R.string.default_url);
//                Button btn_yes = (Button)wind.findViewById(R.id.btn_sure);
//                btn_yes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        EditText edit = (EditText)findViewById(R.id.input_text);
//                        String input = edit.getText().toString();
//                        if(input.equals("")){
//                            Toast.makeText(getApplicationContext(),R.string.warning_string + input,Toast.LENGTH_LONG).show();
//
//                        }else{
//                            Intent intent = new Intent();
//                            intent.putExtra("url",input);
//                            intent.setClass(MainActivity.this,SDLActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                });
//                Button btn_cancel = (Button)wind.findViewById(R.id.btn_cancel);
//                btn_cancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                });


                break;
            }
            default:
                break;
        }
        }

    private void InitBtn(){
        videoBtn = (Button)findViewById(R.id.video_button);
        audioBtn = (Button)findViewById(R.id.audio_button);
        picturBtn = (Button)findViewById(R.id.picture_button);
        customBtn = (Button)findViewById(R.id.buttoncustom);
    }
    private void SetLinstener(){

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listaudio = new ArrayList<String>();
                String str[] = {MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DATA};
                Cursor cursor = MainActivity.this.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,str,
                        null,null,null);
                while (cursor.moveToNext()){
                    System.out.println(cursor.getString(0));  // id
                    System.out.println(cursor.getString(1));  //  name
                    System.out.println(cursor.getString(2));  // 绝对路径
                    listaudio.add(cursor.getString(2));
                }
            }
        });
        picturBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listpicture = new ArrayList<String>();
                String str[] = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATA};
                Cursor cursor = MainActivity.this.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,str,
                        null,null,null);
                while (cursor.moveToNext()){
                    System.out.println(cursor.getString(0));  // id
                    System.out.println(cursor.getString(1));  //  name
                    System.out.println(cursor.getString(2));  // 绝对路径
                    listpicture.add(cursor.getString(2));
                }
            }
        });

    }

    public void testfunc() {
            player = new LibCore();
            ArrayList<String> options = new ArrayList<>();
            options.add("-codec"); // verbosity
            options.add("ffmpeg");
            //player.nativeNew(options);
           // player.nativeNew();
            String st = player.version();
            System.out.print(player.version());
    }
    //TODO test
}
