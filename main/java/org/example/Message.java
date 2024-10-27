package org.example;

import java.util.Map;

/**
 * Класс для хранения message и его редактирования
 * (потокобезопасный)
 */
public class Message {

    private Map<String, String> content;

    /**
     * @param content из которого строится message
     * IllegalArgumentException если в мапе есть поле с null
     */
    public Message(Map<String, String> content) {
        synchronized (this) {
            for (Map.Entry<String, String> entry : content.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new IllegalArgumentException("Создать сообщение невозможно! (одно из полей - null)");
                }
            }
            this.content = content;
        }
    }

    public Map<String, String> getContent() {
        return content;
    }

    /**
     * @param content, который надо добавить к существующему (совпадающие поля
     *                 обновляются)
     */
    public void updateContent(Map<String, String> content) {
        synchronized (this) {
            this.content.putAll(content);
        }
    }
}
