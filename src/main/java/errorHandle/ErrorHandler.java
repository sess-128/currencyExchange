package errorHandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

public class ErrorHandler {
    public static String getMessage (HttpServletResponse response) throws JsonProcessingException {

        String messageByCode = ErrorMessages.getMessageByCode(response.getStatus());
        MessageResponse messageResponse = new MessageResponse(messageByCode);
        return new ObjectMapper().writeValueAsString(messageResponse);
    }
    public static String getMessage (int error) throws JsonProcessingException {

        String messageByCode = ErrorMessages.getMessageByCode(error);
        MessageResponse messageResponse = new MessageResponse(messageByCode);
        return new ObjectMapper().writeValueAsString(messageResponse);
    }
}
