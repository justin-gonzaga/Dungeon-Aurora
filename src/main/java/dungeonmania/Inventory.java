package dungeonmania;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import dungeonmania.battlestrategies.BattleStrategy.BattleDirection;
import dungeonmania.entities.Fighter;
import dungeonmania.entities.collectables.Anduril;
import dungeonmania.entities.collectables.BattleItem;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.OneRing;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.buildables.Bow;
import dungeonmania.entities.collectables.buildables.MidnightArmour;
import dungeonmania.entities.collectables.buildables.Sceptre;
import dungeonmania.entities.collectables.buildables.Shield;
import dungeonmania.entities.collectables.consumables.Potion;
import dungeonmania.entities.logicals.Bomb;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.ItemResponse;

/**
 * Represents an inventory that contains collectable items.
 */
public class Inventory {
    private List<Entity> collectables = new ArrayList<>();

    /**
     * Can't pick up more than one key
     * 
     * @param c collectable to add
     * @return true if the player was able to pick the collectable up
     */
    public boolean add(Entity c) {
        if (c instanceof Key && this.contains(Key.class)) {
            // Player cannot pickup a second key
            return false;
        }
        if (c instanceof Bomb) {
            // Player cannot pickup bomb already placed
            Bomb bomb = (Bomb) c;
            if (bomb.getIsPlaced() == true) {
                return false;
            }
        }
        return this.collectables.add(c);
    }

    /**
     * @param c collectable to remove
     * @return true if the collectable was in the inventory
     */
    public boolean remove(Entity c) {
        return this.collectables.remove(c);
    }

    public boolean remove(String stringType) {
        for (Entity item : this.collectables) {
            if (item.getTypeAsString() == stringType) {
                return remove(item);
            }
        }
        return false;
    }

    /**
     * Removes one of the given class from the inventory.
     * 
     * @return true if the inventory had something removed
     */
    public boolean pay(List<Class<? extends Entity>> list) {
        List<Entity> price = new ArrayList<>();

        list.stream().forEach(t -> {
            Entity item = collectables.stream().filter(c -> t.isInstance(c)).findFirst().orElse(null);

            // Only Pay with the Item if There are Not Enough of the Specified Type Already
            // Paid in the Cost
            if (list.stream().filter(d -> d.equals(t)).count() != price.stream().filter(e -> e.equals(t)).count()) {
                price.add(item);
            }
        });

        if (price.stream().anyMatch(i -> i == null))
            return false;

        // SunStone Takes Priority Over Treasure
        if (price.stream().filter(o -> o.getClass().equals(SunStone.class)).findFirst().isPresent()) {
            // If Inventory Contains SunStone, No Need to Remove the Treasure or the
            // SunStone
            price.stream().filter(e -> !((e instanceof SunStone) || (e instanceof Treasure)))
                    .forEach(i -> collectables.remove(i));
        } else {
            // Inventory Does Not Contain SunStone, Treasure is Used to Pay
            price.stream().forEach(i -> collectables.remove(i));
        }
        return true;
    }

    /**
     * Attempts to build an item with the given string name
     * @param buildable name of item being built
     * @throws InvalidActionException
     */
    public void build(String buildable) throws InvalidActionException {
        List<Entity> items;
        switch (buildable) {
        case Shield.STRING_TYPE:
            items = buildable(Shield.RECIPES);
            if (items == null)
                throw new InvalidActionException("not enough resources to build " + buildable);
            collectables.removeAll(items);
            collectables.add(new Shield(null, null));
            return;
        case Sceptre.STRING_TYPE:
            items = buildable(Sceptre.RECIPES);
            if (items == null)
                throw new InvalidActionException("not enough resources to build " + buildable);
            collectables.removeAll(items);
            collectables.add(new Sceptre(null, null));
            return;
        case Bow.STRING_TYPE:
            items = buildable(Bow.RECIPES);
            if (items == null)
                throw new InvalidActionException("not enough resources to build " + buildable);
            collectables.removeAll(items);
            collectables.add(new Bow(null, null));
            return;
        case MidnightArmour.STRING_TYPE:
            items = buildable(MidnightArmour.RECIPES);
            if (items == null)
                throw new InvalidActionException("not enough resources to build " + buildable);
            collectables.removeAll(items);
            collectables.add(new MidnightArmour(null, null));
            return;
        default:
            throw new IllegalArgumentException("unknown buildable: " + buildable);
        }
    }

