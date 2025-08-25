package com.fhalcom.facturacion.validation;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class HttpCachedBodyRequest extends HttpServletRequestWrapper {
  private final byte[] cachedBody;

  public HttpCachedBodyRequest(HttpServletRequest request) throws IOException {
    super(request);
    InputStream is = request.getInputStream();
    this.cachedBody = is.readAllBytes();
  }

  public byte[] getCachedBody() { return cachedBody; }

  @Override
  public ServletInputStream getInputStream() {
    ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
    return new ServletInputStream() {
      @Override public boolean isFinished() { return bais.available() == 0; }
      @Override public boolean isReady() { return true; }
      @Override public void setReadListener(ReadListener readListener) {}
      @Override public int read() { return bais.read(); }
    };
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }
}
