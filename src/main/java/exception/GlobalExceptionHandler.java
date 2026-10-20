package exception;

import dto.ErroResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Traduz as exceções em respostas HTTP com um corpo de erro padronizado.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> recursoNaoEncontrado(
            RecursoNaoEncontradoException e,
            WebRequest request
    ) {
        return erro(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Object> conflito(
            ConflitoException e,
            WebRequest request
    ) {
        return erro(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler({RegraNegocioException.class, IllegalStateException.class})
    public ResponseEntity<Object> regraNegocio(
            RuntimeException e,
            WebRequest request
    ) {
        return erro(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), request);
    }

    /** Validações dos modelos de domínio (setters) lançam IllegalArgumentException. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> requisicaoInvalida(
            IllegalArgumentException e,
            WebRequest request
    ) {
        return erro(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(ErroPersistenciaException.class)
    public ResponseEntity<Object> erroPersistencia(
            ErroPersistenciaException e,
            WebRequest request
    ) {
        LOG.error("Erro de persistência", e);

        return erro(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> erroInesperado(
            Exception e,
            WebRequest request
    ) {
        LOG.error("Erro inesperado", e);

        return erro(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor.",
                request
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String mensagem = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(campo -> campo.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(" "));

        return erro(HttpStatus.BAD_REQUEST, mensagem, request);
    }

    /** Demais erros do Spring MVC (JSON malformado, método não permitido, etc.). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        HttpStatus http = HttpStatus.resolve(status.value());

        String mensagem = switch (http == null ? 500 : http.value()) {
            case 400 -> "Requisição inválida. Verifique o corpo e os parâmetros enviados.";
            case 404 -> "Recurso não encontrado.";
            case 405 -> "Método HTTP não permitido para este recurso.";
            case 415 -> "Tipo de conteúdo não suportado. Use application/json.";
            default -> "Não foi possível processar a requisição.";
        };

        return erro(status, mensagem, request);
    }

    private ResponseEntity<Object> erro(
            HttpStatusCode status,
            String mensagem,
            WebRequest request
    ) {
        HttpStatus http = HttpStatus.resolve(status.value());

        String caminho = request instanceof ServletWebRequest web
                ? web.getRequest().getRequestURI()
                : "";

        ErroResponse corpo = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                http == null ? "Erro" : http.getReasonPhrase(),
                mensagem,
                caminho
        );

        return ResponseEntity.status(status).body(corpo);
    }
}
