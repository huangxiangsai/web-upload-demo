package com.devsai.ctrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by huangxiangsai on 16/4/19.
 * web上传Demo ，
 * 代码粗陋，只适用于学习
 */
@Controller

public class UploadController {

    private static String tempFilePath = System.getProperty("user.dir")+"/src/main/webapp/static/tempFiles/";

    @RequestMapping(value="formUpload",method = {RequestMethod.POST,RequestMethod.GET}  )
    public ModelAndView formUpload(HttpServletRequest request,HttpServletResponse response,@RequestParam("inputFile") MultipartFile file){
        System.out.println(file.getOriginalFilename());
        try {

            FileOutputStream out = new FileOutputStream(new File(tempFilePath+file.getOriginalFilename()));
            out.write(file.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ModelAndView view = new ModelAndView("redirect:/static/uploadMethod/formUpload.html");
        return view;
    }


    @RequestMapping(value="iframeUpload",method = {RequestMethod.POST,RequestMethod.GET}  )
    @ResponseBody
    public CommonResult iframeUpload(HttpServletRequest request,HttpServletResponse response,@RequestParam("inputFile") MultipartFile file){
        System.out.println(file.getOriginalFilename());
        try {
//            response.set
            FileOutputStream out = new FileOutputStream(new File(tempFilePath+file.getOriginalFilename()));
            out.write(file.getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Cookie c = new Cookie("h","d");
        c.setDomain("local.com");
        response.addCookie(c);
        CommonResult cr = new CommonResult();
        cr.setCode(200);
        cr.setMessage("成功");
        List<Map<String,?>> result = new ArrayList<Map<String,?>>();
        Map<String,String> map = new HashMap<String,String>();
        map.put("filePath", "/static/tempFiles/" + file.getOriginalFilename());
        result.add(map);
        cr.setData(result);
        return cr;
    }

    /**
     * 普通ajax上传
     * @return
     */
    @RequestMapping(value="upload",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult normalUpload(MultipartHttpServletRequest multipartRequest,HttpServletResponse resonse){//MultipartRequest multipartRequest
        MultipartFile file =  multipartRequest.getFile("upload_file");
        CommonResult cr = new CommonResult();
        try {
            FileOutputStream out = new FileOutputStream(new File(tempFilePath+file.getOriginalFilename()));
            out.write(file.getBytes());
            out.close();
            cr.setCode(200);
            cr.setMessage("成功");
            List<Map<String,?>> result = new ArrayList<Map<String,?>>();
            Map<String,String> map = new HashMap<String,String>();
            map.put("filePath", "/static/tempFiles/" + file.getOriginalFilename());
            map.put("name",file.getOriginalFilename());
            result.add(map);
            cr.setData(result);
        } catch (IOException e) {
            cr.setCode(500);
            cr.setMessage("上传失败");
            e.printStackTrace();
        }
        return cr;
    }



    /**
     * 分片上传
     * @param resonse
     * @param request
     * @param multipartRequest
     * @return
     */
    @RequestMapping(value="partupload",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult partUpload(HttpServletResponse resonse,HttpServletRequest request,MultipartHttpServletRequest multipartRequest){
        CommonResult cr = new CommonResult();
        cr.setCode(200);
        cr.setMessage("成功");

        MultipartFile file =  multipartRequest.getFile("upload_file");
        Integer chunks = (Integer.valueOf(request.getParameter("chunks")));
        Long uid = (Long.valueOf(request.getParameter("uid")));
        Integer currchunk = (Integer.valueOf(request.getParameter("currchunk")));
        String filename = request.getParameter("filename");

        if(!this.saveFile(file,filename,chunks,currchunk,uid)){
            cr.setCode(500);
            cr.setMessage("上传失败");
        }

        return cr;
    }

    /**
     * 服务端分片
     * @param resonse
     * @param request
     * @param multipartRequest
     * @return
     */
    @RequestMapping(value="rangeupload",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult serverPartUpload(HttpServletResponse resonse,HttpServletRequest request,MultipartHttpServletRequest multipartRequest){
        CommonResult cr = new CommonResult();
        cr.setCode(200);
        cr.setMessage("成功");

        return cr;
    }


    /**
     * 保存上传的文件
     * @param file  //上传的文件
     * @param chunks //分片总数  (不分片上传时，后两参数为0)
     * @param currChunk //当前的分片 (不分片上传时，后两参数为0)
     */
    public boolean saveFile(MultipartFile file, String fileName, int chunks , int currChunk,Long uid){
        try {
            Date date = new Date();
            Calendar cd = Calendar.getInstance();
            SimpleDateFormat sf = new SimpleDateFormat("yy-MM-dd");
            Long time = System.currentTimeMillis();
            String path = tempFilePath+"/"+sf.format(time)+"/"+uid;
            this.createDir(path);
            FileOutputStream out = new FileOutputStream(new File(path+"/"+fileName+".part_"+currChunk));
            out.write(file.getBytes());
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void createDir(String path){
        File dir = new File(path);
        if(!dir.exists()){
                dir.mkdirs();
        }
    }


    @RequestMapping(value="/getFile/{dir}/{uid}/{fileName}", method = RequestMethod.GET)
    public void getFile(@PathVariable("dir") String dir ,@PathVariable("uid") String uid , @PathVariable("fileName") String fileName ,
                        HttpServletResponse resonse){
        String path = tempFilePath+"/"+dir+"/"+uid;

        File file = new File(path);
        File[] filelist= file.listFiles();
        List<File> result = new ArrayList<File>();
        File[] list = new File[filelist.length];
        //过滤出文件片段
        for(int i = 0 ; i < filelist.length ; i++){
            File f = filelist[i];
            String name = f.getName();
            if(name.indexOf(fileName) != -1){
                list[i] = f;
            }
        }

        //按时间排序
        for(int i = 0 ; i < list.length ; i++){
            for(int j = i+1 ; j < result.size() ; j++){
                if(list[i].lastModified() > list[j].lastModified()){
                    File temp = list[j];
                    list[i] = list[j];
                    list[j] = temp;
                }
            }
        }

        try {
            OutputStream os = resonse.getOutputStream();
            List<byte[]> bytelist = new ArrayList<byte[]>();
            int len = 0;
            for(int i = 0 ; i < list.length ; i++){
                byte[] partFile= this.getPartFileByFile(list[i]);
                bytelist.add(partFile);
                os.write(partFile);
            }
            System.out.println(list.length);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得已上传的文件列表
     * @param resonse
     * @param request
     * @param
     * @return
     */
    @RequestMapping(value="fileList",method = RequestMethod.GET)
    @ResponseBody
    public CommonResult fileList(HttpServletResponse resonse,HttpServletRequest request){
        CommonResult cr = new CommonResult();
        cr.setCode(200);
        Map<String,Object> data = new HashMap<String,Object>();
        List<Map<String,?>> list = new ArrayList<Map<String,?>>();
        File dir = new File(tempFilePath);
        String[] dateDir = dir.list();
        for(int i = 0 ; i < dateDir.length ; i++){
            File f = new File(tempFilePath+"/"+dateDir[i]);
            if(f.isDirectory()){
                System.out.println(tempFilePath+"/"+dateDir[i]);
                File[] fs = f.listFiles((FileFilter)MyHiddenFileFilter.HIDDEN);
                System.out.println(fs.length);
                String[] timeDir = f.list();
                for(int j = 0 ; j < timeDir.length; j++){
                    File f2 = new File(tempFilePath+"/"+dateDir[i]+"/"+timeDir[j]);
                    if(f2.list().length > 0){
                        String name =  f2.list()[0];
                        name = name.replace(".part_0","");
                        Map<String,String> d = new HashMap<String,String>();
                        d.put("fileName",name);
                        d.put("filePath","/getFile/"+dateDir[i]+"/"+timeDir[j]+"/"+name);
                        list.add(d);
                    }
                }
            }
        }

        cr.setData(list);
        return cr;
    }

    private  byte[] getPartFileByFile(File file){
        try {
            InputStream input = new FileInputStream(file);
            byte[] result = new byte[(int)file.length()];
            input.read(result);
            input.close();
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void componentFile(String fileName , int chunks){

        List<byte[]> list = new ArrayList<byte[]>();
        for(int i = 0 ; i < chunks ;i++){
            byte[] partFile= this.getPartFile(fileName,i);
            System.out.println(partFile.length);
            list.add(partFile);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File("/Users/huangxiangsai/Desktop/u_"+fileName));
            for(int i = 0 ; i < list.size(); i++){
                out.write(list.get(i));
            }
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private byte[] getPartFile(String fileName,int currChunk){
        try {
            File file = new File("/Users/huangxiangsai/Desktop/u_"+fileName+".part_"+currChunk);
            InputStream input = new FileInputStream(file);
            byte[] result = new byte[(int)file.length()];
            input.read(result);
            input.close();
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}