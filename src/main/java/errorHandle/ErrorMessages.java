package errorHandle;

public enum ErrorMessages {
    NOT_ISO_FORMAT (4217 , "Код валюты должен быть в формате ISO 4217"),
    BAD_REQUEST (400, "Отсутствует нужное поле валюты или пары"), // 400
    SERVER_ERROR (500, "База данных недоступна"), // 500
    NOT_FOUND (404, "Пара или валюта отсутствует в БД"), // 404
    CONFLICT (409,"Такая пара или валюта уже существует "), // 409,
    DEFAULT (0,"Валюта не найдена");

    private final int code;
    private final String message;

    ErrorMessages(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static String getMessageByCode(int code) {
        for (ErrorMessages errorMessage : ErrorMessages.values()) {
            if (errorMessage.getCode() == code) {
                return errorMessage.getMessage();
            }
        }
        return DEFAULT.getMessage();
    }

}
