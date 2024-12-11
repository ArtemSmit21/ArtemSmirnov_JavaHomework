package org.example.message;

import java.util.Map;

/**
 * Класс для хранения message и его редактирования
 * (потокобезопасный)
 */
public class Message {

  private Map<String, String> content;

  /**
   * @param content из которого строится message
   */
  public Message(Map<String, String> content) {
    this.content = content;
  }

  public Map<String, String> getContent() {
    return content;
  }

  /**
   * @param content, который надо добавить к существующему (совпадающие поля
   *                 обновляются)
   */
  public void updateContent(Map<String, String> content) {
    this.content.putAll(content);
  }
}