    /**
     * 
     * @param recipes of the item
     * @return true if item can be built
     */
    public List<Entity> buildable(List<List<String>> recipes) {
        for (List<String> recipe : recipes) {
            List<Entity> items = findItems(recipe);
            if (items != null)
                return items;
        }
        return null;
    }

    /**
     * 
     * @return true if the inventory contains a sceptre
     */
    public boolean hasSceptre() {
        Sceptre sceptre = (Sceptre) collectables.stream().filter(c -> c instanceof Sceptre).findFirst().orElse(null);

        return sceptre != null;
    }

    /**
     * 
     * @return list of names of currently buildable items.
     */
    public List<String> getBuildables() {
        List<String> buildables = new ArrayList<>();
        if (buildable(Bow.RECIPES) != null)
            buildables.add(Bow.STRING_TYPE);
        if (buildable(Sceptre.RECIPES) != null)
            buildables.add(Sceptre.STRING_TYPE);
        if (buildable(Shield.RECIPES) != null)
            buildables.add(Shield.STRING_TYPE);
        if (buildable(MidnightArmour.RECIPES) != null)
            buildables.add(MidnightArmour.STRING_TYPE);

        return buildables;
    }

    /**
     * Use the item specified by the id. Raise an InvalidArgumentException if the
     * item can't be used. Raise an InvalidActionException if the item is not in the
     * inventory.
     * 
     * @param entityId to be used
     * @return the entity used.
     * 
     */
    public Entity useItem(String entityId) throws IllegalArgumentException, InvalidActionException {

        // find item
        Entity itemUsed = collectables.stream().filter(c -> c.getId().equals(entityId)).findFirst().orElse(null);

        if (itemUsed == null)
            throw new InvalidActionException("Item not in inventory");

        if (!(itemUsed instanceof Potion) && !(itemUsed instanceof Bomb))
            throw new IllegalArgumentException("Item not useable");

        if (itemUsed instanceof Potion) {
            Potion potionDrunk = (Potion) itemUsed;
            potionDrunk.drink();
        }

        collectables.remove(itemUsed);

        return itemUsed;
    }

    /**
     * finds the items from the inventory
     * 
     * @param itemsStringType list of String
     * @return null if inventory does not contain all the items, else return the
     *         list of items
     */
    public List<Entity> findItems(List<String> itemsStringType) {
        List<Entity> found = new ArrayList<>();
        for (String itemStringType : itemsStringType) {
            Optional<Entity> itemOpt = this.collectables.stream()
                    // find an item of the right type that isn't already used, except
                    // if Treasure is required in the recipe then the SunStone can be used even if
                    // it has been previously used
                    .filter(item -> (((item.getTypeAsString().equals(itemStringType) && !found.contains(item))
                            || (item.getTypeAsString().equals(SunStone.STRING_TYPE)
                                    && itemStringType == Treasure.STRING_TYPE))))
                    .findFirst();

            // Do Not Treat the Sun Stone as a Type of Treasure If Sceptre is Crafted with
            // Both SunStone and Treasure
            // (see Assumptions)

            if (Sceptre.RECIPES.stream().anyMatch(o -> ((itemsStringType.containsAll(o))))) {
                itemOpt = this.collectables.stream()
                        // find an item of the right type that isn't already used
                        .filter(item -> (((item.getTypeAsString().equals(itemStringType) && !found.contains(item)))))
                        .findFirst();
            }

            if (itemOpt.isEmpty())
                return null;
            else
                found.add(itemOpt.get());
        }

        return found;
    }

