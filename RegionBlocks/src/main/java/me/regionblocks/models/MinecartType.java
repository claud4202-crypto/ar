package me.regionblocks.models;

public enum MinecartType {

    //          Название                        Цена   Радиус  Обсидиан  Приват(регион)  ЛомаетАрис
    BASIC    ("§7Обычная вагонетка",              20,    4,    false,    true,           false,
              "Стандартный взрыв — радиус 4 блока.\nЛомает обычные блоки и регионы.\nНЕ ломает Арис-блок."),

    STRONG   ("§6Усиленная вагонетка",            50,    8,    false,    true,           false,
              "Усиленный взрыв — радиус 8 блоков.\nЛомает обычные блоки и регионы.\nНЕ ломает Арис-блок."),

    MEGA     ("§5Мега вагонетка",               150,   12,    false,    true,           false,
              "Огромный взрыв — радиус 12 блоков.\nЛомает обычные блоки и регионы.\nНЕ ломает Арис-блок."),

    TITAN    ("§4Титановая вагонетка",           400,    8,    true,     true,           true,
              "Ломает обсидиан, плачущий обсидиан,\nобычные блоки и ВСЕ регионы,\nвключая Арис-блок!");

    private final String  displayName;
    private final long    price;
    private final int     radius;
    private final boolean breakObsidian;
    private final boolean breakRegion;
    private final boolean breakAris;
    private final String  description;

    MinecartType(String displayName, long price, int radius,
                 boolean breakObsidian, boolean breakRegion, boolean breakAris,
                 String description) {
        this.displayName   = displayName;
        this.price         = price;
        this.radius        = radius;
        this.breakObsidian = breakObsidian;
        this.breakRegion   = breakRegion;
        this.breakAris     = breakAris;
        this.description   = description;
    }

    public String  getDisplayName()  { return displayName; }
    public long    getPrice()        { return price; }
    public int     getRadius()       { return radius; }
    public boolean isBreakObsidian() { return breakObsidian; }
    public boolean isBreakRegion()   { return breakRegion; }
    public boolean isBreakAris()     { return breakAris; }
    public String  getDescription()  { return description; }
}
