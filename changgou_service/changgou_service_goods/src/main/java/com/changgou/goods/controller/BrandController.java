package com.changgou.goods.controller;

import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.Page;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin
@RequestMapping("/brand") // 跨域 端口 协议 域名一致才不跨域
public class BrandController {


    @Autowired
    private BrandService brandService;


    // http://localhost:18081/brand/category/11156
    @GetMapping(value="/category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(value = "id")Integer categoryId){
        List<Brand> brands = brandService.findByCategory(categoryId);
        return new Result<>(true,StatusCode.OK,"查询成功",brands);
    }


    /**
     * 查询全部数据 sleep强制阻塞，产生并发
     * http://10.33.72.96/brand
     * @return
     */
    @GetMapping
    public Result findAll(){
//        try {
//            System.out.println("准备睡觉"+Thread.currentThread().getId());
//            Thread.sleep(10000);
//            System.out.println("睡觉结束"+Thread.currentThread().getId());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        List<Brand> brandList = brandService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",brandList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Brand brand = brandService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",brand);
    }


    /***
     * 新增数据
     * @param brand
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Brand brand){
        brandService.add(brand);
        return new Result(true,StatusCode.OK,"添加成功");
    }


    /***
     * 修改数据
     * @param brand
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Brand brand,@PathVariable Integer id){
        brand.setId(id);
        brandService.update(brand);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        brandService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Brand> list = brandService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Brand> pageList = brandService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

//    @GetMapping("/category/{categoryName}")
    public Result<List<Map>> findBrandListByCategoryName(@PathVariable("categoryName")String categoryName){
        List<Map> brandList = brandService.findBrandListByCategoryName(categoryName);
        return new Result<>(true,StatusCode.OK,"查询成功",brandList);
    }
}
