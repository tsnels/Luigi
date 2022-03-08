package be.vdab.luigi.restclients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

class FixerKoersClientTest {
    private FixerKoersClient client;

    @BeforeEach
    void beforeEach() {
        client = new FixerKoersClient();
    }

    @Test
    void deKoersIsPositief() {
        assertThat(client.getDollarKoers()).isPositive();
    }
}