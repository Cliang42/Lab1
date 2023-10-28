
import java.io.IOException;
import java.util.*;

public class Skyline_Test {

    /**
     * 对不同skYLine_test的整合
     * @param readFileName 读取文件名
     * @param funcName “Func”，“norm2”
     * @param skylineType “BB","BNL"
     * @param numPoints 显示top n个，目前不用
     * @param loop 是否是所有data都loop一遍，是的话关闭部分输出语句
     * @param loop2 1.是否把timeUsed存入，以不同data为一个file，用append方法 & 2.是否是同一个data多次loop，不是就每次都写入result point.txt
     * @throws IOException
     */
    //@Test(timeout=1000)
    public static void Skyline_test(String readFileName,String funcName,String skylineType,int numPoints,boolean loop,boolean loop2) throws IOException {
        long start = -1; //计时
        long elapsedTime=-1;
        ArrayList<CusPoint> inputPoints;
        ArrayList<CusPoint> skylineResult = null;
        inputPoints = ReadPoints.readPoints(readFileName); //read
        System.out.println("=========start a new session========");
        //=====================skyline=====================
        if (skylineType=="BNL") {
            start = System.nanoTime(); //计时
            skylineResult = Skyline.BNL_Skyline(inputPoints,loop); // 普通BNL skyline result
            elapsedTime = System.nanoTime() - start;
        }
        else if (skylineType=="BB"){
            //存入Rtree
            RTree rtree = Build_Index.RTree_Index(inputPoints,5,10,loop);//2,5; 5,10 best performance
            start = System.nanoTime(); //计时,不算index时长
            //skyline读取（带function）
            skylineResult=Skyline.BB_Skyline(rtree,funcName,loop);
            elapsedTime = System.nanoTime() - start;
        }

        //=========================show result======================
        System.out.println("==============result==============");

        System.out.println("fileName: "+readFileName);
        System.out.println("Skyline Method: "+ skylineType);
        System.out.println("result_length: " + skylineResult.size());
        System.out.println("time_used: " + elapsedTime + " nano seconds");


    }

    public static void main(String[] args) throws IOException {

        Skyline_test("D:\\lab\\severe lab\\skyline_BB_and_BNL_method\\data\\QWS Date\\qws_normal.txt","Func1","BNL",1000,false,false);
    }
}
