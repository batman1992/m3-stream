package org.m3.view.tabs;

import org.m3.R;
import org.m3.model.Video;
import org.m3.server.ServerService;
import org.m3.util.Utils;
import org.m3.xml.DataHandler;

import com.flazr.rtmp.client.RtmpClient;
import com.flazr.rtmp.client.RtmpClientSession;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.VideoView;
import android.widget.LinearLayout.LayoutParams;


public class VideoActivity extends Activity {
    private Video video;
	private VideoView videoView;
	private ProgressBar progress;
	private int progressStatus;
	private Handler pHandler = new Handler();
	private TableRow mainRow1;
	private ServerService service;
	
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);
        
        final int categoryId = this.getIntent().getExtras().getInt("categoryId");
        final int videoId = this.getIntent().getExtras().getInt("videoId");
        
        //video = DataHandler.getCategories().get(categoryId).getVideos().get(videoId);
        //service = new ServerService(this);
        videoView = (VideoView) findViewById(R.id.videoView);
		MediaController mediaController = new MediaController(this);
		videoView.setMediaController(mediaController);
        String path1="http://commonsware.com/misc/test2.3gp";
        Uri uri=Uri.parse(path1);
  	    videoView.setVideoURI(uri);
		videoView.start();
        
        
        //playVideo();
        /*LinearLayout main = new LinearLayout(this);
        main.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
        
        TableLayout mainPanel = new TableLayout(this);
        mainPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mainPanel.setPadding(5, 20, 5, 10);
        mainPanel.setStretchAllColumns(true);
        mainRow1 = new TableRow(this);
        mainRow1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	*/
        //drawMainRow1();
        
       /* mainPanel.addView(mainRow1);
        main.addView(mainPanel);*/
        
        /*progress = new ProgressBar(this);
	    progress.setLayoutParams(new LayoutParams(40, 40));
	    progress.setMax(10);
        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while(progressStatus < 10) {
                    doCheck();
                    pHandler.post(new Runnable() {
                        public void run() {
                            progress.setProgress(progressStatus);
                        }
                    });
                }
            }
        }).start();
        main.addView(progress);*/
        
        //setContentView(main);
    }
	
    private void drawMainRow1() {
    	//mainRow1.removeAllViews();
    	//videoView = new VideoView(this);
    	//videoView.setPadding(10,10,10,10);
    	//service = new ServerService(this);
        
        //videoView = (VideoView) findViewById(R.id.videoView);
    	
    	//playVideo();
    	/*runOnUiThread(new Runnable(){
			public void run() {
				playVideo();
			}
		});*/

        
       // mainRow1.addView(videoView);
    }
    
 	/*private void doCheck() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
    
    private void playVideo() {
		try {
			
			//String path = Utils.createCacheDir(this, "vmukti").getAbsolutePath();
			//RtmpClientSession session = new RtmpClientSession("rtmp://172.26.24.10:1935/oflaDemo/stream1301409897214");
			//session.setSaveAs(path+"/test.flv");
			//RtmpClient.connect(session);
			
			/*MediaController mc = new MediaController(this); 
			videoView.setMediaController(mc);
			videoView.setVideoURI(Uri.parse("http://people.sc.fsu.edu/~jburkardt/data/mp4/cavity_flow_movie.mp4"));
			//videoView.setVideoPath(path+"/test.flv");
			videoView.start();
			videoView.requestFocus();*/
			
			
			/*MediaController mediaController = new MediaController(this);
			// Set video link (mp4 format )
			Uri video = Uri.parse("http://commonsware.com/misc/test2.3gp");
			videoView.setMediaController(mediaController);
			videoView.setVideoURI(video);
			videoView.start();*/
			

			
	        /*VideoView videoView = (VideoView) findViewById(R.id.VideoView);
			MediaController mediaController = new MediaController(this);
			//mediaController.setAnchorView(videoView);
			// Set video link (mp4 format )
			//Uri video = Uri.parse("http://people.sc.fsu.edu/~jburkardt/data/mp4/cavity_flow_movie.mp4");
			videoView.setMediaController(mediaController);
			//videoView.setVideoURI(video);
			//video.setVideoPath("http://people.sc.fsu.edu/~jburkardt/data/mp4/cavity_flow_movie.mp4");
			//videoView.start();
			 String path="http://www.ted.com/talks/download/video/8584/talk/761";
	         String path1="http://commonsware.com/misc/test2.3gp";
	         Uri uri=Uri.parse(path1);
	  	     videoView.setVideoURI(uri);
			 videoView.start();*/
			
			/*final String path = URL;
			//final String path = service.getVideoURI("video.vms"); // video.getUrl();
			//InputStream stream = service.getStream("video.vms");
			
			Log.v("Viewer", "path: " + path);
			if(path == null || path.length() == 0) {
				Toast.makeText(VideoActivity.this, "File URL/path is empty",
						Toast.LENGTH_LONG).show();
			} else {
				// If the path has not changed, just start the media player
				if (path.equals(currentURL) && videoView != null) {
					videoView.start();
					videoView.requestFocus();
					return;
				}
				Toast.makeText(VideoActivity.this, path,
						Toast.LENGTH_LONG).show();
				currentURL = path;
				//Uri video = Uri.parse(path);
				MediaController mc = new MediaController(this); 
				
				//HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				//int timeoutConnection = 10000;
				//HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				//int timeoutSocket = 10000;
				//HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				
				
				videoView.setMediaController(mc);
				//mVideoView.setVideoURI(video);
				videoView.setVideoPath(getDataSource(path));
				videoView.start();
				videoView.requestFocus();
            }*/
		} catch(Exception e) {
			Log.e("Viewer", "error: " + e.getMessage(), e);
			if(videoView != null) {
				videoView.stopPlayback();
			}
		}
	}
	
	/*private String getDataSource(String path) throws IOException {
		if(!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			final InputStream stream = cn.getInputStream();
			if(stream == null)
				throw new RuntimeException("stream is null");
			final File streamFile = File.createTempFile(STREAM_FILE_NAME, "dat", Utils.getDefaultCacheDir(this));
			streamFile.deleteOnExit();
			String tempPath = streamFile.getAbsolutePath();
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						FileOutputStream out = new FileOutputStream(streamFile);
						byte buf[] = new byte[COPY_CHUNK_SIZE];
						do {
							int numread = stream.read(buf);
							if (numread <= 0)
								break;
							//buf = Base64.decode(buf, Base64.DEFAULT);
							out.write(buf, 0, numread); //buf.length
						} while (true);
						stream.close();
					} catch (IOException ex) {
						Log.e("Viewer", "error: " + ex.getMessage(), ex);
					}
				}
			}).start();
			return tempPath;
		}
	}*/
    
}
