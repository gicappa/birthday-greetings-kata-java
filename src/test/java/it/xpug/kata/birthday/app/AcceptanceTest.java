package it.xpug.kata.birthday.app;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import it.xpug.kata.birthday.domain.BirthdayGreetingsUseCase;
import it.xpug.kata.birthday.infrastructure.CsvEmployeeRepository;
import it.xpug.kata.birthday.infrastructure.JavaxEmailService;
import java.io.FileNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BirthdayGreetingsUseCase")
public class AcceptanceTest {

    private static final int NONSTANDARD_PORT = 9999;
    private BirthdayGreetingsUseCase birthdayGreetingsUseCase;
    private SimpleSmtpServer mailServer;

    @BeforeEach
    public void before() {
        mailServer = SimpleSmtpServer.start(NONSTANDARD_PORT);
    }

    @Nested
    @DisplayName("when it's somebody's birthday")
    class Birthday {

        private SmtpMessage message;

        @BeforeEach
        void before() throws FileNotFoundException {
            birthdayGreetingsUseCase =
                new BirthdayGreetingsUseCase(
                    new CsvEmployeeRepository("employee_data.txt"),
                    new JavaxEmailService("localhost", NONSTANDARD_PORT),
                    clockAt(2008, 10, 8));

            birthdayGreetingsUseCase.sendGreetings();
            message = (SmtpMessage) mailServer.getReceivedEmail().next();
        }

        @Test
        public void it_sends_greetings() {
            assertEquals(1, mailServer.getReceivedEmailSize(), "message not sent?");
        }

        @Test
        public void it_has_a_subject() {
            assertEquals("Happy Birthday!", message.getHeaderValue("Subject"));
        }

        @Test
        public void it_has_a_body() {
            assertEquals("Happy Birthday, dear John!", message.getBody());
        }

        @Test
        public void it_has_a_recipient() {
            var recipients = message.getHeaderValues("To");
            assertEquals(1, recipients.length);
            assertEquals("john.doe@foobar.com", recipients[0]);
        }

    }


    @Nested
    @DisplayName("when it's somebody's birthday")
    class NotBirthday {

        @BeforeEach
        void before() throws FileNotFoundException {
            birthdayGreetingsUseCase =
                new BirthdayGreetingsUseCase(
                    new CsvEmployeeRepository("employee_data.txt"),
                    new JavaxEmailService("localhost", NONSTANDARD_PORT),
                    clockAt(2008, 1, 1));

            birthdayGreetingsUseCase.sendGreetings();
        }

        @Test
        public void it_does_not_send_email() {
            assertEquals(0, mailServer.getReceivedEmailSize(), "what? messages?");
        }
    }


    @AfterEach
    public void after() throws Exception {
        mailServer.stop();

        Thread.sleep(200);
    }

    private Clock clockAt(int year, int month, int day) {
        return Clock.fixed(dateAt(year, month, day), ZoneId.of("CET"));
    }

    private static Instant dateAt(int year, int month, int day) {
        return LocalDate.of(year, month, day)
            .atTime(0, 0)
            .toInstant(UTC);
    }

}
