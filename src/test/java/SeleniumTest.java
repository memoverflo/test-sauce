import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Map;

public class SeleniumTest {

    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        Map<String, String> env = System.getenv();

        boolean travis = Boolean.parseBoolean(env.get("TRAVIS"));
        if (travis) {
            // fetch browser info
            String[] browserInfo = env.get("SAUCE_BROWSER").split(":");
            int numFields = browserInfo.length;
            String browserName = browserInfo[0];
            String browserVersion = numFields > 1 ? browserInfo[1] : "";
            Platform browserPlatform = numFields > 2 ? Platform.ANY : Platform.valueOf(browserInfo[2]);
            DesiredCapabilities caps = new DesiredCapabilities(browserName, browserVersion, browserPlatform);

            // if we run Sauce Connect locally, we don't have a Travis job number set
            String travisJobNumber = env.get("TRAVIS_JOB_NUMBER");
            if (travisJobNumber != null) {
                caps.setCapability("tunnel-identifier", travisJobNumber);
            }

            // talk to Sauce Connect
            String userName = env.get("SAUCE_USERNAME");
            String accessKey = env.get("SAUCE_ACCESS_KEY");
            URL sauceConnectUrl = new URL("http://" + userName + ":" + accessKey + "@ondemand.saucelabs.com/wd/hub");
            driver = new RemoteWebDriver(sauceConnectUrl, caps);
        } else {
            driver = new FirefoxDriver();
        }
    }

    @Test
    public void testLocalhost() throws Exception {
        driver.get("http://127.0.0.1:8080/");
        assertEquals("hello world", driver.getTitle());
    }
}
