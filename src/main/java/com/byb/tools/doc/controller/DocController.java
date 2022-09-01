package com.byb.tools.doc.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.byb.tools.doc.utils.DocTool;
import com.byb.tools.doc.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author baiyanbing
 * @Date 2022/8/23 4:00 下午
 */
@Slf4j
@RestController
@RequestMapping("doc")
public class DocController {

    @PostMapping("uploadAndConvert")
    public Object uploadAndConvertDoc(MultipartFile file, HttpServletResponse response) throws IOException {

        log.info("收到文件 - 文件名：{}, 文件大小：{}", file.getOriginalFilename(), FileUtil.readableFileSize(file.getSize()));
        String filename = FileNameUtil.getPrefix(file.getOriginalFilename());
        String extension = FileNameUtil.extName(file.getOriginalFilename());

        List<String> paragraphs = DocTool.getParagraphs(file.getInputStream());
        log.info("读取文件 - 段落数：{}", paragraphs.size());

        ByteArrayOutputStream baos = DocTool.createTable(paragraphs);
        log.info("转换文件 - 文件大小：{}", FileUtil.readableFileSize(baos.size()));

        String fileUrl = DocTool.uploadToFileServer(baos.toByteArray(), filename + "." + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN) + "." + extension);
        log.info("上传文件 - 文件地址：{}", fileUrl);

        return Result.ok(fileUrl);
    }
}
