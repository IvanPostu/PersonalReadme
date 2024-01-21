package ipostu.mongo.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class DatasetLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DatasetLoader() {
    }

    public static <T> T loadDatasetFromResource(String path, TypeReference<T> valueTypeRef) throws IOException {
        byte[] resourceContent = readResource(path);
        return OBJECT_MAPPER.readValue(resourceContent, valueTypeRef);
    }

    private static byte[] readResource(String path) throws IOException {
        try (InputStream inputStream = DatasetLoader.class.getClassLoader().getResourceAsStream(path)) {
            Objects.requireNonNull(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int byteRead;

            while ((byteRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, byteRead);
            }

            return byteArrayOutputStream.toByteArray();
        }
    }
}
