package service.errorHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

public class ErrorsHandler {
    private static final ErrorsHandler INSTANCE = new ErrorsHandler();

    private ErrorsHandler() {
    }
    public String getMessage (HttpServletResponse response) throws JsonProcessingException {

        String messageByCode = ErrorMessages.getMessageByCode(response.getStatus());
        MessageResponse messageResponse = new MessageResponse(messageByCode);
        return new ObjectMapper().writeValueAsString(messageResponse);
    }
    public String getMessage (int error) throws JsonProcessingException {

        String messageByCode = ErrorMessages.getMessageByCode(error);
        MessageResponse messageResponse = new MessageResponse(messageByCode);
        return new ObjectMapper().writeValueAsString(messageResponse);
    }

    public static ErrorsHandler getInstance() {
        return INSTANCE;
    }




}
