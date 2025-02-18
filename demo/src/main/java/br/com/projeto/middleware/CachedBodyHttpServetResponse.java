package br.com.projeto.middleware;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.*;

public class CachedBodyHttpServetResponse extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream cachedBody = new ByteArrayOutputStream();
    private final PrintWriter writer;
    private final HttpServletResponse originalResponse;
    private boolean responseCommitted = false; // Evita escrita duplicada

    public CachedBodyHttpServetResponse(HttpServletResponse response) throws IOException {
        super(response);
        this.originalResponse = response;
        this.writer = new PrintWriter(new OutputStreamWriter(cachedBody, response.getCharacterEncoding()), true);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new CachedServletOutputStream(cachedBody, originalResponse.getOutputStream());
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    public String getBody() {
        writer.flush(); // Garante que tudo foi escrito antes de pegar os bytes
        return cachedBody.toString();
    }

    public void copyBodyToResponse() throws IOException {
        if (!responseCommitted) { // Evita escrita duplicada
            byte[] bodyBytes = cachedBody.toByteArray();
            originalResponse.getOutputStream().write(bodyBytes);
            originalResponse.getOutputStream().flush();
            responseCommitted = true;
        }
    }

    private static class CachedServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream cachedStream;
        private final ServletOutputStream originalStream;

        public CachedServletOutputStream(ByteArrayOutputStream cachedStream, ServletOutputStream originalStream) {
            this.cachedStream = cachedStream;
            this.originalStream = originalStream;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {}

        @Override
        public void write(int b) throws IOException {
            cachedStream.write(b);
            originalStream.write(b); // Escreve diretamente na resposta real
        }
    }
}
