package com.gdg.gdg_homepage.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gdg.gdg_homepage.common.controller.security.TokenInfo;
import com.gdg.gdg_homepage.common.exception.AuthException;
import com.gdg.gdg_homepage.common.exception.ErrorCode;

public class SecurityUtil {

    private SecurityUtil() {}

    public static Long getCurrentMemberId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AuthException(ErrorCode.JWT_NOT_FOUND);
        }

        if (!(authentication.getPrincipal() instanceof TokenInfo tokenInfo)) {
            throw new AuthException(ErrorCode.JWT_NOT_FOUND);
        }

        return tokenInfo.memberId();
    }
}