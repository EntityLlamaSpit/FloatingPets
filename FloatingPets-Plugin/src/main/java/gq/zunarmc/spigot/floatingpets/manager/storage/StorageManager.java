package gq.zunarmc.spigot.floatingpets.manager.storage;

import gq.zunarmc.spigot.floatingpets.api.model.Pet;
import gq.zunarmc.spigot.floatingpets.api.model.PetType;
import gq.zunarmc.spigot.floatingpets.model.misc.Food;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StorageManager {

    public final LinkedList<Pet> cachedPets;
    @Getter
    public final List<PetType> cachedTypes;
    public final List<Food> cachedFoodItems;
    public final Map<String, Object> cachedLocaleData;

    protected StorageManager() {
        cachedPets       = new LinkedList<>();
        cachedTypes      = new ArrayList<>();
        cachedFoodItems  = new ArrayList<>();
        cachedLocaleData = new HashMap<>();
    }

    public abstract void preload(Type type);

    /* Pet storage */

    public Optional<Pet> getPetByUniqueId(UUID uniqueId) {
        return cachedPets.stream()
                .filter(pet -> pet.getUniqueId().equals(uniqueId))
                .findAny();
    }

    public LinkedList<Pet> getPetsByOwner(UUID uniqueId) {
        Supplier<Stream<Pet>> streamSupplier = () -> cachedPets.stream()
                .filter(pet -> pet.getOwner().equals(uniqueId));

        LinkedList<Pet> collect = streamSupplier.get()
                .filter(Pet::isAlive)
                .sorted(Comparator.comparingInt(o -> o.getEntity().getEntity().getEntityId()))
                .collect(Collectors.toCollection(LinkedList::new));

        collect.addAll(streamSupplier.get()
                .filter(pet -> !pet.isAlive())
                .sorted(Comparator.comparingInt(Object::hashCode))
                .collect(Collectors.toCollection(LinkedList::new)));

        return collect;
    }

    public abstract void storePet(Pet pet, boolean save);

    public abstract void updatePet(Pet pet, StorageManager.Action action);

    /* Type storage */

    public Optional<PetType> getTypeByName(String name){
        return cachedTypes.stream()
                .filter(petType -> petType.getName().equalsIgnoreCase(name))
                .findAny();
    }

    public Optional<PetType> getTypeByUniqueId(UUID uniqueId) {
        return cachedTypes.stream()
                .filter(petType -> petType.getUniqueId().equals(uniqueId))
                .findAny();
    }

    /* Locale */

    public String getLocaleByKey(String key) {
        if(!cachedLocaleData.containsKey(key))
            return key;

        return (String) cachedLocaleData.get(key);
    }

    @SuppressWarnings("unchecked")
    public List<String> getLocaleListByKey(String key) {
        if(!cachedLocaleData.containsKey(key))
            return new ArrayList<>();

        return (List<String>) cachedLocaleData.get(key);
    }

    /* Misc storage */

    public Optional<Food> getFoodItemByStack(ItemStack stack) {
        return cachedFoodItems.stream()
                .filter(foodItem -> foodItem.getMaterial() == stack.getType())
                .filter(foodItem -> stack.getAmount()      >= foodItem.getAmount())
                .findAny();
    }

    /* Model */

    public enum Type {
        LOCALE,
        TYPE,
        PET,
        MISC
    }

    public enum Action {
        RENAME,
        REMOVE,
        PARTICLE
    }

}