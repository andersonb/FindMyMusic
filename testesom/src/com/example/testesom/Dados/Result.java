package com.example.testesom.Dados;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class Result implements Serializable {
    
    
    private String titulo;
    private String album;
    private String artista;
    private String imagem;
    private ArrayList<String> links;
    
    public Result(){
    	
        
    }
    
    public String toString(){
        return this.artista + " - " + this.titulo.split("\\(")[0];
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public ArrayList<String> getLinks() {
        if(links == null)
        	links = new ArrayList<String>();
        return links;
    }

//    public void setLinks(ArrayList<String> links) {
//        this.links = links;
//    }
}
