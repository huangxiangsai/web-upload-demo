package com.devsai.ctrl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

/**
 * Created by huangxiangsai on 16/4/19.
 */
public class MainCtrl {

        public MainCtrl(){

        }


        public static void main(String[] args){

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd");
            Long time = System.currentTimeMillis();
//            System.out.println(sf.format(time));
//            File file = new File("com/"+sf.format(time));
//            if(!file.exists()){
//                try {
//                    boolean success = file.createNewFile();
//                    System.out.println(success);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }

            String name = "huangxiangsai";
            StringBuffer sb = new StringBuffer("huang");

            System.out.println(name.contentEquals(sb));
            System.out.println(name.indexOf("xiang"));

            System.out.println("Array -----------------");
            int[] list = new int[]{3,6,1,62,34,22,9};
            for(int i = 0 ;i < list.length ; i++){
                int x = list[i];
                for(int j = i+1 ; j < list.length ; j++){
                    int y = list[j];
                    if(x > y){
                        int temp = x;
                        list[i] = y;
                        list[j] = temp;
                        x= y;

                    }
                }
            }

            for(int i = 0 ; i < list.length  ; i++){
                System.out.println(list[i]);
            }



        }

}
