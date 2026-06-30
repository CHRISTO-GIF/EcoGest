package holyflame.administration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:./holyflame_uploads}")
    private String uploadDir;

    public String store(MultipartFile file, String subDir) throws IOException {
        Path dir = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String stored = UUID.randomUUID().toString() + (ext != null ? "." + ext.toLowerCase() : "");
        Files.copy(file.getInputStream(), dir.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
        return subDir + "/" + stored;
    }

    public Resource loadAsResource(String relativePath) throws IOException {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path target = base.resolve(relativePath).normalize();
        if (!target.startsWith(base)) {
            throw new IOException("Accès refusé : chemin invalide");
        }
        Resource resource = new UrlResource(target.toUri());
        if (resource.exists() && resource.isReadable()) return resource;
        throw new IOException("Fichier introuvable : " + relativePath);
    }

    public void delete(String relativePath) {
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path target = base.resolve(relativePath).normalize();
            if (target.startsWith(base)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {}
    }
}
