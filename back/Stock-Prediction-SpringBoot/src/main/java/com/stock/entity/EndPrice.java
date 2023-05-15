package com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bbyh
 * @date 2023/4/16 0016 18:51
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndPrice {
    private String stockDate;
    private Double originPrice;
    private Double predictPrice;
}
