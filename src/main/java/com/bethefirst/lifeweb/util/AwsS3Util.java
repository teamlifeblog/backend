package com.bethefirst.lifeweb.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bethefirst.lifeweb.dto.UploadFile;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@NoArgsConstructor
@Slf4j
public class AwsS3Util {

    //Amazon-s3-sdk
    private AmazonS3 s3Client;
    @Value("${aws-s3.access-key}")
    private String accessKey;
    @Value("${aws-s3.secret-key}")
    private String secretKey;
    final private Regions clientRegion = Regions.AP_NORTHEAST_2;
    @Value("${aws-s3.bucket}")
    private String bucket;

    //aws S3 client 생성
    @PostConstruct
    private void createS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(clientRegion)
                .build();
    }

	public void upload(UploadFile uploadFile) {
		MultipartFile multipartFile = uploadFile.getMultipartFile();

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());

		try {
			uploadToS3(new PutObjectRequest(this.bucket, uploadFile.getKey(), multipartFile.getInputStream(), objectMetadata));
		} catch (IOException e) {
			log.error("getInputStream() 오류");
			log.error("AwsS3Util.upload(), fileName : {}, IOException : {}", multipartFile.getOriginalFilename(), e);
			throw new RuntimeException(e);
		}
	}

	public void upload(List<UploadFile> uploadFileList) {
		uploadFileList.forEach(this::upload);
	}

    //PutObjectRequest는 Aws S3 버킷에 업로드할 객체 메타 데이터와 파일 데이터로 이루어져있다.
    private void uploadToS3(PutObjectRequest putObjectRequest) {

        try {
            this.s3Client.putObject(putObjectRequest);
            System.out.println(String.format("[%s] upload complete", putObjectRequest.getKey()));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copy(String orgKey, String copyKey) {
        try {
            //Copy 객체 생성
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    this.bucket, orgKey,
                    this.bucket, copyKey
            );
            //Copy
            this.s3Client.copyObject(copyObjRequest);

            System.out.println(String.format("Finish copying [%s] to [%s]", orgKey, copyKey));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    public void delete(String key) {
        try {
            //Delete 객체 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(this.bucket, key);
            //Delete
            this.s3Client.deleteObject(deleteObjectRequest);
            System.out.println(String.format("[%s] deletion complete", key));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

	public void delete(List<String> keyList) {
		keyList.forEach(this::delete);
	}

}
