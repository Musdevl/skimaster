package fr.univcotedazur.skimaster.cli.commands;

import fr.univcotedazur.skimaster.cli.model.PlanEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PlanCommands.class)
@Import(fr.univcotedazur.skimaster.cli.commands.RestTestConfig.class)
class PlanCommandsTest {

    @Autowired
    private PlanCommands client;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    void plansSetTest() {
        // Given
        mockServer.expect(requestTo(fr.univcotedazur.skimaster.cli.commands.RestTestConfig.TEST_BASE_URL + "/plans"))
                .andRespond(withSuccess("[\"BASIC_PLAN\", \"SUPER_CARD\", \"FAMILY_PLAN\", \"BEGINNER_PASS\"]", MediaType.APPLICATION_JSON));

        // When-Then
        assertEquals(EnumSet.allOf(PlanEnum.class), client.plans());
        mockServer.verify();
    }
}
