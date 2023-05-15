package com.stock.service.impl;

import com.stock.entity.StockName;
import com.stock.mapper.StockNameMapper;
import com.stock.service.IStockNameService;
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
public class StockNameServiceImpl extends ServiceImpl<StockNameMapper, StockName> implements IStockNameService {

}
