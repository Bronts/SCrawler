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

    // ���ڶ��̵߳��̻��б�����
    private ArrayList<ShopInfo>[] subShopArrayList;

    private boolean bIsContinue = false;
    private static boolean bIsNeedStop = false; //���б�־λ

    public TransformCoords(ArrayList<ShopTable> shopTableList, boolean bIsContinue)
    {
        this.shopTableList = shopTableList;
        this.bIsContinue   = bIsContinue;
    }

    public void run()
    {
        if (bIsContinue)
        {
            // �����ϴν���ת������
            Coords_transformAllCoords(shopTableList);
        }
        else
        {
            // ���̻�����и���/�����ֶ�
            Coords_alterShopTableInfo(shopTableList);
            // �����ݱ����λ�ȡ����
            Coords_transformAllCoords(shopTableList);
        }
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_getRunningFlag
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
        return TransformCoords.bIsNeedStop;
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_setRunningFlag
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
        TransformCoords.bIsNeedStop = bIsNeedStop;
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_alterShopTableInfo
            Author: Yangzheng
       Description: ���̻�����и���
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

        System.out.println(" ��ʼ��ȡ�̻���");

        // ��ȡ�̻����ݱ�
        DBHelper.getShopTables(shopTableList);

        for (ShopTable aShopTable : shopTableList)
        {
            // ����m_lat ���ÿ�����Ƿ����Ҫ���ӵ��ֶ�
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "m_lat");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "m_lat", "text", "g_lng");
            }

            // ����m_lng
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "m_lng");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "m_lng", "text", "m_lat");
            }

            // ����gps_lat
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "gps_lat");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "gps_lat", "text", "m_lng");
            }

            // ����gps_lng
            isExist = DBHelper.checkIsFieldInTable(aShopTable.getShopTableName(), "gps_lng");

            if (!isExist)
            {
                DBHelper.addFieldToTable(aShopTable.getShopTableName(), "gps_lng", "text", "gps_lat");
            }

            // ����circle�ֶ�
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
       Description: ��ȡ�̻�����
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
                System.out.println(" ��ǰ�̻�������Ϊ:" + aShopTable.getShopTableName());

                // �жϸ��̻����Ƿ������
                if (Config.TABLE_CRAWLED == (Config.TABLE_CRAWLED & aShopTable.getCrawledFlag()))
                {
                    aShopTable.getShopList().clear();
                    System.out.println(" �Ѿ�ץȡ���,����");
                    continue;
                }

                // ץȡһ���̻������������
                Coords_transformATableShopsCoords(aShopTable);

                // ���ó���(�̻���)���Ϊ��ץȡ���
                aShopTable.setCrawledFlag(Config.TABLE_CRAWLED);
                System.out.println(" ��ǰ�̻�������ץȡ���.");

                synchronized (shopTableList)
                {
                    // ��յ�ǰ���µ��̻�
                    aShopTable.getShopList().clear();
                    logger.info("ת����" + aShopTable.getShopTableName() + "�̻�������,��ո��̻������̻���");
                    InfoSave.SC_savePrcocess(shopTableList, Config.transformDat);
                    System.out.println(" �������");
                }
            }

            logger.info("�������ݱ���̻������Ѿ�ת�����!");
            System.out.println(" �������ݱ���̻������Ѿ�ת�����!");
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
       Description: ��ȡ�̻�����
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

        // ��ȡ��ǰ���������̻���Ϣ
        Coords_getAllShops(aShopTable);

        // ����ץȡ�߳�
        Coords_distributeCrawlTasks(aShopTable);

        // �������̶߳��̻����ݽ���ץȡ
        Coords_transformCoordsMultiply(aShopTable);

        // �ȴ�ץȡ���߳̽���
        Coords_waitForMultiTaskFinish(aShopTable);

        // �������ݱ��������Ϣ�����ݿ�
        Coords_saveATableShopsToDB(aShopTable);

    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_getAllShops
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
    private void Coords_getAllShops(ShopTable aShopTable)
    {
        // ����������Ʊ������Ʋ�����,Ӧ�����Ѿ�ȡ�������ݱ�������̻�,������ץȡ��
        if (Config.SHOPS_CRAWLED != (Config.SHOPS_CRAWLED & aShopTable.getCrawledFlag()))
        {
            DBHelper.getAllShops(aShopTable);

            // ��ȡ���ݺ����ñ�־λ
            aShopTable.setCrawledFlag(Config.SHOPS_CRAWLED);
        }
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_distributeCrawlTasks
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
     Function Name: TransformCoords.Coords_distributeTasks
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
     Function Name: TransformCoords.Coords_transformCoordsMultiply
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
    private void Coords_transformCoordsMultiply(ShopTable aShopTable)
    {
        System.out.println("��ʼ���̴߳����̻���Ϣ,ʵ��Ҫ�����Ĺ����߳�����" + subShopArrayList.length);
        // �����߳̽��ȱ�־λ
        aShopTable.setCoordsCrawlThreadCount(subShopArrayList.length);

        for (int i = 0; i < subShopArrayList.length; i++)
        {
            logger.info("��ʼת��" + aShopTable.getShopTableName() + "����");
            System.out.println("<<<<<<=======�����߳� " + (i + 1) + " ��ʼת���̻�������Ϣ========>>>>>>>");
            TransformShopsCoords transformShops = new TransformShopsCoords(aShopTable, subShopArrayList[i]);
            Thread TransformCoordsThread = new Thread(transformShops);
            TransformCoordsThread.start();
        }
    }

    /*****************************************************************************
     Function Name: TransformCoords.Coords_waitForMultiTaskFinish
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
                // ���������߳�ִ�У����߳�����7��
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
        logger.info("��ʼ����" + aShopTable.getShopTableName() + "��ת�����굽���ݿ�");
        DBHelper.saveShopsMultiCoordsToMySQL(aShopTable);
    }
}

/*****************************************************************************
    Class Name: TransformShopsCoords
        Author: Yangzheng
   Description: ץȡ������
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
            // �������̻�������������ץȡ��ת���洢
            Coords_transformATableShopsCoords(aShopTable, subShopList);

            // ��ʱ����,���ڱ������ڵ��̻�,��Ҫ������������
            int loopCount = 3;

            while (!Coords_checkATableShopsCrawled(aShopTable, subShopList) && !TransformCoords.Coords_getRunningFlag() && loopCount-- > 0)
            {
                // ��ץȡ�쳣���̻�����ץȡ
                logger.info("��ת���쳣���̻���������ץȡ");
                System.out.println("\n " + aShopTable.getShopTableName() + "���ϴ�ץȡʧ�ܵ��̻���������ץȡ");
                Coords_transformATableShopsCoords(aShopTable, subShopList);
            }

            aShopTable.decCoordsCrawlThreadCount();
            System.out.println("\n" + aShopTable.getShopTableName() + "���һ��ת���߳�...��ʣ" + aShopTable.getCoordsCrawlThreadCount() + "���߳�");
            System.out.println("���Ե�,�����ڼ���...");
        }
        catch (Exception ex)
        {
            logger.info("ת���̳߳�������" + ex);
            ex.printStackTrace();
        }
    }


    /*****************************************************************************
     Function Name: TransformShopsCoords.Coords_transformATableShopsCoords
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
    private void Coords_transformATableShopsCoords(ShopTable aShopTable, ArrayList<ShopInfo> subShopList)
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
                if (TransformCoords.Coords_getRunningFlag())
                {
                    System.out.println(" ����ת���߳���Ҫֹͣ�������˳�...");
                    break;
                }

                if (Config.SHOP_INFO_CRAWLED == (Config.SHOP_INFO_CRAWLED & aShop.getShopCrawledFlag()))
                {
                    continue;
                }

                // ����һ���̻�����
                Coords_transformAShopCoords(aShopTable, aShop);

                if (savePoint-- <= 0)
                {
                    // �����б���һ��
                    synchronized (shopTableList)
                    {
                        logger.info("�ﵽת���̻������ޣ�����һ�Ρ�");
                        InfoSave.SC_savePrcocess(shopTableList, Config.transformDat);
                        System.out.println(" �������");
                    }

                    savePoint = Config.SAVE_THRESHOLD;
                }
            }
        }
        catch (Exception ex)
        {
            // ���ñ��λΪδץȡ �ھ��庯�����Ѿ�����
            logger.error("ת������" + ex);
            aExShop.setShopCrawledFlag(Config.CLEAR_FLAG);
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
    Function Name: TransformCoords.Coords_transformAShopCoords
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

        // ����˳���ȡ�̻�����Ȧ
        String circle       = null;

        try
        {
            String searchCoordsUrl = apiBaseUrl  + apiParaKey + apiParaType +
                                     apiParaAddr + b_lat + "," + b_lng;         // URLEncoder.encode(shopAddr, "UTF-8");
            // �Ե�ַ��ѯ����
            Document transformCoordsDoc = CoordsHelper.getWebByJsoup(searchCoordsUrl);

            if (null == transformCoordsDoc)
            {
                System.out.println("ע��:������ܳ������⣬������ҳ�����仯������.\n");

                return;
            }

            if (transformCoordsDoc.select("lat").first().hasText())
            {
                midLatitude  = transformCoordsDoc.select("lat").first().text();
                midLongitude = transformCoordsDoc.select("lng").first().text();
                circle    = transformCoordsDoc.select("business").first().text();
            }

            // ȡ������
            if (null != midLatitude && null != midLongitude)
            {
                System.out.println(" �������IDΪ" + aShopInfo.getCityId() + ",�̻�Ϊ:" + aShopInfo.getCnName() + "������");
                System.out.println(" ������ȦΪ: " + circle);
                System.out.println(" �ٶ�γ��Ϊ: " + b_lat);
                System.out.println(" �ٶȾ���Ϊ: " + b_lng);
                System.out.println(" \n" );
                System.out.println(" �м�γ��Ϊ: " + midLatitude);
                System.out.println(" �м侭��Ϊ: " + midLongitude);

                // ��Ҫת��Ϊ���ֺ���ת��Ϊ�ַ���
                gpsLatitude  = Double.toString(Double.parseDouble(b_lat) * 2 - Double.parseDouble(midLatitude));
                gpsLongitude = Double.toString(Double.parseDouble(b_lng) * 2 - Double.parseDouble(midLongitude));

                System.out.println(" \n" );
                System.out.println(" ����γ��Ϊ: " + gpsLatitude);
                System.out.println(" ���򾭶�Ϊ: " + gpsLongitude);

                aShopInfo.setGpsLat(gpsLatitude);
                aShopInfo.setGpsLng(gpsLongitude);
                aShopInfo.setCircle(circle);

                // ����һ������ת��Ϊ��������
                Coords_transformBaiduCoordsToMars(aShopTable, aShopInfo);
                // ����ץȡ��ɱ��λ
                aShopInfo.setShopCrawledFlag(Config.SHOP_INFO_CRAWLED);
            }
            else
            {
                System.out.println(" û�л��" + cityName + shopAddr + shopName + "�İٶ�����!");
            }
        }
        catch (Exception ex)
        {
            logger.error("ת������" + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: TransformShopsCoords.Coords_transformBaiduCoordsToMars
            Author: Yangzheng
       Description: ���ٶ�����ת��Ϊ��������
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
            System.out.println(" ת���ٶ�����õ� " );
            System.out.println(" ����γ��Ϊ: " + marsLatitude);
            System.out.println(" ���Ǿ���Ϊ: " + marsLongitude);
        }
    }

    /*****************************************************************************
     Function Name: TransformShopsCoords.Coords_checkATableShopsCrawled
            Author: Yangzheng
       Description: ����̻����б��µ��̻��Ƿ������
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



