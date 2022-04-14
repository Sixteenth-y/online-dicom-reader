package com.dicomreader.utils;

import com.dicomreader.pojo.MyDicom;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
//import org.dcm4che3.imageio.plugins.*;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

public class DcmReader {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java440.dll");
        System.load(url.getPath());
        System. setProperty("java.awt.headless", "false");
    }
    private static final int SKELETON = 0;/*骨骼*/
    private static final int THORACIC_CAVITY = 1;/*胸腔*/
    private static final int LUNG = 2;/*肺部*/
    private static final int ABDOMEN = 3;/*腹部*/
    private static final String DEFAULT_PATH = "src/main/resources/static/DICOM/default";
    private static final String DEFAULT_IMAGE_PATH = "src/main/resources/static/DICOM/image/";
    private static final String DEFAULT_DICOM_PATH = "src/main/resources/static/DICOM/";
    private static final String DEFAULT_TEMP_PATH = "src/main/resources/static/DICOM/image/temp/";
    private static final String ERROR =
            "src/main/resources/static/DICOM/image/error.jpg";
    private File dicom = null;
    private static final String BMP = "bmp";
    private static final String JPG = "jpg";
    private static MyDicom myDicom = new MyDicom();
    public String imagePath = "";

    public static void main(String[] args) {
        DcmReader reader = new DcmReader();
        String[] path = {"82821227", "image-00001.dcm", "image-00000.dcm", "image_001.dcm", "image_002.dcm" };
        File dcmFile = new File(DEFAULT_DICOM_PATH + path[0]);
        reader.setDicom(dcmFile);
        //reader.getDcmFile();
        //reader.getDcmImage(BMP);
        reader.openDcmFile();
        try {
            reader.saveDcmFile("D:\\User\\Desktop\\test.dcm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("result: "+isDicomFile(dcmFile));
        reader.getDcmImage();
        try {
            //reader.rgb2gray(reader.getImagePath());
            reader.brightening(ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //reader.imagePix();
    }

    public DcmReader() {
    }


    /**
     * 判断该文件是或否为DICOM文件
     * @param dcmFile   DICOM文件
     * @return  返回判断
     */
    public static boolean isDicomFile(File dcmFile){
        //文件前言128字节 + DICOM文件前缀4字节
        byte[] buffer = new byte[132];
        byte[] dicomHead = new byte[4];
        try {
            FileInputStream fIS = new FileInputStream(dcmFile);
            fIS.read(buffer, 0, 132);
            fIS.close();
            System.arraycopy(buffer, 128, dicomHead, 0, buffer.length - 128);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("[Error] fail to open the file ");
        }
        return "DICM".equals(new String(dicomHead));
    }

    public void setDicom(File dcmFile) {
        if(isDicomFile(dcmFile))
            this.dicom = dcmFile;
        else
            this.dicom = null;
    }

    public File getDicom() {
        if(this.dicom == null){
            System.out.println("[error] not DICOM");
            return null;
        }

        if (dicom.exists() || dicom.isDirectory()){
            System.out.println("[success] DICOM: " + dicom.getPath());
            return this.dicom;
        }else {
            System.out.println("[error] DICOM file not find");
            return null;
        }

    }

    public String getImagePath(){
        return imagePath;
    }

    public MyDicom getMyDicom(){
        return this.myDicom;
    }

    /**
     * 打开DICOM文件，将部分信息保存在MyDicom里。执行后续操作的必要条件
     */
    public void openDcmFile() {
        File dcmFile = getDicom();
        if(dcmFile == null){
            System.out.println("[error] variable 'dicom' is null");
            return;
        }
        if(!isDicomFile(dcmFile)){
            System.out.println("[error] not dicom file");
            return;
        }
        try {
            DicomInputStream dcmIS = new DicomInputStream(dcmFile);
            Attributes fmi = dcmIS.readFileMetaInformation();
            Attributes attrs = dcmIS.readDataset();

            myDicom.setHeight(attrs.getInt(Tag.Rows, 0));
            myDicom.setWidth(attrs.getInt(Tag.Columns, 0));
            //fmi.size();
            myDicom.setSeriesInstanceUID(attrs.getString(Tag.SeriesInstanceUID));
            System.out.println("Series Instance UID:" + myDicom.getSeriesInstanceUID());

            myDicom.setStudyInstanceUID(attrs.getString(Tag.StudyInstanceUID));
            System.out.println("Study Instance UID:" + myDicom.getStudyInstanceUID());

            myDicom.setPatientID(attrs.getString(Tag.PatientID));
            System.out.println("Patient ID:" + myDicom.getPatientID());

            myDicom.setPatientName(attrs.getString(Tag.PatientName));
            System.out.println("Patient Name:S" + attrs.getString(Tag.PatientName));

            myDicom.setSOPInstanceUID(attrs.getString(Tag.SOPInstanceUID));
            System.out.println("SOP Instance UID:" + attrs.getString(Tag.SOPInstanceUID));

            myDicom.setImageType(attrs.getString(Tag.ImageType));
            System.out.println("ImageType: " + attrs.getString(Tag.ImageType));

            byte[] pixelData = attrs.getBytes(Tag.PixelData);

            System.out.println("Modality: " + attrs.getString(Tag.Modality));


            myDicom.setDcmImage(ImageIO.read(dcmFile));
            dcmIS.close();

            System.out.println("[success] open the file");

        }catch (FileNotFoundException e) {
            System.out.println("[error] File Not Found!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[error] can't open the file!");
        }catch(Exception e){
            System.out.println("[error] can't open the file!");
        }

    }

    public BufferedImage myDcmRead(File dcmFile) throws IOException {
        ArrayList<String> list = new ArrayList();
        Collections.addAll(list, ImageIO.getReaderFileSuffixes());

        String fileName = dcmFile.getName();

        BufferedImage image = new BufferedImage(myDicom.getWidth(),
                myDicom.getHeight(),
                TYPE_INT_RGB);


        if(image == null){
            throw new IOException("can't read the dcm file by myDcmRead");
        }
        return image;
    }

    /**
     * 保存DICOM文件，无参函数保存在默认文件加内
     * DEFAULT_PATH
     * "src/main/resources/static/DICOM/default"
     */
    public void saveDcmFile() {
        File dcmFile = getDicom();
        this.saveDcmFile(dcmFile, this.DEFAULT_PATH);
    }

    /**
     * 保存DICOM文件
     * @param dcmFile DICOM 文件
     * @param path  存储路径
     */
    public void saveDcmFile(File dcmFile, String path) {
        try {
            this.saveDcmFile(new FileInputStream(dcmFile), path);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.out.println("[error] can't save the file!!!");
        }

    }

    /**
     * 保存DICOM文件
     * @param dcmIS DICOM文件输入流
     * @throws IOException 异常抛出
     */
    public void saveDcmFile(FileInputStream dcmIS) throws IOException {
        this.saveDcmFile(dcmIS, this.DEFAULT_PATH);
    }

    /**
     * 保存DICOM文件
     * @param path 保存路径
     * @throws IOException 异常抛出
     */
    public void saveDcmFile(String path) throws IOException{
        //DicomInputStream dcmIS = new DicomInputStream(getDicom());
        FileInputStream dcmIS = new FileInputStream(getDicom());
        this.saveDcmFile(dcmIS, path);
    }

    /**
     * 保存DICOM文件
     * @param dcmIS DICOM文件输入流
     * @param path  保存路径
     * @throws IOException 抛出异常
     */
    public void saveDcmFile(FileInputStream dcmIS, String path) throws IOException {

        File dcmFile = new File(path);
        byte[] buffer = new byte[dcmIS.available()];
        dcmIS.read(buffer);

        FileOutputStream dcmOS = new FileOutputStream(dcmFile);
        dcmOS.write(buffer);

        System.out.println("[success] completely open image!");

        dcmIS.close();
        dcmOS.close();

    }

    public void getDcmImage() {
        this.getDcmImage(BMP);
    }

    public void getDcmImage(String imageFormat){
        this.getDcmImage(imageFormat, DEFAULT_IMAGE_PATH);
    }

    /**
     * 显示图片
     * @param imageFormat 图片格式
     * @param directory 保存路径的上级目录
     */
    public void getDcmImage(String imageFormat, String directory) {
        try {
            File dcmFile = this.dicom;
            //System.out.println(dcmFile.isFile());
//            BufferedImage artworkBuffered = ImageIO.read(dcmFile);
//            BufferedImage thumbnailsBuffered = new BufferedImage(1024, 1024,
//                                                                BufferedImage.TYPE_INT_RGB);
//            thumbnailsBuffered.getGraphics().drawImage(artworkBuffered,0,
//                                                    0, 1024,
//                                                1024, null);
            BufferedImage image = ImageIO.read(dcmFile);

            String fileName = name4imgPath(dcmFile.getName())[1];
            File imageFile = new File(directory
                    + fileName + "." + imageFormat);
            ImageIO.write(image, imageFormat, imageFile);
            imagePath = imageFile.getAbsolutePath();
            System.out.println("[success] completely open image!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[error] can't open the image!!!");
        }
    }

    @Deprecated
    public void convertImage() {
        File dcmFile = this.dicom;
        Dcm2Jpg covertUtil = new Dcm2Jpg();
        covertUtil.initImageWriter("JPEG", null, "com.sun.imageio.plugins.*", null, 1);
        try {
            String jpegFile = DEFAULT_IMAGE_PATH + "covertImage-" + dcmFile.getName() + "." + BMP;
            //System.out.println(jpegFile);
            covertUtil.convert(dcmFile, new File(jpegFile));
            System.out.println("[success] conversion complete");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[error] failed to open covert Image");
        }

    }

    /**
     * dcm转jpg
     * @param dcmFile  dcm文件
     * @param jpgFile Jpg文件
     * @return
     */
    public static boolean dcm2jpg(File dcmFile, File jpgFile) {
        try {
            Dcm2Jpg dcm2jpg = new Dcm2Jpg();
            dcm2jpg.initImageWriter("JPEG", null, "com.sun.imageio.plugins.*", null, 1);
            dcm2jpg.convert(dcmFile, jpgFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取图像像素
     */
    public void imagePix() {
        BufferedImage buffer = myDicom.getDcmImage();
        int i = 0;
        byte[] data = new byte[myDicom.getWidth()* myDicom.getHeight()];
        for (int row = 0; row < myDicom.getHeight(); row++) {
            for (int col = 0; col < myDicom.getWidth(); col++) {
                int rgbPixel = buffer.getRGB(row, col);
                int pixel = rgbPixel >> 16;
                int grayPixel = (rgbPixel & 0xFF0000)>>16 *3 + (rgbPixel & 0x00FF00)>>8 * 6
                        + (rgbPixel & 0x0000FF);
                data[i++] = (byte)grayPixel;
                System.out.print(grayPixel);
                System.out.print(" ");
            }
            //System.out.println();
        }
        try {
            FileOutputStream fileOS = new FileOutputStream(DEFAULT_PATH+"/test.txt");
            fileOS.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void imageCopy(BufferedImage copy){
        BufferedImage temp = this.myDicom.getDcmImage();
        copy.copyData((WritableRaster)temp.getData());
    }

    public void processing() {

    }

    /**
     * 从路径提取文件名字
     * @param imgPath 文件路径
     * @return  result[]  0---fullImgName
     *                    1---imgName
     *                    2---format
     *                    3---direction
     */
    public static String[] name4imgPath(String imgPath){
        imgPath = imgPath.replaceAll("\\\\","/");
        String[] result = new String[4];

        String fileName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        result[0] = fileName;

        result[1] = fileName.substring(0,fileName.lastIndexOf("."));

        String format = imgPath.substring(imgPath.lastIndexOf(".")+1);
        result[2] = format;

        result[3] = imgPath.substring(0,imgPath.lastIndexOf("/")+1);
        return result;
    }

    /**
     * Mat转换成BufferedImage
     *
     * @param matrix
     *            要转换的Mat
     * @param fileExtension
     *            格式为 ".jpg", ".png", etc
     * @return
     */
    public static BufferedImage Mat2BufImg (Mat matrix, String fileExtension) {
        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }

    public static void saveProcessImg(BufferedImage image, String filePath)
            throws IOException {
        File dstFile = new File(filePath);
        if(!ImageIO.write(image,"bmp",dstFile)){
            System.out.println(("[error] can't not save the image!"));
        }

    }

    /**
     * 将RGB图像转换为灰度图片
     * @param imgPath 图片路径
     */
    public BufferedImage rgb2gray(String imgPath)throws Exception{
        //调用openCV 的原生库名
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread(imgPath);
        String fileName = name4imgPath(imgPath)[0];
        if(image.empty()){
            throw new Exception("image is empty");
        }
        imshow("Original Image", image);

        // 创建输出单通道图像
        Mat grayImage = new Mat(image.rows(), image.cols(), CvType.CV_8SC1);
        // 进行图像色彩空间转换
        cvtColor(image, grayImage, COLOR_RGB2GRAY);
        String tempFileName = DEFAULT_TEMP_PATH + "gray_"+fileName;
        //imshow("Processed Image", grayImage);
        imwrite(tempFileName, grayImage);
        //waitKey();
        return ImageIO.read(new File(tempFileName));

    }

    /**
     *
     * @param image
     * @return
     * @throws Exception
     */
    public Mat rgb2gray(Mat image)throws Exception{

        if(image.empty()){
            throw new Exception("image is empty");
        }
        imshow("Original Image", image);

        // 创建输出单通道图像
        Mat grayImage = new Mat(image.rows(), image.cols(), CvType.CV_8SC1);
        // 进行图像色彩空间转换
        cvtColor(image, grayImage, COLOR_RGB2GRAY);

        return grayImage;

    }

    /**
     * 调窗算法
     * @param imgPath 图片路径
     * @param type  部位类型
     */
    public void windowLeveling(String imgPath, int type) throws IOException {

        BufferedImage image = myDcmRead(new File(imgPath));

        FileInputStream fIS = new FileInputStream(new File(imgPath));

        byte[] pixelData = new byte[fIS.available()];

        fIS.read(pixelData);
        fIS.close();

        switch(type){
            case SKELETON:
                windowLeveling(pixelData,400, 2000);
                break;
            case THORACIC_CAVITY:
                windowLeveling(pixelData, 50, 350);
                break;
            case LUNG:
                windowLeveling(pixelData, -600, 1500);
                break;
            case ABDOMEN:
                windowLeveling(pixelData, 45, 250);
                break;
            default:
                break;
        }
    }

    /**
     * 调窗算法
     * @param pixelData 照片像素数组
     * @param windowCenter 窗位
     * @param windowWidth   窗宽
     *
     */
    public void windowLeveling( byte[] pixelData, float windowCenter, float windowWidth){
        float fMin = ( 2.0f * windowCenter - windowWidth )/ 2.0f + 0.5f;
        float fMax = ( 2.0f * windowCenter + windowWidth )/ 2.0f + 0.5f;

        BufferedImage bufferedImage = new BufferedImage(myDicom.getWidth(),
                myDicom.getHeight(),
                TYPE_INT_RGB);
    }

    /**
     * 图像亮度提高
     * @param imgPath 图像路径
     * alpha 1.5    beta 10
     */
    public BufferedImage brightening(String imgPath){


        return this.brightening(imgPath, 1.2f, 10);

    }

    /**
     * 图像亮度提高
     * @param imgPath 图像路径
     * @param alpha 增强变量
     * @param beta 偏移量
     */
    public BufferedImage brightening(String imgPath, float alpha, float beta){
        Mat image = Imgcodecs.imread(imgPath);
        BufferedImage bImage =null;
        //Mat imgDst = image.clone();
        Mat imgDst = new Mat(image.rows(), image.cols(), image.type());

        String fileName = name4imgPath(imgPath)[0];
        //g(x,y) = f(x,y) * alpha + beta
        image.convertTo(imgDst, -1, alpha, beta);
        return Mat2BufImg(imgDst,".bmp");

    }

    /**
     * 直方图均衡化
     * @param imgPath 图像路径
     * @return 图像处理结果
     */
    public BufferedImage histEqualize(String imgPath){
        Mat src = Imgcodecs.imread(imgPath);
        BufferedImage bImage = null;
        Mat dst = src.clone();

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> list1 = new ArrayList();

        Core.split(dst, list1);
        Imgproc.equalizeHist(list1.get(0), list1.get(0));
        Core.normalize(list1.get(0), list1.get(0), 0, 255, Core.NORM_MINMAX);
        Core.merge(list1, dst);

        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_YCrCb2BGR);
        return Mat2BufImg(dst,".bmp");
    }

    /**
     *拉普拉斯变换
     * @param imgPath 图像路径
     * @return 处理结果
     */
    public BufferedImage laplaceEnhance(String imgPath) {
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();

        float[] kernel = {0, 0, 0, -1, 5f, -1, 0, 0, 0};
        Mat kernelMat = new Mat(3, 3, CvType.CV_32FC1);
        kernelMat.put(0, 0, kernel);
        Imgproc.filter2D(dst, dst, CvType.CV_8UC3, kernelMat);

        return Mat2BufImg(dst,".bmp");
    }

    /**
     * 对数变换
     * @param imgPath 图像路径
     * @return 处理结果
     */
    public BufferedImage logEnhance(String imgPath) {
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();
        Mat imageResult = new Mat(dst.size(), CvType.CV_32FC3);

        Core.add(dst, new Scalar(5, 5, 5), dst);
        dst.convertTo(dst, CvType.CV_32F);
        Core.log(dst, imageResult);
//        Core.multiply(imageLog, new Scalar(3,3,3), imageLog);

        Core.normalize(imageResult, imageResult, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(imageResult, imageResult);

        return Mat2BufImg(imageResult,".bmp");
    }

    /**
     * 伽马变换
     * @param imgPath 图片路径
     * @return
     */
    public BufferedImage gammaEnhance(String imgPath) {
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();
        dst.convertTo(dst, CvType.CV_32F);

        Core.pow(dst, 4, dst);

        Core.normalize(dst, dst, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(dst, dst);

        return Mat2BufImg(dst,".bmp");
    }

    /**
     * 均值滤波
     * @param imgPath
     * @return
     */
    public BufferedImage blur(String imgPath){
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();

        Size kSize = new Size(9,9);
        Imgproc.blur(src, dst, kSize);
        return Mat2BufImg(dst,".bmp");
    }

    /**
     * 中值滤波
     * @param imgPath
     * @return
     */
    public BufferedImage medianBlur(String imgPath){
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();

        int kSize = 7;
        Imgproc.medianBlur(src, dst, kSize);
        return Mat2BufImg(dst,".bmp");
    }

    /**
     * 高斯模糊
     * @param imgPath 图片路径
     * @return
     */
    public BufferedImage GaussianBlur(String imgPath){
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();
        Size kSize = new Size(9,9);
        Imgproc.GaussianBlur(src, dst, kSize,0,0);
        return Mat2BufImg(dst,".bmp");
    }

    /**
     *拉普拉斯锐化
     * @param imgPath
     * @return
     */
    public BufferedImage sharpen(String imgPath){
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();
        Mat kernelMat = new Mat(3,3,CvType.CV_16SC1);
        kernelMat.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
        Imgproc.filter2D(dst, dst,dst.depth(),kernelMat);
        return Mat2BufImg(dst,".bmp");
    }

    /**
     *开运算
     * @param imgPath
     * @return
     */
    public BufferedImage open(String imgPath){
        return this.openOrClose(imgPath,2,"open");
    }

    /**
     * 闭运算
     * @param imgPath
     * @return
     */
    public BufferedImage close(String imgPath){
        return this.openOrClose(imgPath,2,"close");
    }

    /**
     * 开运算与闭预算
     * @param imgPath
     * @param kSize
     * @param model
     * @return
     */
    public BufferedImage openOrClose(String imgPath, int kSize, final String model){
        Mat src = Imgcodecs.imread(imgPath);
        Mat dst = src.clone();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,
                new Size(2 * kSize + 1, 2 * kSize + 1),
                new Point(-1, -1));
        if("open".equals(model)) {
            Imgproc.erode(dst, dst, kernel);
            Imgproc.dilate(dst, dst, kernel);
        }else if("close".equals(model)) {
            Imgproc.dilate(dst, dst, kernel);
            Imgproc.erode(dst, dst, kernel);
        }
//         MORPH_ERODE = 0,腐蚀  暗色扩张亮色被侵蚀
//         MORPH_DILATE = 1,膨胀 亮色扩张暗色被侵蚀
//         MORPH_OPEN = 2, 开运算 先腐蚀，再膨胀，可清除一些小东西(亮的)，放大局部低亮度的区域
//         MORPH_CLOSE = 3, 闭运算 先膨胀，再腐蚀，可清除小黑点
//         MORPH_GRADIENT = 4, 形态学梯度 膨胀图与腐蚀图之差，提取物体边缘
//         MORPH_TOPHAT = 5,顶帽 原图像-开运算图，突出原图像中比周围亮的区域
//         MORPH_BLACKHAT = 6,黑帽 闭运算图-原图像，突出原图像中比周围暗的区域
//         MORPH_HITMISS = 7;击中与击不中 图片类型为CV_8UC1才可以
//        Imgproc.morphologyEx(dst, dst, Imgproc.MORPH_HITMISS, kernel);
        return Mat2BufImg(dst,".bmp");
    }

    /**
     * 自适应阈值法
     * @param imgPath
     * @return
     */
    public BufferedImage thresholdApt(String imgPath){
        Mat src = Imgcodecs.imread(imgPath);
        Mat temp = src.clone();
        Mat gray = temp.clone();
        try {
            gray = rgb2gray(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Mat dst = gray.clone();

        Imgproc.adaptiveThreshold(gray,dst,255,
                0,THRESH_BINARY_INV,7,8);

        return Mat2BufImg(dst,".bmp");
    }


}
