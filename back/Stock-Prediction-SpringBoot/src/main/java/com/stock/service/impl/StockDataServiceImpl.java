package com.stock.service.impl;

import com.stock.entity.StockData;
import com.stock.mapper.StockDataMapper;
import com.stock.service.IStockDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author admin
 * @since 2023-04-11
 */
@Service
public class StockDataServiceImpl extends ServiceImpl<StockDataMapper, StockData> implements IStockDataService {

}
