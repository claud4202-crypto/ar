package me.arisdonate.models;

import org.bukkit.Material;

import java.util.List;

/**
 * Модель доната.
 *
 *   id            уникальный идентификатор (lowercase)
 *   displayName   как пишется префикс в чате/табе
 *   startHex      цвет градиента слева
 *   endHex        цвет градиента справа
 *   regionLimit   сколько регионов /sethome (или приватов) можно поставить
 *   homeLimit     лимит /sethome'ов
 *   weight        вес ранга (выше = мощнее)
 *   shulkerColor  цвет шалкера для иконки в /donate GUI
 *   description   многострочное описание
 *   commands      список команд, доступных носителю
 *   guiSlot       слот в /donate GUI
 *   kitId         id комплекта-кита, выдаваемого донатчику (или null)
 */
public final class DonateRank {
    private final String id;
    private final String displayName;
    private final String startHex;
    private final String endHex;
    private final int regionLimit;
    private final int homeLimit;
    private final int weight;
    private final Material shulkerMaterial;
    private final List<String> description;
    private final List<String> commands;
    private final int guiSlot;
    private final String kitId;

    public DonateRank(String id, String displayName, String startHex, String endHex,
                      int regionLimit, int homeLimit, int weight,
                      Material shulkerMaterial, List<String> description,
                      List<String> commands, int guiSlot, String kitId) {
        this.id = id;
        this.displayName = displayName;
        this.startHex = startHex;
        this.endHex = endHex;
        this.regionLimit = regionLimit;
        this.homeLimit = homeLimit;
        this.weight = weight;
        this.shulkerMaterial = shulkerMaterial;
        this.description = description;
        this.commands = commands;
        this.guiSlot = guiSlot;
        this.kitId = kitId;
    }

    public String id()            { return id; }
    public String displayName()   { return displayName; }
    public String startHex()      { return startHex; }
    public String endHex()        { return endHex; }
    public int    regionLimit()   { return regionLimit; }
    public int    homeLimit()     { return homeLimit; }
    public int    weight()        { return weight; }
    public Material shulkerMaterial() { return shulkerMaterial; }
    public List<String> description() { return description; }
    public List<String> commands()    { return commands; }
    public int    guiSlot()       { return guiSlot; }
    public String kitId()         { return kitId; }

    /** Готовый MiniMessage-подобный токен `<grad:#start:#end>name</grad>` для парсера {@link me.arisdonate.util.Msg}. */
    public String gradientName() {
        return "<grad:#" + startHex + ":#" + endHex + ">" + displayName + "</grad>";
    }
}
