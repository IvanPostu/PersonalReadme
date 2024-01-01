package ipostu.mongo.demo;

public final class Common {
    private static final String DEFAULT_CONNECTION_STRING = "mongodb://test2:t%25e%29s%24t2@localhost:27017/my_db";

    private Common() {
    }

    public static String getMongoConnectionString() {
        String connectionStringFromEnvVariables = System.getProperty("mongodb.uri");
        if (connectionStringFromEnvVariables == null || connectionStringFromEnvVariables.equals("")) {
            return DEFAULT_CONNECTION_STRING;
        }
        return connectionStringFromEnvVariables;
    }

}
