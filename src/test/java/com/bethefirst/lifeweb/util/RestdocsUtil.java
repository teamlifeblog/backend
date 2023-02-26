package com.bethefirst.lifeweb.util;

import com.bethefirst.lifeweb.dto.CustomUser;
import com.bethefirst.lifeweb.entity.member.Role;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.beans.PropertyDescriptor;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestdocsUtil {

	public static String getJwt(Role role, Long memberId) {

		CustomUser user = new CustomUser("username", "password", Arrays.asList(new SimpleGrantedAuthority(role.name())), memberId);
		PreAuthenticatedAuthenticationToken auth = new PreAuthenticatedAuthenticationToken(user, null, user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		return Jwts.builder()
				.setSubject(user.getUsername())
				.claim("memberId", user.getMemberId())
				.claim("role", user.getAuthorities())
				.compact();
	}

	public static MockHttpServletRequestBuilder createMultiPartRequest(
			MockMultipartHttpServletRequestBuilder multipartRequest,
			Object dto) {

		final PropertyDescriptor[] getterDescriptors = ReflectUtils.getBeanGetters(dto.getClass());
		for (PropertyDescriptor pd : getterDescriptors) {
			try {
				Object invoke = pd.getReadMethod().invoke(dto);

				if (invoke instanceof List<?>) {
					((List<?>) invoke).forEach(o -> fileOrPart(multipartRequest,pd.getName(), o));

				} else {
					fileOrPart(multipartRequest, pd.getName(), invoke);
				}
			} catch (Exception e) {
				log.error("RestdocsUtil.createMultiPartRequest()\nPropertyDescriptorName : {}\nException : {}", pd.getName(), e);
			}
		}

		return multipartRequest;
	}

	private static MockMultipartHttpServletRequestBuilder fileOrPart(
			MockMultipartHttpServletRequestBuilder multipartRequest,
			String name,
			Object value) {

		if (value instanceof MockMultipartFile && !((MockMultipartFile) value).isEmpty()) {
			multipartRequest.file((MockMultipartFile) value);

		} else if (instance(value)) {
			if (!String.valueOf(value).equals("null")) {
				multipartRequest.part(new MockPart(name, (String.valueOf(value)).getBytes()));
			} else {
				multipartRequest.part(new MockPart(name, ("").getBytes()));
			}
		} else {
			log.error("RestdocsUtil.fileOrPart()\nname : {}", name);
		}

		return multipartRequest;
	}

	public static Map<String, String> toMap(Object dto) {

		Map map = new HashMap<>();

		final PropertyDescriptor[] getterDescriptors = ReflectUtils.getBeanGetters(dto.getClass());
		for (PropertyDescriptor pd : getterDescriptors) {
			try {
				Object invoke = pd.getReadMethod().invoke(dto);
				if (instance(invoke)) {
					map.put(pd.getName(), invoke);
				} else {
					if (instance(((List<?>) invoke).get(0))) {
						map.put(pd.getName(), invoke);
					}
				}
			} catch (Exception e) {
				log.error("RestdocsUtil.toMap()\nPropertyDescriptorName : {}\nException : {}", pd.getName(), e);
			}
		}

		return map;
	}

	private static boolean instance(Object value) {
		return value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof LocalDate || value instanceof Enum<?>;
	}

}
