package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {


    /**
     * 批量上架
     * @param spuid 要上架的所有spuid
     */
    void putMany(Long[] spuid);
    /**
     * 上架
     * @param id
     */
    void putSpu(Long id);

    /**
     * 下架
     * @param id
     */
    void pullSpu(Long id);
    /**
     * 商品审核
     * @param id spu的id
     */
    void auditSpu(Long id);
    /**
     * 根据id查询spu与sku列表信息
     * @param id
     * @return
     */
    Goods findGoodsById(long id);

    /**
     * 商品增加
     *
     * @param goods
     */
    void saveGoods(Goods goods);

    /***
     * 查询所有
     * @return
     */
    List<Spu> findAll();

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    Spu findById(String id);

    /***
     * 新增
     * @param spu
     */
    void add(Spu spu);

    /***
     * 修改
     * @param spu
     */
    void update(Spu spu);

    /***
     * 删除
     * @param id
     */
    void delete(String id);

    /***
     * 多条件搜索
     * @param searchMap
     * @return
     */
    List<Spu> findList(Map<String, Object> searchMap);

    /***
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(int page, int size);

    /***
     * 多条件分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    Page<Spu> findPage(Map<String, Object> searchMap, int page, int size);


}
