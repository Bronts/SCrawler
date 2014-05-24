/******************************************************************************

          Copyright (C), 2009-2013, YangZheng. All Rights Reserved

 ******************************************************************************
       File Name: SCrawler.java
         Version: 1.10
          Author: Yangzheng
         Created: 2012/12/5
     Description: 主类：爬虫抓取个网站入口
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

    // 用于保存数据表的商户 2013.07
    public static ArrayList<ShopTable> shopTableList  = new ArrayList<ShopTable>();

    private static Logger logger = LogManager.getLogger(SCrawler.class.getName());

    /*****************************************************************************
     Function Name: SCrawler.main
            Author: Yangzheng
       Description: 主函数入口
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
       Description: 清屏操作
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
       Description: 带参数启动
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
                // 抓取百度商户
                SC_crawlBaiduShops();
            }
            else if (goal.equals("dianping"))
            {
                // 抓取点评商户
                SC_crawlDianpingShops();
            }
            else if (goal.equals("fantong"))
            {
                // 抓取饭统商户
                SC_crawlFantongShops();
            }
            else if (goal.equals("coordinate"))
            {
                // 补全所有商户百度坐标
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
                // 继续抓取百度商户
                SC_continueCrawlBaiduShops();
            }
            else if (goal.equals("dianping"))
            {
                // 继续抓取点评商户
                SC_continueCrawlDianpingShops();
            }
            else if (goal.equals("fantong"))
            {
                // 继续抓取饭统商户
                SC_continueCrawlFantongShops();
            }
            else if (goal.equals("coordinate"))
            {
                // 继续补全缺失百度坐标
                SC_continueCrawlCoords();
            }
            else if (goal.equals("transform"))
            {
                // 继续转换百度坐标为GPS坐标和火星坐标
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
                // 转换百度坐标为GPS坐标和火星坐标
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
       Description: 显示选项
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
       Description: 显示说明头
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
        System.out.println(" *                        iShiyu爬虫 Version 0.6                             *");
        System.out.println(" *  -----------------------------------------------------------------------  *");
        System.out.println(" *          说明 : 按选项前【数字】进入下一步.                               *");
        System.out.println(" *                 按【Ctrl + C】退出运行中的程序(服务器版无效).             *");
        System.out.println(" *                 进度文件在城市列表抓取后生成,抓取中退出将无法保存.        *");
        System.out.println(" *****************************************************************************");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showConfigHead
            Author: Yangzheng
       Description: 显示设置说明
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
        System.out.println(" *                         美食爬虫 Version 0.7                               * ");
        System.out.println(" *  ----------------------------------------------------------------------   * ");
        System.out.println(" *                   说明：按【选项】前数字进入下一步                            * ");
        System.out.println(" *                        设置的【参数】请认真确认，否则将会导致保存错误！         * ");
        System.out.println(" ***************************************************************************** ");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showEnterOption
            Author: Yangzheng
       Description: 显示第一层选项
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
        System.out.println("   1. 抓取各站商户数据.");
        System.out.println("   2. 商户坐标处理.");
        System.out.println("   3. 设置抓取参数.");
        System.out.println("   0. 离开.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealOption
            Author: Yangzheng
       Description: 处理选择的任务
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
                // 跳过回车键
                System.in.skip(2);

                switch (ch)
                {
                        // 抓取各站商户数据
                    case '1':
                    {
                        SC_refeshScreen();
                        SC_crawlShopInfo();
                        break;
                    }

                    // 商户坐标处理.
                    case '2':
                    {
                        SC_refeshScreen();
                        SC_processCoords();
                        break;
                    }

                    // 设置相关参数
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
                        System.out.println("请输入可选数字!");
                        SC_dealOption();
                        break;
                    }
                }
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_crawlShopInfo
            Author: Yangzheng
       Description: 抓取网站选项
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
       Description: 处理坐标
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
       Description: 设置抓取参数
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
       Description: 显示选择项
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
        System.out.println("   1. 设置数据库参数.");
        System.out.println("   2. 恢复数据库参数为默认.");
        System.out.println("   3. 设置图片保存根目录.");
        System.out.println("   4. 恢复图片保存根目录为默认.");
        System.out.println("   5. 查看当前设置的参数.");
        System.out.println("   0. 返回.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showCrawlOption
            Author: Yangzheng
       Description: 显示抓取选项
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
        System.out.println("   1. 抓取百度身边数据.");
        System.out.println("   2. 继续上次抓取百度身边数据.");
        System.out.println("   3. 抓取大众点评网(天津)数据.");
        System.out.println("   4. 继续上次抓取大众点评网(天津)数据.");
        System.out.println("   5. 抓取饭统网数据.");
        System.out.println("   6. 继续上次抓取饭统网数据.");
        System.out.println("   0. 返回.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showCoordsOption
            Author: Yangzheng
       Description: 显示坐标处理选项
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
        System.out.println("   1. 更新商户百度坐标.");
        System.out.println("   2. 继续上次进度更新商户百度坐标.");
        System.out.println("   3. 转换商户百度坐标为GPS与火星坐标.");
        System.out.println("   4. 继续上次进度转换百度坐标为GPS与火星坐标.");
        System.out.println("   0. 返回.");
        System.out.println("\n");
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealConfigOption
            Author: Yangzheng
       Description: 处理选择项
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
                // 跳过回车键
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
                        System.out.println("\n   请输入可选数字!");
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
       Description: 处理抓取选项
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
                // 跳过回车键
                System.in.skip(2);

                switch (ch)
                {
                    case '1':
                    {
                        // 抓取百度商户
                        SC_crawlBaiduShops();
                        break;
                    }

                    case '2':
                    {
                        // 继续抓取吧百度商户
                        SC_continueCrawlBaiduShops();
                        break;
                    }

                    case '3':
                    {
                        // 抓取点评商户
                        SC_crawlDianpingShops();
                        break;
                    }

                    case '4':
                    {
                        // 继续抓取点评商户
                        SC_continueCrawlDianpingShops();
                        break;
                    }

                    case '5':
                    {
                        // 抓取饭统商户
                        SC_crawlFantongShops();
                        break;
                    }

                    case '6':
                    {
                        // 继续抓取饭统商户
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
                        System.out.println("请输入可选数字!");
                        SC_dealCrawlOption();
                        break;
                    }
                }
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_dealCoordsOption
            Author: Yangzheng
       Description: 处理坐标处理选项
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
                // 跳过回车键
                System.in.skip(2);

                switch (ch)
                {
                    case '1':
                    {
                        // 补全所有商户百度坐标
                        SC_crawlCoords();
                        break;
                    }

                    case '2':
                    {
                        // 继续补全缺失百度坐标
                        SC_continueCrawlCoords();
                        break;
                    }

                    case '3':
                    {
                        // 转换百度坐标为GPS坐标和火星坐标
                        SC_transformCoords();
                    }

                    case '4':
                    {
                        // 继续转换百度坐标为GPS坐标和火星坐标
                        SC_continueTransformCoords();
                    }

                    // 返回
                    case '0':
                    {
                        SC_refeshScreen();
                        SC_showOption();
                        SC_dealOption();
                        break;
                    }

                    // 非选项
                    default:
                    {
                        SC_showHeader();
                        SC_showCoordsOption();
                        System.out.println("请输入可选数字!");
                        SC_dealCoordsOption();
                        break;
                    }
                }
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops
            Author: Yangzheng
       Description: 抓取百度商户信息
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
            System.out.println(" 开始抓取百度身边数据...");
            System.out.println(" 百度身边网站暂停运营,暂时不可用...");
            System.out.println(" 请重新输入可选数字!");
            SC_showHeader();
            SC_showCrawlOption();
            SC_dealCrawlOption();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlBaiduShops
            Author: Yangzheng
       Description: 继续抓取百度商户信息
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
            System.out.println(" 开始继续抓取百度身边数据...");
            System.out.println(" 百度身边网站暂停运营,暂时不可用...");
            System.out.println(" 请重新输入可选数字!");
            SC_showHeader();
            SC_showCrawlOption();
            SC_dealCrawlOption();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_crawlDianpingShops
            Author: Yangzheng
       Description: 抓取点评商户信息
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
            System.out.println(" 开始抓取大众点评数据.");
            // 启动守护进程
            SC_startDaemonThread(cityList, Config.dianpingDat);
            // 获取大众点评数据
            CrawlDianping Dianping = new CrawlDianping(cityList, false);
            Thread dianpingThread = new Thread(Dianping);
            dianpingThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlDianpingShops
            Author: Yangzheng
       Description: 继续抓取点评商户信息
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
            System.out.println(" 开始继续抓取大众点评数据.");
            System.out.println(" 正在读取进度文件,请稍等....");

            // 恢复进度文件
            cityList = InfoSave.SC_recoverProcess(Config.dianpingDat);

            if (null == cityList)
            {
                System.out.println(" 进度文件不存在,请重新输入可选数字!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // 启动守护进程
                SC_startDaemonThread(cityList, Config.dianpingDat);
                CrawlDianping Dianping = new CrawlDianping(cityList, true);
                Thread dianpingThread  = new Thread(Dianping);
                dianpingThread.start();
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_crawlFantongShops
            Author: Yangzheng
       Description: 抓取饭统商户信息
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
            System.out.println(" 开始抓取饭统数据.");
            // 启动守护进程
            SC_startDaemonThread(cityList, Config.fantongDat);
            // 获取饭统数据
            CrawlFantong Fantong = new CrawlFantong(cityList, false);
            Thread fantongThread = new Thread(Fantong);
            fantongThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlFantongShops
            Author: Yangzheng
       Description: 继续抓取饭统商户信息
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
            System.out.println(" 开始继续抓取饭统数据.");
            System.out.println(" 正在读取进度文件,请稍等....");

            // 恢复进度文件
            cityList = InfoSave.SC_recoverProcess(Config.fantongDat);

            if (null == cityList)
            {
                System.out.println(" 进度文件不存在,请重新输入可选数字!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // 启动守护进程
                SC_startDaemonThread(cityList, Config.fantongDat);
                CrawlFantong Fantong = new CrawlFantong(cityList, true);
                Thread fantongThread = new Thread(Fantong);
                fantongThread.start();
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_crawlCoords
            Author: Yangzheng
       Description: 补全商户（百度）坐标
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
            // 更新商户坐标
            System.out.println("\n");
            System.out.println(" 开始更新坐标数据.");
            // 启动守护进程
            SC_startDaemonThread(shopTableList, Config.coordsDat);

            CrawlCoords AllCoords = new CrawlCoords(shopTableList, false);
            Thread coordsThread   = new Thread(AllCoords);
            coordsThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueCrawlCoords
            Author: Yangzheng
       Description: 继续补全坐标
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
            // 继续进度更新商户坐标
            System.out.println("\n");
            System.out.println(" 开始继续更新坐标数据.");
            System.out.println(" 正在读取进度文件,请稍等....");

            // 恢复进度文件
            shopTableList = InfoSave.SC_recoverProcess(Config.coordsDat);

            if (null == shopTableList)
            {
                System.out.println(" 进度文件不存在,请重新输入可选数字!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // 启动守护进程
                SC_startDaemonThread(shopTableList, Config.coordsDat);
                CrawlCoords AllCoords = new CrawlCoords(shopTableList, true);
                Thread coordsThread   = new Thread(AllCoords);
                coordsThread.start();
            }

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_transformCoords
            Author: Yangzheng
       Description: 转换坐标
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
            // 转换商户坐标
            System.out.println("\n");
            System.out.println(" 开始转换坐标数据.");
            // 启动守护进程
            SC_startDaemonThread(shopTableList, Config.transformDat);

            TransformCoords transAllCoords = new TransformCoords(shopTableList, false);
            Thread coordsThread   = new Thread(transAllCoords);
            coordsThread.start();

            logger.exit();
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SC_crawlBaiduShops.SC_continueTransformCoords
            Author: Yangzheng
       Description: 继续转换坐标
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
            // 继续进度转换商户坐标
            System.out.println("\n");
            System.out.println(" 开始继续更新坐标数据.");
            System.out.println(" 正在读取进度文件,请稍等....");

            // 恢复进度文件
            shopTableList = InfoSave.SC_recoverProcess(Config.transformDat);

            if (null == shopTableList)
            {
                System.out.println(" 进度文件不存在,请重新输入可选数字!");
                SC_showOption();
                SC_dealOption();
            }
            else
            {
                // 启动守护进程
                SC_startDaemonThread(shopTableList, Config.transformDat);

                TransformCoords transAllCoords = new TransformCoords(shopTableList, true);
                Thread coordsThread   = new Thread(transAllCoords);
                coordsThread.start();

                logger.exit();
            }
        }
        catch (Exception ex)
        {
            logger.error("处理选项出错." + ex);
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_setDateBase
            Author: Yangzheng
       Description: 设置数据库参数
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

            System.out.print("   请输入【数据库名】:");
            String datebase = scanIn.nextLine();            //读取键盘输入的一行（以回车换行为结束输入）

            System.out.print("   请继续输入数据库【用户名】:");
            String user = scanIn.nextLine();

            System.out.print("   请继续输入数据库【用户密码】:");
            String pass = scanIn.nextLine();

            System.out.println("\n");
            System.out.println("   您输入的【数据库名】为:       " + datebase);
            System.out.println("   您输入的数据库【用户名】为:   " + user);
            System.out.println("   您输入的数据库【用户密码】为: " + pass);
            System.out.println("\n");

            System.out.println("   确认请按【1】，重新输入请按【2】，离开请按【0】.");

            char ch = (char)System.in.read();

            System.in.skip(2); // 跳过回车键

            if ('1' == ch)
            {
                DataBaseHelper.DB_setDatabase(datebase);
                DataBaseHelper.DB_setUser(user);
                DataBaseHelper.DB_setPass(pass);

                SC_refeshScreen();
                System.out.println("   数据库设置【已经保存】!");
                SC_setConfig();
            }
            else if ('2' == ch)
            {
                SC_setDateBase();
            }
            else if ('0' == ch)
            {
                SC_refeshScreen();
                System.out.println("   数据库设置【未做更改】!");
                SC_setConfig();
            }
            else
            {
                SC_refeshScreen();
                System.out.println("   数据库设置未做更改!请正确输入【可选数字】并重新设置!");
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
       Description: 恢复数据默认设置
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
        System.out.println("   数据库设置已经【恢复默认值】!");
        SC_setConfig();

        return;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_setImgRootDir
            Author: Yangzheng
       Description: 设置图片保存目录
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

            System.out.println("   注意:输入中子路径使用'/'分割，目录结尾请勿加'/'");
            System.out.print("   请输入【图片保存根目录】: ");
            String imgPath = scanIn.nextLine();            //读取键盘输入的一行（以回车换行为结束输入）

            System.out.println("   您输入的【图片保存根目录】为: " + imgPath);
            System.out.println("\n");
            System.out.println("   确认请按【1】，重新输入请按【2】，离开请按【0】.");

            char ch = (char)System.in.read();

            System.in.skip(2); // 跳过回车键

            if ('1' == ch)
            {
                Config.IMG_DIR = imgPath;
                SC_refeshScreen();
                System.out.println("   图片根目录【已保存】!");
                SC_setConfig();
            }
            else if ('2' == ch)
            {
                SC_setImgRootDir();
            }
            else if ('0' == ch)
            {
                SC_refeshScreen();
                System.out.println("   图片根目录设置【未做更改】!");
                SC_setConfig();
            }
            else
            {
                SC_refeshScreen();
                System.out.println("   图片根目录设置未做更改!请正确输入【可选数字】并重新设置!");
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
       Description: 设置默认图片保存目录
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
        System.out.println("   图片根目录设置已经【恢复默认值】!");
        SC_setConfig();

    }

    /*****************************************************************************
     Function Name: SCrawler.SC_showConfig
            Author: Yangzheng
       Description: 显示当前配置
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
        System.out.println("   当前设置的【数据库名】为:       " + DataBaseHelper.DB_getDatabase());
        System.out.println("   当前设置的数据库【用户名】为:   " + DataBaseHelper.DB_getUser());
        System.out.println("   当前设置的数据库【用户密码】为: " + DataBaseHelper.DB_getPass());
        System.out.println("   当前设置的【图片保存根目录】为: " + Config.IMG_DIR);
        System.out.println("\n");
        System.out.println("   返回请按【0】.");

        try
        {
            char ch = (char)System.in.read();

            System.in.skip(2); // 跳过回车键

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
       Description: 创建守护进程，用于退出时保存进度
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
       Description: 恢复抓取进度
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
            logger.error("进度文件恢复失败" + ex);
            ex.printStackTrace();
        }

        return recoverList;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_readPrcocess
            Author: Yangzheng
       Description: 读取抓取进度
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
            logger.error("进度文件读取失败");
            e.printStackTrace();
        }

        objIns.close();
        fileIns.close();

        return object;
    }

    /*****************************************************************************
     Function Name: SCrawler.SC_savePrcocess
            Author: Yangzheng
       Description: 保存抓取进度
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
            logger.info("删除已存在的进度文件");
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


    // 此处应该加入选择菜单

    // 获取sohu首页图片数据，测试使用
    // SCrawler Sohu = new SCrawler();
    // Sohu.getSohuImgsTest();

    // 获取获取百度身边城市数据(获取了城市名字以及baidu定义的城市ID)
    // SCrawler Baidu= new SCrawler();
    // Baidu.getBaiduCity();

    // 获取百度身边数据
    // CrawlBaidu Baidu = new CrawlBaidu(baiduUrl);
    // Baidu.Baidu_getShopsData();

    // 获取大众点评数据
    // CrawlDianping Dianping = new CrawlDianping(dianpingUrl, cityList);
    // Dianping.Dianping_getShopsData();

    // 获取饭桶网数据
    // CrawlFantong Fantong = new CrawlFantong(fantongUrl);
    // Fantong.Fantong_getRestaurantsData();

    // 抓取香港数据
    // CrawlHongkong Hongkong = new CrawlHongkong(dianpingUrl);
    // Hongkong.Hongkong_updateLatAndLngInfo();
    // Hongkong.Hongkong_getShopsLogos();
    // Hongkong.Hongkong_getShopDishes();
}

/*****************************************************************************
    Class Name: Config
        Author: Yangzheng
   Description: 全局参数类
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/2/25       Yangzheng          Created Class
*****************************************************************************/
class Config
{
    public static final int CLEAR_FLAG       = 0;

