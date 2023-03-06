package com.bethefirst.lifeweb.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UploadFile {
	private final MultipartFile multipartFile;
	private final String key;

	public UploadFile(MultipartFile multipartFile, String imageFolder) {
		this.multipartFile = multipartFile;
		this.key = getFileNameWithPath(multipartFile.getOriginalFilename(), imageFolder);
	}

	public static List<UploadFile> list(List<MultipartFile> multipartFileList, String imageFolder) {
		return multipartFileList
				.stream().map(multipartFile -> new UploadFile(multipartFile, imageFolder))
				.collect(Collectors.toList());
	}

	private String getFileNameWithPath(String filename, String imageFolder) {
		String ext = filename.substring(filename.lastIndexOf("."));
		return "/" + imageFolder + "/" + UUID.randomUUID() + ext;
	}

}
