package com.dicomreader.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface DcmReaderService {
    public void initFile(MultipartFile file);
}
