package com.byb.tools.doc.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.spire.doc.*;
import com.spire.doc.collections.ParagraphCollection;
import com.spire.doc.documents.BorderStyle;
import com.spire.doc.documents.TableRowHeightType;
import com.spire.doc.fields.TextRange;
import com.spire.doc.formatting.Border;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @Description
 * @Author baiyanbing
 * @Date 2022/8/23 4:02 下午
 */
public class DocTool {


    public static List<String> getParagraphs(InputStream input) {
        Document doc = new Document();
        doc.loadFromStream(input, FileFormat.Auto);
        ParagraphCollection paragraphs = doc.getSections().get(0).getParagraphs();

        List<String> textList = new ArrayList<String>();
        for(int i = 0; i < paragraphs.getCount(); i++) {
            String text = paragraphs.get(i).getText();
            if(StrUtil.isBlank(text)) {
                continue;
            }
            textList.add(paragraphs.get(i).getText());
        }
        return textList;
    }

    public static ByteArrayOutputStream createTable(List<String> textList) {


        //创建Word文档
        Document document = new Document();
        //添加一个section
        Section section = document.addSection();

        // 行数
        int rowsNum = textList.size() / 2;
        // 列数
        int columnsNum = 3;

        // 列宽系数
        float[] columnsWidthsRate = {0.08f, 0.46f, 0.46f};

        //添加表格
        Table table = section.addTable(true);
        //设置表格是否跨页断行
        table.getTableFormat().isBreakAcrossPages(false);
        //设置表格的行数和列数
        table.resetCells(rowsNum, columnsNum);

        // 绘制表头（空白行）
        //TableRow tableHeader = table.getRows().get(0);
        //for(int i =0; i < columnsNum; i++) {
        //    TableCell cell = tableHeader.getCells().get(i);
        //    tableHeader.setHeightType(TableRowHeightType.Auto);
        //    tableHeader.getRowFormat().setBackColor(Color.white);
        //    drawCell(cell, StrUtil.EMPTY, table.getWidth() * columnsWidthsRate[i]);
        //}

        int seqNum = 0;
        Iterator<String> it = textList.iterator();
        //添加数据到剩余行
        for (int r = 0; r < rowsNum; r++) {

            TableRow dataRow = table.getRows().get(r);
            dataRow.setHeightType(TableRowHeightType.Auto);
            dataRow.getRowFormat().setBackColor(Color.white);

            if (!it.hasNext()) {
                break;
            }

            String cell2Value = StrUtil.trimToEmpty(it.next());
            String cell3Value = StrUtil.trimToEmpty(it.next());

            TableCell cell0 = dataRow.getCells().get(0);
            TableCell cell1 = dataRow.getCells().get(1);
            TableCell cell2 = dataRow.getCells().get(2);

            drawCell(cell1, cell2Value, table.getWidth() * columnsWidthsRate[1]);
            drawCell(cell2, cell3Value, table.getWidth() * columnsWidthsRate[2]);

            boolean isTopicRow = StrUtil.startWith(cell2Value, "主题") && StrUtil.startWith(cell3Value, "来源");
            if(isTopicRow){
                drawCell(cell0, r == 0 ? DateUtil.format(new Date(), "yyyy") :StrUtil.EMPTY, table.getWidth() * columnsWidthsRate[0]);
                drawCell(cell0, r == 0 ? DateUtil.format(new Date(), "MMdd") :StrUtil.EMPTY, table.getWidth() * columnsWidthsRate[0]);
                dataRow.getCells().forEach(cell -> ((TableCell)cell).getCellFormat().setBackColor(new Color(128, 198, 135)));
            }else {
                drawCell(cell0, String.valueOf(++seqNum), table.getWidth() * columnsWidthsRate[0]);
            }
        }

        //保存文档
        table.autoFit(AutoFitBehaviorType.Fixed_Column_Widths);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.saveToStream(baos, FileFormat.Docx_2013);
        return baos;
    }

    private static void drawCell(TableCell cell, String text, float width) {
        cell.setCellWidth(width, CellWidthType.Point);
        List<Border> borders = CollUtil.newArrayList(cell.getCellFormat().getBorders().getLeft(),
                cell.getCellFormat().getBorders().getTop(),
                cell.getCellFormat().getBorders().getRight(),
                cell.getCellFormat().getBorders().getBottom());
        borders.stream().forEach(border -> {
            border.setBorderType(BorderStyle.Hairline);
            border.setLineWidth(1.0F);
            border.setColor(Color.GRAY);
        });

        TextRange textRange = cell.addParagraph().appendText(text);
        textRange.getCharacterFormat().setFontSize(9f);
        textRange.getCharacterFormat().setTextColor(new Color(89, 89, 89));
    }

    public static String uploadToFileServer(byte[] bytes, String filename) {
        HttpResponse httpResponse = HttpUtil.createPost("http://192.168.2.200:5010/group1/upload")
                .form("file", bytes, filename)
                .execute();
        return httpResponse.body();
    }

}