    // 标记城市信息是否抓取完毕
    public static final int CITY_CRAWLED     = 1;
    public static final int REGIONS_CRAWLED  = 2;
    public static final int SHOPURLS_CRAWLED = 4;
    public static final int DISHES_CRAWLED   = 8;

    // 标记商户表信息是否抓取完毕
    public static final int TABLE_CRAWLED    = 1;
    public static final int SHOPS_CRAWLED    = 2;

    // 标记商户信息是否处理完毕
    public static final int TABLE_PROCESSED  = 1;
    public static final int SHOPS_PROCESSED  = 2;
    public static final int SHOP_PROCESSED   = 4;

    // 标记商户信息是否抓取完毕
    public static final int SHOP_INFO_CRAWLED = 1;
    public static final int SHOP_LOGO_CRAWLED = 2;

    public static final int SAVE_THRESHOLD  = 500;      // 保存商户的门限
    public static final int PAUSE_THRESHOLD = 180;      // 防止反爬虫机制，暂停门限
    public static final int ERROR_THRESHOLD = 5;        // 抓取网站错误容忍门限

    public static final int CRAWL_THREAD_COUNT = 10;     // 开启线程的默认数

    // 坐标运算常数
    public static final double COORDS_PI    = 3.14159265358979324 * 3000.0 / 180.0;

