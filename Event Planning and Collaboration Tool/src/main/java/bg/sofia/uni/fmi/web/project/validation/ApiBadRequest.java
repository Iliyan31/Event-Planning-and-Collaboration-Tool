package bg.sofia.uni.fmi.web.project.validation;

public class ApiBadRequest extends RuntimeException {

    public ApiBadRequest(String message) {
        super(message);
    }

    public ApiBadRequest(String message, Throwable cause) {
        super(message, cause);
    }
}