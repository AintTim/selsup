import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private static final URI base = URI.create("https://ismp.crpt.ru/api/v3/");
    private final RateLimiter limiter;

    public CrptApi(Long requestLimit, TimeUnit timeUnit) {
        limiter = RateLimiter.create(requestLimit, 1, timeUnit);
    }

    public Response createDocument(DocumentDTO document, String signature) {
        /*
            Не совсем ясно из условия, где используется подпись документа
         */
        return post(Method.LK_DOCUMENT_CREATE, writeObjectAsJsonString(document));
    }

    public enum Method {
        LK_DOCUMENT_CREATE("lk/documents/create");

        private final String method;

        Method(String method) {
            this.method = method;
        }
    }

    private Response post(Method method, String jsonStringBody) {
        limiter.acquire()
        return RestAssured.given().body(jsonStringBody).contentType(ContentType.JSON).post(base.resolve(method.method));
    }

    private static <T> String writeObjectAsJsonString(T object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Не удалось преобразовать объект в ожидаемый формат");
        }
    }

    @Builder
    @Jacksonized
    @AllArgsConstructor
    public static class DocumentDTO {
        private Description description;
        @JsonProperty("doc_id")
        private String docId;
        @JsonProperty("doc_status")
        private String docStatus;
        @JsonProperty("doc_type")
        private String docType;
        private Boolean importRequest;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        private String producerDate;
        @JsonProperty("production_type")
        private String productionType;
        private List<Product> products;
        @JsonProperty("reg_date")
        private String regDate;
        @JsonProperty("reg_number")
        private String regNumber;
    }

    @Builder
    @Jacksonized
    @AllArgsConstructor
    public static class Description {
        private String participantInn;
    }

    @Builder
    @Jacksonized
    @AllArgsConstructor
    public static class Product {
        @JsonProperty("certificate_document")
        private String certificateDocument;
        @JsonProperty("certificate_document_date")
        private String certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        private String productionDate;
        @JsonProperty("tnved_code")
        private String tnvedCode;
        @JsonProperty("uit_code")
        private String uitCode;
        @JsonProperty("uitu_code")
        private String uituCode;
    }
}
