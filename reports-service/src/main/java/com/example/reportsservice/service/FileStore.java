package com.example.reportsservice.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStore {

    private final AmazonS3 amazonS3;

    public void upload(String bucket, String key, InputStream inputStream) {
        try {
            ObjectMetadata metadata = defineMetadata(inputStream);
            amazonS3.putObject(bucket, key, inputStream, metadata);
        } catch (AmazonServiceException e) {
            log.error("Failed to upload the file", e);
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public byte[] download(String bucket, String key) {
        try {
            S3Object object = amazonS3.getObject(bucket, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            log.error("Failed to download the file", e);
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    @SneakyThrows
    private ObjectMetadata defineMetadata(InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(inputStream.available());
        objectMetadata.setContentType("application/pdf");
        return objectMetadata;
    }

}