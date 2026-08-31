package yosadchuk.needle.flow.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public static ResourceAlreadyExistsException of(String entity, Object value) {
        return new ResourceAlreadyExistsException(entity + " with name " + value + " already exists");
    }
}
