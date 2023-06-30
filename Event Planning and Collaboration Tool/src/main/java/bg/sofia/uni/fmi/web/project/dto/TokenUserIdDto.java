package bg.sofia.uni.fmi.web.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenUserIdDto {

    @JsonProperty("token")
    private String token;

    @JsonProperty("user_id")
    private Long userId;

}
