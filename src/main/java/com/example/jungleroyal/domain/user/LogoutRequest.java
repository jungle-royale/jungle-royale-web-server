package com.example.jungleroyal.domain.user;

import com.example.jungleroyal.common.types.UserRole;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogoutRequest {
    private UserRole userRole;
}
