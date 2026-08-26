package cn.iocoder.yudao.framework.aspose.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Aspose 自动配置：启动时从 resources 加载 license.xml，依次为 Words / Cells / PDF / Slides 设置授权。
 *
 * <p>
 * 运行方式与 xiningweb/ruoyi-print 保持一致：license.xml 位于本模块
 * {@code src/main/resources} 目录，4 个 aspose jar 在 lib 目录（system scope）。
 * </p>
 */
@AutoConfiguration
@Slf4j
public class YudaoAsposeAutoConfiguration {

    @PostConstruct
    public void initLicense() {
        try (InputStream fis = new ClassPathResource("license.xml").getInputStream()) {
            byte[] licenseBytes = fis.readAllBytes();
            setWordsLicense(licenseBytes);
            setCellsLicense(licenseBytes);
            setPdfLicense(licenseBytes);
            setSlidesLicense(licenseBytes);
            log.info("[initLicense][Aspose 授权加载完成（无水印模式已激活）]");
        } catch (Exception e) {
            log.error("[initLicense][Aspose 授权加载失败，请检查 resources 目录下是否有 license.xml]", e);
        }
    }

    private void setWordsLicense(byte[] licenseBytes) {
        try {
            new com.aspose.words.License().setLicense(new ByteArrayInputStream(licenseBytes));
            log.info("[setWordsLicense][Aspose.Words 授权加载成功]");
        } catch (Exception e) {
            log.warn("[setWordsLicense][Aspose.Words 授权加载失败，输出可能带水印]", e);
        }
    }

    private void setCellsLicense(byte[] licenseBytes) {
        try {
            new com.aspose.cells.License().setLicense(new ByteArrayInputStream(licenseBytes));
            log.info("[setCellsLicense][Aspose.Cells 授权加载成功]");
        } catch (Exception e) {
            log.warn("[setCellsLicense][Aspose.Cells 授权加载失败，输出可能带水印]", e);
        }
    }

    private void setPdfLicense(byte[] licenseBytes) {
        try {
            new com.aspose.pdf.License().setLicense(new ByteArrayInputStream(licenseBytes));
            log.info("[setPdfLicense][Aspose.PDF 授权加载成功]");
        } catch (Exception e) {
            log.warn("[setPdfLicense][Aspose.PDF 授权加载失败，输出可能带水印]", e);
        }
    }

    private void setSlidesLicense(byte[] licenseBytes) {
        try {
            new com.aspose.slides.License().setLicense(new ByteArrayInputStream(licenseBytes));
            log.info("[setSlidesLicense][Aspose.Slides 授权加载成功]");
        } catch (Exception e) {
            log.warn("[setSlidesLicense][Aspose.Slides 授权加载失败，输出可能带水印]", e);
        }
    }

}
