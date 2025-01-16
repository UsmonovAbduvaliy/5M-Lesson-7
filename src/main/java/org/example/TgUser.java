package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TgUser {
    private Long chatId;
    private Integer messageId;
    private Integer chosenUserId;
    private Integer chosenAlbumId;
    private Integer chosenPostId;
    private State state = State.NEW;
}