    public static final String baiduDat     = "baiduCity.dat";
    public static final String dianpingDat  = "dianpingCity.dat";
    public static final String fantongDat   = "fantongCity.dat";
    public static final String coordsDat    = "coords.dat";
    public static final String transformDat = "transformCoords.dat";

    public static final String baiduUrl     = "http://s.baidu.com";
    public static final String dianpingUrl  = "http://m.dianping.com/citylist";
    public static final String fantongUrl   = "http://www.fantong.com/more-cities/";

    // 图片保存根目录
    public static String IMG_DIR        = "E:/IMG";

}

/*****************************************************************************
    Class Name: CtrlCExit
        Author: Yangzheng
   Description: 退出类
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
            System.out.println(" 准备保存数据，正在停止其他线程，即将退出程序...");

            try
            {
                // 在这里增添您需要处理代码
                System.out.println("\n");
                System.out.println(" 正在保存中...");

                // 设置其他线程运行停止标志位
                CrawlCoords.Coords_setRunningFlag(true);
                TransformCoords.Coords_setRunningFlag(true);

                // 守护线程暂停一秒，等待子线程退出
                Thread.sleep(1000);

                // 保存进度
                InfoSave.SC_savePrcocess(processList, savedDatName);
                System.out.println("\n");
                System.out.println(" ......");
                System.out.println("\n");
                System.out.println(" 保存完毕,爬虫程序退出.");
                //Thread.sleep(1000);
            }
            catch (Exception ex)
            {
                logger.error("保存失败" + ex);
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
   Description: 保存类
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
       Description: 恢复保存的进度文件
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
            logger.error("进度文件恢复失败" + ex);
            ex.printStackTrace();
        }

        return recoverList;
    }

    /*****************************************************************************
     Function Name: InfoSave.SC_readPrcocess
            Author: Yangzheng
       Description: 读取进度
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
            logger.error("进度文件读取失败" + ex);
            ex.printStackTrace();
        }

        objIns.close();
        fileIns.close();

        return object;
    }


    /*****************************************************************************
     Function Name: InfoSave.SC_savePrcocess
            Author: Yangzheng
       Description: 保存进度
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
            logger.info("删除已存在的进度文件");
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
   Description: 自动保存类 抓取中每间隔1一小时保存一次进度
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
            System.out.println(" 启动了自动保存线程...");
            logger.info(" 启动了自动保存线程...");

            while (true)
            {
                //在这里增添您需要处理代码
                logger.entry();
                Thread.sleep(3600000);

                synchronized (processList)
                {
                    System.out.println(" 开始自动保存进度...");
                    logger.info("按设置每隔一小时进行进度保存。");
                    InfoSave.SC_savePrcocess(processList, savedDatName);
                    System.out.println(" 保存完毕");
                }

                logger.exit();
            }
        }
        catch (Exception ex)
        {
            logger.error("守护线程保存失败" + ex);
            ex.printStackTrace();
        }
    }
}

/*****************************************************************************
    Class Name: City
        Author: Yangzheng
   Description: 城市类 用于保存城市信息
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/2/25       Yangzheng          Created Class
*****************************************************************************/
class City implements Serializable
{
    // 要包含指向下级区域、下级商圈的数据结构

