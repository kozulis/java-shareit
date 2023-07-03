package ru.practicum.gateway.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.gateway.validation.OnCreate;


import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Integer id;

    @NotBlank(message = "Поле 'text' не должно быть пустым", groups = OnCreate.class)
    private String text;

    private Integer itemId;

    private String authorName;

    private LocalDateTime created;

}
