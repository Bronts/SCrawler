/**
 * @(#)TransformCoords.java
 *
 * SCrawler application
 *
 * @author  Yangzheng
 * @version 1.00 2013/07/08
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.lang.Math;


public class TransformCoords implements Runnable
{
    private DataBaseHelper DBHelper  = new DataBaseHelper();
    private static Logger logger     = LogManager.getLogger(TransformCoords.class.getName());

    public static ArrayList<ShopTable> shopTableList;

    // 用于多线程的商户列表数组
    private ArrayList<ShopInfo>[] subShopArrayList;

    private boolean bIsContinue = false;
    private static boolean bIsNeedStop = false; //运行标志位

    public TransformCoords(ArrayList<ShopTable> shopTableList, boolean bIsContinue)
    {
        this.shopTableList = shopTableList;
        this.bIsContinue   = bIsContinue;
    }

    public void run()
    {
        if (bIsContinue)
        {
            // 继续上次进度转换坐标
            Coords_transformAllCoords(shopTableList);
        }
        else
        {
            // 对商户表进行更改/增加字段
            Coords_alterShopTableInfo(shopTableList);
            // 按数据表依次获取坐标
            Coords_transformAllCoords(shopTableList);
        }
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_getRunningFlag
            Author: Yangzheng
       Description: 获取运行标志位
             Input: NONE
            Output: NONE
            Return: public
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/17       Yangzheng        Created Function
    *****************************************************************************/
    public static boolean Coords_getRunningFlag()
    {
        return TransformCoords.bIsNeedStop;
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_setRunningFlag
            Author: Yangzheng
       Description: 设置运行标志位
             Input: boolean bIsNeedStop
            Output: NONE
            Return: public
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/17       Yangzheng        Created Function
    *****************************************************************************/
    public static void Coords_setRunningFlag(boolean bIsNeedStop)
    {
        TransformCoords.bIsNeedStop = bIsNeedStop;
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_alterShopTableInfo
            Author: Yangzheng
       Description: 对商户表进行更改
             Input: ArrayList<ShopTable> shopTableList
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/7/06       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_alterShopTableInfo(ArrayList<ShopTable> shopTableList)
    {
        boolean isExist = true;

        System.out.println(" 开始获取商户表");

        // 获取商户数据表
        DBHelper.getShopTables(shopTableList);

        for (ShopTable aShopTable : shopTableList)
        {
            // 增加m_lat 检查每个表是否存在要增加的字段
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "m_lat");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "m_lat", "text", "g_lng");
            }

            // 增加m_lng
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "m_lng");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "m_lng", "text", "m_lat");
            }

            // 增加gps_lat
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "gps_lat");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "gps_lat", "text", "m_lng");
            }

            // 增加gps_lng
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "gps_lng");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "gps_lng", "text", "gps_lat");
            }

            // 增加circle字段
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "circle");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "circle", "text", "address");
            }
        }

        return;
    }


    /*****************************************************************************
     Function Name: TransformCoords.Coords_transformAllCoords
            Author: Yangzheng
       Description: 获取商户坐标
             Input: ArrayList<ShopTable> shopTableList
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/7/06       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_transformAllCoords(ArrayList<ShopTable> shopTableList)
    {
        try
        {
            for (ShopTable aShopTable : shopTableList)
            {
                System.out.println(" 当前商户表名字为:" + aShopTable.getShopTableName());

                // 判断该商户表是否处理完毕
                if (Config.TABLE_CRAWLED == (Config.TABLE_CRAWLED & aShopTable.getCrawledFlag()))
                {
                    aShopTable.getShopList().clear();
                    System.out.println(" 已经抓取完毕,跳过");
                    continue;
                }

                // 抓取一个商户表的坐标数据
                Coords_transformATableShopsCoords(aShopTable);

                // 将该城市(商户表)标记为已抓取完毕
                aShopTable.setCrawledFlag(Config.TABLE_CRAWLED);
                System.out.println(" 当前商户表数据抓取完毕.");

                synchronized (shopTableList)
                {
                    // 清空当前表下的商户
                    aShopTable.getShopList().clear();
                    logger.info("转换完" + aShopTable.getShopTableName() + "商户表坐标,清空该商户表下商户。");
                    InfoSave.SC_savePrcocess(shopTableList, Config.transformDat);
                    System.out.println(" 保存完毕");
                }
            }

            logger.info("所有数据表的商户坐标已经转换完毕!");
            System.out.println(" 所有数据表的商户坐标已经转换完毕!");
            // System.exit(0);
        }
        catch (Exception ex)
        {
            logger.error(ex);
            ex.printStackTrace();
        }
    }


    /*****************************************************************************
     Function Name: TransformCoords.Coords_transformATableShopsCoords
            Author: Yangzheng
       Description: 获取商户坐标
             Input: ShopTable aShopTable
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/7/06       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_transformATableShopsCoords(ShopTable aShopTable)
    {

        // 获取当前表中所有商户信息
        Coords_getAllShops(aShopTable);

        // 分配抓取线程
        Coords_distributeCrawlTasks(aShopTable);

        // 启动多线程对商户数据进行抓取
        Coords_transformCoordsMultiply(aShopTable);

        // 等待抓取子线程结束
        Coords_waitForMultiTaskFinish(aShopTable);

        // 创建数据表保存基本信息到数据库
        Coords_saveATableShopsToDB(aShopTable);

    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_getAllShops
            Author: Yangzheng
       Description: 商户表下没有商户数据时从数据库读入数据
             Input: ShopTable aShopTable
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/7/6       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_getAllShops(ShopTable aShopTable)
    {
        // 这里属于设计标记名设计不合理,应该是已经取到了数据表的所有商户,而不是抓取完
        if (Config.SHOPS_CRAWLED != (Config.SHOPS_CRAWLED & aShopTable.getCrawledFlag()))
        {
            DBHelper.getAllShops(aShopTable);

            // 读取数据后设置标志位
            aShopTable.setCrawledFlag(Config.SHOPS_CRAWLED);
        }
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_distributeCrawlTasks
            Author: Yangzheng
       Description: 分配子线程要处理的商户列表
             Input: ShopTable aShopTable
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_distributeCrawlTasks(ShopTable aShopTable)
    {
        // 获得商户列表数组
        subShopArrayList = Coords_distributeTasks(aShopTable);
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_distributeTasks
            Author: Yangzheng
       Description: 分配每个表中子线程需要处理的子商户列表
             Input: ShopTable aShopTable
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    @SuppressWarnings("unchecked")
    private ArrayList<ShopInfo>[] Coords_distributeTasks(ShopTable aShopTable)
    {
        // 定义下载商户线程数
        int threadCount = Config.CRAWL_THREAD_COUNT;

        // 获取商户列表
        ArrayList<ShopInfo> shopTaskList = aShopTable.getShopList();

        // 每个线程至少要执行的任务(商户)数,假如不为零则表示每个线程都会分配到任务
        int minTaskCount = shopTaskList.size() / threadCount;

        // 平均分配后还剩下的任务数，不为零则还有任务依个附加到前面的线程中
        int remainTaskCount = shopTaskList.size() % threadCount;

        // 实际要启动的线程数,如果工作线程比任务还多
        // 自然只需要启动与任务相同个数的工作线程，一对一的执行
        // 毕竟不打算实现了线程池，所以用不着预先初始化好休眠的线程
        int actualThreadCount = minTaskCount > 0 ? threadCount : remainTaskCount;

        // 要启动的线程数组，以及每个线程要执行的任务列表
        ArrayList<ShopInfo> subShopArrayList[] = new ArrayList [actualThreadCount];

        int taskIndex = 0;
        //平均分配后多余任务，每附加给一个线程后的剩余数，重新声明与 remainTaskCount
        //相同的变量，不然会在执行中改变 remainTaskCount 原有值，产生麻烦
        int remainIndces = remainTaskCount;

        for (int i = 0; i < subShopArrayList.length; i++)
        {
            subShopArrayList[i] = new ArrayList<ShopInfo>();

            // 如果大于零，线程要分配到基本的任务
            if (minTaskCount > 0)
            {
                for (int j = taskIndex; j < minTaskCount + taskIndex; j++)
                {
                    subShopArrayList[i].add(shopTaskList.get(j));
                }

                taskIndex += minTaskCount;
            }

            // 假如还有剩下的，则补一个到这个线程中
            if (remainIndces > 0)
            {
                subShopArrayList[i].add(shopTaskList.get(taskIndex++));
                remainIndces--;
            }
        }

        return subShopArrayList;
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_transformCoordsMultiply
            Author: Yangzheng
       Description: 启动多线程抓取任务
             Input: ShopTable aShopTable
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_transformCoordsMultiply(ShopTable aShopTable)
    {
        System.out.println("开始多线程处理商户信息,实际要启动的工作线程数：" + subShopArrayList.length);
        // 设置线程进度标志位
        aShopTable.setCoordsCrawlThreadCount(subShopArrayList.length);

        for (int i = 0; i < subShopArrayList.length; i++)
        {
            logger.info("开始转换" + aShopTable.getShopTableName() + "坐标");
            System.out.println("<<<<<<=======启动线程 " + (i + 1) + " 开始转换商户坐标信息========>>>>>>>");
            TransformShopsCoords transformShops = new TransformShopsCoords(aShopTable, subShopArrayList[i]);
            Thread TransformCoordsThread = new Thread(transformShops);
            TransformCoordsThread.start();
        }
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_waitForMultiTaskFinish
            Author: Yangzheng
       Description: 等待获取坐标线程全部结束
             Input: ShopTable aShopTable
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_waitForMultiTaskFinish(ShopTable aShopTable)
    {
        try
        {
            while (0 < aShopTable.getCoordsCrawlThreadCount())
            {
                // 若还有子线程执行，主线程休眠7秒
                Thread.sleep(7000);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_saveATableShopsToDB
            Author: Yangzheng
       Description: 将抓取数据更新到数据库
             Input: ShopTable aShopTable
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_saveATableShopsToDB(ShopTable aShopTable)
    {
        logger.info("开始保存" + aShopTable.getShopTableName() + "的转换坐标到数据库");
        DBHelper.saveShopsMultiCoordsToMySQL(aShopTable);
    }
}

/*****************************************************************************
    Class Name: TransformShopsCoords
        Author: Yangzheng
   Description: 抓取坐标类
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/7/25       Yangzheng          Created Class
*****************************************************************************/
class TransformShopsCoords implements Runnable
{
    private static Logger logger = LogManager.getLogger(TransformShopsCoords.class.getName());
    private ArrayList<ShopTable> shopTableList;
    private ArrayList<ShopInfo> subShopList;
    private ShopTable aShopTable;

