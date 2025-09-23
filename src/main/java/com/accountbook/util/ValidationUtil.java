package com.accountbook.util;

import com.accountbook.model.LedgerItem;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 프로젝트 요구사항에 따른 입력 유효성 검사를 위한 유틸리티 클래스입니다.
 */
public class ValidationUtil {
    
    private static final LocalDate MIN_DATE = LocalDate.of(2025, 10, 1);
    private static final int MAX_AMOUNT = 100_000_000;
    private static final int MAX_NOTE_LENGTH = 50;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * 요구사항에 따라 날짜 입력을 검증합니다:
     * - 형식: YYYY-MM-DD
     * - 범위: 2025-10-01 이후여야 함
     */
    public static ValidationResult validateDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return new ValidationResult(false, "날짜는 비워둘 수 없습니다.");
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            
            if (date.isBefore(MIN_DATE)) {
                return new ValidationResult(false, "날짜는 2025-10-01 이후여야 합니다.");
            }
            
            return new ValidationResult(true, null, date);
        } catch (DateTimeParseException e) {
            return new ValidationResult(false, "유효하지 않은 날짜 형식입니다. YYYY-MM-DD 형식을 사용해주세요.");
        }
    }
    
    /**
     * 요구사항에 따라 금액 입력을 검증합니다:
     * - 양의 정수만 가능
     * - 선행 0, 쉼표 또는 숫자가 아닌 문자는 허용되지 않음
     * - 최대값: 100,000,000
     */
    public static ValidationResult validateAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return new ValidationResult(false, "금액은 비워둘 수 없습니다.");
        }
        
        String trimmed = amountStr.trim();
        
        // 선행 0 확인 ("0" 제외)
        if (trimmed.length() > 1 && trimmed.startsWith("0")) {
            return new ValidationResult(false, "금액은 선행 0을 가질 수 없습니다.");
        }
        
        try {
            int amount = Integer.parseInt(trimmed);
            
            if (amount <= 0) {
                return new ValidationResult(false, "금액은 양수여야 합니다.");
            }
            
            if (amount > MAX_AMOUNT) {
                return new ValidationResult(false, "금액은 100,000,000을 초과할 수 없습니다.");
            }
            
            return new ValidationResult(true, null, amount);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "금액은 유효한 양의 정수여야 합니다.");
        }
    }
    
    /**
     * 요구사항에 따라 카테고리 입력을 검증합니다:
     * - 미리 정의된 값 중 하나여야 함
     */
    public static ValidationResult validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ValidationResult(false, "카테고리는 비워둘 수 없습니다.");
        }
        
        String trimmed = category.trim();
        
        if (!LedgerItem.isValidCategory(trimmed)) {
            return new ValidationResult(false, 
                "카테고리는 다음 중 하나여야 합니다: " + String.join(", ", LedgerItem.VALID_CATEGORIES));
        }
        
        return new ValidationResult(true, null, trimmed);
    }
    
    /**
     * 요구사항에 따라 메모 입력을 검증합니다:
     * - 문자열, 최대 50자
     * - 특수문자로만 구성될 수 없음
     */
    public static ValidationResult validateNote(String note) {
        if (note == null) {
            return new ValidationResult(true, null, "");
        }
        
        String trimmed = note.trim();
        
        if (trimmed.length() > MAX_NOTE_LENGTH) {
            return new ValidationResult(false, "메모는 50자를 초과할 수 없습니다.");
        }
        
        // 메모가 특수문자로만 구성되었는지 확인
        if (!trimmed.isEmpty() && trimmed.matches("^[^a-zA-Z0-9\\s]+$")) {
            return new ValidationResult(false, "메모는 특수문자로만 구성될 수 없습니다.");
        }
        
        return new ValidationResult(true, null, trimmed);
    }
    
    /**
     * 메뉴 옵션 입력을 검증합니다.
     */
    public static ValidationResult validateMenuOption(String input, int minOption, int maxOption) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, "유효한 옵션을 입력해주세요.");
        }
        
        try {
            int option = Integer.parseInt(input.trim());
            
            if (option < minOption || option > maxOption) {
                return new ValidationResult(false, 
                    String.format("숫자를 %d와 %d 사이로 입력해주세요.", minOption, maxOption));
            }
            
            return new ValidationResult(true, null, option);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "유효한 숫자를 입력해주세요.");
        }
    }
    
    /**
     * 유효성 검사 작업 결과를 위한 클래스
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final Object value;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.value = null;
        }
        
        public ValidationResult(boolean valid, String errorMessage, Object value) {
            this.valid = valid;
            this.errorMessage = errorMessage;
            this.value = value;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public Object getValue() {
            return value;
        }
        
        @SuppressWarnings("unchecked")
        public <T> T getValue(Class<T> type) {
            return (T) value;
        }
    }
}