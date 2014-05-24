/**
 * @(#)CrawlProxy.java
 *
 * GetProxyUrl application
 *
 * @author
 * @version 1.00 2013/5/14
 */

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class CrawlProxy
{
    public static String baseUrl = "http://www.goodip.cn/index.php?typeid=5&hidetype=2&countryid=-1&provinceid=0&page=";
    public static Document proxyWeb;

    public static List<Proxy> proxyList = new ArrayList<Proxy>();

    /*****************************************************************************
     Function Name: CrawlProxy.crawlProxyWeb
            Author: Yangzheng
       Description: 获取代理发布网页
             Input: NONE
            Output: NONE
            Return: public
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/5/21       Yangzheng         Created Function
    *****************************************************************************/
    public void crawlProxyWeb()
    {
        try
        {
            CrawlHelper CrHelper = new CrawlHelper();

            for (int pageCount = 1; pageCount <= 5; pageCount++)
            {
                proxyWeb = CrHelper.getWebByJsoup(baseUrl + pageCount);
                getProxyIP(proxyWeb);
            }

            CheckProxy checkMyProxy = new CheckProxy();
            checkMyProxy.checkProxy();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: CrawlProxy.getProxyIP
            Author: Yangzheng
       Description: 解析并保存所有代理、
             Input: Document AProxyWeb
            Output: NONE
            Return: private
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/5/21       Yangzheng         Created Function
    *****************************************************************************/
    private void getProxyIP(Document AProxyWeb)
    {
        String proxyString;

        try
        {
            Elements proxyUrlList = AProxyWeb.select("table[class=t6]").select("tr[bgcolor=#ffffff]").select("td:contains(:)");

            for (Element aProxyUrlItem : proxyUrlList)
            {
                proxyString = aProxyUrlItem.text().trim();

                if (StringUtils.isNotEmpty(proxyString))
                {
                    Proxy aProxy = new Proxy();

                    String[] proxyInfo = proxyString.split(":");

                    aProxy.setProxyHost(proxyInfo[0]);
                    aProxy.setProxyPort(Integer.valueOf(proxyInfo[1]));

                    proxyList.add(aProxy);
                    System.out.println(proxyString);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}


/*****************************************************************************
    Class Name: Proxy
        Author: Yangzheng
   Description: 代理类
       Caution:
  --------------------------------------------------------------------------
        Date          Author             Description
     2013/5/16       Yangzheng           Created Class
*****************************************************************************/
class Proxy
{
    private String proxyHost;
    private int proxyPort;
    private long responseTime;

    public String getProxyHost()
    {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
    }

    public long getResponseTime()
    {
        return responseTime;
    }

    public void setResponseTime(long responseTime)
    {
        this.responseTime = responseTime;
    }
}

