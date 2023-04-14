package com.whl.spring.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whl.spring.bean.FileInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/file")
public class FileController {
    private static final String FILE_STORE_DIRECTORY = ".." + File.separator + "upload";

    @GetMapping("/list")
    public List<FileInfo> list(HttpServletRequest request) throws Exception {
        final String PATH = request.getServletContext().getRealPath("") + FILE_STORE_DIRECTORY;
        File[] files = new File(PATH).listFiles();
        List<FileInfo> datas = new ArrayList<FileInfo>();

        if (files != null && files.length > 0) {
            for (File file : files) {
                datas.add(this.build(file));
            }
        }
        return datas;
    }

    @PostMapping("/upload")
    public FileInfo upload(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file) throws Exception {
        final String PATH = request.getServletContext().getRealPath("") + FILE_STORE_DIRECTORY;
        String filename = file.getOriginalFilename();
        String suffix = "";

        if (filename.contains(".")) {
            suffix = filename.substring(filename.lastIndexOf("."));
        }
        String storeFileName = UUID.randomUUID().toString() + suffix;
        File destination = new File(PATH + File.separator + storeFileName);
        FileUtils.copyInputStreamToFile(file.getInputStream(), destination);
        return this.build(destination);
    }

    @GetMapping("/download/{name}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable String name) throws Exception {
        final String PATH = request.getServletContext().getRealPath("") + FILE_STORE_DIRECTORY;
        File file = new File(PATH + File.separator + name);

        if (file.exists()) {
            response.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Cache-Control", "public,max-age=604800");
            try (InputStream is = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                IOUtils.copyLarge(is, os);
                os.flush();
            }
            return;
        }
        request.getRequestDispatcher("/404").forward(request, response);
    }

    private FileInfo build(File file) {
        return new FileInfo(file.getName(), file.length(), file.lastModified());
    }

}