    private int     cityId;         // 城市ID，主键
    private int     baiduCityId;    // baidu城市ID
    private int     dianpingCityId; // dianping城市ID
    private int     shopsNum;       // 商户数量
    private String  enName;         // 拼音名
    private String  cnName;         // 中文名
    private String  cityUrl;        // 城市页面链接

    private ArrayList<AreaInfo> areaInfoList;
    private ArrayList<ShopInfo> shopsInfoList;   // 商户连接列表

    private int crawledFlag;              // 标记所有商户是否抓取完毕
    private int dishCrawlThreadCount = 0; // 抓取图片线程数

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
            // 设置标志位
            this.crawledFlag |= crawledFlag;
        }
        else
        {
            // 去除标志位
            this.crawledFlag &= ~crawledFlag;
        }
    }

    // 图片下载线程数
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
    // 区域下的商圈信息存取
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
   Description: 商户表类 按表来存储商户
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/6/25       Yangzheng          Created Class
*****************************************************************************/
class ShopTable implements Serializable
{
    // 要包含指向下级区域、下级商圈的数据结构

    private String tableName;                   // 表ID，主键
    private String cityName;                    // 当前表的城市名

    private int cityId;                         // 对应表的城市ID
    private int shopsNum;                       // 商户数量

    private ArrayList<ShopInfo> shopsInfoList;  // 商户连接列表

