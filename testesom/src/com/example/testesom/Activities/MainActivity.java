package com.example.testesom.Activities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testesom.R;
import com.example.testesom.R.id;
import com.example.testesom.R.layout;
import com.example.testesom.R.menu;
import com.example.testesom.Dados.Result;
import com.example.testesom.Utils.*;

import static com.example.testesom.Utils.Util1.CAMINHO_SONS;
import static com.example.testesom.Utils.Util1.FORMATO;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
                                    implements OnClickListener{
    
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private Button gravarButton = null;
    private ProgressBar progress = null;
    private TextView    statusText = null;
    private contadorTempoGravacao contador = null;
    
    boolean mStartRecording = true;
    private RecMicToMp3 mRecMicToMp3 = new RecMicToMp3(
			Environment.getExternalStorageDirectory() + "/mezzo.mp3", 32000);
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util1.criaDiretorio();
        
        gravarButton = (Button)         findViewById(R.id.buttonGravar);
        progress     = (ProgressBar)    findViewById(R.id.progressBar1);
        statusText   = (TextView)       findViewById(R.id.textView1);
        
        Button bt = (Button) findViewById(R.id.button1);
        bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,GravarActivity.class));
				
			}
        	
        });
        
        
        gravarButton.setOnClickListener(this);
        progress.setVisibility(View.INVISIBLE);
        
   
        
        mRecMicToMp3.setHandle(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RecMicToMp3.MSG_REC_STARTED:
//					statusTextView.setText("");
					break;
				case RecMicToMp3.MSG_REC_STOPPED:
//					statusTextView.setText("");
					break;
				case RecMicToMp3.MSG_ERROR_GET_MIN_BUFFERSIZE:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this,
//							"",
//							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_CREATE_FILE:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this, "",
//							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_REC_START:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this, "",
//							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_AUDIO_RECORD:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this, "",
//							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_AUDIO_ENCODE:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this, "",
//							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_WRITE_FILE:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this, "",
//							Toast.LENGTH_LONG).show();
					break;
				case RecMicToMp3.MSG_ERROR_CLOSE_FILE:
//					statusTextView.setText("");
//					Toast.makeText(MainActivity.this, "",
//							Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonGravar:
            onClickGravar();
            break;

        default:
            break;
        }
        
    }

    private void onClickGravar() {
    	 
    	 if (mStartRecording) {
    		 
    		 mRecMicToMp3.start();
    	 }else{
    		 mRecMicToMp3.stop();
    	 }
    	
    	
       /* onRecord(mStartRecording);
        if (mStartRecording) {
            contador = null;
            contador = new contadorTempoGravacao();
            contador.execute();
            gravarButton.setText("Gravar");
            progress.setVisibility(View.VISIBLE);
            
        } else {
            gravarButton.setText("Parar");
            progress.setVisibility(View.INVISIBLE);
            if(contador != null)
                contador.cancel(true);
            
            enviarAudioServidor();
                
        }
        mStartRecording = !mStartRecording;*/
        
    }
    
    private void onRecord(boolean start) {
        if (start) {
          // iniciaGravar();
        } else {
          // paraGravar();
        }
    }
    
    private void paraGravar() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        
       
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
    
    
    
    private void  enviarAudioServidor(){
        File f = Util1.getArquivo();
        String result = "";
        
        (new AsyncEnviarServidor()).execute(f);
        

        
        
//        /startActivity(i);
    }
    
    
    private class AsyncEnviarServidor extends AsyncTask<File, Void, String>{
        
        
        

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
                    return "ERRO"+e.getMessage();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                	
                    e.printStackTrace();
                    return "ERRO"+e.getMessage();
                } 
            }
            return "ERRO";
        }
        



		protected void onPostExecute(String result){
			Toast.makeText(MainActivity.this, result, 1).show();
			
            JSONObject jobject = null;
            try {
                jobject= new JSONObject(result);
                
                
                Result r = new Result();
                r.setAlbum(jobject.getString("album"));
                r.setArtista(jobject.getString("artista"));
                r.setTitulo(jobject.getString("titulo"));
                r.setImagem(jobject.getString("imagem"));   
                
                
                Intent i = new Intent(MainActivity.this,ResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ResultActivity.EXTRA_RESP, r);

                i.putExtras(bundle);

                
                
                startActivity(i);
                
                
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //Toast.makeText(MainActivity.this, result, 1).show();
            
        }
        
        
    }
    
   /* private void iniciaGravar() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(16);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setOutputFile(getNomeFile());
       

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }*/
    
    
    private String getNomeFile(){
        Date d = new Date(System.currentTimeMillis());
        
        return CAMINHO_SONS + DateFormat.format("hhMMssdd", d) + FORMATO;
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
    

    
    
    
    public class contadorTempoGravacao extends AsyncTask<Void, Integer,Void>{
        
      
        
        protected void onPreExecute(){
            statusText.setText(formataTempo(0));
        }
        
        
        @Override
        protected Void doInBackground(Void... params) {
            Integer tempo = 0;
            while(true){
                if (isCancelled())  
                    return null;
                try{
                    Thread.sleep(1000);
                    tempo ++;
                    publishProgress(tempo);
                    Log.d("ResultActivity","MSG");
                    
                }catch(Exception e ){
                    Log.d("TAG",e.toString());
                    
                }
            }
        }
        
        protected void onProgressUpdate(Integer... p){
            if(p == null){
                statusText.setText(formataTempo(0));
                
            }else if(statusText != null ){
                statusText.setText(formataTempo(p[0]));
            }
        }
        
        
        private String formataTempo(Integer i){
            Double min = (double) (i / 60);
            Double sec = 0.0;
            if(min > 0){
                sec  = min -  min.intValue();
                return min + "min e " + Math.ceil(sec*0.6) + " segundos.";
            }else{
                sec = (double) i;
                return sec + " segundos.";
            }
            
            
            
            
            
        }
        
    }

}
