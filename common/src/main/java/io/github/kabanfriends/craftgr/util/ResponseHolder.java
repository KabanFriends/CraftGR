package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class ResponseHolder implements AutoCloseable {

    private final CloseableHttpResponse response;

    private boolean isClosed;

    public ResponseHolder(CloseableHttpResponse response) {
        this.response = response;
        this.isClosed = false;
    }

    public void close() {
        try {
            response.close();
        } catch (IOException e) {
            CraftGR.getInstance().log(Level.ERROR, "Error while closing the response: " + ExceptionUtil.getStackTrace(e));
        }
        this.isClosed = true;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public boolean isClosed() {
        return isClosed;
    }

}
