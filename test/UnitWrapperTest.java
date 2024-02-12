import org.junit.Test;
import structures.basic.UnitWrapper;
import structures.basic.Player;
import abilities.Deathwatch;
import abilities.UnitAbility;
import structures.basic.Unit;
import static org.junit.Assert.assertEquals;
import org.junit.Before;

// This class tests the UnitWrapper model 

public class UnitWrapperTest {
    private UnitWrapper badOmen;
    private UnitWrapper gloomChaser;

    // Instantiates an instance of bad omen and gloom chaser units, without adding references to Unit and Player before each test
    @Before
    public void setUpUnitWrapper() {
        badOmen = new UnitWrapper("Bad Omen",1,0);
        gloomChaser = new UnitWrapper("Gloom Chaser", 1, 3);
    }

    // Tests initialisation state 

    @Test
    public void abilityInitialisedAsNull() {
        assertEquals(null, badOmen.getAbility());
        assertEquals(null, gloomChaser.getAbility());
    }

    @Test
    public void hasMovedInitialisedToFalse() {
        assertEquals(false, badOmen.getHasMoved());
        assertEquals(false, gloomChaser.getHasMoved());
    }

    // Tests for getters

    @Test
    public void getAttackWorks() {
        assertEquals("Attack is equal to 0", 0, badOmen.getAttack());
        assertEquals("Attack is equal to 3", 3, gloomChaser.getAttack());
    }

    @Test
    public void getHealthWorks() {
        assertEquals("Health is equal to 1", 1, badOmen.getHealth());
        assertEquals("Health is equal to 1", 1, gloomChaser.getHealth());
    }

    @Test
    public void getNameWorks() {
        assertEquals("Name is equal to 'Bad Omen' ","Bad Omen", badOmen.getName());
        assertEquals("Name is equal to 'Gloom Chaser'","Gloom Chaser", gloomChaser.getName());
    }

    @Test
    public void getHasBeenClickedFalseWorks() {
        assertEquals("hasBeenClicked is initialised to false", false, badOmen.getHasBeenClicked());
        assertEquals("hasBeenClicked is initialised to false", false, gloomChaser.getHasBeenClicked());
    }

    @Test
    public void getHasBeenClickedTruedWorks() {
        badOmen.setHasBeenClicked(true);
        gloomChaser.setHasBeenClicked(true);
        assertEquals("hasBeenClicked is equal to true when setClicked is called", true, badOmen.getHasBeenClicked());
        assertEquals("hasBeenClicked is equal to true when setClicked is called", true, gloomChaser.getHasBeenClicked());
    }

    @Test 
    public void getPlayerReturnsNull () {
        assertEquals("Player reference is null when using the flexibile constructor without Player parameter", null, badOmen.getPlayer());
        assertEquals("Player reference is null when using the flexibile constructor without Player parameter", null, gloomChaser.getPlayer());
    }
    
    @Test 
    public void getUnitReturnsNull () {
        assertEquals("Unit reference is null when using constructor without Unit parameter", null, badOmen.getUnit());
        assertEquals("Unit reference is null when using constructor without Unit parameter", null, gloomChaser.getUnit());
    }

    @Test 
    public void getPlayerReturnsCorrectPlayer () {
        Player player = new Player();
        Player playerTwo = new Player();
        badOmen.setPlayer(player);
        gloomChaser.setPlayer(playerTwo);
        assertEquals("Player reference set on bad omen", player, badOmen.getPlayer());
        assertEquals("PlayerTwo reference set on gloom chaser", playerTwo, gloomChaser.getPlayer());
    }
    
    @Test 
    public void getUnitReturnsCorrectUnit () {
        Unit unit = new Unit();
        Unit unitTwo = new Unit();
        badOmen.setUnit(unit);
        gloomChaser.setUnit(unitTwo);
        assertEquals("Unit reference set to bad omen", unit, badOmen.getUnit());
        assertEquals("Unit two reference set to gloom chaser", unitTwo, gloomChaser.getUnit());
    }

    // Tests for setters

    @Test
    public void setHasBeenClickedFalseWorks() {
        badOmen.setHasBeenClicked(true);
        badOmen.setHasBeenClicked(true);
        gloomChaser.setHasBeenClicked(false);
        badOmen.setHasBeenClicked(false);
        assertEquals("hasBeenClicked is equal to false when setClicked is called and setNotClicked is called",
                     false, badOmen.getHasBeenClicked());
         assertEquals("hasBeenClicked is equal to false when setClicked is called and setNotClicked is called",
                     false, gloomChaser.getHasBeenClicked());
    }

    @Test
    public void setAttackWorks() {
        badOmen.setAttack(2);
        gloomChaser.setAttack(5);
        assertEquals("Set attack equal to 2", 2, badOmen.getAttack());
        assertEquals("Set attack equal to 5", 5, gloomChaser.getAttack());
    }

    @Test
    public void setHealthWorks() {
        badOmen.setHealth(3);
        gloomChaser.setHealth(7);
        assertEquals("Set health equal to 3", 3, badOmen.getHealth());
        assertEquals("Set health equal to 3", 7, gloomChaser.getHealth());
    }

    @Test
    public void hasMovedCanBeSetToTrue() {
        badOmen.setHasMoved(true);
        gloomChaser.setHasMoved(true);
        assertEquals(true, badOmen.getHasMoved());
        assertEquals(true, gloomChaser.getHasMoved());
    }

    @Test
    public void hasMovedCanBeSetToFalseAfterItWasTrue() {
        badOmen.setHasMoved(true);
        gloomChaser.setHasMoved(true);
        badOmen.setHasMoved(false);
        gloomChaser.setHasMoved(false);
        assertEquals(false, badOmen.getHasMoved());
        assertEquals(false, gloomChaser.getHasMoved());
    }

    @Test
    public void abilityCanBeAdded() {
        UnitAbility weakDeathwatch = new Deathwatch(1, 1);
        badOmen.setAbility(weakDeathwatch);
        UnitAbility strongDeathwatch  = new Deathwatch(4, 4);
        gloomChaser.setAbility(strongDeathwatch);

        assertEquals(weakDeathwatch, badOmen.getAbility());
        assertEquals(strongDeathwatch, gloomChaser.getAbility());
    }

    // Tests use ability

    @Test
    public void useAbilityWorks() {
        UnitAbility weakDeathwatch = new Deathwatch(1, 1);
        badOmen.setAbility(weakDeathwatch);
        UnitAbility strongDeathwatch  = new Deathwatch(4, 4);
        gloomChaser.setAbility(strongDeathwatch);

        badOmen.useAbility();
        gloomChaser.useAbility();

        assertEquals(1, badOmen.getAttack());
        assertEquals(7, gloomChaser.getAttack());
        assertEquals(2, badOmen.getHealth());
        assertEquals(5, gloomChaser.getHealth());
    }
}
