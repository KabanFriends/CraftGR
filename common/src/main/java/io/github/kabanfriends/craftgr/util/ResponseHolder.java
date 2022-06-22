package io.github.kabanfriends.craftgr.util;

import io.github.kabanfriends.craftgr.CraftGR;
import okhttp3.Response;
import org.apache.logging.log4j.Level;

public class ResponseHolder {

    private final Response response;

    private boolean isClosed;

    public ResponseHolder(Response response) {
        this.response = response;
        this.isClosed = false;
    }

    public void close() {
        //Okhttp issue? Not sure why this happens...
        try {
            response.body().close();
        } catch (IllegalStateException ignored) {
            CraftGR.log(Level.ERROR, "Unbalanced enter/exit");
        }
        this.isClosed = true;
    }

    public boolean isClosed() {
        return isClosed;
    }

}
