/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.libliflin.games.wordrelease.mediawikiexport07.Page;

/**
 *
 * @author wjlaffin
 */
public class EnglishFileApp {
        static String protoFile = "C:\\Users\\wjlaffin\\englishPagesProto.txt";
    public static void main(String[] args) {
        
        long startTime = System.currentTimeMillis();
        //Page[] readEnglishPages = readEnglishPagesFromOOFile();
//        Wiki readEnglishPages = readEnglishPagesfromProtoFile();
        
//        doSomethingWith(readEnglishPages);
        
//        Page[] pages = fromWiki(readEnglishPages);
        //writeProtoFileText(readEnglishPages);
        
//        System.out.println("Pages Length:" + readEnglishPages.getPageCount());
        
        System.out.println("In " + (System.currentTimeMillis() - startTime) / 1000. + " seconds.");
    }
    
//    private static void doSomethingWith(Wiki wiki) {
//        WikiProtos.Page pageProto = wiki.getPage(0);
//        System.out.println(pageProto.getText());
//    }
    
//    public static Page[] fromWiki(Wiki wiki){
//        int n = wiki.getPageCount();
//        Page[] pages = new Page[n];
//        
//        for(int i = 0; i < n; i++){
//            WikiProtos.Page pageProto = wiki.getPage(i);
//            Page page = new Page();
//            page.redirect = pageProto.getRedirect();
//            page.restrictions = pageProto.getRestrictions();
//            page.text = pageProto.getText();
//            page.title = pageProto.getTitle();
//            pages[i] = page;
//        }
//        return pages;
//    }
    
//    public static Wiki readEnglishPagesfromProtoFile(){
//        FileInputStream fin = null;
//        try {
//            fin = new FileInputStream(protoFile);
//            CodedInputStream cis = CodedInputStream.newInstance(fin);
//            cis.setSizeLimit(0x7FFFFFFF);
//            Wiki wiki = Wiki.parseFrom(cis);
//            return wiki;
//        } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            Logger.getLogger(EnglishFileApp.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//                ex.printStackTrace();
//            Logger.getLogger(EnglishFileApp.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                fin.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//                Logger.getLogger(EnglishFileApp.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return null;
//    }
    
    public static Page[] readEnglishPagesFromOOFile() {
        Page[] readPages = null;
        //String filename = Wikt2EnglishPage.englishPagesFile;
        String filename = "C:\\englishPages.txt";
        ObjectInputStream inputStream = null;
        try {
            //Construct the ObjectInputStream object
            inputStream = new ObjectInputStream(new FileInputStream(filename));
            Object obj = null;
            while ((obj = inputStream.readObject()) != null) {
                if (obj instanceof Page[]) {
                    readPages = (Page[])obj;
                }
            }
        } catch (EOFException ex) { //This exception will be caught when EOF is reached
            System.out.println("End of file reached.");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the ObjectInputStream
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return readPages;
    }

//    public static void writeProtoFileText(Page[] readEnglishPages) {
//        Wiki.Builder wikiBuilder = Wiki.newBuilder();
//        
//        for(Page page : readEnglishPages){
//            WikiProtos.Page pageProto = 
//                    WikiProtos.Page.newBuilder()
//                    .setRedirect(page.redirect)
//                    .setRestrictions(page.restrictions != null? page.restrictions: "")
//                    .setText(page.text != null? page.text: "")
//                    .setTitle(page.title != null? page.title: "")
//                    .build();
//            wikiBuilder.addPage(pageProto);
//        }
//        Wiki wikiProto = wikiBuilder.build();
//        try {
//            FileOutputStream fout = new FileOutputStream(protoFile);
//            wikiProto.writeTo(fout);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//            System.out.println(ex);
//            Logger.getLogger(EnglishFileApp.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex){
//            ex.printStackTrace();
//            System.out.println(ex);
//        }
//    }

}
