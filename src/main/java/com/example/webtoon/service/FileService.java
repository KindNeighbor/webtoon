package com.example.webtoon.service;

import com.example.webtoon.config.FileUploadProperties;
import com.example.webtoon.entity.EpisodeFile;
import com.example.webtoon.entity.EpisodeThumbnail;
import com.example.webtoon.entity.WebtoonThumbnail;
import com.example.webtoon.exception.CustomException;
import com.example.webtoon.repository.WebtoonRepository;
import com.example.webtoon.type.ErrorCode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class FileService {

    private final Path dirLocation;

    public FileService(FileUploadProperties fileUploadProperties) {
        this.dirLocation = Paths.get(fileUploadProperties.getLocation())
            .toAbsolutePath().normalize();
    }

    // 웹툰 썸네일 파일 저장
    public WebtoonThumbnail saveWebtoonThumbnailFile(MultipartFile file) {

        //파일 이름(fileName)
        String fileName = StringUtils.cleanPath(
            Objects.requireNonNull(file.getOriginalFilename()));

        //파일 경로(FileUri)
        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/webtoons/")
            .path(fileName)
            .toUriString();

        InputStream inputStream = null;

        try {
            inputStream = file.getInputStream();

            Path targetLocation = this.dirLocation.resolve(fileName);

            // 이름이 같으면 그위에 덮어씀
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.FILE_STORAGE_FAILED);

        } finally {
            // InputStream 닫지 않으면 tmp 파일이 지워지지 않아서 UncheckedException 발생
            IOUtils.closeQuietly(inputStream);
        }

        return new WebtoonThumbnail(fileName, fileUri);
    }

    // 에피소드 이미지 파일 저장
    public EpisodeFile saveEpisodeFile(MultipartFile file) {

        //파일 이름(fileName)
        String fileName = StringUtils.cleanPath(
            Objects.requireNonNull(file.getOriginalFilename()));

        //파일 경로(FileUri)
        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/webtoons/")
            .path(fileName)
            .toUriString();

        InputStream inputStream = null;

        try {
            inputStream = file.getInputStream();

            Path targetLocation = this.dirLocation.resolve(fileName);

            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.FILE_STORAGE_FAILED);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return new EpisodeFile(fileName, fileUri);
    }

    // 에피소드 썸네일 파일 저장
    public EpisodeThumbnail saveEpisodeThumbnailFile(MultipartFile file) {

        //파일 이름(fileName)
        String fileName = StringUtils.cleanPath(
            Objects.requireNonNull(file.getOriginalFilename()));

        //파일 경로(FileUri)
        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/webtoons/")
            .path(fileName)
            .toUriString();

        InputStream inputStream = null;

        try {
            inputStream = file.getInputStream();

            Path targetLocation = this.dirLocation.resolve(fileName);

            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new CustomException(
                HttpStatus.BAD_REQUEST, ErrorCode.FILE_STORAGE_FAILED);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return new EpisodeThumbnail(fileName, fileUri);
    }
}


