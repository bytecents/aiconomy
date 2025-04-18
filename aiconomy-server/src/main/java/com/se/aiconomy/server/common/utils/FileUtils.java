package com.se.aiconomy.server.common.utils;

public class FileUtils {
    /**
     * 获取文件的扩展名
     *
     * @param filePath 文件路径
     * @return 文件扩展名（小写）
     */
    public static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";  // 没有扩展名
        }
        return filePath.substring(dotIndex + 1).toLowerCase();  // 返回扩展名并转换为小写
    }
}
