package ru.itis.ibragimovaidar.jooqgenerator.jooq;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;


@Slf4j
@UtilityClass
public class TestUtils {
    private static final Pattern IN_STATMENT_PATTERN = Pattern.compile("in \\((?<in>(\\$\\d+(?:, )?){3,})\\)");

    @SneakyThrows
    public Path createTempDirectory(String prefix) {
        var rootTmpDir = getEnvPropertyOrDefault("JAVA_IO_TMPDIR", null);
        Path path;
        if (rootTmpDir != null) {
            var rootTmpDirPath = Path.of(rootTmpDir);
            if (!Files.exists(rootTmpDirPath)) {
                Files.createDirectories(rootTmpDirPath);
            }
            path = Files.createTempDirectory(rootTmpDirPath, prefix);
        } else {
            path = Files.createTempDirectory(prefix);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileUtils.deleteQuietly(path.toFile())));
        log.debug("Created temp directory: {}", path.toAbsolutePath());
        return path;
    }

    public String getEnvPropertyOrDefault(String propertyName, String defaultValue) {
        String envValue = System.getenv(propertyName);
        if (envValue != null) {
            return envValue;
        }
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null) {
            return propertyValue;
        }
        return defaultValue;
    }

    /**
     * Убирает из sql запроса вхождения вида in ($2, $3, $4, $5, $6, $7, $8, $9, $10, $11), сокращая их до in ($2, ... $11)
     */
    @SneakyThrows
    public String prettySql(String sql) {
        var result = sql;
        try {
            var matcher = IN_STATMENT_PATTERN.matcher(sql);
            while (matcher.find()) {
                var in = matcher.group("in");
                result = result.replace(in, "%s, ... %s".formatted(in.substring(0, in.indexOf(",")), in.substring(in.lastIndexOf(",") + 2)));
            }
        } catch (Exception ex) {
            log.error(sql, ex);
        }
        return result;

    }

    public static String getResourcesPath(Class<?> clazz) {
        return clazz.getClassLoader().getResource(".").getFile();
    }

    @SneakyThrows
    public int findFreePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
