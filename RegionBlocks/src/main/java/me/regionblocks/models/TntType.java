package me.regionblocks.models;

public enum TntType {

    //        Название                    Цена   Сила  Радиус  Обсидиан  Приват
    BASIC  ("§7Обычное ТНТ",              15,   4.0f,   4,    false,    false,
            "Стандартный взрыв.\nРазрушает обычные блоки\nв радиусе 4 блоков.\nНЕ ломает Арис-блок."),

    STRONG ("§6Усиленное ТНТ",            25,   6.0f,   8,    false,    false,
            "Увеличенный радиус взрыва.\nРазрушает обычные блоки\nв радиусе 8 блоков.\nНЕ ломает Арис-блок."),

    MEGA   ("§5Мега ТНТ",               100,  10.0f,  12,    false,    false,
            "Огромный взрыв — радиус 12 блоков.\nРазрушает обычные блоки.\nНЕ ломает Арис-блок."),

    TITAN  ("§4Титановое ТНТ",           350,   8.0f,   8,    true,     true,
            "Ломает ТОЛЬКО обсидиан\nи плачущий обсидиан.\nУНИЧТОЖАЕТ привати (регионы)\nв радиусе 8 блоков!");

    private final String  displayName;
    private final long    price;
    private final float   power;
    private final int     radius;
    private final boolean breakObsidian;
    private final boolean breakPrivate;
    private final String  description;

    TntType(String displayName, long price, float power, int radius,
            boolean breakObsidian, boolean breakPrivate, String description) {
        this.displayName   = displayName;
        this.price         = price;
        this.power         = power;
        this.radius        = radius;
        this.breakObsidian = breakObsidian;
        this.breakPrivate  = breakPrivate;
        this.description   = description;
    }

    public String  getDisplayName()  { return displayName; }
    public long    getPrice()        { return price; }
    public float   getPower()        { return power; }
    public int     getRadius()       { return radius; }
    public boolean isBreakObsidian() { return breakObsidian; }
    public boolean isBreakPrivate()  { return breakPrivate; }
    public String  getDescription()  { return description; }
}
