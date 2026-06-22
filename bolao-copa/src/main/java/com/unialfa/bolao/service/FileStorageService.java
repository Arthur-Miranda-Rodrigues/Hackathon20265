package com.unialfa.bolao.service;

import com.unialfa.bolao.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Armazena imagens enviadas (avatar de usuário, bandeira de seleção) no disco
 * e devolve a URL pública servida em /uploads/** (ver WebConfig).
 */
@Service
public class FileStorageService {

    private final Path dir;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.dir);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível criar a pasta de uploads.", e);
        }
    }

    public String salvarImagem(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo de imagem vazio.");
        }
        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("O arquivo enviado deve ser uma imagem.");
        }
        String original = arquivo.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.')).toLowerCase();
        }
        String nome = UUID.randomUUID().toString().replace("-", "") + ext;
        try {
            arquivo.transferTo(dir.resolve(nome));
        } catch (IOException e) {
            throw new BusinessException("Falha ao salvar a imagem.");
        }
        return "/uploads/" + nome;
    }
}
