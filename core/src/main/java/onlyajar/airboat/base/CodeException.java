package onlyajar.airboat.base;

import androidx.annotation.NonNull;

public class CodeException extends Exception{
    private final String code;

    public CodeException(String code) {
        super();
        this.code = code;
    }

    public CodeException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @NonNull
    @Override
    public String toString() {
        return "CodeException{" +
                "code='" + code + '\'' +
                "message='" + getMessage() + '\'' +
                '}';
    }
}
