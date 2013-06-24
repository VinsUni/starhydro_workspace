package app.server.worker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface PersistInterface
{
	public void save(String url, Object obj) throws IOException;

	public Object load(String url) throws IOException;

	public InputStream openStream(String url) throws IOException;

	public OutputStream saveStream(String url) throws IOException;

	public void commitStream(String url) throws IOException;

}
