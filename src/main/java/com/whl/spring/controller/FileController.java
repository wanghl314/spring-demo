package com.whl.spring.controller;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
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

    @GetMapping("/list")
    public List<FileInfo> list(HttpServletRequest request) throws Exception {
        Path path = this.getFileStorePath();
        File[] files = new File(path.toString()).listFiles();
        List<FileInfo> datas = new ArrayList<FileInfo>();

        if (files != null) {
            for (File file : files) {
                datas.add(this.build(file));
            }
        }
        return datas;
    }

    @PostMapping("/upload")
    public FileInfo upload(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String suffix = "";

        if (filename != null && filename.contains(".")) {
            suffix = filename.substring(filename.lastIndexOf("."));
        }
        Path path = this.getFileStorePath();
        Path destination = path.resolve(UUID.randomUUID() + suffix);

        if (!Files.exists(destination.getParent())) {
            Files.createDirectories(destination.getParent());
        }
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return this.build(destination.toFile());
    }

    @GetMapping("/download/{name}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable String name) throws Exception {
        Path path = this.getFileStorePath();
        Path destination = path.resolve(name);

        if (Files.exists(destination)) {
            File file = destination.toFile();
            response.setHeader("Content-Type", this.getContentType(file));
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Cache-Control", "public,max-age=604800");

            try (OutputStream os = response.getOutputStream()) {
                Files.copy(destination, os);
                os.flush();
            }
            return;
        }
        request.getRequestDispatcher("/404").forward(request, response);
    }

    private FileInfo build(File file) {
        return new FileInfo(file.getName(), file.length(), file.lastModified());
    }

    public String getContentType(File file) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (file != null && file.exists()) {
            try {
                MediaType mediaType = MediaTypeFactory.getMediaType(new FileSystemResource(file)).orElse(MediaType.APPLICATION_OCTET_STREAM);
                contentType = mediaType.toString();
            } catch (Exception ignored) {
            }
        }
        return contentType;
    }

    private Path getFileStorePath() {
        return Paths.get(System.getProperty("user.dir"), "upload");
    }

}
