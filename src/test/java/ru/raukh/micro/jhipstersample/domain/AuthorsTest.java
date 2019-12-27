package ru.raukh.micro.jhipstersample.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import ru.raukh.micro.jhipstersample.web.rest.TestUtil;

public class AuthorsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Authors.class);
        Authors authors1 = new Authors();
        authors1.setId(1L);
        Authors authors2 = new Authors();
        authors2.setId(authors1.getId());
        assertThat(authors1).isEqualTo(authors2);
        authors2.setId(2L);
        assertThat(authors1).isNotEqualTo(authors2);
        authors1.setId(null);
        assertThat(authors1).isNotEqualTo(authors2);
    }
}
