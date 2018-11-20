import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.opencv.core.CvType.*;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_COUNT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_POS_MSEC;

/**
 * Created by better on 2014/10/4.
 */
public class JavaOpenCVTest extends JComponent{

    //static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    static{ System.load("D:\\opencv\\build\\java\\x64\\opencv_java341.dll"); }

    private  BufferedImage mImg;

    public static List<String>  list = new ArrayList<>(Arrays.asList("视频播放中，请点击按钮进行眼球跟踪"));
    public static List<String>  statusList = new ArrayList<>(Arrays.asList("视频播放中，请点击按钮进行眼球跟踪"));

    Point allpoint = new Point();
    int allRadius = 0;

    private static final long serialVersionUID = 1L;
    private BufferedImage image;
    public static String path = "";
    public static boolean print = true;

    public JavaOpenCVTest() {

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        if(image == null)  {
            g2d.setPaint(Color.BLACK);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        } else {
            g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
            System.out.println("show frame...");
        }
    }

    public void createWin(String title) {
        JDialog ui = new JDialog();
        ui.setTitle(title);
        ui.getContentPane().setLayout(new BorderLayout());
        ui.getContentPane().add(this, BorderLayout.CENTER);
        ui.setSize(new Dimension(330, 240));
        ui.setVisible(true);
    }

/*    public void createWin(String title, Dimension size) {
        JDialog ui = new JDialog();
        ui.setTitle(title);
        ui.getContentPane().setLayout(new BorderLayout());
        ui.getContentPane().add(this, BorderLayout.CENTER);
        ui.setSize(size);
        ui.setVisible(true);
    }*/

