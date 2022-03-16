package com.dicomreader.utils;

import com.dicomreader.pojo.MyDicom;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.tool.dcm2jpg.Dcm2Jpg;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Component
public class DcmReader {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        URL url = ClassLoader.getSystemResource("lib/opencv/opencv_java440.dll");
        System.load(url.getPath());
    }

    private static final String DEFAULT_PATH = "src/main/resources/static/DICOM/default";
    private static final String DEFAULT_IMAGE_PATH = "src/main/resources/static/DICOM/image/";
    private static final String DEFAULT_DICOM_PATH = "src/main/resources/static/DICOM/";
    private File dicom = null;
    private static final String BMP = "bmp";
    private static final String JPG = "jpg";
    private static MyDicom myDicom = new MyDicom();
    public String imagePath = new String();

    public static void main(String[] args) {
        DcmReader reader = new DcmReader();
        String[] path = {"82821227", "image-001.dcm", "image-002.dcm"};
        File dcmFile = new File(DEFAULT_DICOM_PATH + path[1]);
        reader.setDicom(dcmFile);
        //reader.getDcmFile();
        //reader.getDcmImage(BMP);
        reader.openDcmFile();
        try {
            reader.saveDcmFile("D:\\User\\Desktop\\test.dcm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(isDicomFile(dcmFile));
        reader.getDcmImage("bmp", "D:\\User\\Desktop\\");
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
        this.dicom = dcmFile;
    }

    public File getDicom() {
        if (dicom.exists() || dicom.isDirectory()){
            System.out.println("[success] DICOM: " + dicom.getPath());
            return this.dicom;
        }else {
            System.out.println("[error] DICOM file not find");
            return null;
        }

    }

    public String imagePath(){
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
            this.saveDcmFile(new DicomInputStream(dcmFile), path);
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
    public void saveDcmFile(DicomInputStream dcmIS) throws IOException {
        this.saveDcmFile(dcmIS, this.DEFAULT_PATH);
    }

    /**
     * 保存DICOM文件
     * @param path 保存路径
     * @throws IOException 异常抛出
     */
    public void saveDcmFile(String path) throws IOException{
        DicomInputStream dcmIS = new DicomInputStream(getDicom());
        this.saveDcmFile(dcmIS, path);
    }

    /**
     * 保存DICOM文件
     * @param dcmIS DICOM文件输入流
     * @param path  保存路径
     * @throws IOException 抛出异常
     */
    public void saveDcmFile(DicomInputStream dcmIS, String path) throws IOException {

        File dcmFile = new File(path);
        byte[] buffer = new byte[dcmIS.available()];
        dcmIS.read(buffer);

        DicomOutputStream dcmOS = new DicomOutputStream(dcmFile);
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
//            BufferedImage artworkBuffered = ImageIO.read(dcmFile);
//            BufferedImage thumbnailsBuffered = new BufferedImage(1024, 1024,
//                                                                BufferedImage.TYPE_INT_RGB);
//            thumbnailsBuffered.getGraphics().drawImage(artworkBuffered,0,
//                                                    0, 1024,
//                                                1024, null);
            BufferedImage image = ImageIO.read(dcmFile);

            File imageFile = new File(directory
                    + dcmFile.getName() + "." + imageFormat);
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
            System.out.println(jpegFile);
            covertUtil.convert(dcmFile, new File(jpegFile));
            System.out.println("[success] conversion complete");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[error] failed to open covert Image");
        }

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
            System.out.println();
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

    public void processing() {
        //DicomObject dicomObject = new DicomObject();
    }

    /**
     * 将RGB图像转换为灰度图片
     * @param imgPath 图片路径
     */
    public void rgb2gray(String imgPath){
        //调用openCV 的原生库名
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread(imgPath);
    }

}
