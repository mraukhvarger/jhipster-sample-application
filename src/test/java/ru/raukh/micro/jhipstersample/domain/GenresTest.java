package ru.raukh.micro.jhipstersample.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import ru.raukh.micro.jhipstersample.web.rest.TestUtil;

public class GenresTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Genres.class);
        Genres genres1 = new Genres();
        genres1.setId(1L);
        Genres genres2 = new Genres();
        genres2.setId(genres1.getId());
        assertThat(genres1).isEqualTo(genres2);
        genres2.setId(2L);
        assertThat(genres1).isNotEqualTo(genres2);
        genres1.setId(null);
        assertThat(genres1).isNotEqualTo(genres2);
    }
}
