package org.m3;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainScreen extends Activity implements SurfaceHolder.Callback,
        View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback,
        Camera.AutoFocusCallback {

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView preview;
    private VideoRecorder recorder;
    
    private ImageView shotBtn;
    private ImageView recordBtn;
    private Boolean isRecording = false;

    private static final int IDM_PREF = 101;
    private static final int IDM_EXIT = 102;
    
    private int videoBitrate;
    private int videoFramerate;
    private int audioBitrate;
    private int audioSamplingrate;
    private int audioChannels;
    private int videoWidth;
    private int videoHeight;
    private int videoMaxDuration;
    private int videoMaxFileSize;
    private MediaPlayer mp;
    
    private EditText mName;
    private EditText mDescription;
    
 // ��� ���������� ��������
    
    private static final int ALERT_NONE = 0; // ��������� ������� �����
    private static final int ALERT_NAME = 1; // ������������ ���
    private static final int ALERT_DESCRIPTION = 2; // ������������ ��������
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ���� �����, ����� ���������� ���� �������������
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // � ��� ���������
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.video_rec);
        Button btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View arg0) {
        		if(mp != null) {
        			mp.stop();
        		}
            	finish();
			}
        });

        // ���� SurfaceView ����� ��� SurfaceView01
        preview = (SurfaceView) findViewById(R.id.SurfaceView01);

        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // ������ ����� ��� Button01
        shotBtn = (ImageView) findViewById(R.id.imgShot);
        shotBtn.setImageDrawable(MainScreen.this.getResources().getDrawable(R.drawable.shot));
        shotBtn.setOnClickListener(this);

        recordBtn = (ImageView) findViewById(R.id.imgRec);
        recordBtn.setImageDrawable(MainScreen.this.getResources().getDrawable(R.drawable.rec));
        recordBtn.setOnClickListener(this);

        recorder = new VideoRecorder();
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
        recorder.open();
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        videoBitrate = Integer.parseInt(prefs.getString(getString(R.string.video_br), "100000"));
        videoFramerate = Integer.parseInt(prefs.getString(getString(R.string.video_fr), "15"));
        audioBitrate = Integer.parseInt(prefs.getString(getString(R.string.audio_br), "8000"));
        audioSamplingrate = Integer.parseInt(prefs.getString(getString(R.string.audio_sr), "8000"));
        audioChannels = Integer.parseInt(prefs.getString(getString(R.string.audio_ch), "1"));
        videoWidth = Integer.parseInt(prefs.getString(getString(R.string.video_sz_w), "640"));
        videoHeight = Integer.parseInt(prefs.getString(getString(R.string.video_sz_h), "480"));
        videoMaxDuration = Integer.parseInt(prefs.getString(getString(R.string.max_duration), "0"));
        videoMaxFileSize = Integer.parseInt(prefs.getString(getString(R.string.max_filesize), "0"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        recorder.close();
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, IDM_PREF, Menu.NONE, "Settings");
        menu.add(Menu.NONE, IDM_EXIT, Menu.NONE, "Exit");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
	        case IDM_PREF: 	Intent intent = new Intent();
	        				intent.setClass(this, SettingsScreen.class);
	            			startActivity(intent);
	            			break;
	        case IDM_EXIT:  finish();
	            			break;
	        default:		return false;
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        } catch(IOException e) {
            e.printStackTrace();
        }

        Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;

        int previewSurfaceWidth = preview.getWidth();
        int previewSurfaceHeight = preview.getHeight();

        LayoutParams lp = preview.getLayoutParams();

        // ����� ������������ ������ ������������� preview, ����� �� ����
        // ���������

        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            // ���������� ���
            camera.setDisplayOrientation(90);
            lp.height = previewSurfaceHeight;
            lp.width = (int) (previewSurfaceHeight / aspect);
        } else {
            // �����������
            camera.setDisplayOrientation(0);
            lp.width = previewSurfaceWidth;
            lp.height = (int) (previewSurfaceWidth / aspect);
        }

        preview.setLayoutParams(lp);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onClick(View v) {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
    	//������ �������� �������
    	LinearLayout linear = new LinearLayout(this);
    	TableLayout table1 = new TableLayout(this); 
    	//table1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	
    	TableRow row1 = new TableRow(this);
        TableRow row2 = new TableRow(this);
        TableRow row3 = new TableRow(this);
        TableRow row4 = new TableRow(this);
        
        TextView label = new TextView(this); 
        label.setTextSize(20);
        label.setText("Name"); 
        
        TextView labe2 = new TextView(this); 
        labe2.setTextSize(20);
        labe2.setText("Description");
        
    	mName = new EditText(this);
    	mName.setWidth(300);
    	
    	mDescription = new EditText(this);
    	mDescription.setWidth(300);
    	mDescription.setHeight(200);
        	
    	row1.addView(label);
    	row2.addView(mName);
        row3.addView(labe2);
        row4.addView(mDescription);
           
        table1.addView(row1);
        table1.addView(row2);
        table1.addView(row3);
        table1.addView(row4);
                                 
        linear.addView(table1);
  	
    	
        builder
           	.setView(linear)
            .setMessage(R.string.create)
            //��� ������� �� ������ yes �������� ����������
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {
                    	
                    	//��������� ����� ��������� ������
                    	final String name = mName.getText().toString();
                    	final String description = mDescription.getText().toString();
                    	
                    	int alertCode = checkInputParameters(name, description);
                        if (alertCode != ALERT_NONE)
                        {
                        	if(alertCode == ALERT_NAME){
                        	Toast.makeText(MainScreen.this, "������������ ��������",
            						Toast.LENGTH_LONG).show();
                        	}
                        	
                        	if(alertCode == ALERT_DESCRIPTION){
                            	Toast.makeText(MainScreen.this, "�������� �����������",
                						Toast.LENGTH_LONG).show();
                            	}
                        	
                           return;

                        }
                       //onStart();                            
                    }
                })
             //��� ������� �� ������ cancel ������ ��������
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) 
                    {
                    	onStop(); 
                    }
                })
            .show();
        	        	
    	
        if(v == shotBtn) {
            // ���� ������ ������ ��������������� �����
            // ���� �������� ���������� ����������
            // camera.takePicture(null, null, null, this);
            camera.autoFocus(this);
        } else if(v == recordBtn) {
            if(isRecording) {
                recorder.stop();
                try {
                    // ��������� ����� ������ � ������
                    camera.reconnect();
                    // ����� �������� preview ������
                    camera.startPreview();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                recordBtn.setImageDrawable(MainScreen.this.getResources().getDrawable(R.drawable.rec));
                // �������� ������ ����������
                shotBtn.setEnabled(true);
            } else {
                // ��������� ������ ����������
                shotBtn.setEnabled(false);
                // ������������� preview ������ (����� ����� ������)
                camera.stopPreview();
                // ��������� ����� ������ � ������
                camera.unlock();
                // �������� ���������� ��� ��������� ������
                recorder.setCamera(camera);
                // ������ ���������, preview, ��� ����� � �������� ������
                recorder.setRecorderParams(videoBitrate, audioBitrate, audioSamplingrate, audioChannels, videoFramerate, videoWidth, videoHeight, videoMaxDuration, videoMaxFileSize);
                recorder.setPreview(surfaceHolder.getSurface());
                
                recorder.start(String.format("/sdcard/CameraExample/%d.mp4", System.currentTimeMillis()));
                recordBtn.setImageDrawable(MainScreen.this.getResources().getDrawable(R.drawable.stop));
            }
            isRecording = !isRecording;
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera) {
        // ��������� ���������� jpg � ����� /sdcard/CameraExample/
        // ��� ����� - System.currentTimeMillis()
        try {
            File saveDir = new File("/sdcard/CameraExample/");
            if(!saveDir.exists()) {
                saveDir.mkdirs();
            }
            FileOutputStream os = new FileOutputStream(String.format(
                    "/sdcard/CameraExample/%d.jpg", System.currentTimeMillis()));
            os.write(paramArrayOfByte);
            os.close();
        } catch(Exception e) {
        }
        // ����� ����, ��� ������ ������, ����� ������ �����������. ����������
        // �������� ���
        paramCamera.startPreview();
    }

    @Override
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera) {
        if(paramBoolean) {
            // ���� ������� ���������������, ������ ������
            paramCamera.takePicture(null, null, null, this);
        }
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        // ����� ����� ������������ �����������, ������������ � preview
    }
    
    //�������� ���-�� ������ ���� �������
    private int checkInputParameters(String name, String description)
    {
    	 if (name == null)
    	 {
    	     return ALERT_NAME;
    	 }
    	    
    	 if (description == null)
    	 {
    	     return ALERT_DESCRIPTION;
    	 }
    	    
    	 return ALERT_NONE;
    }
    
}