    private DataBaseHelper DBHelper  = new DataBaseHelper();
    private CrawlHelper CoordsHelper = new CrawlHelper();

    public TransformShopsCoords(ShopTable aShopTable, ArrayList<ShopInfo> subShopList)
    {
        this.aShopTable    = aShopTable;
        this.subShopList   = subShopList;
        this.shopTableList = TransformCoords.shopTableList;
    }

    public void run()
    {
        try
        {
            // 对所有商户进行坐标数据抓取并转换存储
            Coords_transformATableShopsCoords(aShopTable, subShopList);

            // 临时调试,对于本身不存在的商户,需要给定条件跳出
            int loopCount = 3;

            while (!Coords_checkATableShopsCrawled(aShopTable, subShopList) && !TransformCoords.Coords_getRunningFlag() && loopCount-- > 0)
            {
                // 对抓取异常的商户重新抓取
                logger.info("对转换异常的商户进行重新抓取");
                System.out.println("\n " + aShopTable.getShopTableName() + "对上次抓取失败的商户进行重新抓取");
                Coords_transformATableShopsCoords(aShopTable, subShopList);
            }

            aShopTable.decCoordsCrawlThreadCount();
            System.out.println("\n" + aShopTable.getShopTableName() + "完成一个转换线程...还剩" + aShopTable.getCoordsCrawlThreadCount() + "个线程");
            System.out.println("请稍等,任务还在继续...");
        }
        catch (Exception ex)
        {
            logger.info("转换线程出现问题" + ex);
            ex.printStackTrace();
        }
    }


