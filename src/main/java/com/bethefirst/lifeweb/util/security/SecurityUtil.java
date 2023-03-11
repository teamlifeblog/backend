package com.bethefirst.lifeweb.util.security;

import com.bethefirst.lifeweb.dto.CustomUser;
import com.bethefirst.lifeweb.entity.member.Role;
import com.bethefirst.lifeweb.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

	private static final String ROLE_PREFIX = "ROLE_";

    private SecurityUtil() {}

	public static Long getCurrentMemberId() {
		CustomUser customUser = getCustomUserFromSecurityContext();
		return customUser.getMemberId();
	}

	public static boolean currentMemberIdEq(Long memberId) {
		if (!getCurrentMemberId().equals(memberId)) {
			return false;
		}
		return true;
	}

    public static Role getCurrentAuthority() {
        CustomUser customUser = getCustomUserFromSecurityContext();
		String authority = customUser.getAuthorities().stream().findFirst().get().getAuthority();
		String role = authority.startsWith(ROLE_PREFIX) ? authority.substring(ROLE_PREFIX.length()) : authority;
		return Role.valueOf(role);
    }

    private static CustomUser getCustomUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException();
        }

        if (authentication.getPrincipal() instanceof CustomUser) {
          return (CustomUser) authentication.getPrincipal();
        } else {
            throw new RuntimeException("CustomUser 캐스팅에 실패 하였습니다.");
        }

    }

}