package com.dzero.filedemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author dzero
 */
@RequestMapping(value = "/file")
@RestController
public class FileController {

    @GetMapping
    public void getFile(HttpServletResponse response, @RequestParam("name") String name) {
        ServletOutputStream out = null;
        FileInputStream fileIps = null;
        try {
            String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("static/logo.svg")).getFile();
            //获取文件存放的路径
            File file = new File(url);
            if (name == null) {
                name = file.getName();
            } else {
                name += ".svg";
            }
            if (!file.exists()) {
                return;
            }
            String fileName = new String(name.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            fileIps = new FileInputStream(file);
            response.setContentType("multipart/form-data");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            out = response.getOutputStream();
            //读取文件流
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = fileIps.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (fileIps != null) {
                    fileIps.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) {
        String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("static/logo.svg")).getFile();
        File file = new File(path);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("template.svg", "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            response.setContentType("application/octet-stream");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            outputStream.write(buffer);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
