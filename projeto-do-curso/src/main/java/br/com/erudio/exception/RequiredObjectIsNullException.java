package br.com.erudio.exception;

public class RequiredObjectIsNullException extends RuntimeException {
    public RequiredObjectIsNullException() {
        super("Não é possível persistir um objeto nulo");
    }
    public RequiredObjectIsNullException(String message) {
        super(message);
    }

}
