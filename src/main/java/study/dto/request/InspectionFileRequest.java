package study.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InspectionFileRequest implements FileRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
