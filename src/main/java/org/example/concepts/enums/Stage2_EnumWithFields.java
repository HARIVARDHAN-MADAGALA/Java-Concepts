package org.example.concepts.enums;

/// Stage 2 — enum with fields and methods
/// Each constant carries its own data (code + message)
/// Real world example: HTTP Status codes

public class Stage2_EnumWithFields {

    enum HttpStatus {


        OK(200, "Success"),
        CREATED(201, "Resource Created"),
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        NOT_FOUND(404, "Not Found"),
        INTERNAL_SERVER_ERROR(500, "Server Error");

        // fields — each constant holds these values
        private final int code;
        private final String message;

        // constructor — called once per constant at class load time
        HttpStatus(int code, String message) {
            this.code    = code;
            this.message = message;
        }

        public int    getCode()    { return code;    }
        public String getMessage() { return message; }

        // method — check if this status is an error
        public boolean isError() {
            return code >= 400;
        }
    }

    static void handleResponse(HttpStatus status) {
        System.out.println("[" + status.getCode() + "] " + status.getMessage()
                + (status.isError() ? " ⚠ ERROR" : " ✓ OK"));
    }

    public static void main(String[] args) {

        handleResponse(HttpStatus.OK);
        handleResponse(HttpStatus.CREATED);
        handleResponse(HttpStatus.NOT_FOUND);
        handleResponse(HttpStatus.INTERNAL_SERVER_ERROR);

        System.out.println("\n── iterate all statuses ──");
        for (HttpStatus s : HttpStatus.values()) {
            System.out.println(s + " -> code=" + s.getCode() + ", error=" + s.isError());
        }
    }
}
