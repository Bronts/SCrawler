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

    // ���ڶ��̵߳��̻��б�����
    private ArrayList<ShopInfo>[] subShopArrayList;

    private boolean bIsContinue = false;
    private static boolean bIsNeedStop = false; //���б�־λ

    public CrawlCoords(ArrayList<ShopTable> shopTableList, boolean bIsContinue)
    {
        this.shopTableList = shopTableList;
        this.bIsContinue   = bIsContinue;
    }

    public void run()
    {
        if (bIsContinue)
        {
            // �����ϴν���
            Coords_getAllShopCoords(shopTableList);
        }
        else
        {
            // ��ȡ�̻����ݱ�
            Coords_getShopTableInfo(shopTableList);
            // �����ݱ����λ�ȡ����
            Coords_getAllShopCoords(shopTableList);
        }
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getRunningFlag
            Author: Yangzheng
       Description: ��ȡ���б�־λ
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
       Description: �������б�־λ
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
       Description: ��ȡ�̻����ݱ�
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
        System.out.println(" ��ʼ��ȡ�̻���");

        // ��ȡ�̻����ݱ�
        DBHelper.getShopTables(shopTableList);

        return;
    }


    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getAllShopCoords
            Author: Yangzheng
       Description: ��ȡ�̻�����
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
                System.out.println(" ��ǰ�̻�������Ϊ:" + aShopTable.getShopTableName());

                // �жϸ��̻����Ƿ�ץȡ���
                if (Config.TABLE_CRAWLED == (Config.TABLE_CRAWLED & aShopTable.getCrawledFlag()))
                {
                    aShopTable.getShopList().clear();
                    System.out.println(" �Ѿ�ץȡ���,����");
                    continue;
                }

                // ץȡһ���̻������������
                Coords_getATableShopsCoords(aShopTable);

                // ���ó���(�̻���)���Ϊ��ץȡ���
                aShopTable.setCrawledFlag(Config.TABLE_CRAWLED);
                System.out.println(" ��ǰ�̻�������ץȡ���.");

                synchronized (shopTableList)
                {
                    // ��յ�ǰ���µ��̻�
                    aShopTable.getShopList().clear();
                    logger.info(aShopTable.getShopTableName() + "ץȡ������ϱ���һ��,��ո��̻������̻��б�");
                    InfoSave.SC_savePrcocess(shopTableList, Config.coordsDat);
                    System.out.println(" �������");
                }
            }

            System.out.println(" �������ݱ���̻���Ϣ�Ѿ�ץȡ���!");
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
       Description: ��ȡ�̻�����
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

        // ��ȡ��ǰ��������Ϊ�յ��̻�
        Coords_getBlankCoordShops(aShopTable);

        // ����ץȡ�߳�
        Coords_distributeCrawlTasks(aShopTable);

        // �������̶߳��̻����ݽ���ץȡ
        Coords_getATableShopsMultiply(aShopTable);

        // �ȴ�ץȡ���߳̽���
        Coords_waitForMultiTaskFinish(aShopTable);

        // �������ݱ��������Ϣ�����ݿ�
        Coords_saveATableShopsToDB(aShopTable);

    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_getBlankCoordShops
            Author: Yangzheng
       Description: �̻�����û���̻�����ʱ�����ݿ��������
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

            // ��ȡ���ݺ����ñ�־λ
            aShopTable.setCrawledFlag(Config.SHOPS_CRAWLED);
        }
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_distributeCrawlTasks
            Author: Yangzheng
       Description: �������߳�Ҫ������̻��б�
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
        // ����̻��б�����
        subShopArrayList = Coords_distributeTasks(aShopTable);
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_distributeTasks
            Author: Yangzheng
       Description: ����ÿ���������߳���Ҫ��������̻��б�
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
        // ���������̻��߳���
        int threadCount = Config.CRAWL_THREAD_COUNT;

        // ��ȡ�̻��б�
        ArrayList<ShopInfo> shopTaskList = aShopTable.getShopList();

        // ÿ���߳�����Ҫִ�е�����(�̻�)��,���粻Ϊ�����ʾÿ���̶߳�����䵽����
        int minTaskCount = shopTaskList.size() / threadCount;

        // ƽ�������ʣ�µ�����������Ϊ�����������������ӵ�ǰ����߳���
        int remainTaskCount = shopTaskList.size() % threadCount;

        // ʵ��Ҫ�������߳���,��������̱߳����񻹶�
        // ��Ȼֻ��Ҫ������������ͬ�����Ĺ����̣߳�һ��һ��ִ��
        // �Ͼ�������ʵ�����̳߳أ������ò���Ԥ�ȳ�ʼ�������ߵ��߳�
        int actualThreadCount = minTaskCount > 0 ? threadCount : remainTaskCount;

        // Ҫ�������߳����飬�Լ�ÿ���߳�Ҫִ�е������б�
        ArrayList<ShopInfo> subShopArrayList[] = new ArrayList [actualThreadCount];

        int taskIndex = 0;
        //ƽ��������������ÿ���Ӹ�һ���̺߳��ʣ���������������� remainTaskCount
        //��ͬ�ı�������Ȼ����ִ���иı� remainTaskCount ԭ��ֵ�������鷳
        int remainIndces = remainTaskCount;

        for (int i = 0; i < subShopArrayList.length; i++)
        {
            subShopArrayList[i] = new ArrayList<ShopInfo>();

            // ��������㣬�߳�Ҫ���䵽����������
            if (minTaskCount > 0)
            {
                for (int j = taskIndex; j < minTaskCount + taskIndex; j++)
                {
                    subShopArrayList[i].add(shopTaskList.get(j));
                }

                taskIndex += minTaskCount;
            }

            // ���绹��ʣ�µģ���һ��������߳���
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
       Description: �������߳�ץȡ����
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
        System.out.println("��ʼץȡ�̻���Ϣ,ʵ��Ҫ�����Ĺ����߳�����" + subShopArrayList.length);
        // �����߳̽��ȱ�־λ
        aShopTable.setCoordsCrawlThreadCount(subShopArrayList.length);

        for (int i = 0; i < subShopArrayList.length; i++)
        {
            System.out.println("<<<<<<=======�����߳� " + i + " ��ʼ��ȡ�̻�������Ϣ========>>>>>>>");
            CrawlShopsCoords crawlShopsCoords = new CrawlShopsCoords(aShopTable, subShopArrayList[i]);
            Thread crawlCoordsThread = new Thread(crawlShopsCoords);
            crawlCoordsThread.start();
        }
    }

    /*****************************************************************************
     Function Name: CrawlCoords.Coords_waitForMultiTaskFinish
            Author: Yangzheng
       Description: �ȴ���ȡ�����߳�ȫ������
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
                // ���������߳�ִ�У����߳�����10��
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
       Description: ��ץȡ���ݸ��µ����ݿ�
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
   Description: ץȡ������
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
        // �����пհ������̻�������������ץȡ���洢
        Coords_getATableShopsCoords(aShopTable, subShopList);

        // ��ʱ����,���ڱ������ڵ��̻�,��Ҫ������������
        int loopCount = 3;

        while (!Coords_checkATableShopsCrawled(aShopTable, subShopList) && !CrawlCoords.Coords_getRunningFlag() && loopCount-- > 0)
        {
            // ��ץȡ�쳣���̻�����ץȡ
            System.out.println("\n ���ϴ�ץȡʧ�ܵ��̻���������ץȡ");
            Coords_getATableShopsCoords(aShopTable, subShopList);
        }

        // �����߳̽�������
        aShopTable.decCoordsCrawlThreadCount();
    }


    /*****************************************************************************
     Function Name: CrawlShopsCoords.Coords_getATableShopsCoords
            Author: Yangzheng
       Description: �����б��е��̻���������ץȡ
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
        // ���ñ����־λ
        int savePoint  = Config.SAVE_THRESHOLD;
        // �쳣�̻�
        ShopInfo aExShop = null;

        try
        {
            System.out.println("\n4.��ʼ��ȡ" + aShopTable.getCityName() + "���̻�������Ϣ");

            for (ShopInfo aShop : subShopList)
            {
                aExShop = aShop;

                // ������б�־λ
                if(CrawlCoords.Coords_getRunningFlag())
                {
                    System.out.println(" ����ץȡ�߳���Ҫֹͣ�������˳�...");
                    break;
                }

                if (Config.SHOP_INFO_CRAWLED == (Config.SHOP_INFO_CRAWLED & aShop.getShopCrawledFlag()))
                {
                    continue;
                }

                // ץȡһ���̻�����
                Coords_getShopBaiduCoords(aShopTable, aShop);

                if (savePoint-- <= 0)
                {
                    // �����б���һ��
                    synchronized (shopTableList)
                    {
                        logger.info("�ﵽץȡ�̻����ޣ�����һ�Ρ�");
                        InfoSave.SC_savePrcocess(shopTableList, Config.coordsDat);
                        System.out.println(" �������");
                    }

                    savePoint = Config.SAVE_THRESHOLD;
                }
            }
        }
        catch (Exception ex)
        {
            // ���ñ��λΪδץȡ �ھ��庯�����Ѿ�����
            aExShop.setShopCrawledFlag(Config.CLEAR_FLAG);
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
    Function Name: CrawlCoords.Coords_getShopBaiduCoords
           Author: Yangzheng
      Description: �����̻���ַ���̻�����ѯ�ٶ�����
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
            // �Ե�ַ��ѯ����
            Document addressCoordsDoc = CoordsHelper.getWebByJsoup(searchAddressUrl);


            String searchShopNameUrl = apiBaseUrl  + apiParaKey +
                                       apiParaCity + cityName +      // URLEncoder.encode(cityName, "UTF-8") +
                                       apiParaAddr + shopName;       // URLEncoder.encode(shopName, "UTF-8");
            // ���̻�����ѯ����
            Document shopNameCoordsDoc = CoordsHelper.getWebByJsoup(searchShopNameUrl);

            if (null == addressCoordsDoc && null == shopNameCoordsDoc)
            {
                System.out.println("ע��:������ܳ������⣬������ҳ�����仯������.\n");

                return;
            }

            if (addressCoordsDoc.select("lat").first().hasText())
            {
                latitude  = addressCoordsDoc.select("lat").first().text();
                longitude = addressCoordsDoc.select("lng").first().text();
                System.out.println("ͨ����ַ��ȡ��" + aShopInfo.getCnName());
            }
            else if (shopNameCoordsDoc.select("lat").first().hasText())
            {
                latitude  = shopNameCoordsDoc.select("lat").first().text();
                longitude = shopNameCoordsDoc.select("lng").first().text();
                System.out.println("ͨ���̻�����ȡ��" + aShopInfo.getCnName());
            }
            else if (shopAddr.contains("("))
            {
                subAddress = shopAddr.substring(0, shopAddr.lastIndexOf('('));

                String searcSubAddressUrl = apiBaseUrl  + apiParaKey +
                                            apiParaCity + cityName +   // URLEncoder.encode(cityName, "UTF-8") +
                                            apiParaAddr + subAddress;  // URLEncoder.encode(subAddress, "UTF-8");

                // ��ַ�п��ܺ������ţ�ȥ�����ź��ز�
                Document subAddressCoordsDoc = CoordsHelper.getWebByJsoup(searcSubAddressUrl);

                if (subAddressCoordsDoc != null && subAddressCoordsDoc.select("lat").first().hasText())
                {
                    latitude  = subAddressCoordsDoc.select("lat").first().text();
                    longitude = subAddressCoordsDoc.select("lng").first().text();
                    System.out.println("ͨ���ӵ�ַ��ȡ��" + aShopInfo.getCnName());
                }
            }

            // ȡ������
            if (null != latitude && null != longitude)
            {
                System.out.println("Baiduγ��Ϊ: " + latitude);
                System.out.println("Baidu����Ϊ: " + longitude);
                System.out.println("\n");

                aShopInfo.setbLat(latitude);
                aShopInfo.setbLng(longitude);

                // ����ץȡ��ɱ��λ
                aShopInfo.setShopCrawledFlag(Config.SHOP_INFO_CRAWLED);
            }
            else
            {
                System.out.println("û�л��" + cityName + shopAddr + shopName + "�İٶ�����!");
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
       Description: ����̻����б��µ��̻��Ƿ�ץȡ���
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


