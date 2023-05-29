package com.olrtc.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author njy
 * @since 2023/4/18 09:46
 */
@Data
@Accessors(chain = true)
public class UserCourseTableDto {

    private List<TodayTodo> todayTodos;




    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayTodo {
        private String title;
        private boolean complete;
    }
}
