package info.kupczynski.linkchecker.common;

import org.junit.Before;
import org.mockito.MockitoAnnotations;


public class BaseTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}
