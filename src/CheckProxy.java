import java.io.*;
import java.net.*;
import java.util.*;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CheckProxy
{
    public static ArrayList<HttpHost> httpHostList = new ArrayList<HttpHost>();

    /*****************************************************************************
     Function Name: CheckProxy.checkProxy
            Author: Yangzheng
       Description: 检查代理有效性
             Input: NONE
            Output: NONE
            Return: public
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/5/21       Yangzheng         Created Function
    *****************************************************************************/
    public void checkProxy()
    {
        int proxyCount = 1;
        CrawlHelper CrHelper = new CrawlHelper();
        String testShopUrl = "http://www.dianping.com/shop/2635024";
        Elements briefIntroduction = null;

        try
        {
            System.out.println("-----设置代理服务器----");

            for (Proxy aProxyItem : CrawlProxy.proxyList)
            {
                HttpHost httpHost = new HttpHost(aProxyItem.getProxyHost(), aProxyItem.getProxyPort());
                long start = System.currentTimeMillis();

                System.out.println("\n------正在验证第" + (proxyCount++) + "个,还剩下" + (CrawlProxy.proxyList.size() - proxyCount + 2) + "个----------");

                if (excuteProxy(httpHost))
                {
                    long usetime = System.currentTimeMillis() - start;

                    if (usetime > 1000)
                    {
                        System.out.println("-----测试完毕,不使用,响应时间太长:" + usetime + "毫秒----");
                        System.out.println("-----代理服务器有效总数:" + httpHostList.size() + "----");
                    }

                    else
                    {
                        Document doc = CrHelper.getDianpingWebByHCProxy(testShopUrl, httpHost);

                        if (doc != null)
                        {
                            //System.out.println(doc);
                            // 1.找出商户摘要的元素(包括:店名、人均消费)
                            briefIntroduction = doc.select("h1[class=shop-title]");

                            if (!briefIntroduction.isEmpty())
                            {
                                System.out.println("-----测试店名: " + briefIntroduction.first().text());
                                System.out.println("-----测试完毕,可使用,使用时间:" + usetime + "毫秒----");
                                aProxyItem.setResponseTime(usetime);
                                httpHostList.add(httpHost);
                                System.out.println("-----增加一个代理服务器，有效总数:" + httpHostList.size() + "----");
                            }
                        }
                        else
                        {
                            System.out.println("-----请求网页失败,不可使用,请求时间 :" + usetime + "毫秒----");
                            System.out.println("-----代理服务器有效总数:" + httpHostList.size() + "----");
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("-----代理服务器有效总数:" + httpHostList.size() + "----");
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: CheckProxy.excuteProxy
            Author: Yangzheng
       Description: 执行代理访问
             Input: HttpHost proxyHttpHost
            Output: NONE
            Return: boolean
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/5/21       Yangzheng         Created Function
    *****************************************************************************/
    private boolean excuteProxy(HttpHost proxyHttpHost) throws Exception
    {
        boolean checkOK = true;
        DefaultHttpClient httpclient = new DefaultHttpClient();

        try
        {
            //设置超时时间
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHttpHost);
            // 设置超时次数
            DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(0, false);
            httpclient.setHttpRequestRetryHandler(retryHandler);

            HttpHost target = new HttpHost("www.dianping.com");//, 80, "http"
            HttpGet req = new HttpGet("/");

            System.out.println("-----通过代理 " + proxyHttpHost + " 访问 " + target);
            HttpResponse rsp = httpclient.execute(target, req);
            HttpEntity entity = rsp.getEntity();

            System.out.println("----------------------------------------");
            System.out.println("-----代理服务器返回响应代码:");
            System.out.println("-----" + rsp.getStatusLine());
            System.out.println("----------------------------------------");

            if (200 == rsp.getStatusLine().getStatusCode())
            {
                System.out.println("-----代理有效");
            }
            else
            {
                System.out.println("-----代理超时或无效");
                checkOK = false;
            }
        }
        catch (Exception ex)
        {
            checkOK = false;
            System.out.println("-----异常,代理超时或无效-----------");
        }
        finally
        {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        return checkOK;
    }
}
