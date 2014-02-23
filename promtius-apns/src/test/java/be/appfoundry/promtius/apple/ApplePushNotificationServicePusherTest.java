package be.appfoundry.promtius.apple;


import be.appfoundry.promtius.ClientToken;
import be.appfoundry.promtius.ClientTokenFactory;
import be.appfoundry.promtius.ClientTokenService;
import be.appfoundry.promtius.PushPayload;
import com.notnoop.apns.ApnsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Mike Seghers
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplePushNotificationServicePusherTest {
    @Mock
    private ApnsService apnsService;

    @Mock
    private ClientTokenService<String, String> clientTokenService;
    @Mock
    private ClientTokenFactory<String, String> clientTokenFactory;

    @Captor
    ArgumentCaptor<ClientToken<String, String>> pushTokenCaptor;

    private static final String TEST_PLATFORM = "iOS";
    private ClientToken<String, String> tokenA;
    private ClientToken<String, String> tokenB;

    private ApplePushNotificationServicePusher<String> pusher;

    @Before
    public void setUp() throws Exception {
        pusher = new ApplePushNotificationServicePusher<>(apnsService, clientTokenService, clientTokenFactory, TEST_PLATFORM);
        tokenA = new TestClientToken("token1");
        tokenB = new TestClientToken("token2");
    }

    @Test
    public void test_sendPush() throws Exception {
        PushPayload payload = new PushPayload("message");
        List<ClientToken<String, String>> tokens = Arrays.asList(tokenA, tokenB);
        when(clientTokenService.findClientTokensForOperatingSystem(TEST_PLATFORM)).thenReturn(tokens);

        pusher.sendPush(payload);

        verify(apnsService).push(eq(Arrays.asList("token1", "token2")), contains("message"));
    }

    @Test
    public void test_removeInactiveDevices() throws Exception {
        Map<String, Date> inactive = new HashMap<String, Date>();
        inactive.put("token1", new Date());
        inactive.put("token2", new Date());
        when(apnsService.getInactiveDevices()).thenReturn(inactive);
        when(clientTokenFactory.createClientToken("token1", TEST_PLATFORM)).thenReturn(tokenA);
        when(clientTokenFactory.createClientToken("token2", TEST_PLATFORM)).thenReturn(tokenB);

        PushPayload payload = new PushPayload("message");
        pusher.sendPush(payload);

        verify(clientTokenService, times(2)).unregisterClientToken(pushTokenCaptor.capture());
        List<ClientToken<String, String>> allValues = pushTokenCaptor.getAllValues();
        assertThat(allValues.get(0), is(tokenA));
        assertThat(allValues.get(1), is(tokenB));
    }

    @Test
    public void test_getPlatform() throws Exception {
        assertThat(pusher.getPlatform(), is(TEST_PLATFORM));
    }

    private static class TestClientToken implements ClientToken<String, String> {

        private final String token;

        private TestClientToken(String token) {
            this.token = token;
        }

        @Override
        public String getToken() {
            return token;
        }

        @Override
        public String getPlatform() {
            return TEST_PLATFORM;
        }
    }
}