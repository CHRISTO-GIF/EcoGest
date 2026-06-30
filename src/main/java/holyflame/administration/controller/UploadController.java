package holyflame.administration.controller;

import holyflame.administration.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("/uploads")
public class UploadController {

    @Autowired private FileStorageService fileStorageService;

    @GetMapping("/**")
    public ResponseEntity<Resource> serve(HttpServletRequest request) throws IOException {
        String path = request.getRequestURI().replaceFirst("/uploads/", "");
        Resource resource = fileStorageService.loadAsResource(path);
        String ct = request.getServletContext().getMimeType(resource.getFilename());
        if (ct == null) ct = "application/octet-stream";
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(ct))
            .body(resource);
    }
}
