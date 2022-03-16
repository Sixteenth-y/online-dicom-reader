package com.dicomreader.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author Johnson
 * @describ
 * @create 2021-10-14
 */
@Slf4j
@RestController
@RequestMapping(value = "/DcmReader")
public class DcmReaderController {


    SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-DD HH:mm:ss Z");

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView indexPage(){
        return new ModelAndView("dcmReader");
    }

    @RequestMapping(value = "upload")
    public void uploadFile(@RequestParam(value = "file",required = true)MultipartFile file)
        throws IOException {
        String wordFileName = file.getOriginalFilename();
        String fileName = file.getOriginalFilename();
    }
}
