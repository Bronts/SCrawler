/******************************************************************************

          Copyright (C), 2009-2013, YangZheng. All Rights Reserved

 ******************************************************************************
       File Name: SCrawler.java
         Version: 1.10
          Author: Yangzheng
         Created: 2012/12/5
     Description: ���ࣺ����ץȡ����վ���
  --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/6       Yangzheng              Created
******************************************************************************/

import java.io.*;
import java.net.*;
import java.util.*;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.safety.Whitelist;

import java.sql.PreparedStatement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class SCrawler
{
    public static ArrayList<City> cityList  = new ArrayList<City>();

    public static DataBaseHelper DBHelper   = new DataBaseHelper();
    public static CrawlHelper SCrawlHelper  = new CrawlHelper();

    // ���ڱ������ݱ���̻� 2013.07
    public static ArrayList<ShopTable> shopTableList  = new ArrayList<ShopTable>();

    private static Logger logger = LogManager.getLogger(SCrawler.class.getName());

    /*****************************************************************************
     Function Name: SCrawler.main
            Author: Yangzheng
       Description: ���������
             Input: String[] args
            Output: NONE
            Return: public
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/6       Yangzheng         Created Function
    *****************************************************************************/
    public static void main(String[] args) throws Exception
    {
        if (args.length > 0 && args.length < 3)
        {
            SC_dealArgsOption(args);
        }
        else
        {
            SC_refeshScreen();
            SC_showOption();
            SC_dealOption();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_refeshScreen
            Author: Yangzheng
       Description: ��������
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/13       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_refeshScreen()
    {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealArgsOption
            Author: Yangzheng
       Description: ����������
             Input: String[] args
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_dealArgsOption(String[] args)
    {
        String action = args[0];
        String goal   = args[1];

        if (action.equals("crawl"))
        {
            if (goal.equals("baidu"))
            {
                // ץȡ�ٶ��̻�
                SC_crawlBaiduShops();
            }
            else if (goal.equals("dianping"))
            {
                // ץȡ�����̻�
                SC_crawlDianpingShops();
            }
            else if (goal.equals("fantong"))
            {
                // ץȡ��ͳ�̻�
                SC_crawlFantongShops();
            }
            else if (goal.equals("coordinate"))
            {
                // ��ȫ�����̻��ٶ�����
                SC_crawlCoords();
            }
            else
            {
                System.exit(0);
            }
        }
        else if (action.equals("continue"))
        {
            if (goal.equals("baidu"))
            {
                // ����ץȡ�ٶ��̻�
                SC_continueCrawlBaiduShops();
            }
            else if (goal.equals("dianping"))
            {
                // ����ץȡ�����̻�
                SC_continueCrawlDianpingShops();
            }
            else if (goal.equals("fantong"))
            {
                // ����ץȡ��ͳ�̻�
                SC_continueCrawlFantongShops();
            }
            else if (goal.equals("coordinate"))
            {
                // ������ȫȱʧ�ٶ�����
                SC_continueCrawlCoords();
            }
            else if (goal.equals("transform"))
            {
                // ����ת���ٶ�����ΪGPS����ͻ�������
                SC_continueTransformCoords();
            }
            else
            {
                System.exit(0);
            }
        }
        else if (action.equals("transform"))
        {
            if (goal.equals("coordinate"))
            {
                // ת���ٶ�����ΪGPS����ͻ�������
                SC_transformCoords();
            }
            else
            {
                System.exit(0);
            }
        }
    }


    /*****************************************************************************
     Function Name: SCrawler.SC_showOption
            Author: Yangzheng
       Description: ��ʾѡ��
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/13       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showOption()
    {
        SC_showHeader();
        SC_showEnterOption();
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showHeader
            Author: Yangzheng
       Description: ��ʾ˵��ͷ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showHeader()
    {
        System.out.println("\n");
        System.out.println(" *****************************************************************************");
        System.out.println(" *                        iShiyu���� Version 0.6                             *");
        System.out.println(" *  -----------------------------------------------------------------------  *");
        System.out.println(" *          ˵�� : ��ѡ��ǰ�����֡�������һ��.                               *");
        System.out.println(" *                 ����Ctrl + C���˳������еĳ���(����������Ч).             *");
        System.out.println(" *                 �����ļ��ڳ����б�ץȡ������,ץȡ���˳����޷�����.        *");
        System.out.println(" *****************************************************************************");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showConfigHead
            Author: Yangzheng
       Description: ��ʾ����˵��
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showConfigHead()
    {
        System.out.println(" ***************************************************************************** ");
        System.out.println(" *                         ��ʳ���� Version 0.7                               * ");
        System.out.println(" *  ----------------------------------------------------------------------   * ");
        System.out.println(" *                   ˵��������ѡ�ǰ���ֽ�����һ��                            * ");
        System.out.println(" *                        ���õġ�������������ȷ�ϣ����򽫻ᵼ�±������         * ");
        System.out.println(" ***************************************************************************** ");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showEnterOption
            Author: Yangzheng
       Description: ��ʾ��һ��ѡ��
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showEnterOption()
    {
        System.out.println("   1. ץȡ��վ�̻�����.");
        System.out.println("   2. �̻����괦��.");
        System.out.println("   3. ����ץȡ����.");
        System.out.println("   0. �뿪.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealOption
            Author: Yangzheng
       Description: ����ѡ�������
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/13       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_dealOption()
    {
        char ch;

        try
        {
            logger.entry();

            if ((ch = (char)System.in.read()) != ' ')
            {
                // �����س���
                System.in.skip(2);

                switch (ch)
                {
                        // ץȡ��վ�̻�����
                    case '1':
                    {
                        SC_refeshScreen();
                        SC_crawlShopInfo();
                        break;
                    }

                    // �̻����괦��.
                    case '2':
                    {
                        SC_refeshScreen();
                        SC_processCoords();
                        break;
                    }

                    // ������ز���
                    case '3':
                    {
                        SC_refeshScreen();
                        SC_setConfig();
                        break;
                    }

                    case '0':
                    {
                        break;
                    }

                    default:
                    {
                        SC_showOption();
                        System.out.println("�������ѡ����!");
                        SC_dealOption();
                        break;
                    }
                }
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_crawlShopInfo
            Author: Yangzheng
       Description: ץȡ��վѡ��
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_crawlShopInfo()
    {
        SC_showHeader();
        SC_showCrawlOption();
        SC_dealCrawlOption();
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_processCoords
            Author: Yangzheng
       Description: ��������
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_processCoords()
    {
        SC_showHeader();
        SC_showCoordsOption();
        SC_dealCoordsOption();
    }


    /*****************************************************************************
     Function Name: SCrawler.SC_setConfig
            Author: Yangzheng
       Description: ����ץȡ����
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_setConfig()
    {
        SC_showConfigHead();
        SC_showConfigOption();
        SC_dealConfigOption();
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showConfigOption
            Author: Yangzheng
       Description: ��ʾѡ����
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showConfigOption()
    {
        System.out.println("   1. �������ݿ����.");
        System.out.println("   2. �ָ����ݿ����ΪĬ��.");
        System.out.println("   3. ����ͼƬ�����Ŀ¼.");
        System.out.println("   4. �ָ�ͼƬ�����Ŀ¼ΪĬ��.");
        System.out.println("   5. �鿴��ǰ���õĲ���.");
        System.out.println("   0. ����.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showCrawlOption
            Author: Yangzheng
       Description: ��ʾץȡѡ��
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showCrawlOption()
    {
        System.out.println("   1. ץȡ�ٶ��������.");
        System.out.println("   2. �����ϴ�ץȡ�ٶ��������.");
        System.out.println("   3. ץȡ���ڵ�����(���)����.");
        System.out.println("   4. �����ϴ�ץȡ���ڵ�����(���)����.");
        System.out.println("   5. ץȡ��ͳ������.");
        System.out.println("   6. �����ϴ�ץȡ��ͳ������.");
        System.out.println("   0. ����.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showCoordsOption
            Author: Yangzheng
       Description: ��ʾ���괦��ѡ��
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showCoordsOption()
    {
        System.out.println("   1. �����̻��ٶ�����.");
        System.out.println("   2. �����ϴν��ȸ����̻��ٶ�����.");
        System.out.println("   3. ת���̻��ٶ�����ΪGPS���������.");
        System.out.println("   4. �����ϴν���ת���ٶ�����ΪGPS���������.");
        System.out.println("   0. ����.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealConfigOption
            Author: Yangzheng
       Description: ����ѡ����
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_dealConfigOption()
    {
        char ch;

        try
        {
            if ((ch = (char)System.in.read()) != ' ')
            {
                // �����س���
                System.in.skip(2);

                switch (ch)
                {
                    case '1':
                    {
                        SC_setDateBase();
                        break;
                    }

                    case '2':
                    {
                        SC_setDefaultDateBase();
                        break;
                    }

                    case '3':
                    {
                        SC_setImgRootDir();
                        break;
                    }

                    case '4':
                    {
                        SC_setDefaultImgRootDir();
                        break;
                    }

                    case '5':
                    {
                        SC_showConfig();
                        break;
                    }

                    case '0':
                    {
                        SC_refeshScreen();
                        SC_showOption();
                        SC_dealOption();
                        break;
                    }

                    default:
                    {
                        System.out.println("\n   �������ѡ����!");
                        SC_dealConfigOption();
                        break;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealCrawlOption
            Author: Yangzheng
       Description: ����ץȡѡ��
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_dealCrawlOption()
    {
        char ch;

        try
        {
            logger.entry();

            if ((ch = (char)System.in.read()) != ' ')
            {
                // �����س���
                System.in.skip(2);

                switch (ch)
                {
                    case '1':
                    {
                        // ץȡ�ٶ��̻�
                        SC_crawlBaiduShops();
                        break;
                    }

                    case '2':
                    {
                        // ����ץȡ�ɰٶ��̻�
                        SC_continueCrawlBaiduShops();
                        break;
                    }

                    case '3':
                    {
                        // ץȡ�����̻�
                        SC_crawlDianpingShops();
                        break;
                    }

                    case '4':
                    {
                        // ����ץȡ�����̻�
                        SC_continueCrawlDianpingShops();
                        break;
                    }

                    case '5':
                    {
                        // ץȡ��ͳ�̻�
                        SC_crawlFantongShops();
                        break;
                    }

                    case '6':
                    {
                        // ����ץȡ��ͳ�̻�
                        SC_continueCrawlFantongShops();
                        break;
                    }

                    case '0':
                    {
                        SC_refeshScreen();
                        SC_showOption();
                        SC_dealOption();
                        break;
                    }

                    default:
                    {
                        SC_showHeader();
                        SC_showCrawlOption();
                        System.out.println("�������ѡ����!");
                        SC_dealCrawlOption();
                        break;
                    }
                }
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealCoordsOption
            Author: Yangzheng
       Description: �������괦��ѡ��
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_dealCoordsOption()
    {
        char ch;

        try
        {
            logger.entry();

            if ((ch = (char)System.in.read()) != ' ')
            {
                // �����س���
                System.in.skip(2);

                switch (ch)
                {
                    case '1':
                    {
                        // ��ȫ�����̻��ٶ�����
                        SC_crawlCoords();
                        break;
                    }

                    case '2':
                    {
                        // ������ȫȱʧ�ٶ�����
                        SC_continueCrawlCoords();
                        break;
                    }

                    case '3':
                    {
                        // ת���ٶ�����ΪGPS����ͻ�������
                        SC_transformCoords();
                    }

                    case '4':
                    {
                        // ����ת���ٶ�����ΪGPS����ͻ�������
                        SC_continueTransformCoords();
                    }

                    // ����
                    case '0':
                    {
                        SC_refeshScreen();
                        SC_showOption();
                        SC_dealOption();
                        break;
                    }

                    // ��ѡ��
                    default:
                    {
                        SC_showHeader();
                        SC_showCoordsOption();
                        System.out.println("�������ѡ����!");
                        SC_dealCoordsOption();
                        break;
                    }
                }
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops
            Author: Yangzheng
       Description: ץȡ�ٶ��̻���Ϣ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_crawlBaiduShops()
    {
        try
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ��ʼץȡ�ٶ��������...");
            System.out.println(" �ٶ������վ��ͣ��Ӫ,��ʱ������...");
            System.out.println(" �����������ѡ����!");
            SC_showHeader();
            SC_showCrawlOption();
            SC_dealCrawlOption();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlBaiduShops
            Author: Yangzheng
       Description: ����ץȡ�ٶ��̻���Ϣ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_continueCrawlBaiduShops()
    {
        try
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ��ʼ����ץȡ�ٶ��������...");
            System.out.println(" �ٶ������վ��ͣ��Ӫ,��ʱ������...");
            System.out.println(" �����������ѡ����!");
            SC_showHeader();
            SC_showCrawlOption();
            SC_dealCrawlOption();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_crawlDianpingShops
            Author: Yangzheng
       Description: ץȡ�����̻���Ϣ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_crawlDianpingShops()
    {
        try
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ��ʼץȡ���ڵ�������.");
            // �����ػ�����
            SC_startDaemonThread(cityList, Config.dianpingDat);
            // ��ȡ���ڵ�������
            CrawlDianping Dianping = new CrawlDianping(cityList, false);
            Thread dianpingThread = new Thread(Dianping);
            dianpingThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlDianpingShops
            Author: Yangzheng
       Description: ����ץȡ�����̻���Ϣ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_continueCrawlDianpingShops()
    {
        try
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ��ʼ����ץȡ���ڵ�������.");
            System.out.println(" ���ڶ�ȡ�����ļ�,���Ե�....");

            // �ָ������ļ�
            cityList = InfoSave.SC_recoverProcess(Config.dianpingDat);

            if (null == cityList)
            {
                System.out.println(" �����ļ�������,�����������ѡ����!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // �����ػ�����
                SC_startDaemonThread(cityList, Config.dianpingDat);
                CrawlDianping Dianping = new CrawlDianping(cityList, true);
                Thread dianpingThread  = new Thread(Dianping);
                dianpingThread.start();
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_crawlFantongShops
            Author: Yangzheng
       Description: ץȡ��ͳ�̻���Ϣ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_crawlFantongShops()
    {
        try
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ��ʼץȡ��ͳ����.");
            // �����ػ�����
            SC_startDaemonThread(cityList, Config.fantongDat);
            // ��ȡ��ͳ����
            CrawlFantong Fantong = new CrawlFantong(cityList, false);
            Thread fantongThread = new Thread(Fantong);
            fantongThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlFantongShops
            Author: Yangzheng
       Description: ����ץȡ��ͳ�̻���Ϣ
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_continueCrawlFantongShops()
    {
        try
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ��ʼ����ץȡ��ͳ����.");
            System.out.println(" ���ڶ�ȡ�����ļ�,���Ե�....");

            // �ָ������ļ�
            cityList = InfoSave.SC_recoverProcess(Config.fantongDat);

            if (null == cityList)
            {
                System.out.println(" �����ļ�������,�����������ѡ����!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // �����ػ�����
                SC_startDaemonThread(cityList, Config.fantongDat);
                CrawlFantong Fantong = new CrawlFantong(cityList, true);
                Thread fantongThread = new Thread(Fantong);
                fantongThread.start();
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_crawlCoords
            Author: Yangzheng
       Description: ��ȫ�̻����ٶȣ�����
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_crawlCoords()
    {
        try
        {
            logger.entry();
            // �����̻�����
            System.out.println("\n");
            System.out.println(" ��ʼ������������.");
            // �����ػ�����
            SC_startDaemonThread(shopTableList, Config.coordsDat);

            CrawlCoords AllCoords = new CrawlCoords(shopTableList, false);
            Thread coordsThread   = new Thread(AllCoords);
            coordsThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlCoords
            Author: Yangzheng
       Description: ������ȫ����
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_continueCrawlCoords()
    {
        try
        {
            logger.entry();
            // �������ȸ����̻�����
            System.out.println("\n");
            System.out.println(" ��ʼ����������������.");
            System.out.println(" ���ڶ�ȡ�����ļ�,���Ե�....");

            // �ָ������ļ�
            shopTableList = InfoSave.SC_recoverProcess(Config.coordsDat);

            if (null == shopTableList)
            {
                System.out.println(" �����ļ�������,�����������ѡ����!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // �����ػ�����
                SC_startDaemonThread(shopTableList, Config.coordsDat);
                CrawlCoords AllCoords = new CrawlCoords(shopTableList, true);
                Thread coordsThread   = new Thread(AllCoords);
                coordsThread.start();
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_transformCoords
            Author: Yangzheng
       Description: ת������
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_transformCoords()
    {
        try
        {
            logger.entry();
            // ת���̻�����
            System.out.println("\n");
            System.out.println(" ��ʼת����������.");
            // �����ػ�����
            SC_startDaemonThread(shopTableList, Config.transformDat);

            TransformCoords transAllCoords = new TransformCoords(shopTableList, false);
            Thread coordsThread   = new Thread(transAllCoords);
            coordsThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueTransformCoords
            Author: Yangzheng
       Description: ����ת������
             Input: NONE
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/8/9       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_continueTransformCoords()
    {
        try
        {
            logger.entry();
            // ��������ת���̻�����
            System.out.println("\n");
            System.out.println(" ��ʼ����������������.");
            System.out.println(" ���ڶ�ȡ�����ļ�,���Ե�....");

            // �ָ������ļ�
            shopTableList = InfoSave.SC_recoverProcess(Config.transformDat);

            if (null == shopTableList)
            {
                System.out.println(" �����ļ�������,�����������ѡ����!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // �����ػ�����
                SC_startDaemonThread(shopTableList, Config.transformDat);

                TransformCoords transAllCoords = new TransformCoords(shopTableList, true);
                Thread coordsThread   = new Thread(transAllCoords);
                coordsThread.start();

                logger.exit();
            }
        }
        catch (Exception ex)
        {
            logger.error("����ѡ�����." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_setDateBase
            Author: Yangzheng
       Description: �������ݿ����
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_setDateBase()
    {
        try
        {
            Scanner scanIn = new Scanner(System.in);

            System.out.print("   �����롾���ݿ�����:");
            String datebase = scanIn.nextLine();            //��ȡ���������һ�У��Իس�����Ϊ�������룩

            System.out.print("   ������������ݿ⡾�û�����:");
            String user = scanIn.nextLine();

            System.out.print("   ������������ݿ⡾�û����롿:");
            String pass = scanIn.nextLine();

            System.out.println("\n");
            System.out.println("   ������ġ����ݿ�����Ϊ:       " + datebase);
            System.out.println("   ����������ݿ⡾�û�����Ϊ:   " + user);
            System.out.println("   ����������ݿ⡾�û����롿Ϊ: " + pass);
            System.out.println("\n");

            System.out.println("   ȷ���밴��1�������������밴��2�����뿪�밴��0��.");

            char ch = (char)System.in.read();

            System.in.skip(2); // �����س���

            if ('1' == ch)
            {
                DataBaseHelper.DB_setDatabase(datebase);
                DataBaseHelper.DB_setUser(user);
                DataBaseHelper.DB_setPass(pass);

                SC_refeshScreen();
                System.out.println("   ���ݿ����á��Ѿ����桿!");
                SC_setConfig();
            }
            else if ('2' == ch)
            {
                SC_setDateBase();
            }
            else if ('0' == ch)
            {
                SC_refeshScreen();
                System.out.println("   ���ݿ����á�δ�����ġ�!");
                SC_setConfig();
            }
            else
            {
                SC_refeshScreen();
                System.out.println("   ���ݿ�����δ������!����ȷ���롾��ѡ���֡�����������!");
                SC_setConfig();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_setDefaultDateBase
            Author: Yangzheng
       Description: �ָ�����Ĭ������
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_setDefaultDateBase()
    {
        DataBaseHelper.DB_setDatabase("shiyu");
        DataBaseHelper.DB_setUser("root");
        DataBaseHelper.DB_setPass("123");

        SC_refeshScreen();
        System.out.println("   ���ݿ������Ѿ����ָ�Ĭ��ֵ��!");
        SC_setConfig();

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_setImgRootDir
            Author: Yangzheng
       Description: ����ͼƬ����Ŀ¼
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_setImgRootDir()
    {
        try
        {
            Scanner scanIn = new Scanner(System.in);

            System.out.println("   ע��:��������·��ʹ��'/'�ָĿ¼��β�����'/'");
            System.out.print("   �����롾ͼƬ�����Ŀ¼��: ");
            String imgPath = scanIn.nextLine();            //��ȡ���������һ�У��Իس�����Ϊ�������룩

            System.out.println("   ������ġ�ͼƬ�����Ŀ¼��Ϊ: " + imgPath);
            System.out.println("\n");
            System.out.println("   ȷ���밴��1�������������밴��2�����뿪�밴��0��.");

            char ch = (char)System.in.read();

            System.in.skip(2); // �����س���

            if ('1' == ch)
            {
                Config.IMG_DIR = imgPath;
                SC_refeshScreen();
                System.out.println("   ͼƬ��Ŀ¼���ѱ��桿!");
                SC_setConfig();
            }
            else if ('2' == ch)
            {
                SC_setImgRootDir();
            }
            else if ('0' == ch)
            {
                SC_refeshScreen();
                System.out.println("   ͼƬ��Ŀ¼���á�δ�����ġ�!");
                SC_setConfig();
            }
            else
            {
                SC_refeshScreen();
                System.out.println("   ͼƬ��Ŀ¼����δ������!����ȷ���롾��ѡ���֡�����������!");
                SC_setConfig();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_setDefaultImgRootDir
            Author: Yangzheng
       Description: ����Ĭ��ͼƬ����Ŀ¼
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_setDefaultImgRootDir()
    {
        Config.IMG_DIR = "E:/IMG";

        SC_refeshScreen();
        System.out.println("   ͼƬ��Ŀ¼�����Ѿ����ָ�Ĭ��ֵ��!");
        SC_setConfig();

    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showConfig
            Author: Yangzheng
       Description: ��ʾ��ǰ����
             Input: NONE
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_showConfig()
    {
        SC_refeshScreen();
        SC_showConfigHead();
        System.out.println("   ��ǰ���õġ����ݿ�����Ϊ:       " + DataBaseHelper.DB_getDatabase());
        System.out.println("   ��ǰ���õ����ݿ⡾�û�����Ϊ:   " + DataBaseHelper.DB_getUser());
        System.out.println("   ��ǰ���õ����ݿ⡾�û����롿Ϊ: " + DataBaseHelper.DB_getPass());
        System.out.println("   ��ǰ���õġ�ͼƬ�����Ŀ¼��Ϊ: " + Config.IMG_DIR);
        System.out.println("\n");
        System.out.println("   �����밴��0��.");

        try
        {
            char ch = (char)System.in.read();

            System.in.skip(2); // �����س���

            if ('0' == ch)
            {
                SC_refeshScreen();
                SC_setConfig();
            }
            else
            {
                SC_refeshScreen();
                SC_setConfig();
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_startDaemonThread
            Author: Yangzheng
       Description: �����ػ����̣������˳�ʱ�������
             Input: ArrayList<City> cityList
                    String savedDatName
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/13       Yangzheng         Created Function
    *****************************************************************************/
    private static void SC_startDaemonThread(Object processList, String savedDatName)
    {
        CtrlCExit ctrlcExit = new CtrlCExit(processList, savedDatName);
        //AutoSave  autoSave  = new AutoSave(cityList, savedDatName);
        Thread exitThread = new Thread(ctrlcExit);
        exitThread.setName("DaemonThread");
        exitThread.start();
    }


    /*
    /*****************************************************************************
     Function Name: SCrawler.SC_recoverProcess
            Author: Yangzheng
       Description: �ָ�ץȡ����
             Input: String savedDatname
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************
    @SuppressWarnings("unchecked")
    private static Object SC_recoverProcess(String savedDatname)
    {
        Object recoverList = null;

        try
        {
            recoverList = SC_readPrcocess(savedDatname);
        }
        catch (Exception ex)
        {
            logger.error("�����ļ��ָ�ʧ��" + ex);
            ex.printStackTrace();
        }

        return recoverList;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_readPrcocess
            Author: Yangzheng
       Description: ��ȡץȡ����
             Input: String savedDatName
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************
    @SuppressWarnings("unchecked")
    private static Object SC_readPrcocess(String savedDatName) throws IOException
    {
        Object object = null;
        File file = new File(savedDatName);

        if (!file.exists())
        {
            return null;
        }

        FileInputStream fileIns = new FileInputStream(file);
        ObjectInputStream objIns = new ObjectInputStream(fileIns);

        try
        {
            object = objIns.readObject();
        }
        catch (ClassNotFoundException e)
        {
            logger.error("�����ļ���ȡʧ��");
            e.printStackTrace();
        }

        objIns.close();
        fileIns.close();

        return object;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_savePrcocess
            Author: Yangzheng
       Description: ����ץȡ����
             Input: ArrayList<City> cityList
                    String savedDatName
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/20       Yangzheng         Created Function
    *****************************************************************************
    @SuppressWarnings("unchecked")
    public static void SC_savePrcocess(Object objectList, String savedDatName) throws IOException
    {
        File file = new File(savedDatName);

        if (file.exists())
        {
            file.delete();
            logger.info("ɾ���Ѵ��ڵĽ����ļ�");
        }

        FileOutputStream fileOuts = new FileOutputStream(file);
        ObjectOutputStream objOuts = new ObjectOutputStream(fileOuts);
        objOuts.writeObject(objectList);
        objOuts.flush();
        objOuts.close();
        fileOuts.close();

        return;
    }
    */


    // �˴�Ӧ�ü���ѡ��˵�

    // ��ȡsohu��ҳͼƬ���ݣ�����ʹ��
    // SCrawler Sohu = new SCrawler();
    // Sohu.getSohuImgsTest();

    // ��ȡ��ȡ�ٶ���߳�������(��ȡ�˳��������Լ�baidu����ĳ���ID)
    // SCrawler Baidu= new SCrawler();
    // Baidu.getBaiduCity();

    // ��ȡ�ٶ��������
    // CrawlBaidu Baidu = new CrawlBaidu(baiduUrl);
    // Baidu.Baidu_getShopsData();

    // ��ȡ���ڵ�������
    // CrawlDianping Dianping = new CrawlDianping(dianpingUrl, cityList);
    // Dianping.Dianping_getShopsData();

    // ��ȡ��Ͱ������
    // CrawlFantong Fantong = new CrawlFantong(fantongUrl);
    // Fantong.Fantong_getRestaurantsData();

    // ץȡ�������
    // CrawlHongkong Hongkong = new CrawlHongkong(dianpingUrl);
    // Hongkong.Hongkong_updateLatAndLngInfo();
    // Hongkong.Hongkong_getShopsLogos();
    // Hongkong.Hongkong_getShopDishes();
}

/*****************************************************************************
    Class Name: Config
        Author: Yangzheng
   Description: ȫ�ֲ�����
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/2/25       Yangzheng          Created Class
*****************************************************************************/
class Config
{
    public static final int CLEAR_FLAG       = 0;

    // ��ǳ�����Ϣ�Ƿ�ץȡ���
    public static final int CITY_CRAWLED     = 1;
    public static final int REGIONS_CRAWLED  = 2;
    public static final int SHOPURLS_CRAWLED = 4;
    public static final int DISHES_CRAWLED   = 8;

    // ����̻�����Ϣ�Ƿ�ץȡ���
    public static final int TABLE_CRAWLED    = 1;
    public static final int SHOPS_CRAWLED    = 2;

    // ����̻���Ϣ�Ƿ������
    public static final int TABLE_PROCESSED  = 1;
    public static final int SHOPS_PROCESSED  = 2;
    public static final int SHOP_PROCESSED   = 4;

    // ����̻���Ϣ�Ƿ�ץȡ���
    public static final int SHOP_INFO_CRAWLED = 1;
    public static final int SHOP_LOGO_CRAWLED = 2;

    public static final int SAVE_THRESHOLD  = 500;      // �����̻�������
    public static final int PAUSE_THRESHOLD = 180;      // ��ֹ��������ƣ���ͣ����
    public static final int ERROR_THRESHOLD = 5;        // ץȡ��վ������������

    public static final int CRAWL_THREAD_COUNT = 10;     // �����̵߳�Ĭ����

    // �������㳣��
    public static final double COORDS_PI    = 3.14159265358979324 * 3000.0 / 180.0;

    public static final String baiduDat     = "baiduCity.dat";
    public static final String dianpingDat  = "dianpingCity.dat";
    public static final String fantongDat   = "fantongCity.dat";
    public static final String coordsDat    = "coords.dat";
    public static final String transformDat = "transformCoords.dat";

    public static final String baiduUrl     = "http://s.baidu.com";
    public static final String dianpingUrl  = "http://m.dianping.com/citylist";
    public static final String fantongUrl   = "http://www.fantong.com/more-cities/";

    // ͼƬ�����Ŀ¼
    public static String IMG_DIR        = "E:/IMG";

}

/*****************************************************************************
    Class Name: CtrlCExit
        Author: Yangzheng
   Description: �˳���
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/2/25       Yangzheng          Created Class
*****************************************************************************/
class CtrlCExit implements Runnable
{
    private boolean bExit = false;
    private String savedDatName = null;
    Object processList = null;

    private static Logger logger = LogManager.getLogger(CtrlCExit.class.getName());

    public CtrlCExit(Object processList, String savedDatName)
    {
        this.processList = processList;
        this.savedDatName = savedDatName;
        Runtime.getRuntime().addShutdownHook(new ExitHandler());
    }

    public void run()
    {
        while (!bExit)
        {
            // Do some thing
        }
    }

    private class ExitHandler extends Thread
    {
        public ExitHandler()
        {
            super("Exit Handler");
        }
        public void run()
        {
            logger.entry();
            System.out.println("\n");
            System.out.println(" ׼���������ݣ�����ֹͣ�����̣߳������˳�����...");

            try
            {
                // ��������������Ҫ�������
                System.out.println("\n");
                System.out.println(" ���ڱ�����...");

                // ���������߳�����ֹͣ��־λ
                CrawlCoords.Coords_setRunningFlag(true);
                TransformCoords.Coords_setRunningFlag(true);

                // �ػ��߳���ͣһ�룬�ȴ����߳��˳�
                Thread.sleep(1000);

                // �������
                InfoSave.SC_savePrcocess(processList, savedDatName);
                System.out.println("\n");
                System.out.println(" ......");
                System.out.println("\n");
                System.out.println(" �������,��������˳�.");
                //Thread.sleep(1000);
            }
            catch (Exception ex)
            {
                logger.error("����ʧ��" + ex);
                ex.printStackTrace();
            }

            bExit = true;
            logger.exit();
        }
    }
}


/*****************************************************************************
    Class Name: InfoSave
        Author: Yangzheng
   Description: ������
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/9/6        Yangzheng          Created Class
*****************************************************************************/
class InfoSave
{
    private static Logger logger = LogManager.getLogger(InfoSave.class.getName());

    /*****************************************************************************
     Function Name: InfoSave.SC_recoverProcess
            Author: Yangzheng
       Description: �ָ�����Ľ����ļ�
             Input: T recoverList
                    String savedDataName
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/9/6       Yangzheng         Created Function
    *****************************************************************************/
    public static <T> T SC_recoverProcess(String savedDataName)
    {
        T recoverList = null;

        try
        {
            recoverList = (T)SC_readPrcocess(savedDataName);
        }
        catch (Exception ex)
        {
            logger.error("�����ļ��ָ�ʧ��" + ex);
            ex.printStackTrace();
        }

        return recoverList;
    }

    /*****************************************************************************
     Function Name: InfoSave.SC_readPrcocess
            Author: Yangzheng
       Description: ��ȡ����
             Input: String savedDatName
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/9/6       Yangzheng         Created Function
    *****************************************************************************/
    private static Object SC_readPrcocess(String savedDatName) throws IOException
    {
        Object object = null;
        File file = new File(savedDatName);

        if (!file.exists())
        {
            return null;
        }

        FileInputStream fileIns = new FileInputStream(file);
        ObjectInputStream objIns = new ObjectInputStream(fileIns);

        try
        {
            object = objIns.readObject();
        }
        catch (ClassNotFoundException ex)
        {
            logger.error("�����ļ���ȡʧ��" + ex);
            ex.printStackTrace();
        }

        objIns.close();
        fileIns.close();

        return object;
    }


    /*****************************************************************************
     Function Name: InfoSave.SC_savePrcocess
            Author: Yangzheng
       Description: �������
             Input: T saveList
                    String saveDatName
            Output: NONE
            Return: public
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/9/6       Yangzheng         Created Function
    *****************************************************************************/
    public static <T> void SC_savePrcocess(T saveList, String saveDatName) throws IOException
    {
        File file = new File(saveDatName);

        if (file.exists())
        {
            file.delete();
            logger.info("ɾ���Ѵ��ڵĽ����ļ�");
        }

        FileOutputStream fileOuts = new FileOutputStream(file);
        ObjectOutputStream objOuts = new ObjectOutputStream(fileOuts);
        objOuts.writeObject(saveList);
        objOuts.flush();
        objOuts.close();
        fileOuts.close();
    }
}


/*****************************************************************************
    Class Name: AutoSave
        Author: Yangzheng
   Description: �Զ������� ץȡ��ÿ���1һСʱ����һ�ν���
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/2/25       Yangzheng          Created Class
*****************************************************************************/
class AutoSave implements Runnable
{
    private String savedDatName = null;
    ArrayList<Object> processList = null;

    private static Logger logger = LogManager.getLogger(AutoSave.class.getName());

    public AutoSave(ArrayList<Object> processList, String savedDatName)
    {
        this.processList = processList;
        this.savedDatName = savedDatName;
    }
    public void run()
    {
        try
        {
            System.out.println(" �������Զ������߳�...");
            logger.info(" �������Զ������߳�...");

            while (true)
            {
                //��������������Ҫ�������
                logger.entry();
                Thread.sleep(3600000);

                synchronized (processList)
                {
                    System.out.println(" ��ʼ�Զ��������...");
                    logger.info("������ÿ��һСʱ���н��ȱ��档");
                    InfoSave.SC_savePrcocess(processList, savedDatName);
                    System.out.println(" �������");
                }

                logger.exit();
            }
        }
        catch (Exception ex)
        {
            logger.error("�ػ��̱߳���ʧ��" + ex);
            ex.printStackTrace();
        }
    }
}

/*****************************************************************************
    Class Name: City
        Author: Yangzheng
   Description: ������ ���ڱ��������Ϣ
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/2/25       Yangzheng          Created Class
*****************************************************************************/
class City implements Serializable
{
    // Ҫ����ָ���¼������¼���Ȧ�����ݽṹ

    private int     cityId;         // ����ID������
    private int     baiduCityId;    // baidu����ID
    private int     dianpingCityId; // dianping����ID
    private int     shopsNum;       // �̻�����
    private String  enName;         // ƴ����
    private String  cnName;         // ������
    private String  cityUrl;        // ����ҳ������

    private ArrayList<AreaInfo> areaInfoList;
    private ArrayList<ShopInfo> shopsInfoList;   // �̻������б�

    private int crawledFlag;              // ��������̻��Ƿ�ץȡ���
    private int dishCrawlThreadCount = 0; // ץȡͼƬ�߳���

    public City()
    {
        this.crawledFlag = 0;
        this.shopsNum    = 0;
        this.areaInfoList  = new ArrayList<AreaInfo>();
        this.shopsInfoList = new ArrayList<ShopInfo>();
    }

    public int getCrawledFlag()
    {
        return crawledFlag;
    }
    public void setCrawledFlag(int crawledFlag)
    {
        if (0 != crawledFlag)
        {
            // ���ñ�־λ
            this.crawledFlag |= crawledFlag;
        }
        else
        {
            // ȥ����־λ
            this.crawledFlag &= ~crawledFlag;
        }
    }

    // ͼƬ�����߳���
    public int getDishCrawlThreadCount()
    {
        return dishCrawlThreadCount;
    }
    public void setDishCrawlThreadCount(int dishCrawlThreadCount)
    {
        this.dishCrawlThreadCount = dishCrawlThreadCount;
    }

    public synchronized void decDishCrawlThreadCount()
    {
        this.dishCrawlThreadCount--;
    }

    public int getCityId()
    {
        return cityId;
    }
    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }

    public int getDianpingCityId()
    {
        return dianpingCityId;
    }
    public void setDianpingCityId(int dianpingCityId)
    {
        this.dianpingCityId = dianpingCityId;
    }

    public int getBaiduCityId()
    {
        return baiduCityId;
    }
    public void setBaiduCityId(int baiduCityId)
    {
        this.baiduCityId = baiduCityId;
    }

    public int getShopsNum()
    {
        return shopsNum;
    }
    public void setShopsNum(int shopsNum)
    {
        this.shopsNum = shopsNum;
    }
    public void addAShopNum()
    {
        this.shopsNum++;
    }

    public String getEnName()
    {
        return enName;
    }
    public void setEnName(String enName)
    {
        this.enName = enName;
    }

    public String getCnName()
    {
        return cnName;
    }
    public void setCnName(String cnName)
    {
        this.cnName = cnName;
    }

    public String getCityUrl()
    {
        return cityUrl;
    }
    public void setCityUrl(String cityUrl)
    {
        this.cityUrl = cityUrl;
    }
    // �����µ���Ȧ��Ϣ��ȡ
    public ArrayList<AreaInfo> getAreaInfoList()
    {
        return areaInfoList;
    }
    public void setAreaInfo(AreaInfo aAreaInfo)
    {
        this.areaInfoList.add(aAreaInfo);
    }

    public ArrayList<ShopInfo> getShopList()
    {
        return shopsInfoList;
    }
    public void addAShop(ShopInfo aShop)
    {
        this.shopsInfoList.add(aShop);
        this.shopsNum++;
    }
}

/*****************************************************************************
    Class Name: ShopTable
        Author: Yangzheng
   Description: �̻����� �������洢�̻�
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/6/25       Yangzheng          Created Class
*****************************************************************************/
class ShopTable implements Serializable
{
    // Ҫ����ָ���¼������¼���Ȧ�����ݽṹ

    private String tableName;                   // ��ID������
    private String cityName;                    // ��ǰ��ĳ�����

    private int cityId;                         // ��Ӧ��ĳ���ID
    private int shopsNum;                       // �̻�����

    private ArrayList<ShopInfo> shopsInfoList;  // �̻������б�

    private int crawledFlag;                    // ��������̻��Ƿ�ץȡ���
    private int CrawlThreadCount = 0;           // ץȡ�����߳���

    public ShopTable()
    {
        this.crawledFlag   = 0;
        this.shopsNum      = 0;
        this.shopsInfoList = new ArrayList<ShopInfo>();
    }

    public String getShopTableName()
    {
        return tableName;
    }
    public void setShopTableName(String tableName)
    {
        this.tableName = tableName;
    }
    public String getCityName()
    {
        return cityName;
    }
    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }
    public int getCityId()
    {
        return cityId;
    }
    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }

    public int getCrawledFlag()
    {
        return crawledFlag;
    }
    public void setCrawledFlag(int crawledFlag)
    {
        if (0 != crawledFlag)
        {
            // ���ñ�־λ
            this.crawledFlag |= crawledFlag;
        }
        else
        {
            // ȥ����־λ
            this.crawledFlag &= ~crawledFlag;
        }
    }

    // ��������߳���
    public int getCoordsCrawlThreadCount()
    {
        return CrawlThreadCount;
    }
    public void setCoordsCrawlThreadCount(int CrawlThreadCount)
    {
        this.CrawlThreadCount = CrawlThreadCount;
    }

    public synchronized void decCoordsCrawlThreadCount()
    {
        this.CrawlThreadCount--;
    }

    public ArrayList<ShopInfo> getShopList()
    {
        return shopsInfoList;
    }
    public void addAShop(ShopInfo aShop)
    {
        this.shopsInfoList.add(aShop);
        this.shopsNum++;
    }
}


