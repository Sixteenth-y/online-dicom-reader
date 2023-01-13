package com.dicomreader.controller;

import com.dicomreader.service.DcmReaderService;
import com.dicomreader.utils.DcmReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;

/**
 * @author Johnson
 * @describ
 * @create 2021-10-14
 */
@Controller
@RequestMapping(value = "/DcmReader")
public class DcmReaderController {

    @Resource
    private DcmReaderService dcmReaderService;

    private DcmReader dcmReader = DcmReader.getInstance();
    @Value("${tempFilePath.directory}")
    private String TEMP_DIRECTORY;

    //操作错误
    private String ERROR = "/static/DICOM/image/error2.png";
    //图片为空
    private String NULL = "/static/DICOM/image/NULL.jpeg";
    //操作记录
    private String OPERATION_PROCESS = "OPERATION_PROCESS";
    //时间格式
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    private HttpSession session;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView indexPage(){
        return new ModelAndView("dcmReader");
    }

    /**
     * 上传dicom文件
     * @param file 上传的文件
     * @return 保存的文件路径
     * @throws IOException
     */
    @RequestMapping(value = "upload")
    @ResponseBody
    public String uploadFile(@RequestParam(value = "file",required = true)MultipartFile file, HttpServletRequest request)
            throws IOException {
        String name =  dcmReaderService.initFile(file);
        BufferedImage image = dcmReaderService.getImageFile(file);
        if(image == null)
            return NULL;

        String paths[] = setFile(request, name.substring(name.lastIndexOf("/") + 1));
        String imagePath = paths[1];
        //paths[0]存储的绝对路径
        ImageIO.write(image,"bmp",new File(paths[0]));

        setOperationProgress(imagePath,request);
        return imagePath;
    }

    /**
     * 增亮
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/brightening")
    @ResponseBody
    public String brightening(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.brightening(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    /**
     * 直方图均衡化
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/histEqualize")
    @ResponseBody
    public String histEqualize(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.histEqualize(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }


    /**
     * 拉普拉斯变换
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/laplaceEnhance")
    @ResponseBody
    public String laplaceEnhance(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.laplaceEnhance(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    /**
     * 对数变换
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/logEnhance")
    @ResponseBody
    public String logEnhance(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.logEnhance(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 拉普拉斯变换
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/gammaEnhance")
    @ResponseBody
    public String gammaEnhance(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.gammaEnhance(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 均值滤波
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/blur")
    @ResponseBody
    public String blur(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.blur(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 中值滤波
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/medianBlur")
    @ResponseBody
    public String medianBlur(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.medianBlur(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 高斯模糊
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/GaussianBlur")
    @ResponseBody
    public String GaussianBlur(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.GaussianBlur(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 拉普拉斯锐化
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/sharpen")
    @ResponseBody
    public String sharpen(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.sharpen(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 开运算
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/open")
    @ResponseBody
    public String open(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.open(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 闭运算
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/close")
    @ResponseBody
    public String close(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.close(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 自适应阈值法
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/thresholdApt")
    @ResponseBody
    public String thresholdApt(@RequestBody Map<String, String> body
            , HttpServletRequest request){

        String path = body.get("path");
        //设置文件的所在的绝对路径
        String srcPath = TEMP_DIRECTORY + path
                .substring(path.lastIndexOf("static/") + 7);
        //提取文件名
        path = path.substring(path.lastIndexOf("/")+1);
        //分别获取处理后文件的绝对路径和网页访问路径
        String paths[] = setFile(request, path);
        try {
            //输入源文件的绝对路径
            BufferedImage image = dcmReader.thresholdApt(srcPath);
            //写入到处理后的文件
            ImageIO.write(image,"bmp",new File(paths[0]));
            //记录操作进程
            setOperationProgress(paths[1],request);
            return paths[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }
    /**
     * 撤消
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/back")
    @ResponseBody
    public String back(@RequestBody Map<String, String> body
            , HttpServletRequest request){
        session = request.getSession();
        String path = body.get("path");
        ArrayList<String> list = (ArrayList<String>)session.getAttribute(OPERATION_PROCESS);
        if(list != null || !list.isEmpty()){
            String result = list.size() <= 1
                    ? list.get(0)
                    : list.get(list.size() - 2);
            if(list.size() > 1) {
                list.remove(list.size() - 1);
                session.setAttribute(OPERATION_PROCESS, list);
            }
            return result;
        }


        return ERROR;
    }

    /**
     * 重置
     * @param body 请求主体
     * @return 处理后的文件路径
     */
    @PostMapping("/reset")
    @ResponseBody
    public String reset(@RequestBody Map<String, String> body
            , HttpServletRequest request){
        session = request.getSession();
        ArrayList<String> list = (ArrayList<String>)session.getAttribute(OPERATION_PROCESS);
        String result = "";
        if(list != null || !list.isEmpty()) {
            result = list.get(0);
            list.clear();
            list.add(result);
            session.setAttribute(OPERATION_PROCESS,list);
            return result;
        }

        return ERROR;
    }





    /**
     * 保存文件
     * @param path 请求参数
     * @param response 响应
     * @throws IOException
     */
    @RequestMapping("/download")
    public void downloadImage(@RequestParam String path, HttpServletResponse response)
            throws IOException {
        String name = URLDecoder.decode(path,"UTF-8");
        name = name.substring(name.lastIndexOf("static/") + 7);
        File newFile = new File(TEMP_DIRECTORY + name);
        if (!newFile.exists()) {
            throw new IOException(name + "文件不存在");
        }
        response.reset();
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition"
                , "attachment;fileName="
                        + URLEncoder.encode(newFile.getName(),"UTF-8"));
        response.setContentType("application/octet-stream");

        InputStream is = new BufferedInputStream(new FileInputStream(newFile));
        byte[] buffer = new byte[is.available()];
        try {

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());

            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 设置网页访问路径和文件保存路径
     * @param req 请求
     * @param fileName 文件名
     * @return "Absolute File Path","URL"
     */
    private String[] setFile(HttpServletRequest req,String fileName){
        //创建文件目录
        String result[] = {"FilePath","URL"};
        String format = sdf.format(new Date());
        File folder = new File(TEMP_DIRECTORY + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        String newName = System.currentTimeMillis()
                + fileName.substring(fileName.lastIndexOf("."));
        //网页访问的文件时的路径
        String filePath = req.getScheme() + "://" + req.getServerName()
                + ":" + req.getServerPort() +"/static/" + format + newName;
        result[0] = folder.getPath() +"/"+ newName ;
        result[1] = filePath;

        return result;
    }

    /**
     * 记录操作进程
     * @param path 图片路径-url
     * @param req 请求
     */
    private void setOperationProgress(String path, HttpServletRequest req){
        session = req.getSession();
        ArrayList<String> list = (ArrayList<String>) session.getAttribute(OPERATION_PROCESS);
        if(list == null)
            list = new ArrayList<String>();
        list.add(path);
        session.setAttribute(OPERATION_PROCESS,list);
    }
}
