package com.example.testesom.Activities;

import static com.example.testesom.Utils.Util1.CAMINHO_SONS;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testesom.R;
import com.example.testesom.Dados.Result;
import com.example.testesom.Utils.MsgType;
import com.example.testesom.Utils.Util;
import com.example.testesom.Utils.Util1;

public class GravarActivity extends ListActivity implements OnClickListener{
	
	
	private Button 			gravarButton = null;
	private boolean 		isRecording = false;
	private Chronometer 	recordTime;
	private Chronometer 	currentTime;
	private TextView   		statusTextView;
	private Animation 		blinker;
	private Animation		blinkerWait;
	
	private static final String LOG_TAG = "GravarActivity";
	
	private Util util = Util.getInstance();
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		getGravarButton().setBackgroundResource (R.drawable.round_start_button);
		getGravarButton().setOnClickListener (this);
		
		
		util.getMp3Lame().setHandle (new Handler() {
			@Override
			public void handleMessage(Message msg)
			{
				MsgType msgType = MsgType.getType(msg.what);
//				getStatusTextView().setText(msgType.name());
				if (msgType!=MsgType.None && msgType!=MsgType.RecStarted && msgType!=MsgType.RecStopped)
					Toast.makeText(GravarActivity.this, msgType.name(), Toast.LENGTH_LONG).show();
			}
		});
		
	}
	
	public void onResume(){
		super.onResume();
        getGravarButton().clearAnimation();
		getGravarButton().setBackgroundResource (R.drawable.round_start_button);
		getGravarButton().setText ("Gravar");
		getRecordTime().setBase(SystemClock.elapsedRealtime());
		getStatusTextView().setText("");
	}
	
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.RecordButton:
				 onClickGravar();
			break;
		}
		
	}
		
	private void onClickGravar(){
		
		if (!isRecording) {
			startRecording ();
   		
   	 	}else{
   	 		stopRecording ();
   	 	}
	}
	
    private void  enviarAudioServidor(){
        File f = Util1.getArquivo();
        String result = "";
        
        (new AsyncEnviarServidor()).execute(f);
    }
	
	private void stopRecording() {
		isRecording = false;
		getGravarButton().clearAnimation();
		getGravarButton().setBackgroundResource (R.drawable.round_start_button);
		getGravarButton().setText ("Gravar");
		getRecordTime().stop();
//		getStatusTextView().setText (MsgType.RecStopped.name());
		util.getMp3Lame().stop();	
		enviarAudioServidor();
	}


	private void startRecording(){
		isRecording = true;
		util.clearMp3Pathname();
		getGravarButton().setBackgroundResource (R.drawable.round_stop_button);
		getGravarButton().setText ("Parar");
		getGravarButton().startAnimation (getBlinker());
		getRecordTime().setBase (SystemClock.elapsedRealtime());
		getRecordTime().start();
//		getStatusTextView().setText (MsgType.RecStarted.name());
		util.getMp3Lame().setFilePath (util.getMp3Pathname());
		util.getMp3Lame().start();
//		getTvFilename().setText ("["+Util.getFilenameFromPath (util.getMp3Pathname())+"]");
	}
	
	private Button getGravarButton(){
		if (gravarButton == null)
			gravarButton = (Button) findViewById(R.id.RecordButton);
		return gravarButton;
	}
	
	private TextView getStatusTextView(){
		if (statusTextView == null)
			statusTextView = (TextView) findViewById(R.id.tvStatus);
		
		return statusTextView;
	}
	
	private Chronometer getRecordTime()
	{
		if (recordTime == null)
		{
			recordTime = (Chronometer) findViewById(R.id.RecordTime);
			recordTime.setBase(SystemClock.elapsedRealtime());
			recordTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
	        @Override
	        public void onChronometerTick(Chronometer chronometer) {
	            CharSequence text = chronometer.getText();
	            if (text.length()  == 4)
	                chronometer.setText("0"+text);
        	}});
		}
		return recordTime;
	}
	
	private Animation getBlinker()
	{
		if (blinker == null)
		{
			blinker = new AlphaAnimation(1, 0);
			blinker.setDuration(1000);
			blinker.setInterpolator(new LinearInterpolator());
			blinker.setRepeatCount(Animation.INFINITE);
			blinker.setRepeatMode(Animation.REVERSE);
		}
		return blinker;
	}
	

	
	
    public String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                inputStream.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
	
    private String getUrl() {
    	BufferedReader br = null;
    	 //BufferedReader br = new BufferedReader(new FileReader(CAMINHO_SONS+"/ini"));
    	    try {
    	    	String sCurrentLine;
    	    	 
    			br = new BufferedReader(new FileReader(CAMINHO_SONS+"/ini"));
     
    			while ((sCurrentLine = br.readLine()) != null) {
    				return sCurrentLine;
    			}
    			
    			br.close();
    	    } catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		return "";
	}
	
    private class AsyncEnviarServidor extends AsyncTask<File, Void, String>{
        
        
    	protected void onPreExecute(){
    		
    		getGravarButton().setBackgroundResource (R.drawable.round_wait_button);
    		getGravarButton().setAnimation(getBlinker());
    		getGravarButton().setText("Buscando..");
    		getGravarButton().setTextSize(25);
    	}
    	

		@Override
        protected String doInBackground(File... params) {
            String fileName = params[0].getName();
            
            File sourceFile =params[0]; 
            
            HttpURLConnection conn = null;
            DataOutputStream dos = null;  
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024; 
            int serverResponseCode = 0;
            
            String upLoadServerUri = getUrl();//"http://192.168.0.79/testesom/server.php";
            
            if (!params[0].isFile()) {
                //dialog.dismiss(); 
                Log.d(LOG_TAG, "Source File not exist :");
            }else{
                
                try {
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);
                     
                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection(); 
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName); 
                     
                    dos = new DataOutputStream(conn.getOutputStream());
           
                    dos.writeBytes(twoHyphens + boundary + lineEnd); 
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);
                     
                    dos.writeBytes(lineEnd);
           
                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available(); 
           
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
           
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                    
                    
                    while (bytesRead > 0) {
                        
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                         
                       }
                    
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
           
                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                      
                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);
                    
                    if(serverResponseCode == 200){
                        InputStream instream = conn.getInputStream();
                        String data = convertStreamToString(instream);
                        
                        return data;
                    }
                    
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "ERRO"+e.getMessage();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "ERRO:MalformedURLException"+e.getMessage();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                	
                    e.printStackTrace();
                    return "ERRO"+e.getMessage();
                } 
            }
            return "ERRO";
        }
        



		protected void onPostExecute(String result){
//			Toast.makeText(GravarActivity.this, result, 1).show();
			
			
            JSONObject jobject = null;
            try {
                jobject= new JSONObject(result);
                
                
                Result r = new Result();
                r.setAlbum(jobject.getString("album"));
                r.setArtista(jobject.getString("artista"));
                r.setTitulo(jobject.getString("titulo"));
                r.setImagem(jobject.getString("imagem"));   
                
                
                Intent i = new Intent(GravarActivity.this,ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ResultActivity.EXTRA_RESP, r);

                i.putExtras(bundle);


                
                startActivity(i);
                
                
            } catch (JSONException e) {
                showErrorScreen();
            }
            
            
            //Toast.makeText(MainActivity.this, result, 1).show();
            
        }
        
        
    }

	public void showErrorScreen() {
		Toast.makeText(GravarActivity.this, "resultado indisponível", 1).show();
		getGravarButton().clearAnimation();
		getGravarButton().setBackgroundResource (R.drawable.round_start_button);
		getGravarButton().setText ("Gravar");
		getRecordTime().setBase(SystemClock.elapsedRealtime());
		getStatusTextView().setText("");
		
	}
	
}
