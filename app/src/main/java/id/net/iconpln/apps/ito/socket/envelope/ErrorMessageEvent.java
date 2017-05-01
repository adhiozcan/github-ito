package id.net.iconpln.apps.ito.socket.envelope;

/**
 * Created by Ozcan on 13/04/2017.
 */

public class ErrorMessageEvent {
    private String errorCode;
    private String message;

    public ErrorMessageEvent(String message) {
        this.message = message;
    }

    public ErrorMessageEvent(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
