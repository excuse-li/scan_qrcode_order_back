package cn.llq.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author huhui
 * @Date 2019/8/19 15:44
 * @Description
 */
public class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(cn.llq.utils.HttpClientUtil.class);

    private static CloseableHttpClient httpClient = null;

    private static final Object syncLock = new Object();

    private static final int DEFAULT_TIME_OUT = 5000;

    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            if (arr[1].contains("?")) {
                String[] temp = arr[1].split("\\?");
                port = Integer.parseInt(temp[0]);
            } else {
                port = Integer.parseInt(arr[1]);
            }
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(500, 50, 200, hostname, port);
                }
            }
        }
        return httpClient;
    }

    public static CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory factory = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory socketFactory = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("http", factory)
                .register("https", socketFactory).build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);

        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由的基础增强
        cm.setDefaultMaxPerRoute(maxPerRoute);

        // 将目标主机的最大连接数增加
        HttpHost httpHost = new HttpHost(hostname, port);
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(DEFAULT_TIME_OUT).build();
        cm.setDefaultSocketConfig(socketConfig);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        return httpClient;
    }

    private static void config(HttpRequestBase httpRequestBase, int timeout) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setStaleConnectionCheckEnabled(true).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * http get 请求方式,带请求头
     *
     * @param url
     * @param header
     * @return
     */
    public static String execGet(String url, Map<String, String> header) {
        return execGetByTimeout(url, header, DEFAULT_TIME_OUT);
    }

    /**
     * http get 请求方式
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return execGetByTimeout(url, null, DEFAULT_TIME_OUT);
    }

    public static String execGetByTimeout(String url, Map<String, String> header, int timeOut) {

        logger.info("get method,url=[{}]", url);

        CloseableHttpClient httpClient = getHttpClient(url);

        HttpGet httpget = new HttpGet(url);
        config(httpget, timeOut);
        if (header != null) {
            for (Map.Entry<String, String> property : header.entrySet()) {
                httpget.setHeader(property.getKey(), property.getValue());
            }
        }

        HttpEntity httpEntity = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpget);
            int status = response.getStatusLine().getStatusCode();
            if (status >= HttpStatus.SC_OK
                    && status < HttpStatus.SC_MULTIPLE_CHOICES
                    && status != HttpStatus.SC_NO_CONTENT) {
                httpEntity = response.getEntity();
                String resultContent = EntityUtils.toString(httpEntity,
                        Charset.forName("utf-8"));

                logger.info("result:[{}]", resultContent);
                if (httpEntity != null) {
                    EntityUtils.consume(httpEntity);
                }

                return resultContent;
            } else {
                httpEntity = response.getEntity();
                if (httpEntity != null) {
                    EntityUtils.consume(httpEntity);
                }
                httpget.abort();
                if (status != HttpStatus.SC_NO_CONTENT) {
                    logger.error("error in get,url=[{}],errorCode=[{}]",
                            new Object[]{url,
                                    response.getStatusLine().getStatusCode()});
                }
                return null;
            }

        } catch (Exception ex) {
            logger.error(
                    "err in get url=[{}],get method,exception=[{}]",
                    url, ex);
            if (httpget != null) {
                httpget.abort();
            }
        } finally {
            try {
                if (httpEntity != null) {
                    EntityUtils.consume(httpEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String doPost(String url, Map<String, String> paramMap, Map<String, String> header,
                                Object... params) {
        return execPostByTimeout(url, paramMap, header, DEFAULT_TIME_OUT, params);
    }

    /**
     * Post 请求方式
     *
     * @param url
     * @param paramMap
     * @param params
     * @return
     */
    public static String execPostByTimeout(String url, Map<String, String> paramMap, Map<String, String> header,
                                           int timeOut, Object... params) {

        logger.info("get by post method,url=[{}]", url);

        CloseableHttpClient httpClient = getHttpClient(url);
        HttpEntity httpEntity = null;
        StringEntity stringEntity = null;
        CloseableHttpResponse response = null;
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            config(httpPost, timeOut);
            if (header != null) {
                for (Map.Entry<String, String> property : header.entrySet()) {
                    httpPost.setHeader(property.getKey(), property.getValue());
                }
            }
            stringEntity = getStringEntity(paramMap, params);
            logger.info("get method, param=[{}]", paramMap);
            httpPost.setEntity(stringEntity);

            response = httpClient.execute(httpPost);

            int status = response.getStatusLine().getStatusCode();
            if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES
                    && status != HttpStatus.SC_NO_CONTENT) {
                httpEntity = response.getEntity();
                String resultContent = EntityUtils.toString(httpEntity, "utf-8");
                logger.info("result:[{}]", resultContent);
                if (stringEntity != null) {
                    EntityUtils.consume(stringEntity);
                }
                if (httpEntity != null) {
                    EntityUtils.consume(httpEntity);
                }

                return resultContent;
            } else {
                httpEntity = response.getEntity();
                if (httpEntity != null) {
                    EntityUtils.consume(httpEntity);
                }
                httpPost.abort();
                if (status != HttpStatus.SC_NO_CONTENT) {
                    logger.error("error in post,url=[{}],paramMap=[{}],params=[{}],errorCode=[{}]",
                            new Object[]{url, paramMap, params, response.getStatusLine().getStatusCode()});
                }
                return null;
            }
        } catch (Exception ex) {
            logger.error("err in post from url=[{}],param=[{},err=[{}]],post method", new Object[]{url, paramMap, ex});
            if (httpPost != null) {
                httpPost.abort();
            }
        } finally {
            try {
                if (httpEntity != null) {
                    EntityUtils.consume(httpEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (stringEntity != null) {
                    EntityUtils.consume(stringEntity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 发送HttpPost请求，参数为map
     *
     * @param url
     * @param map
     * @return
     */
    public static String sendPost(String url, Map<String, String> map, String jsonData) {
        HttpPost httppost = new HttpPost(url);
        if (!CollectionUtils.isEmpty(map)) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity ufEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            httppost.setEntity(ufEntity);
        }
        if (StringUtils.isNotEmpty(jsonData)) {
            httppost.setEntity(new StringEntity(jsonData, "UTF-8"));
        }
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        try {
            //将返回的数据直接转成String
            result = EntityUtils.toString(entity, "UTF-8");
            System.out.println("-----------------------------------------------------");
            //注意这里不能写成EntityUtils.toString(entity, "UTF-8"),因为EntityUtils只能调用一次，否则会报错：java.io.IOException: Attempted read from closed stream
            System.out.println("Response content: " + result);
            System.out.println("------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static StringEntity getStringEntity(Map<String, String> paramMap,
                                                Object... params) throws UnsupportedEncodingException {

        StringEntity stringEntity = null;
        if (paramMap != null) {
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            if (paramMap != null) {
                for (Map.Entry<String, String> pmap : paramMap.entrySet()) {
                    formparams.add(new BasicNameValuePair(pmap.getKey(), pmap
                            .getValue()));
                }
            }
            stringEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        }
        if (params != null && params.length > 0) {
            stringEntity = new StringEntity((String) params[0], "utf-8");
        }
        return stringEntity;
    }

}
