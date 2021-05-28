package entityTests;

import com.vik.models.Card;
import org.junit.Assert;
import org.junit.Test;

public class TestCard {
    @Test
    public void shouldSplitCardNumber() {
        Card card = new Card();
        card.setCardNumber(1111_1111_1111_1111L);
        Assert.assertEquals("1111 1111 1111 1111", card.getCardNumberSplit());
    }

    @Test
    public void shouldNotSplitCardNumber() {
        Card card = new Card();
        card.setCardNumber(30003L);
        Assert.assertEquals("30003", card.getCardNumberSplit());
    }
}
