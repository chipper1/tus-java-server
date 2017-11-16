package me.desair.tus.server;

import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.file.FileStorageService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Helper class that implements the server side tus v1.0.0 upload protocol
 */
public class TusFileUploadHandler {

    private static final Logger log = LoggerFactory.getLogger(TusFileUploadHandler.class);

    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private FileStorageService fileStorageService;

    public TusFileUploadHandler(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
        Validate.notNull(servletRequest, "The HTTP Servlet request cannot be null");
        Validate.notNull(servletResponse, "The HTTP Servlet response cannot be null");
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
    }

    public TusFileUploadHandler withFileStoreService(final FileStorageService fileStorageService) {
        Validate.notNull(fileStorageService, "The FileStorageService cannot be null");
        this.fileStorageService = fileStorageService;

        return this;
    }

    public void process() throws IOException {
        HttpMethod method = HttpMethod.getMethod(servletRequest);
        log.debug("Processing request with method {} and URL {}", method, servletRequest.getRequestURL());

        try {
            validateTusResumableHeader(method);

            //TODO process tus upload

        } catch (TusException e) {
            processTusException(method, e);
        }
    }

    protected void validateTusResumableHeader(final HttpMethod method) throws TusException {
        new TusResumableValidator(method, servletRequest).validate();
    }

    private void processTusException(final HttpMethod method, final TusException ex) throws IOException {
        int status = ex.getStatus();
        String message = ex.getMessage();
        log.warn("Unable to process request {} {}. Send response status {} with message \"{}\"", method, servletRequest.getRequestURL(), status, message);
        servletResponse.sendError(status, message);
    }

}
