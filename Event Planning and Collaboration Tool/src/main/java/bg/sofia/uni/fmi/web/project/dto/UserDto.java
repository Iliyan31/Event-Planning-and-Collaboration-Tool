package bg.sofia.uni.fmi.web.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("email")
    private String email;

//    @JsonProperty("description")
//    private String description;

    @JsonProperty("verified")
    private boolean verified;

    @JsonProperty("profile_photo_link")
    private String profilePhotoLink;

    @JsonProperty("address")
    private String address;
}
