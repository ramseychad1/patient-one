package com.hubaccess.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private Meta meta;
    private ErrorDetail error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Meta {
        @Builder.Default
        private String timestamp = Instant.now().toString();
        @Builder.Default
        private String requestId = UUID.randomUUID().toString();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String message;
        private String field;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .data(data)
            .meta(Meta.builder().build())
            .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .error(ErrorDetail.builder().code(code).message(message).build())
            .meta(Meta.builder().build())
            .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, String field) {
        return ApiResponse.<T>builder()
            .error(ErrorDetail.builder().code(code).message(message).field(field).build())
            .meta(Meta.builder().build())
            .build();
    }
}
