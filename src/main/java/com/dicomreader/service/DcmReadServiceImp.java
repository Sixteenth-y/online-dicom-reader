package com.dicomreader.service;

import com.dicomreader.pojo.MyDicom;
import com.dicomreader.utils.DcmReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class DcmReadServiceImp implements DcmReaderService{

    //    private String FILE_DIRECTORY = "src/main/resources/static/DICOM/image/temp/";
//    private String IMAGE_DIRECTORY = "/static/DICOM/image/temp/";
    private String IMAGE_NULL = "/static/DICOM/image/NULL.jpeg";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    @Value("${tempFilePath.directory}")
    private String TEMP_DIRECTORY;

    private DcmReader dicom = DcmReader.getInstance();

    /**
     * 对上传dicom的文件进行处理
     * @param mFile
     * @return
     */
    @Override
    public String initFile(MultipartFile mFile){
        String filePath = TEMP_DIRECTORY + "/Dicom/" + sdf.format(new Date());
        String fileName = mFile.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        fileName = System.currentTimeMillis() + suffixName;
        //创建dicom存储文件夹
        File folder = new File(filePath);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        File file = new File(folder,fileName);
        try {
            String imageName = file.getName();
            imageName = imageName.substring(imageName.lastIndexOf("/")+1);
            imageName = imageName.replaceAll(".dcm",".bmp");
            //将上传的文件存储到本地
            mFile.transferTo(file);

            BufferedImage image = dicom.openDcmFile(file).getDcmImage();

            ImageIO.write(image,"bmp",new File(filePath + imageName));

            return imageName;
        }catch (IOException e){
            e.printStackTrace();
        }

        return IMAGE_NULL;
    }

    /**
     * 获取dicom文件图片
     * @return
     */
    @Override
    public BufferedImage getImageFile(MultipartFile mFile) {
        try {
            MyDicom myDicom = dicom.openDcmFile(mFile.getOriginalFilename());
            if(myDicom.getDcmImage()!= null)
                return myDicom.getDcmImage();
            else
                return ImageIO.read(new File(IMAGE_NULL));

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