/*****************************************************************************
    Class Name: AreaInfo
        Author: Yangzheng
   Description: ������Ϣ
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class AreaInfo implements Serializable
{
    private int cityId;             // ����ID
    private int areaId;             // ����ID
    private int isSubArea;          // �Ƿ�Ϊ������(������)
    private String areaCode;        // �������
    private String areaCnName;      // ��������
    private String areaUrl;         // ������ַ

    private ArrayList<CircleInfo> circleInfoList;

    public AreaInfo()
    {
        circleInfoList = new ArrayList<CircleInfo>();
    }
    public int getCityId()
    {
        return cityId;
    }
    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }
    public int getAreaId()
    {
        return areaId;
    }
    public void setAreaId(int areaId)
    {
        this.areaId = areaId;
    }
    public int getIsSubArea()
    {
        return isSubArea;
    }
    public void setIsSubArea(int isSubArea)
    {
        this.isSubArea = isSubArea;
    }
    public String getAreaCnName()
    {
        return areaCnName;
    }
    public void setAreaCnName(String areaCnName)
    {
        this.areaCnName = areaCnName;
    }
    public String getAreaCode()
    {
        return areaCode;
    }
    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }
    public void setAreaUrl(String areaUrl)
    {
        this.areaUrl = areaUrl;
    }
    public String getAreaUrl()
    {
        return areaUrl;
    }
    // �����µ���Ȧ��Ϣ��ȡ
    public ArrayList<CircleInfo> getCircleInfoList()
    {
        return circleInfoList;
    }
    public void setCircleInfo(CircleInfo aCircleInfo)
    {
        this.circleInfoList.add(aCircleInfo);
    }
}


/*****************************************************************************
    Class Name: CircleInfo
        Author: Yangzheng
   Description: ��Ȧ��Ϣ
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class CircleInfo implements Serializable
{
    private int     circleId;           // ��������(��Ȧ)ID
    private int     areaId;             // һ������ID
    private int     cityId;             // ����ID
    private String  circleName;         // ��Ȧ����
    private String  circleCode;         // ��Ȧ����
    private String  circleUrl;          // ������ַ

    public int getCircleId()
    {
        return circleId;
    }
    public void setCircleId(int circleId)
    {
        this.circleId = circleId;
    }

    public int getAreaId()
    {
        return areaId;
    }
    public void setAreaId(int areaId)
    {
        this.areaId = areaId;
    }

    public int getCityId()
    {
        return cityId;
    }
    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }

    public String getCircleName()
    {
        return circleName;
    }
    public void setCircleName(String circleName)
    {
        this.circleName = circleName;
    }
    public String getCircleCode()
    {
        return circleCode;
    }
    public void setCircleCode(String circleCode)
    {
        this.circleCode = circleCode;
    }
    public String getCircleUrl()
    {
        return circleUrl;
    }
    public void setCircleUrl(String circleUrl)
    {
        this.circleUrl = circleUrl;
    }
}



/*****************************************************************************
    Class Name: ShopInfo
        Author: Yangzheng
   Description: �̻���
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class ShopInfo implements Serializable
{
    // �̻�ID������
    private int shopId;
    // ����ID
    private int cityId;
    // �̻�������
    private String cnName;
    // �̻�����
    private String cnTinyName;
    // �̻�����������
    private String cityName;

    private int shopCrawledFlag;

    private boolean bIsDishCrawled;

    // �̻��ٶ�����
    private String blat;
    private String blng;
    // �̻�google����
    private String glat;
    private String glng;
    // �̻�GPS����
    private String gpsLat;
    private String gpsLng;
    // �̻���������
    private String mLat;
    private String mLng;

    // �̻�ƴ����
    private String enName;
    // ��������վ�̻�����
    private String shopCode;
    // �̻���ַ
    private String shopUrl;
    // �̻�logo��ַ
    private String shopLogoUrl;
    // �̻�logo·��
    private String shopLogoPath;

    // �ۺ�����
    private String comprehensiveStar;
    // ��ζ����
    private String tasteStar;
    // ��������
    private String serviceStar;
    // ��������
    private String environmentStar;
    // ��ϵ
    private String cuisineStyle;
    // �˾�����
    private String perCost;
    // ��ַ
    private String address;
    // �绰
    private String telephone;
    // Ӫҵʱ��
    private String businessHours;
    // ������·
    private String busLine;
    // �Ƽ���
    private String recommendDishes;
    // ���
    private String introduction;
    // ��ɫ����
    private String characService;
    // ����
    private String comment;
    // ����ID
    private int menuId;
    // �����б�
    private ArrayList<Dish> dishList;

    // ��������ID
    private int areaId;
    // ������ȦID
    private int circleId;
    // ������Ȧ
    private String circle;
    // ��������ID
    private int lineId;

    public ShopInfo()
    {
        this.dishList = new ArrayList<Dish>();
        this.shopCrawledFlag = 0;
        this.bIsDishCrawled = false;
    }

    public int getShopCrawledFlag()
    {
        return shopCrawledFlag;
    }
    public void setShopCrawledFlag(int shopCrawledFlag)
    {
        if (0 != shopCrawledFlag)
        {
            // ���ñ�־λ
            this.shopCrawledFlag |= shopCrawledFlag;
        }
        else
        {
            // ȥ����־λ
            this.shopCrawledFlag &= shopCrawledFlag;
        }
    }

    public boolean getDishCrawledFlag()
    {
        return bIsDishCrawled;
    }
    public void setDishCrawledFlag(boolean bIsDishCrawled)
    {
        this.bIsDishCrawled = bIsDishCrawled;
    }

    public int getShopId()
    {
        return shopId;
    }
    public void setShopId(int shopId)
    {
        this.shopId = shopId;
    }
    public int getCityId()
    {
        return cityId;
    }
    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }

    public int getAreaId()
    {
        return areaId;
    }
    public void setAreaId(int areaId)
    {
        this.areaId = areaId;
    }

    public int getCircleId()
    {
        return circleId;
    }
    public void setCircleId(int circleId)
    {
        this.circleId = circleId;
    }

    public String getShopCode()
    {
        return shopCode;
    }
    public void setShopCode(String shopCode)
    {
        this.shopCode = shopCode;
    }

    public String getShopUrl()
    {
        return shopUrl;
    }
    public void setShopUrl(String shopUrl)
    {
        this.shopUrl = shopUrl;
    }

    public String getShopLogoUrl()
    {
        return shopLogoUrl;
    }
    public void setShopLogoUrl(String shopLogoUrl)
    {
        this.shopLogoUrl = shopLogoUrl;
    }

    public String getShopLogoPath()
    {
        return shopLogoPath;
    }
    public void setShopLogoPath(String shopLogoPath)
    {
        this.shopLogoPath = shopLogoPath;
    }

    public String getEnName()
    {
        return enName;
    }
    public void setEnName(String enName)
    {
        this.enName = enName;
    }
    public String getCnName()
    {
        return cnName;
    }
    public void setCnName(String cnName)
    {
        this.cnName = cnName;
    }
    public String getCnTinyName()
    {
        return cnTinyName;
    }
    public void setCnTinyName(String cnTinyName)
    {
        this.cnTinyName = cnTinyName;
    }
    public String getCityName()
    {
        return cityName;
    }
    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    // �ٶ�����
    public String getbLat()
    {
        return blat;
    }
    public void setbLat(String blat)
    {
        this.blat = blat;
    }

    public String getbLng()
    {
        return blng;
    }
    public void setbLng(String blng)
    {
        this.blng = blng;
    }

    // google����
    public String getgLat()
    {
        return glat;
    }
    public void setgLat(String glat)
    {
        this.glat = glat;
    }

    public String getgLng()
    {
        return glng;
    }
    public void setgLng(String glng)
    {
        this.glng = glng;
    }
    //Gps����
    public String getGpsLat()
    {
        return gpsLat;
    }
    public void setGpsLat(String gpsLat)
    {
        this.gpsLat = gpsLat;
    }
    public String getGpsLng()
    {
        return gpsLng;
    }
    public void setGpsLng(String gpsLng)
    {
        this.gpsLng = gpsLng;
    }
    // ��������
    public String getMLat()
    {
        return mLat;
    }
    public void setMLat(String mLat)
    {
        this.mLat = mLat;
    }
    public String getMLng()
    {
        return mLng;
    }
    public void setMLng(String mLng)
    {
        this.mLng = mLng;
    }

    public String getComprehensiveStar()
    {
        return comprehensiveStar;
    }
    public void setComprehensiveStar(String comprehensiveStar)
    {
        this.comprehensiveStar = comprehensiveStar;
    }
    public String getServiceStar()
    {
        return serviceStar;
    }
    public void setServiceStar(String serviceStar)
    {
        this.serviceStar = serviceStar;
    }
    public String getTasteStar()
    {
        return tasteStar;
    }
    public void setTasteStar(String tasteStar)
    {
        this.tasteStar = tasteStar;
    }
    public String getEnvironmentStar()
    {
        return environmentStar;
    }
    public void setEnvironmentStar(String environmentStar)
    {
        this.environmentStar = environmentStar;
    }
    public String getCuisineStyle()
    {
        return cuisineStyle;
    }
    public void setCuisineStyle(String cuisineStyle)
    {
        this.cuisineStyle = cuisineStyle;
    }
    public String getPerCost()
    {
        return perCost;
    }
    public void setPerCost(String perCost)
    {
        this.perCost = perCost;
    }
    public String getAddress()
    {
        return address;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }
    public String getTelephone()
    {
        return telephone;
    }
    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }
    public String getBusinessHours()
    {
        return businessHours;
    }
    public void setBusinessHours(String businessHours)
    {
        this.businessHours = businessHours;
    }
    public String getBusLine()
    {
        return busLine;
    }
    public void setBusLine(String busLine)
    {
        this.busLine = busLine;
    }
    public String getRecommendDishes()
    {
        return recommendDishes;
    }
    public void setRecommendDishes(String recommendDishes)
    {
        this.recommendDishes = recommendDishes;
    }
    public String getIntroduction()
    {
        return introduction;
    }
    public void setIntroduction(String introduction)
    {
        this.introduction = introduction;
    }
    // ��ɫ����
    public String getCharacService()
    {
        return characService;
    }
    public void setCharacService(String characService)
    {
        this.characService = characService;
    }
    // ��Ȧ
    public String getCircle()
    {
        return circle;
    }
    public void setCircle(String circle)
    {
        this.circle = circle;
    }
    // �����б�
    public ArrayList<Dish> getDishList()
    {
        return dishList;
    }
    public void addADish(Dish aDish)
    {
        this.dishList.add(aDish);
    }
    // �����
    // ����ID
    // private int menuId;
    // ��������ID
    // private int areaId;
    // ������ȦID
    // private int CircleId;
    // ��������ID
    // private int lineId;
}


/*****************************************************************************
    Class Name: Dish
        Author: Yangzheng
   Description: ������
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class Dish implements Serializable
{
    // ����ID
    private int dishId;
    /// �Ƿ��Ƽ���
    private int isRecommendDish;
    // ����
    private String dishName;
    // baidu(����վ)����ͼƬ����
    private String dishPicCode;
    // ����(����)
    private String dishMethod;

    // ��ʼ����ͼƬ��ַ
    private String dishPicUrl;
    // ��ʼ���ȱ���ͼƬ·��
    private String dishPicPath;
    // tiny����ͼƬ��ַ
    private String dishTinyPicUrl;
    // tiny����ͼƬ����·��
    private String dishTinyPicPath;
    // small����ͼƬ��ַ
    private String dishSmallPicUrl;
    // small����ͼƬ����·��
    private String dishSmallPicPath;
    // middle����ͼƬ��ַ
    private String dishMiddlePicUrl;
    // middle����ͼƬ����·��
    private String dishMiddlePicPath;
    // large����ͼƬ��ַ
    private String dishLargePicUrl;
    // large����ͼƬ����·��
    private String dishLargePicPath;

    public int getDishId()
    {
        return dishId;
    }
    public void setDishId(int dishId)
    {
        this.dishId = dishId;
    }

    public int getIsRecommendDish()
    {
        return isRecommendDish;
    }
    public void setIsRecommendDish(int isRecommendDish)
    {
        this.isRecommendDish = isRecommendDish;
    }

    public String getDishName()
    {
        return dishName;
    }
    public void setDishName(String dishName)
    {
        this.dishName = dishName;
    }

    public String getDishPicCode()
    {
        return dishPicCode;
    }
    public void setDishPicCode(String dishPicCode)
    {
        this.dishPicCode = dishPicCode;
    }

    public String getDishPicUrl()
    {
        return dishPicUrl;
    }
    public void setDishPicUrl(String dishPicUrl)
    {
        this.dishPicUrl = dishPicUrl;
    }

    public String getDishPicPath()
    {
        return dishPicPath;
    }
    public void setDishPicPath(String dishPicPath)
    {
        this.dishPicPath = dishPicPath;
    }

    public String getDishTinyPicUrl()
    {
        return dishTinyPicUrl;
    }
    public void setDishTinyPicUrl(String dishTinyPicUrl)
    {
        this.dishTinyPicUrl = dishTinyPicUrl;
    }

    public String getDishTinyPicPath()
    {
        return dishTinyPicPath;
    }
    public void setDishTinyPicPath(String dishTinyPicPath)
    {
        this.dishTinyPicPath = dishTinyPicPath;
    }

    public String getDishSmallPicUrl()
    {
        return dishSmallPicUrl;
    }
    public void setDishSmallPicUrl(String dishSmallPicUrl)
    {
        this.dishSmallPicUrl = dishSmallPicUrl;
    }

    public String getDishSmallPicPath()
    {
        return dishSmallPicPath;
    }
    public void setDishSmallPicPath(String dishSmallPicPath)
    {
        this.dishSmallPicPath = dishSmallPicPath;
    }

    public String getDishMiddlePicUrl()
    {
        return dishMiddlePicUrl;
    }
    public void setDishMiddlePicUrl(String dishMiddlePicUrl)
    {
        this.dishMiddlePicUrl = dishMiddlePicUrl;
    }

    public String getDishMiddlePicPath()
    {
        return dishMiddlePicPath;
    }
    public void setDishMiddlePicPath(String dishMiddlePicPath)
    {
        this.dishMiddlePicPath = dishMiddlePicPath;
    }

    public String getDishLargePicUrl()
    {
        return dishLargePicUrl;
    }
    public void setDishLargePicUrl(String dishLargePicUrl)
    {
        this.dishLargePicUrl = dishLargePicUrl;
    }

    public String getDishLargePicPath()
    {
        return dishLargePicPath;
    }
    public void setDishLargePicPath(String dishLargePicPath)
    {
        this.dishLargePicPath = dishLargePicPath;
    }
}


/*****************************************************************************
    Class Name: ShopCoords
        Author: Yangzheng
   Description: ������
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class ShopCoords implements Serializable
{
    int    shopId;
    String shopName;
    String blat;
    String blng;
    String glat;
    String glng;
    String clat;
    String clng;
    String shopAddress;

    public int getShopId()
    {
        return shopId;
    }
    public void setShopId(int shopId)
    {
        this.shopId = shopId;
    }

    public String getShopName()
    {
        return shopName;
    }
    public void setShopName(String shopName)
    {
        this.shopName = shopName;
    }

    // �ٶ�����
    public String getbLat()
    {
        return blat;
    }
    public void setbLat(String blat)
    {
        this.blat = blat;
    }

    public String getbLng()
    {
        return blng;
    }
    public void setbLng(String blng)
    {
        this.blng = blng;
    }

    // google����
    public String getgLat()
    {
        return glat;
    }
    public void setgLat(String glat)
    {
        this.glat = glat;
    }

    public String getgLng()
    {
        return glng;
    }
    public void setgLng(String glng)
    {
        this.glng = glng;
    }
    String getClat()
    {
        return clat;
    }
    public void setClat(String clat)
    {
        this.clat = clat;
    }
    String getClng()
    {
        return clng;
    }

    public void setClng(String clng)
    {
        this.clng = clng;
    }

    public String getAddress()
    {
        return shopAddress;
    }
    public void setAddress(String shopAddress)
    {
        this.shopAddress = shopAddress;
    }
}


