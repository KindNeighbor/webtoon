package com.example.webtoon.service;

import com.example.webtoon.config.FileUploadProperties;
import com.example.webtoon.entity.EpisodeFile;
import com.example.webtoon.entity.EpisodeThumbnail;
import com.example.webtoon.entity.WebtoonThumbnail;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Path dirLocation;

    @Autowired
    public FileService(FileUploadProperties fileUploadProperties) {
        this.dirLocation = Paths.get(fileUploadProperties.getLocation())
            .toAbsolutePath().normalize();
    }

    // 웹툰 썸네일 파일 저장
    public WebtoonThumbnail saveWebtoonThumbnailFile(MultipartFile file) throws IOException {

        List<String> fileInfo = saveFile(file, "wtThumbnail_");
        return new WebtoonThumbnail(fileInfo.get(0), fileInfo.get(1));
    }

    // 에피소드 이미지 파일 저장
    public EpisodeFile saveEpisodeFile(MultipartFile file) throws IOException {

        List<String> fileInfo = saveFile(file, "epFile_");
        return new EpisodeFile(fileInfo.get(0), fileInfo.get(1));
    }

    // 에피소드 썸네일 파일 저장
    public EpisodeThumbnail saveEpisodeThumbnailFile(MultipartFile file) throws IOException {

        List<String> fileInfo = saveFile(file, "epThumbnail_");
        return new EpisodeThumbnail(fileInfo.get(0), fileInfo.get(1));
    }

    // 파일 저장 메소드
    private List<String> saveFile(MultipartFile file, String prefix) throws IOException {

        //파일 이름(fileName)
        String fileName = prefix + StringUtils.cleanPath(
            Objects.requireNonNull(file.getOriginalFilename()));

        //파일 경로(FileUri)
        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/webtoons/")
            .path(fileName)
            .toUriString();


        try (InputStream inputStream = file.getInputStream()) {

            Path targetLocation = this.dirLocation.resolve(fileName);

            // 이름이 같으면 그위에 덮어씀
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

            List<String> fileInfo = new ArrayList<>();
            fileInfo.add(fileName); fileInfo.add(fileUri);
            return fileInfo;
        }
    }
}


