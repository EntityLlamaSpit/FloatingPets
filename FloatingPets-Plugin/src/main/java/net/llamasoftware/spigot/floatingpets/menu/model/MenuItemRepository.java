package net.llamasoftware.spigot.floatingpets.menu.model;

import net.llamasoftware.spigot.floatingpets.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuItemRepository {

    private final List<MenuItem> items = new ArrayList<>();

    public MenuItemRepository(MenuItem... items){
        this.items.addAll(Arrays.stream(items)
                  .collect(Collectors.toList()));
    }

    public List<MenuItem> getAll() {
        return items;
    }

    public MenuItemRepository add(MenuItem item){
        removeIfExists(item);
        this.items.add(item);
        return this;
    }

    public MenuItemRepository add(List<MenuItem> items){
        items.forEach(this::removeIfExists);
        this.items.addAll(items);
        return this;
    }

    public MenuItemRepository add(MenuItemRepository repository){
        repository.getAll().forEach(this::removeIfExists);
        this.items.addAll(repository.getAll());
        return this;
    }
    
    private void removeIfExists(MenuItem item){
        items.removeIf(menuItem -> item.getSlot() == menuItem.getSlot());
    }

    public MenuItemRepository filledRow(Material material, int... rows){
        MenuItemRepository filledRepository = new MenuItemRepository();
        for(int row : rows) {
            row = (row - 1) * 9;
            for (int i=row; i < row + 9; i++){
                filledRepository.add(new MenuItem(new ItemBuilder(material).name("&7").build(), i) {
                    @Override public void onClick(Player player) { }
                });
            }
        }

        add(filledRepository);
        return this;
    }

}