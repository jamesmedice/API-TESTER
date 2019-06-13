package com.framework.gft.api.apiTemplate.router;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * 
 * @author a73s
 *
 */
public class CachingServletInputStream extends ServletInputStream {

	private final InputStream cachedInputStream;
	private boolean finished = false;

	public CachingServletInputStream(InputStream cachedInputStream) {
		Objects.requireNonNull(cachedInputStream, "inputStream must not be null");
		this.cachedInputStream = cachedInputStream;
	}

	@Override
	public int read() throws IOException {
		int data = cachedInputStream.read();
		if (data == -1) {
			finished = true;
		}
		return data;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		super.close();
		cachedInputStream.close();
	}
}
