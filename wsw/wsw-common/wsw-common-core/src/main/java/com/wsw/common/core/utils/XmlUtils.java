package com.wsw.common.core.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * XML解析工具类
 *
 * @author chenzhongxin
 * @date 2024/12/30
 */
public class XmlUtils {
    public static Map<String, String> parseXml(String xmlData) throws Exception {
        Map<String, String> result = new HashMap<>();
        Document document = DocumentHelper.parseText(xmlData);
        Element root = document.getRootElement();
        for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
            Element element = it.next();
            result.put(element.getName(), element.getText());
        }
        return result;
    }
}
