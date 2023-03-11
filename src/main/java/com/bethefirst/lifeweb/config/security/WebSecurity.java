package com.bethefirst.lifeweb.config.security;

import com.bethefirst.lifeweb.dto.CustomUser;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.exception.ForbiddenException;
import com.bethefirst.lifeweb.exception.UnauthorizedException;
import com.bethefirst.lifeweb.util.security.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class WebSecurity {

	public boolean checkAuthority(Long memberId) {

		if (!(SecurityUtil.currentMemberIdEq(memberId) || SecurityUtil.getCurrentAuthority().equals(Role.ADMIN))) {
			throw new ForbiddenException();
		}

		return true;
	}

	public boolean checkAuthority(Authentication authentication, Long memberId) {

		if (authentication.getPrincipal().equals("anonymousUser")) {
			throw new UnauthorizedException();

		} else {
			CustomUser customUser = (CustomUser) authentication.getPrincipal();

			if (!(authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.getValue()))
					|| customUser.getMemberId().equals(memberId))) {
				throw new ForbiddenException();
			}

		}

		return true;
	}

	public boolean checkRequest(HttpServletRequest request) {
		return checkAuthority(getMemberId(request));
	}

	public boolean checkRequest(Authentication authentication, HttpServletRequest request) {
		return checkAuthority(authentication, getMemberId(request));
	}

	private Long getMemberId(HttpServletRequest request) {

		// parameter
		String parameter = request.getParameter("memberId");
		if (parameter != null || !parameter.isBlank()) {
			return Long.valueOf(parameter);
		}

		return null;
	}

}