    /*****************************************************************************
     Function Name: TransformShopsCoords.Coords_transformATableShopsCoords
            Author: Yangzheng
       Description: 对子列表中的商户进行坐标抓取
             Input: ShopTable aShopTable
                    ArrayList<ShopInfo> subShopList
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_transformATableShopsCoords(ShopTable aShopTable, ArrayList<ShopInfo> subShopList)
    {
        // 设置保存标志位
        int savePoint  = Config.SAVE_THRESHOLD;
        // 异常商户
        ShopInfo aExShop = null;

        try
        {
            System.out.println("\n4.开始获取" + aShopTable.getCityName() + "的商户坐标信息");

            for (ShopInfo aShop : subShopList)
            {
                aExShop = aShop;

                // 检查运行标志位
                if (TransformCoords.Coords_getRunningFlag())
                {
                    System.out.println(" 坐标转换线程需要停止，即将退出...");
                    break;
                }

                if (Config.SHOP_INFO_CRAWLED == (Config.SHOP_INFO_CRAWLED & aShop.getShopCrawledFlag()))
                {
                    continue;
                }

                // 处理一个商户坐标
                Coords_transformAShopCoords(aShopTable, aShop);

                if (savePoint-- <= 0)
                {
                    // 运行中保存一次
                    synchronized (shopTableList)
                    {
                        logger.info("达到转换商户数门限，保存一次。");
                        InfoSave.SC_savePrcocess(shopTableList, Config.transformDat);
                        System.out.println(" 保存完毕");
                    }

                    savePoint = Config.SAVE_THRESHOLD;
                }
            }
        }
        catch (Exception ex)
        {
            // 设置标记位为未抓取 在具体函数里已经设置
            logger.error("转换错误" + ex);
            aExShop.setShopCrawledFlag(Config.CLEAR_FLAG);
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
    Function Name: TransformCoords.Coords_transformAShopCoords
           Author: Yangzheng
      Description: 按照商户地址或商户名查询百度坐标
            Input: ShopTable aShopTable
                   ShopInfo aShopInfo
           Output: NONE
           Return: private
          Caution:
     --------------------------------------------------------------------------
           Date          Author             Description
        2013/7/29       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_transformAShopCoords(ShopTable aShopTable, ShopInfo aShopInfo)
    {
        String apiBaseUrl  = "http://api.map.baidu.com/geocoder/v2/";
        String apiParaKey  = "?ak=79a3cf6c6942a9cecf3607dce8525aa4";
        String apiParaType = "&coordtype=wgs84ll";
        String apiParaAddr = "&location=";

        String cityName    = aShopTable.getCityName();
        String shopAddr    = aShopInfo.getAddress();
        String shopName    = aShopInfo.getCnName();
        String b_lat       = aShopInfo.getbLat();
        String b_lng       = aShopInfo.getbLng();

        String midLatitude  = null;
        String midLongitude = null;
        String gpsLatitude  = null;
        String gpsLongitude = null;

        // 可以顺便获取商户的商圈
        String circle       = null;

        try
        {
            String searchCoordsUrl = apiBaseUrl  + apiParaKey + apiParaType +
                                     apiParaAddr + b_lat + "," + b_lng;         // URLEncoder.encode(shopAddr, "UTF-8");
            // 以地址查询坐标
            Document transformCoordsDoc = CoordsHelper.getWebByJsoup(searchCoordsUrl);

            if (null == transformCoordsDoc)
            {
                System.out.println("注意:网络可能出现问题，或者网页发生变化，请检查.\n");

                return;
            }

            if (transformCoordsDoc.select("lat").first().hasText())
            {
                midLatitude  = transformCoordsDoc.select("lat").first().text();
                midLongitude = transformCoordsDoc.select("lng").first().text();
                circle    = transformCoordsDoc.select("business").first().text();
            }

            // 取出坐标
            if (null != midLatitude && null != midLongitude)
            {
                System.out.println(" 处理城市ID为" + aShopInfo.getCityId() + ",商户为:" + aShopInfo.getCnName() + "的坐标");
                System.out.println(" 所属商圈为: " + circle);
                System.out.println(" 百度纬度为: " + b_lat);
                System.out.println(" 百度经度为: " + b_lng);
                System.out.println(" \n" );
                System.out.println(" 中间纬度为: " + midLatitude);
                System.out.println(" 中间经度为: " + midLongitude);

                // 需要转换为数字后再转换为字符串
                gpsLatitude  = Double.toString(Double.parseDouble(b_lat) * 2 - Double.parseDouble(midLatitude));
                gpsLongitude = Double.toString(Double.parseDouble(b_lng) * 2 - Double.parseDouble(midLongitude));

                System.out.println(" \n" );
                System.out.println(" 地球纬度为: " + gpsLatitude);
                System.out.println(" 地球经度为: " + gpsLongitude);

                aShopInfo.setGpsLat(gpsLatitude);
                aShopInfo.setGpsLng(gpsLongitude);
                aShopInfo.setCircle(circle);

                // 增加一个函数转换为火星坐标
                Coords_transformBaiduCoordsToMars(aShopTable, aShopInfo);
                // 设置抓取完成标记位
                aShopInfo.setShopCrawledFlag(Config.SHOP_INFO_CRAWLED);
            }
            else
            {
                System.out.println(" 没有获得" + cityName + shopAddr + shopName + "的百度坐标!");
            }
        }
        catch (Exception ex)
        {
            logger.error("转换错误" + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: TransformShopsCoords.Coords_transformBaiduCoordsToMars
            Author: Yangzheng
       Description: 将百度坐标转换为火星坐标
             Input: ShopTable aShopTable
                    ShopInfo aShopInfo
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/10       Yangzheng        Created Function
    *****************************************************************************/
    private void Coords_transformBaiduCoordsToMars(ShopTable aShopTable, ShopInfo aShopInfo)
    {
        String baiduLatitude  = aShopInfo.getbLat();
        String baiduLongitude = aShopInfo.getbLng();
        String marsLatitude    = null;
        String marsLongitude   = null;

        double bdLat = Double.parseDouble(baiduLatitude);
        double bdLng = Double.parseDouble(baiduLongitude);
        double marsLat = 0;
        double marsLng = 0;

        if (bdLat > 0)
        {
            double x = bdLng - 0.0065;
            double y = bdLat - 0.006;
            double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Config.COORDS_PI);
            double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Config.COORDS_PI);

