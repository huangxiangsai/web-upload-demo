package com.devsai.test;

import com.devsai.model.UploadMsg;
import com.google.protobuf.ByteString;

/**
 * Created by huangxiangsai on 16/7/8.
 */
public class MyProtoBuf {

    public static void main(String[] args) {

        // creating the cat
        UploadMsg.Upload uploadMsg= UploadMsg.Upload.newBuilder()
                .setFilename("")
                .setChunks(10)
                .setUid(1)
                .setCurrchunk(1)
//                .setFilenameBytes(new byte[4])
                .build();

        ByteString uploadFile = uploadMsg.getUploadFile();
        byte[] content = new byte[uploadFile.size()];
        uploadFile.copyTo(content,0);


//        try {
//            // write
//            FileOutputStream output = new FileOutputStream("cat.ser");
//            pusheen.writeTo(output);
//            output.close();
//
//            // read
//            Cat catFromFile = Cat.parseFrom(new FileInputStream("cat.ser"));
//            System.out.println(catFromFile);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
