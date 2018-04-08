package com.moviebook.ar_dynamic12;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML解析器，用于解析模型name和url的一一对应关系
 */
public class XmlParseUtil {
    private final String ns = null;
    private Context context;

    public XmlParseUtil(Context context) {
        this.context = context;
    }

    public List<ImageTarget> parse(String xmlPath) {
        List<ImageTarget> list = new ArrayList<ImageTarget>();
        ImageTarget person = null;
        InputStream stream = null;
        XmlPullParser xmlParse = Xml.newPullParser();
        try {

            File file = new File(xmlPath);
            stream = new FileInputStream(file);
            xmlParse.setInput(stream, "utf-8");
            int evnType = xmlParse.getEventType();
            while (evnType != XmlPullParser.END_DOCUMENT) {
                switch (evnType) {
                    case XmlPullParser.START_TAG:
                        String tag = xmlParse.getName();
                        if (tag.equalsIgnoreCase("imageTarget")) {
                            person = new ImageTarget();
                            person.setId(xmlParse.getAttributeValue(ns, "id"));
                        } else if (person != null) {
                            if (tag.equalsIgnoreCase("name")) {
                                person.setName(xmlParse.nextText());
                            } else if (tag.equalsIgnoreCase("url")) {
                                person.setUrl(xmlParse.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParse.getName().equalsIgnoreCase("imageTarget") && person != null) {
                            list.add(person);
                            person = null;
                        }
                        break;
                    default:
                        break;
                }
                evnType = xmlParse.next();
            }
        } catch (Exception e) {
            Log.d("xmlParseUtil", e.toString());
        }
        return list;
    }
}