            marsLat = z * Math.sin(theta);
            marsLng = z * Math.cos(theta);

            marsLatitude  = Double.toString(marsLat);
            marsLongitude = Double.toString(marsLng);

            aShopInfo.setMLat(marsLatitude);
            aShopInfo.setMLng(marsLongitude);

            System.out.println(" \n" );
            System.out.println(" 转换百度坐标得到 " );
            System.out.println(" 火星纬度为: " + marsLatitude);
            System.out.println(" 火星经度为: " + marsLongitude);
        }
    }

    /*****************************************************************************
     Function Name: TransformShopsCoords.Coords_checkATableShopsCrawled
            Author: Yangzheng
       Description: 检查商户子列表下的商户是否处理完毕
             Input: ShopTable aShopTable
                    ArrayList<ShopInfo> subShopList
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/2       Yangzheng         Created Function
    *****************************************************************************/
    private boolean Coords_checkATableShopsCrawled(ShopTable aShopTable, ArrayList<ShopInfo> subShopList)
    {
        boolean bAllCrawled = true;

        for (ShopInfo aShop : subShopList)
        {
            if (Config.SHOP_INFO_CRAWLED == (Config.SHOP_INFO_CRAWLED & aShop.getShopCrawledFlag()))
            {
                continue;
            }
            else
            {
                bAllCrawled = false;
                break;
            }
        }

        return bAllCrawled;
    }
}



