package com.java.xdd.word.util;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DocUtil {

    public static void main(String[] args) throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("${title}", "标题");
        params.put("${createUnit}", "单位");
        params.put("${id}", "编号");
        params.put("${date}", "2017-05-05");
        params.put("${level}", "1");
        params.put("${place}", "深圳");
        params.put("${type}", "火灾");
        params.put("${loss}", "1000");
        params.put("${result}", "这是结果");
        params.put("${check}", "小明");
        params.put("${create}", "本人");

        //new DocUtil().createDoc(params, "temp", "D:\\JackWorkSpace\\Java\\yishuju_dubbo\\xdd-netty\\src\\main\\");

        CustomXWPFDocument doc = DocUtil.generateWord
                (params, "D:\\JackWorkSpace\\Java\\yishuju_dubbo\\xdd-netty\\src\\main\\resources\\temp.docx");
        FileOutputStream fopts = new FileOutputStream("D:\\yan.docx");
        doc.write(fopts);
        fopts.close();
    }

    public static CustomXWPFDocument generateWord(Map<String, Object> param, String template) {
        CustomXWPFDocument doc = null;
        try {
            OPCPackage pack = POIXMLDocument.openPackage(template);
            doc = new CustomXWPFDocument(pack);
            //XWPFDocument xwpfDocument = new XWPFDocument(pack);
            if (param != null && param.size() > 0) {
                //处理段落
                List<XWPFParagraph> paragraphList = doc.getParagraphs();
                processParagraphs(paragraphList, param, doc);
                //处理表格
                Iterator<XWPFTable> it = doc.getTablesIterator();
                while (it.hasNext()) {
                    XWPFTable table = it.next();
                    String text = table.getText();
                    List<XWPFTableRow> rows = table.getRows(); //获取所有的行
                    for (XWPFTableRow row : rows) {
                        List<XWPFTableCell> cells = row.getTableCells(); //获取所有的列
                        for (XWPFTableCell cell : cells) {
                            List<XWPFParagraph> paragraphListTable = cell.getParagraphs();
                            XWPFParagraph xwpfParagraph = paragraphListTable.get(0);
                            String text1 = xwpfParagraph.getText();
                            IBody body = xwpfParagraph.getBody();
                            String text2 = cell.getText();
                            if ("${level}".equals(text2)) {
                                text2 = text2.replace("${level}", "1");
                                List<XWPFRun> runs = paragraphListTable.get(0).getRuns();
                                runs = null;
                                cell.setText(text2);
                            }
                            processParagraphs(paragraphListTable, param, doc);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    /**
     * 处理段落
     *
     * @param paragraphList
     * @param param
     * @param doc
     */
    public static void processParagraphs(List<XWPFParagraph> paragraphList, Map<String, Object> param, CustomXWPFDocument doc) {
        if (paragraphList != null && paragraphList.size() > 0) {
            for (XWPFParagraph paragraph : paragraphList) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    String text = run.getText(0);
                    if (text != null) {
                        boolean isSetText = false;
                        for (Map.Entry<String, Object> entry : param.entrySet()) {
                            String key = entry.getKey();
                            if (text.indexOf(key) != -1) {
                                isSetText = true;
                                Object value = entry.getValue();
                                if (value instanceof String) { //文本替换
                                    text = text.replace(key, value.toString());
                                } else if (value instanceof Map) { //图片替换
                                    text = text.replace(key, "");
                                    Map pic = (Map) value;
                                    int width = Integer.parseInt(pic.get("width").toString());
                                    int height = Integer.parseInt(pic.get("height").toString());
                                    int picType = getPictureType(pic.get("type").toString());
                                    byte[] byteArray = (byte[]) pic.get("content");
                                    ByteArrayInputStream byteInputStream = new ByteArrayInputStream(byteArray);
                                    try {
                                        String ind = doc.addPictureData(byteInputStream, picType);
                                        doc.createPicture(Integer.valueOf(ind), width, height, paragraph);
                                    } catch (InvalidFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (isSetText) {
                            run.setText(text, 0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据图片类型获取对应的图片类型代码
     *
     * @param picType
     * @return
     */
    public static int getPictureType(String picType) {
        int res = CustomXWPFDocument.PICTURE_TYPE_PICT;
        if (picType != null) {
            if (picType.equalsIgnoreCase("png")) {
                res = CustomXWPFDocument.PICTURE_TYPE_PNG;
            } else if (picType.equalsIgnoreCase("dib")) {
                res = CustomXWPFDocument.PICTURE_TYPE_DIB;
            } else if (picType.equalsIgnoreCase("emf")) {
                res = CustomXWPFDocument.PICTURE_TYPE_EMF;
            } else if (picType.equalsIgnoreCase("jpg") || picType.equalsIgnoreCase("jpeg")) {
                res = CustomXWPFDocument.PICTURE_TYPE_JPEG;
            } else if (picType.equalsIgnoreCase("wmf")) {
                res = CustomXWPFDocument.PICTURE_TYPE_WMF;
            }
        }
        return res;
    }


}