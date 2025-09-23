package com.accountbook.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 개인 가계부의 핵심 데이터 엔티티(entity)인 가계부 항목을 나타냅니다.
 * 유효성 검사 로직과 미리 정의된 카테고리를 포함합니다.
 */
public class LedgerItem {
    
    // 쉬운 확장을 위해 상수로 미리 정의된 카테고리
    public static final List<String> VALID_CATEGORIES = Arrays.asList(
        "Food", "Transport", "Living", "Shopping", "Transfer", "Hobby"
    );
    
    // 고유 식별자 (자동 증가)
    private int id;
    
    // 거래 날짜: 유효해야 하며 2025-10-01 이후여야 함
    private LocalDate date;
    
    // 금액: 1억 이하의 양의 정수
    private int amount;
    
    // 카테고리: 미리 정의된 값 중 하나여야 함
    private String category;
    
    // 메모: 최대 길이 50자의 문자열
    private String note;
    
    // 기본 생성자
    public LedgerItem() {}
    
    // 모든 필드를 포함하는 생성자
    public LedgerItem(int id, LocalDate date, int amount, String category, String note) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.note = note;
    }
    
    // ID가 없는 생성자 (새 항목용)
    public LedgerItem(LocalDate date, int amount, String category, String note) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.note = note;
    }
    
    // Getter와 Setter
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    @Override
    public String toString() {
        return String.format("%d | %s | %s | %d | %s", 
            id, date, category, amount, note != null ? note : "");
    }
    
    /**
     * 카테고리가 미리 정의된 목록에 있는지 유효성을 검사합니다.
     */
    public static boolean isValidCategory(String category) {
        return VALID_CATEGORIES.contains(category);
    }
}