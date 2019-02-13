package com.example.generator.util;

import java.io.File;

/**
 * @Description:
 * @Author: zyw
 * @Date: 2019/2/12
 */
public abstract class FileUtil {
    public static void deleteFile(File file)
    {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else {
            for (File subFile : file.listFiles()) {
                deleteFile(subFile);
            }
        }
        file.delete();
    }
}
