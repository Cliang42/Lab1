import java.util.ArrayList;
import java.util.TreeMap;

/**
 * return a list of skyline points
 *
 */
public class Skyline {
    /**
     * BNL method
     *         1)	窗口中存在一点q，q点支配p点，则p点不可能是SP成员，将p点丢弃。
     *         2)	p点支配窗口中的一个或多个点，则被p点支配的所有的点不可能是SP成员，将它们删去。
     *         3)	p点与窗口内所有的点都不相互支配，若窗口的容量仍能够存放p点则插入p点，否则将p点插入临时文件T中.
     *
     * @param inputPoints ArrayList<CusPoint>
     * @return res ArrayList<CusPoint>
     * @param loop 控制是否显示运行进程,true=关闭
     */

    public static ArrayList<CusPoint> BNL_Skyline(ArrayList<CusPoint> inputPoints,boolean loop) {
        double inf = Double.POSITIVE_INFINITY;
        int memorySize= (int) inf;
        int totalPointNum=inputPoints.size();
        ArrayList<CusPoint> disk=inputPoints;
        ArrayList<CusPoint> inMemory=new ArrayList<CusPoint>();
        ArrayList<CusPoint> res = new ArrayList<CusPoint>();
        System.out.println("BNL processing...");
        //遍历取skyline直到取到足够数量或disk取/排除完
        while (!disk.isEmpty()){
            //遍历一次disk，去除点，选出(inMemory size)个点，多余可能符合条件的放回disk
            int diskSize=disk.size();
            for (int i=0;i<diskSize;i++){
                //显示还剩多少
                if(!loop) {
                int processed = totalPointNum-disk.size();
                int n = totalPointNum/10;
                if(processed%n ==0){System.out.println( "BNL: "+ processed +"is processed..."+processed/n*10+"%");}
                }
                CusPoint p=disk.remove(0);
                int flag=1;
                //遍历inMemory确定选出点符不符合条件
                for (int j = 0; j <inMemory.size(); j++) {
                    //inMemory 存在 dominant point，舍弃选出的disk point
                    if (inMemory.get(j).dominant(p)){flag=0; break;}
                    //inMemory 被dominant,就去除Inmemory 的point
                    if (p.dominant(inMemory.get(j))){inMemory.remove(j); j--;} // !!j-- 因为遍历的list长度改变，当然也可以反向遍历就不用j--
                    }
                //符合条件入memory/disk,位置不够就返回disk
                //入memory的必为skyline，返回的因为没有后续比较，从下一个遍历开始就不一定是。
                if (flag==1 & memorySize>inMemory.size()){
                    inMemory.add(p);
                }
                else if(flag==1 & memorySize<=inMemory.size()) {
                    disk.add(p);
                }
            }
            //清空inMemory读入res
            res.addAll(inMemory);
            inMemory.clear();
        }
        return res;
    }

    /**
     * BB Method
     *          Input: A Dataset D (r-tree).
     *          Output: The Set of skyline points of dataset D.
     *          1. S=∅ // list of skyline points
     *          2. insert all entries of the root D in the heap
     *          3. while heap not empty do
     *          4. remove top entry e
     *              5. if e is dominated by some point in S do discard e
     *              6. else // e is not dominated
     *                  7. if e is an intermediate entry then
     *                      8. for each child ei of e do
     *                          9. if ei is not dominated by some point in S then
     *                              10. insert ei into heap
     *                  11. else // e is a data point
     *                      12. insert ei into S
     *          13. end while
     *          !!!不需要set inMemory，because normally # retreat points << inMemory
     * @param rtree
     * @param funcName
     * @param loop 控制是否显示运行进程,true=关闭. 没用，r-tree无法显示进程
     * @return
     */
    public static ArrayList<CusPoint> BB_Skyline(RTree rtree,String funcName,boolean loop) {
        //1. S=∅ // list of skyline points
        ArrayList<CusPoint> res = new ArrayList<CusPoint>();
        System.out.println("BB processing...");
        //2.开始时把root中Node放入heap
        // 用TreeMap(默认排序): key-value存点，key: distance to origin, value: Cuspoints
        int numDim = rtree.getNumDims();
        CusPoint origin=new CusPoint(numDim); //设置原点
        TreeMap treeMap = new TreeMap();
        Node root= rtree.getRoot();
        for (int i=0;i<root.children.size();i++){
            Node child=root.children.get(i);//每个child入heap
            CusPoint recMinPoint=new CusPoint(child.coords,numDim);//算离0，0最近点 dist作为key
            treeMap.put(recMinPoint.dist(origin, funcName), child);//！！heap中存Node
        }
        //3.while heap not empty do
        while(!treeMap.isEmpty()){
            //4. remove top entry e
            Double topKey = (Double) treeMap.firstKey();
            Node topNode = (Node) treeMap.get(topKey);
            treeMap.remove(topKey);
            CusPoint recMinPoint = new CusPoint(topNode.coords, numDim);
            //判断 5.if e is dominated by some point in S
            int flag = 0;
            for (int i = 0; i < res.size(); i++) {
                if (res.get(i).dominant(recMinPoint)) {
                    flag = 1;
                    break;
                }
            }
            //5. if e is dominated by some point in S do discard e
            if (flag == 1) {
            }
            //6. else // e is not dominated
            else if (flag == 0) {
                //7. if e is an intermediate entry then
                if (!(topNode.children.size() ==0)) {
                    //8. for each child ei of e do
                    for (int i = 0; i < topNode.children.size(); i++) {
                        Node child = topNode.children.get(i);
                        CusPoint recMinPoint2 = new CusPoint(child.coords, numDim);//算离0，0最近点 dist作为key
                        //9. if ei is not dominated by some point in S then
                        int flag2=0;
                        for (int j = 0; j < res.size(); j++) {
                            if (res.get(j).dominant(recMinPoint)) {
                                flag2 = 1;
                                break;
                            }
                        }
                        if(flag2==0) {
                            //10. insert ei into heap
                            treeMap.put(recMinPoint2.dist(origin, funcName), child);//！！heap中存Node
                        }
                    }
                }
                //11. else // e is a data point
                else if (topNode.children.size() ==0) {
                    //12. insert ei into S
                    CusPoint resPoint=new CusPoint(topNode.coords,topNode.coords.length);
                    res.add(resPoint);
                }
            }

            System.out.println("test");

        }
        System.out.println("test");
        System.out.println("task finished");
        return res ;
    }


}
