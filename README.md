SCrawler
========

A crawler to scratch datas of delicious


项目简介
========
这个项目最初是用来抓取几个网站的数据的，使用纯Java完成。
这个项目是本人边学Java前后几个月写完的，架构比较简单，甚至非常初级。
但是还是能够完成抓取指定网页内容、图片，转换GPS坐标等一些功能。


项目依赖
========
本项目使用了几个第三方库来实现一些基本的网页抓取和数据库操作功能，罗列如下：       
1.Jsoup  用于网页的抓取和解析，使用了非常简便的类似CSS选择器，详见：http://jsoup.org    
2.Httpclient  一个强大的Http协议客户端编程工具包，详见：http://hc.apache.org        
3.log4j  一个日志输出工具，用来记录程序运行日志  详见：http://logging.apache.org/log4j/2.x/     
4.MySQL Connector 用于进行数据库操作的驱动包  详见：MySQL Connector      
