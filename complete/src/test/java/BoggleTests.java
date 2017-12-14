
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

public class BoggleTests {

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private PrintStream ps = new PrintStream(baos);

    @Before
    public void setup() {
        System.setOut(ps);
    }

    /**
     * Dont provie args
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void noArgs() {
        Boggle.main(new String[] {});
    }

    /**
     * Count number of solutions
     */
    @Test
    public void solutionOneCorrect() {
        String[] args = { "resources/dictionary.txt", "resources/problem1/problem.txt" };
        Boggle.main(args);
        assertThat(output(), containsString("14"));
    }


    /**
     * Count number of solutions
     */
    @Test
    public void solutionTwoCorrect() {
        String[] args = { "resources/dictionary.txt", "resources/problem2/problem.txt" };
        Boggle.main(args);
        assertThat(output(), containsString("82"));
    }
    /**
     * Problem text has a board size too big
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void malformattedProblemText() {
        String[] args = { "resources/dictionary.txt", "resources/problem1error/problem.txt" };
        Boggle.main(args);
    }
    private String output() {
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
