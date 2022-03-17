package com.dicomreader;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.io.DicomInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@SpringBootTest
class DicomReaderApplicationTests {

    @Test
    public void contextLoads() {
        File dcmFile = new File("src/main/resources/static/DICOM/image-001.dcm");
            Raster raster = null ;
            System.out.println("Input: " + dcmFile.getName());

            //Open the DICOM file and get its pixel data
            try {
                Iterator iter = ImageIO.getImageReadersByFormatName("DICOM");
                ImageReader reader = (ImageReader) iter.next();
                DicomImageReadParam param = (DicomImageReadParam) reader.getDefaultReadParam();
                ImageInputStream iis = ImageIO.createImageInputStream(dcmFile);
                reader.setInput(iis, false);
                //Returns a new Raster (rectangular array of pixels) containing the raw pixel data from the image stream
                raster = reader.readRaster(0, param);
                if (raster == null)
                    System.out.println("Error: couldn't read Dicom image!");
                iis.close();
            }
            catch(Exception e) {
                System.out.println("Error: couldn't read dicom image! "+ e.getMessage());
                e.printStackTrace();
            }
            BufferedImage bufferedImage =  get16bitBuffImage(raster);


    }
    public BufferedImage get16bitBuffImage(Raster raster) {
        short[] pixels = ((DataBufferUShort) raster.getDataBuffer()).getData();
        ColorModel colorModel = new ComponentColorModel(
                ColorSpace.getInstance(ColorSpace.CS_GRAY),
                new int[]{16},
                false,
                false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_USHORT);
        DataBufferUShort db = new DataBufferUShort(pixels, pixels.length);
        WritableRaster outRaster = Raster.createInterleavedRaster(
                db,
                raster.getWidth(),
                raster.getHeight(),
                raster.getWidth(),
                1,
                new int[1],
                null);
        return new BufferedImage(colorModel, outRaster, false, null);
    }


    @Test
    public void getImageTest(){
        File dcmFile = new File("src/main/resources/static/DICOM/image-001.dcm");
        try {
            ImageInputStream imageIS = ImageIO.createImageInputStream(dcmFile);
            BufferedImage image = ImageIO.read(imageIS);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
