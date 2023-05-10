package cn.llq.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * @ClassName XmlUtil
 * @Description xml工具类
 * @Author huhui
 * @Date 2019/8/12 17:32
 */
public class XmlUtil {

    /**
     * @Author huhui
     * @Date 2019/8/13 9:38
     * @Description 把String类型的xml格式字符串转为map
     * @Param [xml]
     * @Return java.util.Map<java.lang.String, java.lang.String>
     */
    public static Map<String, String> xmlToMap(String xml) throws Exception {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<>();
        Document dom = DocumentHelper.parseText(xml);
        Element root = dom.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        return map;
    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, Object> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.toString().trim();

            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value.toString()));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        } catch (Exception ex) {
        }
        return output;
    }

    /**
     * 将Map转换为XML,Map可以多层转
     * @param params 需要转换的map。
     * @param parentName 就是map的根key,如果map没有根key,就输入转换后的xml根节点。
     * @return String-->XML
     */
    @SuppressWarnings("unchecked")
    public static String createXmlByMap(Map map, String parentName) {
        //获取map的key对应的value
        Map<String, Object> rootMap=(Map<String, Object>)map.get(parentName);
        if (rootMap==null) {
            rootMap=map;
        }
        Document doc = DocumentHelper.createDocument();
        //设置根节点
        doc.addElement(parentName);
        String xml = iteratorXml(doc.getRootElement(), parentName, rootMap);
        return formatXML(xml);
    }
    /**
     * 循环遍历params创建xml节点
     * @param element 根节点
     * @param parentName 子节点名字
     * @param params map数据
     * @return String-->Xml
     */
    @SuppressWarnings("unchecked")
    public static String iteratorXml(Element element, String parentName, Map<String, Object> params) {
        Element e = element.addElement(parentName);
        Set<String> set = params.keySet();
        for (Iterator<String> it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (params.get(key) instanceof Map) {
                iteratorXml(e, key, (Map<String, Object>) params.get(key));
            } else if (params.get(key) instanceof List) {
                List<Object> list = (ArrayList<Object>) params.get(key);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof Map) {                           //判断是否最底层的list
                        iteratorXml(e, key, (Map<String, Object>) list.get(i));
                    }else {
                        e.addElement(key).addText((String) list.get(i));
                    }
                }
            } else {
                String value = params.get(key) == null ? "" : params.get(key).toString();
                e.addElement(key).addText(value);
                // e.addElement(key).addCDATA(value);
            }
        }
        return e.asXML();
    }
    /**
     * 格式化xml
     * @param xml
     * @return
     */
    public static String formatXML(String xml) {
        String requestXML = null;
        XMLWriter writer = null;
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new StringReader(xml));
            if (document != null) {
                StringWriter stringWriter = new StringWriter();
                OutputFormat format = new OutputFormat("    ", true);// 格式化，每一级前的空格
                format.setEncoding("utf-8"); //后添加编码格式
                format.setNewLineAfterDeclaration(false); // xml声明与内容是否添加空行
                format.setSuppressDeclaration(false); // 是否设置xml声明头部 false：添加
                format.setNewlines(true); // 设置分行
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
            }
            return requestXML;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }
    /**
     * 第二种
     * 手动组装map转为string
     * @param map
     * @return
     */
    public static String moveMapToXML(Map<?, ?> map) {
        StringBuffer sb = new StringBuffer();
        mapToXML(map, sb);
        return formatXML(sb.toString());
    }
    /**
     *
     * @param map 循环遍历map节点和value
     * @param sb 输出xml
     */
    private static void mapToXML(Map<?, ?> map, StringBuffer sb) {
        Set<?> set = map.keySet();
        for (Iterator<?> it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (value instanceof HashMap) {
                sb.append("<" + key + ">");
                mapToXML((HashMap<?, ?>) value, sb);
                sb.append("</" + key + ">");
            } else if (value instanceof ArrayList) {
                List<?> list = (ArrayList<?>) map.get(key);
                for (int i = 0; i < list.size(); i++) {
                    sb.append("<" + key + ">");
                    Map<?, ?> hm = (HashMap<?, ?>) list.get(i);
                    mapToXML(hm, sb);
                    sb.append("</" + key + ">");
                }
            } else {
                sb.append("<" + key + ">" + value + "</" + key + ">");
            }
        }
    }

}
