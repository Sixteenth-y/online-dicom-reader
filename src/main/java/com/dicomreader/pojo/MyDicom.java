package com.dicomreader.pojo;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class MyDicom implements Serializable {
    private String seriesInstanceUID;
    private String studyInstanceUID;
    private String patientID;
    private String patientName;
    private String SOPInstanceUID;
    private String imageType;
    private int height;
    private int width;
    private BufferedImage dcmImage;

    public MyDicom() {
    }

    public MyDicom(String seriesInstanceUID,
                   String studyInstanceUID,
                   String patientID,
                   String patientName,
                   String SOPInstanceUID,
                   String imageType,
                   int height,
                   int width,
                   BufferedImage dcmImage) {
        this.seriesInstanceUID = seriesInstanceUID;
        this.studyInstanceUID = studyInstanceUID;
        this.patientID = patientID;
        this.patientName = patientName;
        this.SOPInstanceUID = SOPInstanceUID;
        this.imageType = imageType;
        this.height = height;
        this.width = width;
        this.dcmImage = dcmImage;
    }

    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    public void setSeriesInstanceUID(String seriesInstanceUID) {
        this.seriesInstanceUID = seriesInstanceUID;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public void setStudyInstanceUID(String studyInstanceUID) {
        this.studyInstanceUID = studyInstanceUID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getSOPInstanceUID() {
        return SOPInstanceUID;
    }

    public void setSOPInstanceUID(String SOPInstanceUID) {
        this.SOPInstanceUID = SOPInstanceUID;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public BufferedImage getDcmImage() {
        return dcmImage;
    }

    public void setDcmImage(BufferedImage dcmImage) {
        this.dcmImage = dcmImage;
    }

    @Override
    public String toString() {
        return "Dicom{" +
                "seriesInstanceUID='" + seriesInstanceUID + '\'' +
                "\n studyInstanceUID='" + studyInstanceUID + '\'' +
                "\n patientID='" + patientID + '\'' +
                "\n patientName='" + patientName + '\'' +
                "\n SOPInstanceUID='" + SOPInstanceUID + '\'' +
                "\n imageType='" + imageType + '\'' +
                "\n height=" + height +
                "\n width=" + width +
                "\n dcmImage=" + dcmImage +
                '}';
    }


}
