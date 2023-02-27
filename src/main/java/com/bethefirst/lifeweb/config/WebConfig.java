package com.bethefirst.lifeweb.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		List<String> imageFolders = Arrays.asList("member", "campaign");

		for (String imageFolder : imageFolders) {

			String path = "file:///" + System.getProperty("user.dir").substring(3) + "/src/img/" + imageFolder;

			File folder = new File(System.getProperty("user.dir") + "/src/img/" + imageFolder);
			if (!(folder.exists())) {
				folder.mkdirs();
				log.info("폴더 생성 : " + path);
			}

			registry.addResourceHandler("/img/" + imageFolder + "/**")
					.addResourceLocations(path + "/")
					.setCachePeriod(3600)
					.resourceChain(true)
					.addResolver(new PathResourceResolver());
		}

	}

}