    /**主界面*/
    public void createWin(String title, Dimension size) {
        Mat image = Imgcodecs.imread("D:\\women.jpg");
        JFrame ui = new JFrame();
        showFrame(image, this, 0, 0);
        ui.setTitle(title);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.getContentPane().setLayout(new BorderLayout());
        ui.getContentPane().add(this, BorderLayout.CENTER);
        JPanel box = new JPanel();
        JButton jb = new JButton("选择文件");
        jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
                jfc.showDialog(new JLabel(), "选择");
                File file=jfc.getSelectedFile();
                if(file.isDirectory()){
                    System.out.println("文件夹:"+file.getAbsolutePath());
                }else if(file.isFile()){
                    System.out.println("文件:"+file.getAbsolutePath());
                }
                JavaOpenCVTest.path = file.getAbsolutePath();
                System.out.println(jfc.getSelectedFile().getName());
                ui.dispose();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        videoTest2(0, 0, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        box.add(jb);
        ui.getContentPane().add(box, BorderLayout.PAGE_END);
        ui.setSize(size);
        ui.setVisible(true);
    }

    /**展示界面*/
    public void createWin2(String title, Dimension size) {
        JFrame ui = new JFrame();
        ui.setTitle(title);
        ui.getContentPane().setLayout(new BorderLayout());
        ui.getContentPane().add(this, BorderLayout.CENTER);
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /**展示横纵坐标*/
        Box boxSide = Box.createVerticalBox();
        boxSide.setPreferredSize(new Dimension(size.width/2, size.height));
        JLabel tf1 = new JLabel("视频播放中，请点击相应算法显示数据");
        JLabel tf2 = new JLabel("视频播放中，请点击相应算法显示数据");
        JLabel tf3 = new JLabel("视频播放中，请点击相应算法显示数据");
        /*tf1.setEditable(false);
        tf2.setEditable(false);*/
        tf1.setFont(new Font("", Font.BOLD, 16));
        tf2.setFont(new Font("", Font.BOLD, 16));
        tf3.setFont(new Font("", Font.BOLD, 16));
        tf1.setPreferredSize(new Dimension(size.width/2, size.height/3));
        tf2.setPreferredSize(new Dimension(size.width/2, size.height/3));
        tf3.setPreferredSize(new Dimension(size.width/2, size.height/3));
        boxSide.add(tf1);
        boxSide.add(tf2);
        boxSide.add(tf3);
        Thread fextField = new Thread(new Runnable() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                while(JavaOpenCVTest.print){
                    if(list != null){
                        tf1.setText(list.get(list.size()-1));
                        //tf2.setText(list.get(list.size()-1));
                        tf2.setText(statusList.get(statusList.size()-1));
                        tf3.setText(new SimpleDateFormat("mm:ss:SS").format(System.currentTimeMillis()-current));
                    }
                }
            }
        });
        fextField.start();
        ui.getContentPane().add(boxSide, BorderLayout.EAST);
        /**创建展示窗口*/
        JPanel box = new JPanel();
        JButton jb1 = new JButton("  霍夫变换  ");
        jb1.setToolTipText("  霍夫变换  ");
        jb1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("霍夫变换"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(2, 0, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        JButton jb2 = new JButton("二值化分割");
        jb2.setToolTipText("二值化分割");
        jb2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("二值化"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(1, 0, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        JButton jb5 = new JButton("最大流分割");
        jb5.setToolTipText("最大流分割");
        jb5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("最大流分割"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(3, 0, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        JButton jb6 = new JButton("  聚类分析  ");
        jb6.setToolTipText("  聚类分析  ");
        jb6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("聚类分析"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(4, 0, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        JButton jb7 = new JButton("霍夫变换(实现)");
        jb7.setToolTipText("霍夫变换(实现)");
        jb7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("霍夫变换"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(2, 1, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        JButton jb8 = new JButton("二值化分割(实现)");
        jb8.setToolTipText("二值化分割(实现)");
        jb8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("二值化"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(1, 1, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        JButton jb9 = new JButton("聚类分析（实现）");
        jb9.setToolTipText("聚类分析（实现）");
        jb9.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.list = new ArrayList<>(Arrays.asList("聚类分析"));
                ui.dispose();
                JavaOpenCVTest.print = false;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        videoTest2(4, 1, JavaOpenCVTest.path);
                    }
                });
                t.start();
            }
        });
        Box boxCal = Box.createVerticalBox();
        boxCal.add(jb1);
        boxCal.add(Box.createVerticalStrut(10));
        boxCal.add(jb7);
        boxCal.add(Box.createVerticalStrut(10));
        boxCal.add(jb2);
        boxCal.add(Box.createVerticalStrut(10));
        boxCal.add(jb8);
        boxCal.add(Box.createVerticalStrut(10));
        boxCal.add(jb5);
        boxCal.add(Box.createVerticalStrut(10));
        boxCal.add(jb6);
        boxCal.add(Box.createVerticalStrut(10));
        boxCal.add(jb9);
        JButton jb3 = new JButton("返回菜单");
        jb3.setToolTipText("返回菜单");
        jb3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ui.dispose();
                JavaOpenCVTest.print = false;
                // 进行逻辑处理即可
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sleep();
                        JavaOpenCVTest.print = true;
                        new JavaOpenCVTest().createWin("测试",new Dimension(660, 480));
                    }
                });
                t.start();
            }
        });
        JButton jb4 = new JButton("信息展示");
        jb4.setToolTipText("信息展示");
        jb4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JavaOpenCVTest.print = false;
                for(int i=0; i<JavaOpenCVTest.list.size(); i++){
                    System.out.println(JavaOpenCVTest.list.get(i));
                }
                try{File f=new File(JavaOpenCVTest.path.substring(0,JavaOpenCVTest.path.length()-4)+".txt");
                    BufferedWriter bw=new BufferedWriter(new FileWriter(f));
                    for(int i=0;i<JavaOpenCVTest.list.size();i++){
                        bw.write(JavaOpenCVTest.list.get(i));
                        bw.newLine();
                    }
                    bw.close();
                } catch(Exception exc){

                }
            }
        });
        box.add(jb3);
        box.add(jb4);
        ui.getContentPane().add(box, BorderLayout.PAGE_END);
        ui.getContentPane().add(boxCal, BorderLayout.WEST);
        ui.setSize(new Dimension(size.width*3/2,size.height));
        ui.setVisible(true);
    }

    public void imshow(BufferedImage image) {
        this.image = image;
        this.repaint();
    }

    private BufferedImage mat2BI(Mat mat){
        int dataSize =mat.cols()*mat.rows()*(int)mat.elemSize();
        byte[] data=new byte[dataSize];
        mat.get(0, 0,data);
        int type=mat.channels()==1?
                BufferedImage.TYPE_BYTE_GRAY:BufferedImage.TYPE_3BYTE_BGR;

        if(type==BufferedImage.TYPE_3BYTE_BGR){
            for(int i=0;i<dataSize;i+=3){
                byte blue=data[i+0];
                data[i+0]=data[i+2];
                data[i+2]=blue;
            }
        }
        BufferedImage image=new BufferedImage(mat.cols(),mat.rows(),type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }

    public void sleep(){
        try
        {
            Thread.currentThread().sleep(100);//毫秒
        }
        catch(Exception e){}
    }

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());

        Mat image = Imgcodecs.imread("D:\\face22.jpg");