    private int crawledFlag;                    // 标记所有商户是否抓取完毕
    private int CrawlThreadCount = 0;           // 抓取坐标线程数

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
            // 设置标志位
            this.crawledFlag |= crawledFlag;
        }
        else
        {
            // 去除标志位
            this.crawledFlag &= ~crawledFlag;
        }
    }

    // 坐标更新线程数
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
   Description: 区域信息
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class AreaInfo implements Serializable
{
    private int cityId;             // 城市ID
    private int areaId;             // 区域ID
    private int isSubArea;          // 是否为子区域(点评网)
    private String areaCode;        // 区域代码
    private String areaCnName;      // 区域名称
    private String areaUrl;         // 区域网址

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
    // 区域下的商圈信息存取
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
   Description: 商圈信息
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class CircleInfo implements Serializable
{
    private int     circleId;           // 二级区域(商圈)ID
    private int     areaId;             // 一级区域ID
    private int     cityId;             // 城市ID
    private String  circleName;         // 商圈名称
    private String  circleCode;         // 商圈代码
    private String  circleUrl;          // 区域网址

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
   Description: 商户类
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class ShopInfo implements Serializable
{
    // 商户ID，主键
    private int shopId;
    // 城市ID
    private int cityId;
    // 商户中文名
    private String cnName;
    // 商户别名
    private String cnTinyName;
    // 商户所属城市名
    private String cityName;

    private int shopCrawledFlag;

    private boolean bIsDishCrawled;

    // 商户百度坐标
    private String blat;
    private String blng;
    // 商户google坐标
    private String glat;
    private String glng;
    // 商户GPS坐标
    private String gpsLat;
    private String gpsLng;
    // 商户火星坐标
    private String mLat;
    private String mLng;

    // 商户拼音名
    private String enName;
    // 第三方网站商户代码
    private String shopCode;
    // 商户网址
    private String shopUrl;
    // 商户logo网址
    private String shopLogoUrl;
    // 商户logo路径
    private String shopLogoPath;

    // 综合评级
    private String comprehensiveStar;
    // 口味评级
    private String tasteStar;
    // 服务评级
    private String serviceStar;
    // 环境评级
    private String environmentStar;
    // 菜系
    private String cuisineStyle;
    // 人均消费
    private String perCost;
    // 地址
    private String address;
    // 电话
    private String telephone;
    // 营业时间
    private String businessHours;
    // 公交线路
    private String busLine;
    // 推荐菜
    private String recommendDishes;
    // 简介
    private String introduction;
    // 特色服务
    private String characService;
    // 点评
    private String comment;
    // 菜谱ID
    private int menuId;
    // 菜肴列表
    private ArrayList<Dish> dishList;

    // 所属区域ID
    private int areaId;
    // 所属商圈ID
    private int circleId;
    // 所属商圈
    private String circle;
    // 所属环线ID
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
            // 设置标志位
            this.shopCrawledFlag |= shopCrawledFlag;
        }
        else
        {
            // 去除标志位
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

    // 百度坐标
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

    // google坐标
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
    //Gps坐标
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
    // 火星坐标
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
    // 特色服务
    public String getCharacService()
    {
        return characService;
    }
    public void setCharacService(String characService)
    {
        this.characService = characService;
    }
    // 商圈
    public String getCircle()
    {
        return circle;
    }
    public void setCircle(String circle)
    {
        this.circle = circle;
    }
    // 菜肴列表
    public ArrayList<Dish> getDishList()
    {
        return dishList;
    }
    public void addADish(Dish aDish)
    {
        this.dishList.add(aDish);
    }
    // 待添加
    // 菜谱ID
    // private int menuId;
    // 所属区域ID
    // private int areaId;
    // 所属商圈ID
    // private int CircleId;
    // 所属环线ID
    // private int lineId;
}


/*****************************************************************************
    Class Name: Dish
        Author: Yangzheng
   Description: 菜肴类
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/3/6       Yangzheng           Created Class
*****************************************************************************/
class Dish implements Serializable
{
    // 菜肴ID
    private int dishId;
    /// 是否推荐菜
    private int isRecommendDish;
    // 菜名
    private String dishName;
    // baidu(各网站)菜肴图片代码
    private String dishPicCode;
    // 菜谱(做法)
    private String dishMethod;

    // 初始菜肴图片地址
    private String dishPicUrl;
    // 初始菜肴本地图片路径
    private String dishPicPath;
    // tiny菜肴图片地址
    private String dishTinyPicUrl;
    // tiny菜肴图片本地路径
    private String dishTinyPicPath;
    // small菜肴图片地址
    private String dishSmallPicUrl;
    // small菜肴图片本地路径
    private String dishSmallPicPath;
    // middle菜肴图片地址
    private String dishMiddlePicUrl;
    // middle菜肴图片本地路径
    private String dishMiddlePicPath;
    // large菜肴图片地址
    private String dishLargePicUrl;
    // large菜肴图片本地路径
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
   Description: 坐标类
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

    // 百度坐标
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

    // google坐标
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


