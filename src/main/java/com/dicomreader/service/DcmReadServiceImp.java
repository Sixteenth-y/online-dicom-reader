package com.dicomreader.service;

import com.dicomreader.utils.DcmReader;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//@Slf4j
@Service
public class DcmReadServiceImp implements DcmReaderService{

    private static DcmReader dicom = new DcmReader();
    @Override
    public void initFile(MultipartFile mFile){

        File file = null;
        try {
            String filePath = mFile.getOriginalFilename();
            String[] fileName = filePath.split("\\.");
            FileInputStream fileIS = new FileInputStream(filePath);
            FileOutputStream fileOS = new FileOutputStream("");
            byte[] buffer = new byte[fileIS.available()];
            fileIS.read(buffer);
            fileIS.close();
            fileOS.write(buffer);
            fileOS.flush();
            fileOS.close();
            dicom.setDicom(file);
        }catch (IOException e){
            e.printStackTrace();
        }


    }
}
