package com.example.generator.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description:
 * @Author: zyw
 * @Date: 2019/2/12
 */
public abstract class ZipUtil {
    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

    public static void zip(String srcFilePath, String destFilePath)
    {
        File src = new File(srcFilePath);
        if (!src.exists()) {
            throw new RuntimeException("文件:" + srcFilePath + " 不存在");
        }
        File zipFile = new File(destFilePath);
        try
        {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            String baseDir = "";
            zipByType(src, zos, baseDir);
            zos.close();
        }
        catch (Throwable e)
        {
            log.error("压缩失败", e);
        }
    }

    private static void zipByType(File src, ZipOutputStream zos, String baseDir)
    {
        if (!src.exists()) {
            return;
        }
        if (src.isFile()) {
            zipFile(src, zos, baseDir);
        } else if (src.isDirectory()) {
            zipDir(src, zos, baseDir);
        }
    }

    private static void zipFile(File file, ZipOutputStream zos, String baseDir)
    {
        if (!file.exists()) {
            return;
        }
        try
        {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zos.putNextEntry(entry);

            byte[] buf = new byte['?'];
            int count;
            while ((count = bis.read(buf)) != -1) {
                zos.write(buf, 0, count);
            }
            bis.close();
        }
        catch (Throwable e)
        {
            log.error("压缩文件失败", e);
        }
    }

    private static void zipDir(File dir, ZipOutputStream zos, String baseDir)
    {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if ((files == null) || (files.length == 0)) {
            try
            {
                zos.putNextEntry(new ZipEntry(baseDir + dir.getName() + File.separator));
            }
            catch (Throwable e)
            {
                log.error("压缩文件夹失败", e);
            }
        } else {
            for (File file : files) {
                zipByType(file, zos, baseDir + dir.getName() + File.separator);
            }
        }
    }
}
