package com.accountbook.service;

import com.accountbook.model.LedgerItem;
import com.accountbook.util.CsvFileHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 가계부 작업(CRUD)을 관리하기 위한 서비스 클래스입니다.
 */
public class LedgerService {
    
    private List<LedgerItem> items;
    private CsvFileHandler fileHandler;
    private int nextId;
    
    public LedgerService() {
        this.fileHandler = new CsvFileHandler();
        this.items = new ArrayList<>();
        this.nextId = 1;
        loadData();
    }
    
    public LedgerService(String fileName) {
        this.fileHandler = new CsvFileHandler(fileName);
        this.items = new ArrayList<>();
        this.nextId = 1;
        loadData();
    }
    
    /**
     * 시작 시 파일에서 데이터를 불러옵니다.
     */
    private void loadData() {
        items = fileHandler.loadFromFile();
        
        // 기존 항목을 기반으로 다음 ID를 계산
        if (!items.isEmpty()) {
            nextId = items.stream()
                .mapToInt(LedgerItem::getId)
                .max()
                .orElse(0) + 1;
        }
    }
    
    /**
     * 가계부에 새 항목을 추가합니다.
     */
    public boolean addItem(LocalDate date, int amount, String category, String note) {
        LedgerItem newItem = new LedgerItem(nextId, date, amount, category, note);
        items.add(newItem);
        nextId++;
        
        boolean saved = saveData();
        if (saved) {
            System.out.printf("항목이 ID: %d로 성공적으로 추가되었습니다.%n", newItem.getId());
        }
        return saved;
    }
    
    /**
     * ID로 항목을 삭제합니다.
     */
    public boolean deleteItem(int id) {
        boolean removed = items.removeIf(item -> item.getId() == id);
        
        if (!removed) {
            System.out.printf("ID %d를 가진 항목이 존재하지 않습니다.%n", id);
            return false;
        }
        
        boolean saved = saveData();
        if (saved) {
            System.out.printf("ID %d를 가진 항목이 성공적으로 삭제되었습니다.%n", id);
        }
        return saved;
    }
    
    /**
     * 모든 항목을 ID별로 정렬(오름차순)하여 가져옵니다.
     */
    public List<LedgerItem> getAllItems() {
        return items.stream()
            .sorted(Comparator.comparingInt(LedgerItem::getId))
            .collect(Collectors.toList());
    }
    
    /**
     * 날짜 범위 내의 항목을 가져옵니다.
     */
    public List<LedgerItem> getItemsByDateRange(LocalDate startDate, LocalDate endDate) {
        return items.stream()
            .filter(item -> {
                LocalDate itemDate = item.getDate();
                return !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate);
            })
            .sorted(Comparator.comparingInt(LedgerItem::getId))
            .collect(Collectors.toList());
    }
    
    /**
     * 카테고리별 항목을 가져옵니다.
     */
    public List<LedgerItem> getItemsByCategory(String category) {
        return items.stream()
            .filter(item -> item.getCategory().equals(category))
            .sorted(Comparator.comparingInt(LedgerItem::getId))
            .collect(Collectors.toList());
    }
    
    /**
     * 주어진 ID를 가진 항목이 존재하는지 확인합니다.
     */
    public boolean itemExists(int id) {
        return items.stream().anyMatch(item -> item.getId() == id);
    }
    
    /**
     * 전체 항목 수를 가져옵니다.
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * 데이터를 수동으로 파일에 저장합니다.
     */
    public boolean saveData() {
        return fileHandler.saveToFile(items);
    }
    
    /**
     * 데이터를 수동으로 파일에서 불러옵니다.
     */
    public boolean loadData(boolean overwrite) {
        if (!overwrite && !items.isEmpty()) {
            System.out.println("이미 데이터가 불러와져 있습니다. 다시 불러오려면 overwrite=true를 사용하세요.");
            return false;
        }
        
        List<LedgerItem> loadedItems = fileHandler.loadFromFile();
        if (loadedItems != null) {
            items = loadedItems;
            
            // 다음 ID 재계산
            if (!items.isEmpty()) {
                nextId = items.stream()
                    .mapToInt(LedgerItem::getId)
                    .max()
                    .orElse(0) + 1;
            } else {
                nextId = 1;
            }
            
            return true;
        }
        
        return false;
    }
    
    /**
     * 영속성에 사용되는 파일명을 가져옵니다.
     */
    public String getFileName() {
        return fileHandler.getFileName();
    }
    
    /**
     * 데이터 파일이 존재하는지 확인합니다.
     */
    public boolean dataFileExists() {
        return fileHandler.fileExists();
    }
    
    /**
     * 항목 목록을 형식화된 표로 표시합니다.
     */
    public void displayItems(List<LedgerItem> itemsToDisplay) {
        if (itemsToDisplay.isEmpty()) {
            System.out.println("표시할 항목이 없습니다.");
            return;
        }
        
        System.out.println("==================================================================");
        System.out.printf(" %-3s | %-12s | %-10s | %-11s | %-20s%n", 
            "ID", "날짜", "카테고리", "금액", "메모");
        System.out.println("------------------------------------------------------------------");
        
        for (LedgerItem item : itemsToDisplay) {
            System.out.printf(" %-3d | %-12s | %-10s | %-11d | %-20s%n",
                item.getId(),
                item.getDate(),
                item.getCategory(),
                item.getAmount(),
                item.getNote() != null ? item.getNote() : ""
            );
        }
        
        System.out.println("==================================================================");
        System.out.printf("총 항목 수: %d%n", itemsToDisplay.size());
    }
}