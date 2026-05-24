package me.arisdonate.models;

/**
 * Модель служебного ранга (не доната).
 *
 *  id           идентификатор (lowercase: helper, dhelper, mlmoder, ...)
 *  displayName  как пишется префикс (например "Helper")
 *  startHex     цвет градиента слева (без #)
 *  endHex       цвет градиента справа
 *  weight       чем больше — тем «выше» ранг (Owner = самый большой)
 */
public record StaffRank(
        String id,
        String displayName,
        String startHex,
        String endHex,
        int weight
) {
    /** Готовый MiniMessage-подобный токен `<grad:#start:#end>name</grad>` для {@link me.arisdonate.util.Msg}. */
    public String gradientName() {
        return "<grad:#" + startHex + ":#" + endHex + ">" + displayName + "</grad>";
    }
}
