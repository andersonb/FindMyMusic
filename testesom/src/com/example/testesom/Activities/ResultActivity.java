package com.example.testesom.Activities;



import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.testesom.R;
import com.example.testesom.Dados.Result;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity
                            implements OnItemClickListener {
    
    public static final String EXTRA_RESP = "com.example.testesom.Activities.EXTRA_RESP";
    
    public static final String TAG = "FindMyMusic";
    
    private ListView     listaBusca;
    private ImageView    image;
   // private ProgressBar  progress;
    
    private TextView    texViewTitulo;
    private TextView    texViewArtista;
    private TextView    texViewAlbum;
    private ProgressBar progressBar;
    private MediaPlayer mPlayer;
    
    private  Result r ;
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        
        r = (Result) getIntent().getExtras().getSerializable(EXTRA_RESP);
        
        listaBusca = (ListView)  findViewById(R.id.listView1);
        
        texViewTitulo     = (TextView)  findViewById(R.id.texViewTitulo);
        texViewArtista    = (TextView)  findViewById(R.id.textViewArtista);
        texViewAlbum      = (TextView)  findViewById(R.id.textViewAlbum);
        image 			  = (ImageView) findViewById(R.id.imageView1);
        progressBar       = (ProgressBar) findViewById(R.id.progressBar);
        
        image.setVisibility(View.GONE);
        
        
        
        String[] listItems = {"YouTube", "Spotify", "Google" }; 
        listaBusca.setAdapter(new MyArrayAdapter(this,R.layout.item_list,listItems));
        listaBusca.setOnItemClickListener(this);
        
        if(r != null){
            Log.d(TAG,"AsyncBaixaImagem");
            (new DownloadAsyncTask()).execute(r.getImagem());
            texViewAlbum.setText(r.getAlbum());
            texViewArtista.setText(r.getArtista());
            texViewTitulo.setText(r.getTitulo());
            Toast.makeText(getApplicationContext(),r.getLinks().get(0),Toast.LENGTH_LONG).show();
            Log.d(TAG,r.getLinks().get(0));
            Log.d(TAG,r.getLinks().toString());
            (new PlayAsyncTask()).execute(r.getLinks().get(0));
        }
        
       
    }

    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
       }
        
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Intent intent = null;
        String appPackageName = "";
        String apps = "/details?id=";
        switch (position) {
	        case 0:       	
	            intent = new Intent(Intent.ACTION_SEARCH);
	            intent.setPackage("com.google.android.youtube");
	            intent.putExtra("query",  r.toString());
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            appPackageName = apps + "com.google.android.youtube";
	            break;
	        case 1: 
	            intent = new Intent(Intent.ACTION_MAIN);
	            intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
	            intent.setPackage("com.spotify.music");
	            intent.putExtra(SearchManager.QUERY, r.toString());
	            appPackageName = apps +  "com.spotify.music";
	            break;
	        case 2: 
	            intent = new Intent(Intent.ACTION_WEB_SEARCH);
	            intent.putExtra(SearchManager.QUERY,r.toString());
	            break;
	
	        default:
	            break;
        }
        
        try{
        	startActivity(intent);
        }catch(Exception e){
        	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps" + appPackageName)));
        	startActivity(intent);
        }
    }
    
    class MyArrayAdapter extends ArrayAdapter<String>{
    	
    	private String[] lista;

		public MyArrayAdapter(Context context, int textViewResourceId,String[] objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			this.lista = objects;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			
			String s = lista[position];
			
			 if (convertView == null) {
		          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
		     }
			 TextView text = (TextView)  convertView.findViewById(R.id.textView_Item);  
	         ImageView img = (ImageView) convertView.findViewById(R.id.imageView_Item);
	
	         text.setText(s);
	         img.setImageResource(getImg(s));
	         
	         return convertView;
		}

		private int getImg(String s) {
			if(s.equals("YouTube"))
				return R.drawable.youtube;
			
			if(s.equals("Spotify"))
				return R.drawable.spotfy;
			
			if(s.equals("Google"))
				return R.drawable.google;
			
			return R.drawable.ic_launcher;
		}
		
		


    	
    	
    }
    
    public void onPause(){
    	super.onPause();
    	if(mPlayer != null)
    		mPlayer.release();
    }
    
    
    private class PlayAsyncTask extends AsyncTask<String, Void, String>{
    	
    	
    	

    	protected String doInBackground(String... params){
    		
    		mPlayer = new MediaPlayer();
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mPlayer.setDataSource(params[0]);
			} catch (IllegalArgumentException e) {
				return "You might not set the URI correctly!"+ e.toString();
			} catch (SecurityException e) {
				return "You might not set the URI correctly!"+ e.toString();
			} catch (IllegalStateException e) {
				return "You might not set the URI correctly!"+ e.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				mPlayer.prepare();
			} catch (IllegalStateException e) {
				return "You might not set the URI correctly!" + e.toString();
			} catch (IOException e) {
				return"You might not set the URI correctly!"+ e.toString();
			}
			mPlayer.start();
			return null;
    	}
    	
    	protected void onPostExecute(String result){
    		Log.d(TAG,"RESULT"+result);
    		Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
    		
    	}
    	
    	
    }
    
    
    
    private class DownloadAsyncTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d(TAG,"doInBackground");
              try{
                  
                  URL imageUrl = new URL(params[0]);
                  Log.d(TAG,imageUrl.toString());
                  return BitmapFactory.decodeStream(imageUrl.openStream());
                  
              }catch(IOException e ){
                  Log.d(TAG,e.toString());
                  return null;
                  
              }
        }
        
        protected void onPostExecute(Bitmap result){
            Log.d(TAG,result == null ? "SIM": "NAO");
            progressBar.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            if(result != null){
                image.setImageBitmap(result);
            }else{
                image.setImageResource(R.drawable.ic_launcher);
            }
        }
         
        
    }
}
