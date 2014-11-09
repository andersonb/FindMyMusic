package com.example.testesom.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.example.testesom.Dados.Result;

import android.os.Environment;

public class Util1 {
    
    
    
    
    
    public static final String CAMINHO_SONS = Environment.getExternalStorageDirectory() + "/FindMyMusic/";
    public static final String FORMATO      = ".mp3";
    
    
    
    public static void criaDiretorio(){
        
        File f = new File(CAMINHO_SONS);
        if(!f.exists())
            f.mkdirs();
    }
    
    
    public static File getArquivo(){
        
        File directory = new File(CAMINHO_SONS);
        File[] files = directory.listFiles();
        
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            } });
        
        return files[files.length-1];
        
    }
    
    
    public static Result resultFromJSON(String json){
        Result r = new Result();
        
        return r;  
    }

}
