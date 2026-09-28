package me.hektortm.wosCore.api;

/**
 * A failed wos-api call. {@link #status()} is the HTTP status (0 when the API
 * could not be reached at all) and {@link #code()} the stable error code from
 * the API's error envelope ({@code {"error":{"code","message"}}}), if any.
 */
public class ApiException extends Exception {
    private final int status;
    private final String code;

    public ApiException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.status = 0;
        this.code = "unreachable";
    }

    public int status() { return status; }

    public String code() { return code; }

    public boolean isNotFound() { return status == 404; }

    /** True when the API is down, unreachable, or failing server-side. */
    public boolean isUnavailable() { return status == 0 || status >= 500; }
}
