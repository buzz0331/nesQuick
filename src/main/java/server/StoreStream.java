package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StoreStream {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    public StoreStream(ObjectInputStream in, ObjectOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }
}
