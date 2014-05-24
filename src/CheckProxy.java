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
       Description: ��������Ч��
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
            System.out.println("-----���ô��������----");

            for (Proxy aProxyItem : CrawlProxy.proxyList)
            {
                HttpHost httpHost = new HttpHost(aProxyItem.getProxyHost(), aProxyItem.getProxyPort());
                long start = System.currentTimeMillis();

                System.out.println("\n------������֤��" + (proxyCount++) + "��,��ʣ��" + (CrawlProxy.proxyList.size() - proxyCount + 2) + "��----------");

                if (excuteProxy(httpHost))
                {
                    long usetime = System.currentTimeMillis() - start;

                    if (usetime > 1000)
                    {
                        System.out.println("-----�������,��ʹ��,��Ӧʱ��̫��:" + usetime + "����----");
                        System.out.println("-----�����������Ч����:" + httpHostList.size() + "----");
                    }

                    else
                    {
                        Document doc = CrHelper.getDianpingWebByHCProxy(testShopUrl, httpHost);

                        if (doc != null)
                        {
                            //System.out.println(doc);
                            // 1.�ҳ��̻�ժҪ��Ԫ��(����:�������˾�����)
                            briefIntroduction = doc.select("h1[class=shop-title]");

                            if (!briefIntroduction.isEmpty())
                            {
                                System.out.println("-----���Ե���: " + briefIntroduction.first().text());
                                System.out.println("-----�������,��ʹ��,ʹ��ʱ��:" + usetime + "����----");
                                aProxyItem.setResponseTime(usetime);
                                httpHostList.add(httpHost);
                                System.out.println("-----����һ���������������Ч����:" + httpHostList.size() + "----");
                            }
                        }
                        else
                        {
                            System.out.println("-----������ҳʧ��,����ʹ��,����ʱ�� :" + usetime + "����----");
                            System.out.println("-----�����������Ч����:" + httpHostList.size() + "----");
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("-----�����������Ч����:" + httpHostList.size() + "----");
            ex.printStackTrace();
        }
    }

    /*****************************************************************************
     Function Name: CheckProxy.excuteProxy
            Author: Yangzheng
       Description: ִ�д������
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
            //���ó�ʱʱ��
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHttpHost);
            // ���ó�ʱ����
            DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(0, false);
            httpclient.setHttpRequestRetryHandler(retryHandler);

            HttpHost target = new HttpHost("www.dianping.com");//, 80, "http"
            HttpGet req = new HttpGet("/");

            System.out.println("-----ͨ������ " + proxyHttpHost + " ���� " + target);
            HttpResponse rsp = httpclient.execute(target, req);
            HttpEntity entity = rsp.getEntity();

            System.out.println("----------------------------------------");
            System.out.println("-----���������������Ӧ����:");
            System.out.println("-----" + rsp.getStatusLine());
            System.out.println("----------------------------------------");

            if (200 == rsp.getStatusLine().getStatusCode())
            {
                System.out.println("-----������Ч");
            }
            else
            {
                System.out.println("-----����ʱ����Ч");
                checkOK = false;
            }
        }
        catch (Exception ex)
        {
            checkOK = false;
            System.out.println("-----�쳣,����ʱ����Ч-----------");
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
