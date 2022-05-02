package com.pengfu.inote.manager;

import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.service.exception.ServiceException;
import com.pengfu.inote.utils.MD5Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
@Log4j2
public class FileManager {

    private static final String BASE_URL = "/home/file";
    private static final String AVATAR_URL = "/avatar";
    private static final String COVER_URL = "/cover";

    /**
     * 创建文件
     */
    public Boolean createFile(String url) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("创建文件: {}", BASE_URL + url);
        }
        File file = new File(BASE_URL + url);
        return file.createNewFile();
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file) throws Exception {
        if (file.getSize() >= 500 * 1024) {
            throw new ServiceException(1001, "图片大小超过500KB");
        }

        String name = MD5Util.md5(file.getBytes()) + ".jpg";
        File f = new File(BASE_URL + AVATAR_URL + "/" + name);
        if (f.exists()) {
            return AVATAR_URL + '/' + name;
        }

        InputStream in = new ByteArrayInputStream(file.getBytes());
        FileOutputStream fos = new FileOutputStream(f);
        byte[] b = new byte[1024];
        int nRead;
        while ((nRead = in.read(b)) != -1) {
            fos.write(b, 0, nRead);
        }
        fos.flush();
        fos.close();
        in.close();

        return AVATAR_URL + '/' + name;
    }

    /**
     * 上传文章封面
     */
    public String uploadCover(MultipartFile file) throws Exception {
        if (file.getSize() >= 5 * 1024 * 1024) {
            throw new ServiceException(1001, "图片大小超过5MB");
        }

        String name = MD5Util.md5(file.getBytes()) + ".jpg";
        File f = new File(BASE_URL + COVER_URL + "/" + name);
        if (f.exists()) {
            return COVER_URL + '/' + name;
        }

        InputStream in = new ByteArrayInputStream(file.getBytes());
        FileOutputStream fos = new FileOutputStream(f);
        byte[] b = new byte[1024];
        int nRead;
        while ((nRead = in.read(b)) != -1) {
            fos.write(b, 0, nRead);
        }
        fos.flush();
        fos.close();
        in.close();

        return COVER_URL + '/' + name;
    }

    /**
     * 读取文件内容
     */
    public byte[] read(String url) throws IOException {
        File file = new File(BASE_URL + url);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[inputStream.available()];
        int read = inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }

    /**
     * 写入文件内容
     */
    public void write(String url, String content) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(BASE_URL + url));
        out.write(content);
        out.close();
    }

    /**
     * 删除文件
     */
    public boolean delete(String url) {
        File file = new File(BASE_URL + url);

        if (!file.exists()) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        return file.delete();
    }

}
