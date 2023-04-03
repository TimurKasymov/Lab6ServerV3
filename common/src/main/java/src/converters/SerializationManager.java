package src.converters;

import src.loggerUtils.LoggerManager;

import java.io.*;


public class SerializationManager {

    public byte[] serialize(Object object) {
        LoggerManager.getLogger(SerializationManager.class).info("starting to convert obj to bytes..");

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            LoggerManager.getLogger(SerializationManager.class).error(e.getMessage());
        }
        return new byte[0];
    }

    public Object deserialize(byte[] object) {
        LoggerManager.getLogger(SerializationManager.class).info("starting to convert bytes to obj");
        try (ByteArrayInputStream inp = new ByteArrayInputStream(object);
             ObjectInputStream ois = new ObjectInputStream(inp);
        ) {
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            LoggerManager.getLogger(SerializationManager.class).error(e.getMessage());
        }
        return null;
    }
}
