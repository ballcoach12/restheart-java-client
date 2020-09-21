package org.restheartclient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.restheartclient.connection.HttpClientPoolingFactory;
import org.restheartclient.connection.HttpConnectionUtils;

/**
 * Created by Alon Eirew on 7/16/2017.
 */
public class RestHeartMultiThreadClientApiTest {

    private static final Logger LOGGER = Logger.getLogger(RestHeartBasicClientApiTest.class.getName());

    private static RestHeartClientApi api;

    @BeforeAll
    public static void initTest() {
        HttpConnectionUtils httpConnectionUtils = new HttpConnectionUtils(new HttpClientPoolingFactory());
        api = new RestHeartClientApi(httpConnectionUtils);
    }

    @AfterAll
    public static void releaseTestResources() {
        if (api != null) {
            try {
                api.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to release resources", e);
            }
        }
    }
}
