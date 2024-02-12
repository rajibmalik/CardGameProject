import org.junit.Test;
import structures.basic.Avatar;
import structures.basic.Player;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

public class AvatarTest {
    private Avatar humanAvatar;
    private Player human;

    @Before
    public void setUpAvatarWrapper() {
        human = new Player();
        humanAvatar = new Avatar("Human", human);
    }

    @Test
    public void initialisedWithTwoAttack() {
        assertEquals(2, humanAvatar.getAttack());
    }

    @Test
    public void initialisedWithTwoHealth() {
        assertEquals(2, humanAvatar.getHealth());
    }

    @Test
    public void initialsedWithPlayer() {
        assertEquals(human, humanAvatar.getPlayer());
    }

    @Test
    public void initialsedWithZeroRobustness() {
        assertEquals(0, humanAvatar.getRobustness());
    }

    @Test
    public void setRobustnessToThree() {
        humanAvatar.setRobustness(3);
        assertEquals(3, humanAvatar.getRobustness());
    }


    
}
