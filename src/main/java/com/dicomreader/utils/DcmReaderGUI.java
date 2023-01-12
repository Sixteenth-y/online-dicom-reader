package com.dicomreader.utils;

import com.dicomreader.pojo.MyDicom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class DcmReaderGUI extends JFrame {
    private JButton button;
    private JLabel label;
    private  JFileChooser chooser;
    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 768;
    private static final String DEFAULT_PATH = "static/DICOM/image";
    public DcmReader reader = new DcmReader();
    public MyDicom myDicom = reader.getMyDicom();

    public static void main(String[] args) {
//        String[] path = {"src/main/resources/static/DICOM/82821227"
//            ,"src/main/resources/static/DICOM/image-002.dcm"
//            ,"src/main/resources/static/DICOM/image-001.dcm"};
//
        DcmReaderGUI gui = new DcmReaderGUI();
//        gui.reader.setDicom(new File(path[2]));
//        gui.reader.openDcmFile();
//        gui.creatGUI();
        int width = gui.reader.getMyDicom().getWidth();
        int height = gui.reader.getMyDicom().getHeight();
        System.out.println("width: "+ width + "\n"
                            +"height: " + height);

    }

    public DcmReaderGUI(){
        super();
        setTitle("DcmReader");
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        label = new JLabel();
        add(label);

        this.chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("文件");
        menuBar.add(menu);

        JMenuItem openItem = new JMenuItem("打开图片");
        menu.add(openItem);

        openItem.addActionListener(new OpenDcmListener());

        JMenuItem exitItem = new JMenuItem("退出");
        menu.add(exitItem);
        exitItem.addActionListener(new ExitListener());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        setVisible(true);

    }

    public void creatGUI(){
        JFrame frame = new JFrame();

        //button = new JButton("click me");
        //button.addActionListener(this);

        DrawPanel panel = new DrawPanel(myDicom.getDcmImage());
        /*关闭window时，停止程序*/
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        //frame.getContentPane().add(button);
        frame.setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        frame.setVisible(true);

    }

    class DrawPanel extends JPanel{

        Image img;
        public DrawPanel(Image img){
            this.img = img;
            repaint();
            int[] fh = {1,2,3};

        }

        public void paintComponent(Graphics graphics){
            super.paintComponent(graphics);
            graphics.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            //graphics.setColor(new Color(255,255,255));
            //graphics.fill3DRect(20,50,100,100,true);
        }
    }

    class OpenDcmListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){

            int result = chooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                String fileName = chooser.getSelectedFile().getPath();
                reader.setDicom(new File(fileName));
                reader.openDcmFile();
                reader.getDcmImage();
                label.setIcon(new ImageIcon(reader.imagePath));
                System.out.println(reader.imagePath);


            }
        }
    }

    class ExitListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent event){
            System.exit(0);
        }
    }


}
