package com.devsai.service;

import com.devsai.model.UploadFile;
import com.devsai.model.UploadMsg;
import com.google.protobuf.InvalidProtocolBufferException;

import javax.servlet.Servlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangxiangsai on 16/5/22.
 */
public class UploadService {
    private static String tempFilePath = System.getProperty("user.dir")+"/src/main/webapp/static/tempFiles/";


    public UploadFile saveUploadFile(UploadFile uploadFile){



        UploadFile uf = new UploadFile();
        return uf;
    }


    public void saveFile(byte[] res){
        try {
            UploadMsg.Upload upload= UploadMsg.Upload.parseFrom(res);
            System.out.println(upload.getChunks());
            System.out.println(upload.getCurrchunk() );
            System.out.println(upload.getUploadFile().size());

            Integer uid =  upload.getUid();
            String fileName = upload.getFilename();
            Integer currChunk = upload.getCurrchunk();

            SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd");
            Long time = System.currentTimeMillis();
            String path = tempFilePath+"/"+sf.format(time)+"/"+uid;
            this.createDir(path);
            FileOutputStream out = new FileOutputStream(new File(path+"/"+fileName+".part_"+currChunk));
            upload.getUploadFile().writeTo(out);
            out.close();

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFullFile(byte[] res){
        try {
            UploadMsg.Upload upload= UploadMsg.Upload.parseFrom(res);
            System.out.println(upload.getChunks());
            System.out.println(upload.getCurrchunk() );

            Integer uid =  upload.getUid();
            String fileName = upload.getFilename();
            Integer currChunk = upload.getCurrchunk();

            SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd");
            Long time = System.currentTimeMillis();
            String path = tempFilePath+"/"+sf.format(time)+"/"+uid;
            this.createDir(path);
            FileOutputStream out = new FileOutputStream(new File(path+"/"+fileName+".part_"+currChunk));
            upload.getUploadFile().writeTo(out);
            out.close();

        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createDir(String path){
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }


    public static void main(String[] args){

        System.out.println("sss");
    }


}
