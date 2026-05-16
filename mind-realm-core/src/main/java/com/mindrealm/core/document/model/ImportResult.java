package com.mindrealm.core.document.model;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {

    private int totalCount;
    private int successCount;
    private int failCount;
    private List<String> errors;
    private List<Long> importedIds;

    public ImportResult() {}

    public ImportResult(int totalCount, int successCount, int failCount, List<String> errors, List<Long> importedIds) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failCount = failCount;
        this.errors = errors;
        this.importedIds = importedIds;
    }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    public List<Long> getImportedIds() { return importedIds; }
    public void setImportedIds(List<Long> importedIds) { this.importedIds = importedIds; }

    public static ImportResult success(int count, List<Long> ids) {
        ImportResult result = new ImportResult();
        result.totalCount = count;
        result.successCount = count;
        result.failCount = 0;
        result.importedIds = ids != null ? ids : new ArrayList<>();
        result.errors = new ArrayList<>();
        return result;
    }

    public static ImportResult partial(int total, int success, int fail, List<String> errors) {
        ImportResult result = new ImportResult();
        result.totalCount = total;
        result.successCount = success;
        result.failCount = fail;
        result.errors = errors != null ? errors : new ArrayList<>();
        result.importedIds = new ArrayList<>();
        return result;
    }
}
