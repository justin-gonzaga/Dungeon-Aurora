package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController.GameMode;
import dungeonmania.entities.logicals.Bomb;
import dungeonmania.entities.logicals.FloorSwitch;
import dungeonmania.entities.logicals.LightBulb;
import dungeonmania.entities.logicals.SwitchDoor;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.jupiter.api.Test;

// import dungeonmania.DungeonManiaController.GameMode;
// import dungeonmania.response.models.DungeonResponse;
// import dungeonmania.util.Direction;
// import dungeonmania.util.Position;

public class TestLogicSwitch {

// test and, or, xor, not, co_and

    @Test
    public void testAndLogic() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("logic_switch_and", GameMode.PEACEFUL.getValue());

        // At the start the light bulb is turned off, bomb exists, door is locked, switch not activated
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.OFF)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE)));
        assertEquals(false, resp.getEntities().stream().anyMatch(e -> e.getType().equals(FloorSwitch.STRING_TYPE + FloorSwitch.ACTIVATED)));

        // Place boulder on two adjacent switches
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.RIGHT);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.LEFT);
        ctr.tick(null, Direction.UP);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        resp = ctr.tick(null, Direction.RIGHT);
        

        // Now light bulb should be turned on, door unlocked and switch activated
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.ON)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE + SwitchDoor.UNLOCKED)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(FloorSwitch.STRING_TYPE + FloorSwitch.ACTIVATED)));
        
        // Bomb should have exploded
        assertEquals(false, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));
    }

    @Test
    public void testOrLogic() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("logic_switch_or", GameMode.PEACEFUL.getValue());

        // At the start the light bulb is turned off, bomb exists, door is locked
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.OFF)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE)));

        // Place boulder on first adjacent switch
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.RIGHT);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.LEFT);
        resp = ctr.tick(null, Direction.UP);

        // Now light bulb should be turned on, door unlocked and switch activated
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.ON)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE + SwitchDoor.UNLOCKED)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(FloorSwitch.STRING_TYPE + FloorSwitch.ACTIVATED)));
        
        // Bomb should have exploded
        assertEquals(false, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));

        // Activate the second adjacent switch
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        resp = ctr.tick(null, Direction.RIGHT);
        
        // Everything should still be activated
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.ON)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE + SwitchDoor.UNLOCKED)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(FloorSwitch.STRING_TYPE + FloorSwitch.ACTIVATED)));
        
        // Bomb should have exploded
        assertEquals(false, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));
    }

    @Test
    public void testXorLogic() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("logic_switch_xor", GameMode.PEACEFUL.getValue());

        // At the start the light bulb is turned off, bomb exists, door is locked
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.OFF)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE)));

        // Place boulder on first adjacent switch
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.RIGHT);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.LEFT);
        resp = ctr.tick(null, Direction.UP);

        // Now light bulb should be turned off, door locked, switch activate, bomb not exploded
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.OFF)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(FloorSwitch.STRING_TYPE + FloorSwitch.ACTIVATED)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));

    }

    @Test
    public void testNotLogic() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("logic_switch_not", GameMode.PEACEFUL.getValue());
        resp = ctr.tick(null, Direction.NONE);
        // At the start the light bulb is turned on, door is unlocked, bomb does not interact with not logic
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.ON)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE + SwitchDoor.UNLOCKED)));
        

    }

    @Test
    public void testCoAndLogic() {
        DungeonManiaController ctr = new DungeonManiaController();
        DungeonResponse resp = ctr.newGame("logic_switch_co_and", GameMode.PEACEFUL.getValue());

        // Place boulder on first adjacent switch
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.RIGHT);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.DOWN);
        ctr.tick(null, Direction.LEFT);
        resp = ctr.tick(null, Direction.UP);

        // Now the light bulb should turn on because it is co_and.
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(LightBulb.STRING_TYPE + LightBulb.ON)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(Bomb.STRING_TYPE)));
        assertEquals(true, resp.getEntities().stream().anyMatch(e -> e.getType().equals(SwitchDoor.STRING_TYPE)));

    }


}
