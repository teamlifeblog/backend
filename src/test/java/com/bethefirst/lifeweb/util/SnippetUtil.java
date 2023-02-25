package com.bethefirst.lifeweb.util;

import com.bethefirst.lifeweb.entity.member.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.restdocs.snippet.Attributes.Attribute;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class SnippetUtil {

	public static Attribute info(Object info) {
		if (info instanceof Role) {
			return new Attribute("info", ((Role) info).getDescription());
		} else {
			return new Attribute("info", info.toString());
		}
	}

	public static Attribute info(Role... roles) {
		return info(Arrays.stream(roles)
				.map(Role::getDescription)
				.collect(Collectors.joining(", ")));
	}

	public static Attribute type(Object value) {
		return new Attribute("type", value.toString());
	}

	public static Attribute path(String path) {
		return new Attribute("path", path);
	}

	public static String enumType(Class<? extends Enum> anEnum) {
		String simpleName = anEnum.getSimpleName();
		String kebab = StringUtil.convertPascalToKebab(simpleName);
		return "link:enum/" + kebab + ".html[" + simpleName + ", role=\"popup\"]";
	}

	public static String arrayType(Object value) {
		if (value instanceof String || value instanceof Number || value instanceof Enum<?>) {
			return "Array<" + value + ">";
		} else if (value instanceof Class<?>) {
			return "Array<" + ((Class) value).getSimpleName() + ">";
		} else {
			log.error("SnippetUtil.arrayType()\nvalue : {}", value);
			return "error";
		}
	}

}