/*        new com.how2java.springboot.JavaOpenCVTest().run();
        new com.how2java.springboot.JavaOpenCVTest().recognizeEye();*/
        //new com.how2java.springboot.JavaOpenCVTest().canny(image);
        //new com.how2java.springboot.JavaOpenCVTest().doBackgroundRemoval(image);
        //new com.how2java.springboot.JavaOpenCVTest().grabCut(image);
        //new com.how2java.springboot.JavaOpenCVTest().kMeans(image);
        //new com.how2java.springboot.JavaOpenCVTest().hough(image);
        //new com.how2java.springboot.JavaOpenCVTest().hough1(image);
        //new JavaOpenCVTest().videoTest2(2);
        new JavaOpenCVTest().createWin("测试",new Dimension(660, 480));
    }
    /**
     * 反色
     */
    public void run(){
        Mat image = Imgcodecs.imread("D:\\face22.jpg");
        int width = image.cols();
        int height = image.rows();
        int dims = image.channels();
        byte[] data = new byte[width*height*dims];
        image.get(0, 0, data);

        int index = 0;
        int r=0, g=0, b=0;
        for(int row=0; row<height; row++) {
            for(int col=0; col<width*dims; col+=dims) {
                index = row*width*dims + col;
                b = data[index]&0xff;
                g = data[index+1]&0xff;
                r = data[index+2]&0xff;

                r = 255 - r;
                g = 255 - g;
                b = 255 - b;

                data[index] = (byte)b;
                data[index+1] = (byte)g;
                data[index+2] = (byte)r;
            }
        }

        image.put(0, 0, data);
        Imgcodecs.imwrite("D:\\face3.jpg", image);
    }
    /**
     * 人眼识别
     */
    public void recognizeEye(){
        Mat image = Imgcodecs.imread("D:\\face22.jpg");
        CascadeClassifier objDetector=new CascadeClassifier("D:\\haarcascade_eye_tree_eyeglasses.xml");
        MatOfRect objDetections=new MatOfRect();
        objDetector.detectMultiScale(image, objDetections);
        if(objDetections.toArray().length<=0){
            return ;
        }
        for(Rect rect:objDetections.toArray()){
            Imgproc.rectangle(image, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0,0,255),2);
        }
        Imgcodecs.imwrite("D:\\face4.jpg", image);
    }
    /**
     * 边缘检测
     */
    public Mat canny(Mat image){
        // Mat image = Imgcodecs.imread("D:\\face22.jpg");
        Mat dst = image.clone();
        Mat edge = dst.clone();
        Imgproc.blur(image, dst,new Size(8,8));
        //Imgcodecs.imwrite("D:\\face8.jpg", dst);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2GRAY);
        //Imgproc.pyrMeanShiftFiltering(dst, dst, 50, 50);\
        Imgproc.Canny(dst, edge, 50, 55, 3, true);
        //Imgcodecs.imwrite("D:\\face5.jpg", edge);
        return edge;
    }
    /**
     * hough变换+二值法变换实现内圆捕获
     */
    public Mat hough(Mat result, int aim){
        //Mat image = Imgcodecs.imread("D:\\face5.jpg");//canny后图的链接
        //Mat result = Imgcodecs.imread("D:\\face22.jpg");//原图的链接
        //Mat image = canny(result);
        Mat resultgray = new Mat();
        double maxratio = 0;
        double ratio = 0;
        Mat threshdst = result.clone();
        Point lastCenter = new Point();
        int lastRadius = 0;
//        Imgproc.cvtColor(image, dst, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(result, resultgray, Imgproc.COLOR_BGR2GRAY);
        Mat circles = new Mat();//霍夫变换的圆集合
        Imgproc.threshold(result, threshdst, 50, 255, THRESH_BINARY);
        Mat test = canny(threshdst);
        if(aim == 1) return test;
        Imgproc.HoughCircles(test, circles, Imgproc.HOUGH_GRADIENT, 1, 10, 200, 30, 20, 80);
        for (int i = 0; i < circles.cols(); i++)
        {
            double[] vCircle = circles.get(0, i);

            Point center = new Point(vCircle[0], vCircle[1]);
            int radius = (int) Math.round(vCircle[2]);

            Rect rect = new Rect((int) center.x-radius, (int) center.y-radius, 2*radius,2*radius);
            try{
                Mat checkCircle =new Mat(threshdst, rect);
                //Imgcodecs.imwrite("D:\\fa"+i+".jpg", checkCircle);
                ratio = checkCircle(checkCircle);
            }catch (Exception e){
                System.out.println("失败一次");
            }

            //System.out.println(ratio);
            if(maxratio < ratio) {
                lastCenter = center;
                lastRadius = radius;
                maxratio = ratio;
                if(maxratio > 0.9) break;
            }

        }
        if(maxratio == 0){
            Imgproc.circle(result, new Point(100,100), 10, new Scalar(0, 0, 255), -1, 8, 0);
            JavaOpenCVTest.statusList.add("闭眼");
        }else{
            // circle center
            Imgproc.circle(result, lastCenter, 5, new Scalar(0, 255, 0), -1, 8, 0);
            // circle outline
            Imgproc.circle(result, lastCenter, lastRadius, new Scalar(0, 0, 255), 1, 8, 0);
            JavaOpenCVTest.statusList.add("睁眼");
/*      allpoint = lastCenter;
        allRadius = lastRadius;*/
        }
        JavaOpenCVTest.list.add("霍夫圆变换圆心x:"+lastCenter.x+"圆心y:"+lastCenter.y+"半径:"+lastRadius);
        System.out.println("圆心x:"+lastCenter.x+"圆心y:"+lastCenter.y+"半径:"+lastRadius);
        //Imgcodecs.imwrite("D:\\face6.jpg", result);
        //Imgcodecs.imwrite("D:\\face7.jpg", threshdst);
        return result;

    }
    /**
     * 检测hough圆中黑色点的比例
     */
    public double checkCircle(Mat mat){
        Point center = new Point();
        int width = mat.width();
        int height = mat.height();
        int r = width/2;
        double[] value = {-1};
        int count = 0;
        double ratio = 0;
        center.x = Math.rint(width/2);
        center.y = Math.rint(height/2);
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++){/*
                System.out.println(Math.pow(center.y,2));
                System.out.println(Math.pow(center.x,2));
                System.out.println(Math.sqrt((Math.pow(center.x,2)+Math.pow(center.y,2))));*/
                if ((Math.sqrt((Math.pow(center.x-j,2)+Math.pow(center.y-i,2))))<r){
                    value = mat.get(i, j);
                    if(value[0]==0 && value[1]==0 && value[2]==0 ){
                        count++;
                    }
                }
            }
        }
        ratio = (double)count/(3.14*r*r);
        if (ratio < 0.7)  ratio =0;
        return  ratio;
    }
    /**
     * 视频流的读取
     */
    public void run2(){
        VideoCapture cap = new VideoCapture();
        cap.open("D:\\face.avi");
        System.out.println(cap.isOpened());
        Mat frame = new Mat();
        double frameCount = cap.get(CV_CAP_PROP_FRAME_COUNT);
        double fps = cap.get(CV_CAP_PROP_FPS);
        double len = frameCount / fps;
        Double d_s = new Double(len);
        for (int i=0; i<d_s.intValue(); i++ ){
            cap.set(CV_CAP_PROP_POS_MSEC, i*1000);
            if(cap.read(frame)){
                Imgcodecs.imwrite("D:\\fps\\"+i+".jpg", frame);
            }
        }
    }

    /**
     * 视频流的画图处理2
     */
    public void videoTest2(int temp, int aim){
        System.load("D:\\opencv\\build\\java\\x64\\opencv_java341.dll");

        // 打开摄像头或者视频文件
        VideoCapture capture = new VideoCapture();
        //capture.open(0);
        capture.open("D:\\face.avi");
        if(!capture.isOpened()) {
            System.out.println("could not load video data...");
            return;
        }
        int frame_width = (int)capture.get(3);
        int frame_height = (int)capture.get(4);
        JavaOpenCVTest gui = new JavaOpenCVTest();
        gui.createWin2("算法界面", new Dimension(frame_width, frame_height));
        Mat frame = new Mat();
        boolean have = false;
        while(have = capture.read(frame) && (temp ==1 || temp==2 || temp ==3 || temp == 4) ) {
            showFrame(frame, gui, temp, aim);
        }
        /*        while(true) {
            boolean have = capture.read(frame);
//            boolean have1 = capture.read(frame);
//            if(!have1) break;
            //Core.flip(frame, frame, 1);// Win上摄像头
            if(!have) break;
            if(!frame.empty()) {

                gui.imshow(mat2BI(hough1(frame)));
                gui.repaint();
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    /**
     * 视频流的画图处理2
     */
    public void videoTest2(int temp, int aim, String path){
        System.load("D:\\opencv\\build\\java\\x64\\opencv_java341.dll");

        // 打开摄像头或者视频文件
        VideoCapture capture = new VideoCapture();
        //capture.open(0);
        capture.open(path);
        if(!capture.isOpened()) {
            System.out.println("could not load video data...");
            return;
        }
        int frame_width = (int)capture.get(3);
        int frame_height = (int)capture.get(4);
        JavaOpenCVTest gui = new JavaOpenCVTest();
        gui.createWin2("测试", new Dimension(frame_width, frame_height));
        Mat frame = new Mat();
        boolean have = false;
        while(have = capture.read(frame) && (temp == 0 || temp ==1 || temp==2 || temp ==3 || temp == 4) && JavaOpenCVTest.print) {
            showFrame(frame, gui, temp, aim);
        }
    }

    public void showFrame(Mat frame, JavaOpenCVTest gui, int i, int aim){
        if(!frame.empty()) {
            if(i == 0 ){
                gui.imshow(mat2BI(frame));
                gui.repaint();
            }
            if(i == 1 ){
                gui.imshow(mat2BI(thresh(frame, aim)));
                gui.repaint();
            } else if (i == 2){
                gui.imshow(mat2BI(hough(frame, aim)));
                gui.repaint();
            }else if (i == 3){
                gui.imshow(mat2BI(grabCut(frame)));
                gui.repaint();
            }else if (i == 4){
                gui.imshow(mat2BI(kMeans(frame, aim)));
                gui.repaint();
            }
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * hough变换+二值法变换实现内圆捕获
     */
    public Mat thresh(Mat result , int aim){
        Mat threshdst = result.clone();
        cvtColor(threshdst,threshdst,COLOR_RGB2GRAY);
        Imgproc.threshold(threshdst, threshdst, 35, 255, THRESH_BINARY);//二值化原图=threshdst
        //Imgcodecs.imwrite("D:\\face9.jpg", threshdst);

        if (aim ==1) return threshdst;
        Point point = check(threshdst);
        //Point point = checkContours(result);
        // circle center
        if(point.x ==0 ) {
            Imgproc.circle(result, new Point(100,100), 10, new Scalar(0, 0, 255), -1, 8, 0);
            JavaOpenCVTest.statusList.add("闭眼");
        }else{
            Imgproc.circle(result, point, 5, new Scalar(0, 255, 0), -1, 8, 0);
            JavaOpenCVTest.statusList.add("睁眼");
        }
        JavaOpenCVTest.list.add("二值化圆心x:"+point.x+"圆心y:"+point.y);
        System.out.println("圆心x:"+point.x+"圆心y:"+point.y);
        //Imgcodecs.imwrite("D:\\face6.jpg", result);
        return result;

    }
    /**
     * 检测瞳孔算法
     */
    public Point check(Mat image){
        int width = image.width()/2;
        int height = image.height()/2;
        double[] value = {-1};
        int x = 0;
        int y = 0;
/*        drawPoint(width-50,height);
        int k = width -50;
        value = checkCircle.get(k , height/2);
        System.out.println(value[0]+","+value[1]+","+value[2]+","+(width-30));*/
        x = checkCows(image , height , width);
        if(x==-1) return new Point(0,0);
        if(x==0) x = checkCows(image , 5*height/4, width);
        if(x==0) x = checkCows(image , 3*height/4, width);
        if(x==0) x = checkCows(image , 4*height/3, width);
        if(x==0) x = checkCows(image , 2*height/3, width);
        if(x==0) x = checkCows(image , 3*height/2, width);
        if(x==0) x = checkCows(image , 1*height/2, width);
        if(x==0) x = checkCows(image , 5*height/3, width);
        y = checkCols(image , height , x);
        //System.out.println("X："+x);
        if(x > 8*width/3 || y< height/2 || x == 822 ) {
            Point centerPoint = new Point(0,0);
            return centerPoint;
        }
        Point centerPoint = new Point(x/2,y/2);
        return centerPoint;
    }
    /**
     * 检测瞳孔算法
     */
    public Point check2(Mat image){
        int width = image.width()/2;
        int height = image.height()/2;
        double[] value = {-1};
        int x = 0;
        int y = 0;
        x = checkCows(image , 5*height/4, width);
        if(x==0 || x>width*3/2 || x < width/3) x = checkCows(image , height , width);
        y = checkCols(image , height , x);
        if(x > 8*width/3 || y< height/2 || x == 822 ) {
            Point centerPoint = new Point(0,0);
            return centerPoint;
        }
        Point centerPoint = new Point(x/2,y/2);
        return centerPoint;
    }
    /**
     * 画点
     */
    public void drawPoint(int x, int y){
        Point point = new Point(x,y);
        Mat image = Imgcodecs.imread("D:\\face9.jpg");
        Imgproc.circle(image, point, 5, new Scalar(0, 255, 0), -1, 8, 0);
        Imgcodecs.imwrite("D:\\face9.jpg", image);
    }
    /**
     * 横向检测
     */
    public int checkCows(Mat image,int height,int width){
        double[] value = {-1};
        double[] value2 = {-1};
        int x=0;
        //第一个黑点
        for(int i = width/2 ;i<3*width/2; i++){
            try{
                value = image.get(height,i);
                if(value[0]<30 ){
                    value2 = image.get(height, i+15);
                    if(value2[0]<30){
                        //drawPoint(i,height);
                        x=x+i;
                        //System.out.println("圆心x:"+x);
                        break;
                    }
                }
            }catch (Exception e){
            }
        }
        if(Math.abs(2*x-width)<3) return -1;
        if(x==0) return x;
        //黑点后第一个白点
        for(int i = x+10 ;i<3*width/2; i++){
            try{
                value = image.get(height,i);
                if(value[0]>250 ){
                    value2 = image.get(height, i+15);
                    if(value2[0]>250){
                        //drawPoint(i,height);
                        x=x+i;
                        //System.out.println("圆心x:"+x);
                        break;
                    }
                }
            }catch (Exception e){
            }
        }
        return x;
    }
    /**
     * 纵向检测
     */
    public int checkCols(Mat image, int height, int width){
        double[] value = {-1};
        double[] value2 = {-1};
        int y=0;
        for(int i = 3*height/2 ; i>height/2 ; i--){
            try{
                value = image.get(i,width/2);
                //System.out.println(value[0]+","+value[1]+","+value[2]+","+i);
                if (value[0]<35 ){
                    value2 = image.get(i-15, width/2);
                    if(value2[0]<40 ){
                        //drawPoint(x/2,i);
                        y=y+i;
                        //System.out.println("圆心y:"+y);
                        break;
                    }
                }
            }catch (Exception e){
            }
        }
        for(int i = y-15 ; i>height/2 ; i--){
            try{
                value=image.get(i,width/2);
                if (value[0]>35){
                    value2 = image.get(i-15, width/2);
                    if(value2[0]>35){
                        //drawPoint(x/2,i);
                        y=y+i;
                        //System.out.println("圆心y:"+y);
                        break;
                    }
                }
            }catch (Exception e){
            }
        }
        return y;
    }
    /**
     * findcontours
     */
    public Point checkContours(Mat image){
        Point point = new Point();
        Mat hierarchy = new Mat();
        Mat drawimage = new Mat();
        drawimage.create(image.size(), CV_8UC1);
        int sum = 0;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        cvtColor(image,image,COLOR_RGB2GRAY);

        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        //Imgproc.drawContours(drawimage, contours, -1, new Scalar(0,255,0));
        for (int i = 0; i < contours.size(); i++)
        {
            Imgproc.drawContours(drawimage, contours, i, new Scalar(0,255,0),2, 8, hierarchy, 0, new Point());
            /*MatOfPoint vvCircle = contours.get(i);

            double[] aCircle = vvCircle.get(0,0);
            double[] bCircle = vvCircle.get(vvCircle.rows()/2, vvCircle.cols()/2);
            System.out.println(aCircle[0]+"和"+aCircle[1]);
            System.out.println(bCircle[0]+"和"+bCircle[1]+"和"+vvCircle.rows()+"个数据"+i);
            Point C = new Point(aCircle[0]/2+bCircle[0]/2, aCircle[1]/2+bCircle[1]/2);
            if(sum<vvCircle.rows()){
                sum = vvCircle.rows();
                point = C;
            }*/
        }
        Imgcodecs.imwrite("D:\\face10.jpg", drawimage);
        return point;
    }

    /**秒表*/
    class Clockxian extends Thread{
        public void run(){
            while(true){
                System.out.println(new Date());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void doBackgroundRemoval(Mat frame)
    {
        Rect rect = new Rect(frame.cols()/5, frame.rows()/5, frame.cols()*3/5, frame.rows()*3/5);
        Core.bitwise_not(frame, frame);
        frame = new Mat(frame, rect);
        // init
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();
        int thresh_type = Imgproc.THRESH_BINARY_INV;
// threshold the image with the average hue value
        hsvImg.create(frame.size(), CV_8U);
        Imgproc.cvtColor(frame, hsvImg,
                Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        Scalar average=Core.mean(hsvPlanes.get(0));
        double threshValue =average.val[0];
        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);
        Imgproc.blur(thresholdImg, thresholdImg, new Size(5, 5));
        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);
        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);
        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));        thresholdImg.convertTo(thresholdImg, CV_8U);
        frame.copyTo(foreground, thresholdImg);//掩膜图像复制
        Imgcodecs.imwrite("D:\\face15.jpg", foreground);
    }

    private Mat grabCut(Mat frame){
        Mat bgModel= new Mat();
        Mat fgModel = new Mat();
        Mat mask = new Mat();
        Mat threshdst = new Mat();
        Mat source = new Mat(1, 1, CvType.CV_8UC1, new Scalar(GC_PR_FGD));
        Rect rect = new Rect(frame.cols()/3, frame.rows()/4, frame.cols()*1/3, frame.rows()*1/2);
        Core.bitwise_not(frame, frame);
        frame = new Mat(frame, rect);
        //Imgcodecs.imwrite("D:\\face12.jpg", frame);
        rect = new Rect(frame.cols()/4, frame.rows()/10, frame.cols()*1/2, frame.rows()*4/5);
        mask.create(frame.size(), CV_8UC1);
        mask.setTo(new Scalar(GC_BGD));
        Imgproc.threshold(frame, threshdst, 50, 255, THRESH_BINARY);
        Imgproc.grabCut(frame, mask, rect, bgModel, fgModel, 3, GC_INIT_WITH_RECT);
        //Imgcodecs.imwrite("D:\\face13.jpg", mask);
        Core.compare(mask, source, mask, Core.CMP_EQ);
        //Imgcodecs.imwrite("D:\\face14.jpg", mask);
        // 产生输出图像
        System.out.println("准备产生输出图像");
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
        frame.copyTo(foreground, mask);
        //Imgcodecs.imwrite("D:\\face15.jpg", foreground);
        System.out.println("准备写入结果");
        System.out.println(mask.size());
        Core.bitwise_not(foreground, foreground);
        //Imgcodecs.imwrite("D:\\face16.jpg", foreground);
        Point point = check2(foreground);
        Core.bitwise_not(frame, frame);
        if(point.x ==0 ) {
            Imgproc.circle(foreground, new Point(100,100), 10, new Scalar(0, 0, 255), -1, 8, 0);
            JavaOpenCVTest.statusList.add("闭眼");
        }else{
            Imgproc.circle(foreground, point, 5, new Scalar(0, 255, 0), -1, 8, 0);
            JavaOpenCVTest.statusList.add("睁眼");
        }
        JavaOpenCVTest.list.add("最大流圆心x:"+point.x+"圆心y:"+point.y);
        System.out.println("圆心x:"+point.x+"圆心y:"+point.y);
        //Imgcodecs.imwrite("D:\\face17.jpg", frame);
        return foreground;
    }

    private Mat kMeans(Mat frame, int aim){

        Mat image = frame.clone();
        //Imgproc.threshold(image, image, 30, 255, THRESH_BINARY);
        //Core.bitwise_not(image, image);

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
        //Imgcodecs.imwrite("D:\\face17.jpg", image);
        Mat data = new Mat();
        image.convertTo(image, CV_32F);
        //data.create(image.cols()*image.rows(), 1,1);
        for (int i=0; i < image.rows(); i++)
            for(int j = 0; j< image.cols(); j++) {
                Mat tmp = new Mat();
                tmp.create(1,1,1);
                tmp.convertTo(tmp, CV_32F);
                tmp.put(0, 0, image.get(i, j));
                //System.out.println(image.get(i, j));
                //System.out.println(tmp.get(0,0));
                data.push_back(tmp);
                //System.out.println(data.get(i*image.cols()+j ,0));
            }
        data.convertTo(data, CV_32F);
        //System.out.println(data.rows());
        //System.out.println(data.cols());
        Mat labels = new Mat();
        int attempts = 3;
        Mat centers = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,100,0.1);
        int clusterCount = 3;
        Core.kmeans(data, clusterCount, labels, criteria, attempts, Core.KMEANS_PP_CENTERS);
        //System.out.println(labels.rows());
        //System.out.println(labels.cols());

        labels.convertTo(labels, CV_32F);
        Mat result = new Mat();
        result.create(image.size(), CV_8U);
        int n = 0;
        double[] black = {0,0,0};
        double[] green = {65, 0,0};
        double[] white = {255, 255, 255};
        if(aim == 1){
            for (int i=0; i < image.rows(); i++)
                for(int j = 0; j< image.cols(); j++) {
                    double[] color = labels.get(n,0);
                    int a = (int)color[0];
                    if(a==1){
                        result.put(i, j, white);
                    } else if(a==0){
                        result.put(i, j, black);
                    } else{
                        result.put(i, j, green);
                    }
                    //System.out.println(a);
                    n++;
                }
            return result;
        }
        Mat dstResult = colorChange(frame, labels, result);
        Point point = check(dstResult);
        //Imgcodecs.imwrite("D:\\face19.jpg", result);
        //System.out.println(point);

        if(point.x ==0 ) {
            Imgproc.circle(frame, new Point(100,100), 10, new Scalar(255, 0, 255), -1, 8, 0);
            JavaOpenCVTest.statusList.add("闭眼");
        }else{
            point.y = point.y+15;
            Imgproc.circle(frame, point, 5, new Scalar(255, 255, 0), -1, 8, 0);
            JavaOpenCVTest.statusList.add("睁眼");
        }
        JavaOpenCVTest.list.add("聚类分析圆心x:"+point.x+"圆心y:"+point.y);
        System.out.println("圆心x:"+point.x+"圆心y:"+point.y);
        //Imgcodecs.imwrite("D:\\face18.jpg", frame);
        return frame;
    }

    public Mat colorChange(Mat image , Mat labels, Mat result){
        int n = 0;
        double[] black = {0,0,0};
        double[] green = {65, 0,0};
        double[] white = {255, 255, 255};
        int row = image.rows();
        int col = image.cols();
        int mid = row*col/2;
        double[] dst = labels.get(mid, 0);
        int dstColor = (int)dst[0];
        for (int i=0; i < image.rows(); i++)
            for(int j = 0; j< image.cols(); j++) {
                double[] color = labels.get(n,0);
                int ab = (int)color[0];
                if(ab==dstColor){
                    result.put(i, j, black);
                } else{
                    result.put(i, j, white);
                }
                //System.out.println(a);
                n++;
            }
        return result;
    }
}

