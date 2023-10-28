import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
*read points from file
*/

public class ReadPoints {
    /**
     *
     * @param fileName 读取文件名
     * @return res ArrayList<CusPoint>
     * @throws FileNotFoundException
     */
    public static ArrayList<CusPoint> readPoints(String fileName) throws FileNotFoundException {
        ArrayList<CusPoint> res = new ArrayList<CusPoint>();
        CusPoint point;
        float[] pointDims;
        int numDims;
        String[] s;
        File file = new File(fileName);

        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            s= sc.nextLine().toString().split(" ");
            numDims=s.length;
            pointDims = new float[numDims];
            for (int i=0;i<numDims;i++) {
                pointDims[i]=Float.parseFloat(s[i]);
            }
            point=new CusPoint(pointDims,numDims);
            res.add(point);
//            System.out.println(sc.nextLine());
            //res.add(point);
        }
        return res;
    }


}
