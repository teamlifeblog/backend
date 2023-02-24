package com.bethefirst.lifeweb.util;

import com.bethefirst.lifeweb.entity.member.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.restdocs.snippet.Attributes;

@Slf4j
public class SnippetUtil {

	public static Attributes.Attribute role(Role role) {
		return new Attributes.Attribute("role", role.getDescription());
	}

	public static Attributes.Attribute type(Object value) {
		return new Attributes.Attribute("type", value.toString());
	}

	public static Attributes.Attribute path(Object value) {
		return new Attributes.Attribute("path", value.toString());
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
