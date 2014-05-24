/**
 * @(#)CrawlHelper.java
 *
 * SCrawler application
 *
 * @author
 * @version 1.00 2012/12/25
 */

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CrawlHelper
{
    private static Logger logger = LogManager.getLogger(CrawlHelper.class.getName());

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByJsoup
            Author: Yangzheng
       Description: ����������ַ��ȡָ�����ҳ�ĵ�
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByJsoup(String url) throws Exception
    {
        Document doc = null;
        int retryCount = 3;

        try
        {
            do
            {
                // ��Ҫ���ó�ʱ�����
                Connection conn = Jsoup.connect(url).timeout(5000);
                doc = conn.get();
                System.out.println("-----------��ȡĿ����ҳԴ����ɹ�------------");
            }
            while ((null == doc) && (retryCount-- > 0));

            // ����ʱ�����»�ȡ

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return doc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByJsoup
            Author: Yangzheng
       Description: ��������baidu��ַ��ȡָ�����ҳ�ĵ�
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByJsoupBaidu(String url) throws Exception
    {
        Document doc = null;
        int retryCount = 3;
        String cookie  = "BAIDUID=2A0A83AC95E5210D65AD3A82BCB478EB:FG=1; BAIDU_WISE_UID=6474BEFA47428CBF6D4A78560DE0FBAF; TTKlinkFirst=1";
        String cookie2 = "BAIDUID=34C69E5BF716D4A11F60F746E7EEB71C:FG=1";

        try
        {
            do
            {
                // ��Ҫ���ó�ʱ�����
                Connection conn = Jsoup.connect(url)
                                  .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1")
                                  .header("Cookie", cookie)
                                  .timeout(5000);
                doc = conn.get();
                System.out.println("-----------��ȡĿ����ҳԴ����ɹ�------------");
            }
            while ((null == doc) && (retryCount-- > 0));

            // ����ʱ�����»�ȡ

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return doc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByJsoupDianping
            Author: Yangzheng
       Description: ��ȡ���ڵ�������ҳ
             Input: String url
            Output: NONE
            Return: NONE
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/8       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByJsoupDianping(String url) throws Exception
    {
        Document webDoc = null;
        int retryCount = 3;
        String userAgent      = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";
        String DianpingCookie = "JSESSIONID=4E02874863D64B753C9DD5F95EC35906; uniquekey=Bv23Z8wvAC3EQ5CVegZAI6RFkHxITKV4; _hc.v=\"\\\"cc84bd59-a436-4c97-8ffd-c5fa6f414f7b.1362575178\\\"\"; TTKlinkFirst=1; tt.tt=201392394.20480.0000";
        String dianpingCookie = "_hc.v=\"\\\"42120288-1253-43c6-ab55-e291fc123e57.1368451572\\\"\"; JSESSIONID=63A480D1FD9E7AFB76E56C24F47A28B9; aburl=1; cy=10; __utma=1.199411877.1368451573.1368451573.1368451573.1; __utmb=1.3.10.1368451573; __utmc=1; __utmz=1.1368451573.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); lb.dp=3003580682.20480.0000";

        try
        {
            logger.entry();

            do
            {
                // ��Ҫ���ó�ʱ�����
                Connection conn = Jsoup.connect(url)
                                  .header("User-Agent", userAgent)
                                  .header("Cookie", dianpingCookie)
                                  .timeout(5000);
                webDoc = conn.get();

                if (null != webDoc)
                {
                    System.out.println("<-----------��ȡĿ����ҳԴ����ɹ�------------>");
                }
            }   // ����ʱ�����»�ȡ

            while ((null == webDoc) && (retryCount-- > 0));
        }
        catch (Exception ex)
        {
            logger.error("��ȡ��ҳʧ��" + ex);
            ex.printStackTrace();
        }

        logger.exit();
        return webDoc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByJsoupFantong
            Author: Yangzheng
       Description: ��ͳ��
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByJsoupFantong(String url) throws Exception
    {
        Document webDoc = null;
        int retryCount = 3;
        String userAgent     = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1";
        String FantongCookie = "stat_visitorid=7262397d398688cd498d9d2dc96b7e21; TTKlinkFirst=1; sh0=%E9%A4%90%E9%A6%86;";

        try
        {
            do
            {
                // ��Ҫ���ó�ʱ�����
                Connection conn = Jsoup.connect(url)
                                  .header("User-Agent", userAgent)
                                  .header("Cookie", FantongCookie)
                                  .timeout(10000);
                webDoc = conn.get();

                if (null != webDoc)
                {
                    System.out.println("<-----------��ȡĿ����ҳԴ����ɹ�------------>");
                }
            }   // ����ʱ�����»�ȡ

            while ((null == webDoc) && (retryCount-- > 0));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return webDoc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByJsoupWithCookie
            Author: Yangzheng
       Description: ����cookie��
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByJsoupWithCookie(String url) throws Exception
    {
        Document doc = null;
        int retryCount = 3;
        String cookie = "_hc.v=\"\\\"db4e625d-5487-4e43-bebc-3b033259416d.1358334724\\\"\"; is=476219343015; ano=hroDIwQEzgEkAAAANzU5ZDg5MjYtYzY5Mi00YTFmLThmM2ItZTk0MGJjOGE2NGNktMwcDtAA3dshNFWrDsojll8HM501; uniquekey=pUfdh6U8tvHThaBCj0B1vfBrwVJDpckM; TTKlinkFirst=1; JSESSIONID=25C79C25A2144C3135105BE72720A58F; aburl=1; wapvisithistory=4290496|5484475|585534|2519656|3380797; cy=341; cye=hongkong; __utma=1.2038112202.1358334722.1360054687.1360058532.9; __utmb=1.3.10.1360058532; __utmc=1; __utmz=1.1360058532.9.4.utmcsr=dphome|utmccn=(not%20set)|utmcmd=banner|utmctr=daydayup; cityid=341; citypinyin=hongkong; cityname=6aaZ5riv; __utma=1.2038112202.1358334722.1360054687.1360058532.9; __utmb=1.4.10.1360058532; __utmc=1; __utmz=1.1360058532.9.4.utmcsr=dphome|utmccn=(not%20set)|utmcmd=banner|utmctr=daydayup";
        String baiduCookie = "BAIDUID=B18BD6656DCED3FC33397D78E3373775:FG=1; c=104; cn=%E6%98%86%E6%98%8E%E5%B8%82; TTKlinkFirst=1";

        try
        {
            do
            {
                // ��Ҫ���ó�ʱ�����
                Connection conn = Jsoup.connect(url)
                                  .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1")
                                  .header("Cookie", baiduCookie)
                                  .timeout(5000);
                doc = conn.get();
                System.out.println("-----------��ȡĿ����ҳԴ����ɹ�------------");
            }
            while ((null == doc) && (retryCount-- > 0));

            // ����ʱ�����»�ȡ

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return doc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByJsoupPost
            Author: Yangzheng
       Description: ʹ��POSTģʽ
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByJsoupPost(String url) throws Exception
    {
        Document doc = null;
        int retryCount = 3;
        String refferUrl = "http://www.dianping.com/search/category/341/10/";
        String cookie = "uniquekey=jVDme1220bQIJ1jfY9JMhVW8LXNWBFdc; _hc.v=\"\\\"2c19eac5-e28f-4886-bf96-a7053136acf8.1360062401\\\"\"; TTKlinkFirst=1; cityid=341; citypinyin=hongkong; cityname=6aaZ5riv; JSESSIONID=91AA9287EE3876976E2511061A5C17C2; __utma=1.1423292728.1360062400.1360062400.1360062400.1; __utmb=1.5.9.1360062413369; __utmc=1; __utmz=1.1360062400.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)";
        String postData = "";

        try
        {
            do
            {
                // ��Ҫ���ó�ʱ�����
                // ���ڵ�����������������ƣ�header��Ҫ�����������Ϣ����cookie��dataò�Ʋ�����
                Connection conn = Jsoup.connect(url)
                                  //.header("Host", "www.dianping.com")
                                  //.header("Connection", "keep-alive")
                                  //.header("Content-Length", "190")
                                  //.header("Origin", "http://www.dianping.com")
                                  //.header("X-Request", "JSON")
                                  //.header("X-Requested-With", "XMLHttpRequest")
                                  //.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8;")
                                  //.header("Accept", "application/json, text/javascript")
                                  //.header("Referer", "http://www.dianping.com/search/category/341/10/g0r0")
                                  //.header("Accept-Encoding", "gzip,deflate,sdch")
                                  //.header("Accept-Language", "zh-CN,zh;q=0.8")
                                  //.header("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3")
                                  .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1")
                                  .cookie("uniquekey", "jVDme1220bQIJ1jfY9JMhVW8LXNWBFdc")
                                  .cookie("_hc.v", "\"\\\"2c19eac5-e28f-4886-bf96-a7053136acf8.1360062401\\\"\"")
                                  //.data("do", "getcorr")
                                  //.data("t", "10")
                                  //.data("d", "0")
                                  //.data("cityId", "341")
                                  //.data("s", "3380797,2519656,571270,2841064,574261,2779777,2296084,559186,4178369,1922446,3137893,3380857,4521230,558070,1888498")
                                  //.data("limit", "3")
                                  .timeout(5000);
                //.cookie("Cookie",cookie)

                doc = conn.post();
                System.out.println("------------��ȡĿ����ҳԴ����ɹ�---------------");
            }
            while ((null == doc) && (retryCount-- > 0));

            // ����ʱ�����»�ȡ
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return doc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getWebByHttpclientDianpingGet
            Author: Yangzheng
       Description: ��httpclient��ȡ��ҳ
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getWebByHttpclientDianpingGet(String webUrl) throws Exception
    {
        String dianpingCookie = "_hc.v=\"\\\"42120288-1253-43c6-ab55-e291fc123e57.1368451572\\\"\"" +
                                "JSESSIONID=63A480D1FD9E7AFB76E56C24F47A28B9; aburl=1; cy=10;" +
                                " __utma=1.199411877.1368451573.1368451573.1368451573.1; __utmb=1.3.10.1368451573;" +
                                " __utmc=1; __utmz=1.1368451573.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none);" +
                                "lb.dp=3003580682.20480.0000";
        HttpClient httpClient = new DefaultHttpClient();

        String webString = null;
        Document webDoc  = null;

        try
        {
            logger.entry();
            HttpGet httpGet = new HttpGet(webUrl);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
            httpGet.setHeader("Cookie", dianpingCookie);

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // ��ӡ��Ӧ״̬
            System.out.println(response.getStatusLine());

            if (entity != null)
            {
                webString = EntityUtils.toString(entity);
                webDoc = Jsoup.parse(webString);
                System.out.println("��ȡ��ҳ�ĵ��ɹ�");
            }
        }
        catch (Exception e)
        {
            logger.error("��ȡ��ҳʧ��" + e);
            e.printStackTrace();
        }
        finally
        {
            //�ر����ӣ��ͷ���Դ
            httpClient.getConnectionManager().shutdown();
        }

        logger.exit();
        return webDoc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getDianpingWebByHCProxy
            Author: Yangzheng
       Description: ��httpclientץȡͼƬ
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public Document getDianpingWebByHCProxy(String webUrl, HttpHost proxyHttpHost) throws Exception
    {
        String dianpingCookie = "_hc.v=\"\\\"42120288-1253-43c6-ab55-e291fc123e57.1368451572\\\"\"" +
                                "JSESSIONID=63A480D1FD9E7AFB76E56C24F47A28B9; aburl=1; cy=10;" +
                                " __utma=1.199411877.1368451573.1368451573.1368451573.1; __utmb=1.3.10.1368451573;" +
                                " __utmc=1; __utmz=1.1368451573.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none);" +
                                "lb.dp=3003580682.20480.0000";
        HttpClient httpClient = new DefaultHttpClient();
        DefaultHttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(0, false);

        String webString = null;
        Document webDoc  = null;

        try
        {
            logger.entry();

            // ���ó�ʱʱ��
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1500);
            // ���ô���
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHttpHost);
            // ���ó�ʱģʽ
            ((AbstractHttpClient)httpClient).setHttpRequestRetryHandler(retryHandler);

            // ���÷���Ŀ��ҳ��
            HttpGet httpGet = new HttpGet(webUrl);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
            httpGet.setHeader("Cookie", dianpingCookie);

            System.out.println(" ͨ������ " + proxyHttpHost + " ��ȡ���ڵ���ҳ��");

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            // ��ӡ��Ӧ״̬������ȷ��Ӧ
            if (200 == response.getStatusLine().getStatusCode() && entity != null)
            {
                System.out.println(" ������Ч��ץȡ�ɹ�\n " + response.getStatusLine());
                webString = EntityUtils.toString(entity);
                webDoc = Jsoup.parse(webString);
                System.out.println(" ��ȡ��ҳ�ĵ��ɹ�");
            }
        }
        catch (Exception ex)
        {
            logger.error("��ȡ��ҳʧ��" + ex);
            ex.printStackTrace();
        }
        finally
        {
            //�ر����ӣ��ͷ���Դ
            httpClient.getConnectionManager().shutdown();
        }

        logger.exit();
        return webDoc;
    }

    /*****************************************************************************
     Function Name: CrawlHelper.getImgByHttpclientGet
            Author: Yangzheng
       Description: ��httpclientץȡͼƬ
             Input: String url
            Output: NONE
            Return: Document
           Caution:
      --------------------------------------------------------------------------
            Date          Author             Description
         2013/3/15       Yangzheng         Created Function
    *****************************************************************************/
    public byte[] getImgByHttpclientGet(String imgUrl) throws Exception
    {
        String cookie = "_hc.v=\"\\\"db4e625d-5487-4e43-bebc-3b033259416d.1358334724\\\"\"; is=476219343015; ano=hroDIwQEzgEkAAAANzU5ZDg5MjYtYzY5Mi00YTFmLThmM2ItZTk0MGJjOGE2NGNktMwcDtAA3dshNFWrDsojll8HM501; uniquekey=pUfdh6U8tvHThaBCj0B1vfBrwVJDpckM; TTKlinkFirst=1; JSESSIONID=25C79C25A2144C3135105BE72720A58F; aburl=1; wapvisithistory=4290496|5484475|585534|2519656|3380797; cy=341; cye=hongkong; __utma=1.2038112202.1358334722.1360054687.1360058532.9; __utmb=1.3.10.1360058532; __utmc=1; __utmz=1.1360058532.9.4.utmcsr=dphome|utmccn=(not%20set)|utmcmd=banner|utmctr=daydayup; cityid=341; citypinyin=hongkong; cityname=6aaZ5riv; __utma=1.2038112202.1358334722.1360054687.1360058532.9; __utmb=1.4.10.1360058532; __utmc=1; __utmz=1.1360058532.9.4.utmcsr=dphome|utmccn=(not%20set)|utmcmd=banner|utmctr=daydayup";
        String baiduCookie = "BAIDUID=118072A90C21AE96E48932742B95E2CA:FG=1";
        HttpClient httpClient = new DefaultHttpClient();
        byte[] imgBits = null;

        try
        {
            logger.entry();
            HttpGet httpGet = new HttpGet(imgUrl);
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
            //httpGet.setHeader("Cookie", cookie);

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            // ��ӡ��Ӧ״̬
            System.out.println(response.getStatusLine());

            if (entity != null)
            {
                imgBits = EntityUtils.toByteArray(entity);
                System.out.println("����ͼƬ�ɹ�");
            }

            // GetMethod getMethod = new GetMethod(imgUrl);

            // ÿ�η�������Ȩ����ַʱ�����ǰ��� cookie ��Ϊͨ��֤
            // getMethod.setRequestHeader("cookie", cookie);
            // getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");

            // �㻹����ͨ�� PostMethod/GetMethod ���ø�������������
            // ���磬referer ���������ģ�UA ���������涼������Լ���˭�����������������
            // getMethod.setRequestHeader("Referer", "http://unmi.cc");

            // httpClient.executeMethod(getMethod);

            // ��ӡ���������ݣ�����һ���Ƿ�ɹ�
            // String text = getMethod.getResponseBodyAsString();
            // System.out.println(text);
        }
        catch (Exception e)
        {
            logger.error("����ͼƬʧ��" + e);
            e.printStackTrace();
        }
        finally
        {
            //�ر����ӣ��ͷ���Դ
            httpClient.getConnectionManager().shutdown();
        }

        logger.exit();
        return imgBits;
    }
}
