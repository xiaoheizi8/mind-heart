package com.mindrealm.core.document.service;

import com.mindrealm.core.document.model.ImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @className: IDocumentImportService
 * @description: 文档导入服务接口
 * @author: 一朝风月
 * @createTime: 2026.4.8
 */
public interface IDocumentImportService {

    /**
     * 从Excel导入
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importFromExcel(MultipartFile file) throws IOException;

    /**
     * 从Word导入
     * @param file Word文件
     * @return 导入结果
     */
    ImportResult importFromWord(MultipartFile file) throws IOException;

    /**
     * 从CSV导入
     * @param file CSV文件
     * @return 导入结果
     */
    ImportResult importFromCsv(MultipartFile file) throws IOException;

    /**
     * 获取导入模板
     * @return 模板内容
     */
    String getImportTemplate();
}
