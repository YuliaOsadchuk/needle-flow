package yosadchuk.needle.flow.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public static ResourceAlreadyExistsException of(String entity, Object id) {
        return new ResourceAlreadyExistsException(entity + " with name " + id + " already exists");
    }
}
