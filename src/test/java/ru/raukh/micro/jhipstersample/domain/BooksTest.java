package ru.raukh.micro.jhipstersample.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import ru.raukh.micro.jhipstersample.web.rest.TestUtil;

public class BooksTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Books.class);
        Books books1 = new Books();
        books1.setId(1L);
        Books books2 = new Books();
        books2.setId(books1.getId());
        assertThat(books1).isEqualTo(books2);
        books2.setId(2L);
        assertThat(books1).isNotEqualTo(books2);
        books1.setId(null);
        assertThat(books1).isNotEqualTo(books2);
    }
}
