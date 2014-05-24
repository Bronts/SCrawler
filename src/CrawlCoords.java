/**
 * @(#)CrawlCoords.java
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

public class CrawlCoords implements Runnable
{
    private DataBaseHelper DBHelper  = new DataBaseHelper();
    private static Logger logger     = LogManager.getLogger(CrawlCoords.class.getName());

    public static ArrayList<ShopTable> shopTableList;
    public static ArrayList<ShopInfo>  shopList;

    // 用于多线程的商户列表数组
    private ArrayList<ShopInfo>[] subShopArrayList;

    private boolean bIsContinue = false;
    private static boolean bIsNeedStop = false; //运行标志位

    public CrawlCoords(ArrayList<ShopTable> shopTableList, boolean bIsContinue)
    {
        this.shopTableList = shopTableList;
        this.bIsContinue   = bIsContinue;
    }

    public void run()
    {
        if (bIsContinue)
        {
            // 继续上次进度
            Coords_getAllShopCoords(shopTableList);
        }
        else
        {
            // 获取商户数据表
            Coords_getShopTableInfo(shopTableList);
            // 按数据表依次获取坐标
            Coords_getAllShopCoords(shopTableList);
        }
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getRunningFlag
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
        return CrawlCoords.bIsNeedStop;
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_setRunningFlag
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
        CrawlCoords.bIsNeedStop = bIsNeedStop;
    }


    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getShopTableInfo
            Author: Yangzheng
       Description: 获取商户数据表
             Input: ArrayList<ShopTable> shopTableList
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/7/06       Yangzheng         Created Function
    *****************************************************************************/
    private void Coords_getShopTableInfo(ArrayList<ShopTable> shopTableList)
    {
        System.out.println(" 开始获取商户表");

        // 获取商户数据表
        DBHelper.getShopTables(shopTableList);

        return;
    }


    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getAllShopCoords
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
    private void Coords_getAllShopCoords(ArrayList<ShopTable> shopTableList)
    {
        try
        {
            for (ShopTable aShopTable : shopTableList)
            {
                System.out.println(" 当前商户表名字为:" + aShopTable.getShopTableName());

                // 判断该商户表是否抓取完毕
                if (Config.TABLE_CRAWLED == (Config.TABLE_CRAWLED & aShopTable.getCrawledFlag()))
                {
                    aShopTable.getShopList().clear();
                    System.out.println(" 已经抓取完毕,跳过");
                    continue;
                }

                // 抓取一个商户表的坐标数据
                Coords_getATableShopsCoords(aShopTable);

                // 将该城市(商户表)标记为已抓取完毕
                aShopTable.setCrawledFlag(Config.TABLE_CRAWLED);
                System.out.println(" 当前商户表数据抓取完毕.");

                synchronized (shopTableList)
                {
                    // 清空当前表下的商户
                    aShopTable.getShopList().clear();
                    logger.info(aShopTable.getShopTableName() + "抓取坐标完毕保存一次,清空该商户表下商户列表。");
                    InfoSave.SC_savePrcocess(shopTableList, Config.coordsDat);
                    System.out.println(" 保存完毕");
                }
            }

            System.out.println(" 所有数据表的商户信息已经抓取完毕!");
            // System.exit(0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getATableShopsCoords
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
    private void Coords_getATableShopsCoords(ShopTable aShopTable)
    {

        // 获取当前表中坐标为空的商户
        Coords_getBlankCoordShops(aShopTable);

        // 分配抓取线程
        Coords_distributeCrawlTasks(aShopTable);

        // 启动多线程对商户数据进行抓取
        Coords_getATableShopsMultiply(aShopTable);

        // 等待抓取子线程结束
        Coords_waitForMultiTaskFinish(aShopTable);

        // 创建数据表保存基本信息到数据库
        Coords_saveATableShopsToDB(aShopTable);

    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getBlankCoordShops
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
    private void Coords_getBlankCoordShops(ShopTable aShopTable)
    {
        if (Config.SHOPS_CRAWLED != (Config.SHOPS_CRAWLED & aShopTable.getCrawledFlag()))
        {
            DBHelper.getBlankCoordShops(aShopTable);

            // 读取数据后设置标志位
            aShopTable.setCrawledFlag(Config.SHOPS_CRAWLED);
        }
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_distributeCrawlTasks
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
     Function Name: CrawlCoords.Coords_distributeTasks
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
     Function Name: CrawlCoords.Coords_getATableShopsMultiply
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
    private void Coords_getATableShopsMultiply(ShopTable aShopTable)
    {
        System.out.println("开始抓取商户信息,实际要启动的工作线程数：" + subShopArrayList.length);
        // 设置线程进度标志位
        aShopTable.setCoordsCrawlThreadCount(subShopArrayList.length);

        for (int i = 0; i < subShopArrayList.length; i++)
        {
            System.out.println("<<<<<<=======启动线程 " + i + " 开始获取商户坐标信息========>>>>>>>");
            CrawlShopsCoords crawlShopsCoords = new CrawlShopsCoords(aShopTable, subShopArrayList[i]);
            Thread crawlCoordsThread = new Thread(crawlShopsCoords);
            crawlCoordsThread.start();
        }
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_waitForMultiTaskFinish
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
                // 若还有子线程执行，主线程休眠10秒
                Thread.sleep(10000);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_saveATableShopsToDB
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
        DBHelper.saveShopsCoordsToMySQL(aShopTable);
    }
}

/*****************************************************************************
    Class Name: CrawlShopsCoords
        Author: Yangzheng
   Description: 抓取坐标类
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/7/25       Yangzheng          Created Class
*****************************************************************************/
class CrawlShopsCoords implements Runnable
{
    private static Logger logger = LogManager.getLogger(CrawlShopsCoords.class.getName());
    private ArrayList<ShopTable> shopTableList;
    private ArrayList<ShopInfo> subShopList;
    private ShopTable aShopTable;

    private DataBaseHelper DBHelper  = new DataBaseHelper();
    private CrawlHelper CoordsHelper = new CrawlHelper();

    public CrawlShopsCoords(ShopTable aShopTable, ArrayList<ShopInfo> subShopList)
    {
        this.aShopTable    = aShopTable;
        this.subShopList   = subShopList;
        this.shopTableList = CrawlCoords.shopTableList;
    }

    public void run()
    {
        // 对所有空白坐标商户进行坐标数据抓取并存储
        Coords_getATableShopsCoords(aShopTable, subShopList);

        // 临时调试,对于本身不存在的商户,需要给定条件跳出
        int loopCount = 3;

        while (!Coords_checkATableShopsCrawled(aShopTable, subShopList) && !CrawlCoords.Coords_getRunningFlag() && loopCount-- > 0)
        {
            // 对抓取异常的商户重新抓取
            System.out.println("\n 对上次抓取失败的商户进行重新抓取");
            Coords_getATableShopsCoords(aShopTable, subShopList);
        }

        // 更新线程进度数量
        aShopTable.decCoordsCrawlThreadCount();
    }


    /*****************************************************************************
     Function Name: CrawlShopsCoords.Coords_getATableShopsCoords
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
    private void Coords_getATableShopsCoords(ShopTable aShopTable, ArrayList<ShopInfo> subShopList)
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
                if(CrawlCoords.Coords_getRunningFlag())
                {
                    System.out.println(" 坐标抓取线程需要停止，即将退出...");
                    break;
                }

                if (Config.SHOP_INFO_CRAWLED == (Config.SHOP_INFO_CRAWLED & aShop.getShopCrawledFlag()))
                {
                    continue;
                }

                // 抓取一个商户坐标
                Coords_getShopBaiduCoords(aShopTable, aShop);

                if (savePoint-- <= 0)
                {
                    // 运行中保存一次
                    synchronized (shopTableList)
                    {
                        logger.info("达到抓取商户门限，保存一次。");
                        InfoSave.SC_savePrcocess(shopTableList, Config.coordsDat);
                        System.out.println(" 保存完毕");
                    }

                    savePoint = Config.SAVE_THRESHOLD;
                }
            }
        }
        catch (Exception ex)
        {
            // 设置标记位为未抓取 在具体函数里已经设置
            aExShop.setShopCrawledFlag(Config.CLEAR_FLAG);
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
    Function Name: CrawlCoords.Coords_getShopBaiduCoords
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
    private void Coords_getShopBaiduCoords(ShopTable aShopTable, ShopInfo aShopInfo)
    {
        String apiBaseUrl  = "http://api.map.baidu.com/geocoder/v2/";
        String apiParaKey  = "?ak=79a3cf6c6942a9cecf3607dce8525aa4";
        String apiParaCity = "&city=";
        String apiParaAddr = "&address=";

        String cityName    = aShopTable.getCityName();
        String shopAddr    = aShopInfo.getAddress();
        String shopName    = aShopInfo.getCnName();

        String subAddress = null;

        String latitude    = null;
        String longitude   = null;

        try
        {
            String searchAddressUrl = apiBaseUrl  + apiParaKey +
                                      apiParaCity + cityName +      // URLEncoder.encode(cityName, "UTF-8") +
                                      apiParaAddr + shopAddr;       // URLEncoder.encode(shopAddr, "UTF-8");
            // 以地址查询坐标
            Document addressCoordsDoc = CoordsHelper.getWebByJsoup(searchAddressUrl);


            String searchShopNameUrl = apiBaseUrl  + apiParaKey +
                                       apiParaCity + cityName +      // URLEncoder.encode(cityName, "UTF-8") +
                                       apiParaAddr + shopName;       // URLEncoder.encode(shopName, "UTF-8");
            // 以商户名查询坐标
            Document shopNameCoordsDoc = CoordsHelper.getWebByJsoup(searchShopNameUrl);

            if (null == addressCoordsDoc && null == shopNameCoordsDoc)
            {
                System.out.println("注意:网络可能出现问题，或者网页发生变化，请检查.\n");

                return;
            }

            if (addressCoordsDoc.select("lat").first().hasText())
            {
                latitude  = addressCoordsDoc.select("lat").first().text();
                longitude = addressCoordsDoc.select("lng").first().text();
                System.out.println("通过地址获取到" + aShopInfo.getCnName());
            }
            else if (shopNameCoordsDoc.select("lat").first().hasText())
            {
                latitude  = shopNameCoordsDoc.select("lat").first().text();
                longitude = shopNameCoordsDoc.select("lng").first().text();
                System.out.println("通过商户名获取到" + aShopInfo.getCnName());
            }
            else if (shopAddr.contains("("))
            {
                subAddress = shopAddr.substring(0, shopAddr.lastIndexOf('('));

                String searcSubAddressUrl = apiBaseUrl  + apiParaKey +
                                            apiParaCity + cityName +   // URLEncoder.encode(cityName, "UTF-8") +
                                            apiParaAddr + subAddress;  // URLEncoder.encode(subAddress, "UTF-8");

                // 地址中可能含有括号，去掉括号后重查
                Document subAddressCoordsDoc = CoordsHelper.getWebByJsoup(searcSubAddressUrl);

                if (subAddressCoordsDoc != null && subAddressCoordsDoc.select("lat").first().hasText())
                {
                    latitude  = subAddressCoordsDoc.select("lat").first().text();
                    longitude = subAddressCoordsDoc.select("lng").first().text();
                    System.out.println("通过子地址获取到" + aShopInfo.getCnName());
                }
            }

            // 取出坐标
            if (null != latitude && null != longitude)
            {
                System.out.println("Baidu纬度为: " + latitude);
                System.out.println("Baidu经度为: " + longitude);
                System.out.println("\n");

                aShopInfo.setbLat(latitude);
                aShopInfo.setbLng(longitude);

                // 设置抓取完成标记位
                aShopInfo.setShopCrawledFlag(Config.SHOP_INFO_CRAWLED);
            }
            else
            {
                System.out.println("没有获得" + cityName + shopAddr + shopName + "的百度坐标!");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: CrawlShopsCoords.Coords_checkATableShopsCrawled
            Author: Yangzheng
       Description: 检查商户子列表下的商户是否抓取完毕
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


