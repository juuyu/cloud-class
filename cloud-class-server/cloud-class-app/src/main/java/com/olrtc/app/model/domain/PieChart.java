package com.olrtc.app.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author njy
 * @since 2023/4/18 13:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PieChart {
    private Long value;

    private String name;
}
