package com.dicomreader.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

@Service
public interface DcmReaderService {
    public String initFile(MultipartFile file);
    public BufferedImage getImageFile();
}