    /**
     * decreases the items' durability
     * 
     * @param d
     */
    public void usedItemsForBattle(BattleDirection d) {
        List<Entity> deadItems = new ArrayList<>();
        for (Entity item : this.collectables) {
            if (item instanceof BattleItem) {
                BattleItem bitem = (BattleItem) item;
                bitem.usedForBattleRound(d);
                if (bitem.getDurability() <= 0) {
                    deadItems.add(item);
                }
            }
        }
        this.collectables.removeAll(deadItems);
    }

    /**
     * @param bitem battle item
     * @param d     direction
     * @return true if the battle item is dead
     */
    public boolean usedItemForBattle(BattleItem bitem, BattleDirection d) {
        bitem.usedForBattleRound(d);
        if (bitem.getDurability() <= 0) {
            assert bitem instanceof Entity;
            this.collectables.remove((Entity) bitem);
            return true;
        }
        return false;
    }

    /**
     * See the list below for what is classified as a weapon
     * 
     * @return a weapon, or null
     */
    public BattleItem getOneWeapon() {
        // we could add another layer (make a super class Weapon, but this is
        // good enough)
        List<Class<? extends BattleItem>> weapons = List.of(Sword.class, Bow.class, Anduril.class);
        return (BattleItem) this.collectables.stream().filter(e -> weapons.contains(e.getClass())).findFirst()
                .orElse(null);
    }

    /**
     * Total bonus added by the inventory in the specified direction
     *
     * Notice that attack damage adds, but defence coefficients multiply.
     * 
     * @param d battle direction
     * @return total bonus
     */
    public float totalBonus(BattleDirection d, Fighter target) {
        float bonus = 1;
        if (d == BattleDirection.ATTACK) {
            bonus = 0;
        } else if (d == BattleDirection.DEFENCE) {
            bonus = 1;
        }
        for (Entity item : this.collectables) {
            if (item instanceof BattleItem) {
                BattleItem bitem = (BattleItem) item;
                if (d == BattleDirection.ATTACK) {
                    bonus += bitem.getAttackDamageBonus(target);
                } else if (d == BattleDirection.DEFENCE) {
                    bonus *= bitem.getDefenceCoefBonus();
                }
            }
        }
        return bonus;
    }

    /**
     * @usage for example,
     *        {@code inventory.itemsOfType(Shield).forEach(shield -> foobar)}
     * @param <T>  type
     * @param type type
     * @return Stream of Entities
     */
    public <T extends Entity> Stream<T> itemsOfType(Class<T> type) {
        return this.collectables.stream().filter(e -> type.isInstance(e)).map(e -> {
            @SuppressWarnings("unchecked")
            T t = (T) e; // bruh
            return t;
        });
    }

    /**
     * generates a list of item responses from the current inventory
     * @return the item responses
     */
    public List<ItemResponse> asItemResponses() {
        List<ItemResponse> outputListItemResponses = new ArrayList<ItemResponse>();
        for (Entity item : collectables) {
            String id = item.getId();
            String type = item.getTypeAsString();
            ItemResponse currItemResponse = new ItemResponse(id, type);
            outputListItemResponses.add(currItemResponse);
        }
        return outputListItemResponses;
    }

    /**
     * 
     * @return list of stored collectable entities
     */
    public List<Entity> getCollectables() {
        return this.collectables;
    }

    /**
     * Checks for an instance of a class in the inventory.
     * 
     * @param type
     * @return true if an instance of type exists in the inventory.
     */
    public boolean contains(Class<?> type) {
        return collectables.stream().anyMatch(c -> c.getClass().equals(type));
    }

    /**
     * Remove everything from the inventory.
     */
    public void clear() {
        collectables.clear();
    }

    public void purgeOneRing() {
        List<Entity> copy = new ArrayList<>(collectables);
        copy.stream().forEach(e -> {
            if (e instanceof OneRing) {
                collectables.remove(e);
            }
        });
    }
}
