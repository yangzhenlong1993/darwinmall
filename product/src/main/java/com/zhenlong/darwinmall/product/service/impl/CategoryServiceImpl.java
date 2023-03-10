package com.zhenlong.darwinmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhenlong.darwinmall.product.service.CategoryBrandRelationService;
import com.zhenlong.darwinmall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhenlong.common.utils.PageUtils;
import com.zhenlong.common.utils.Query;

import com.zhenlong.darwinmall.product.dao.CategoryDao;
import com.zhenlong.darwinmall.product.entity.CategoryEntity;
import com.zhenlong.darwinmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1. ??????????????????
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //2. ???????????????????????????
        //2.1 ????????????????????????
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter((categoryEntity) ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildren(menu, categoryEntities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1.????????????????????????????????????????????????????????????
        //???????????????????????????deleteBatchIds???????????????
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * ??????catelogId???????????????
     * ??????/???/??????
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        //?????????2???25???225
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[0]);
    }

    /**
     * ?????????????????????????????????
     * ??????????????????????????????@Caching
     * ??????????????????????????????????????????@CacheEvict(value = "category", allEntries = true)
     * @param category
     */

//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'getLevelOneCategory'"),
//            @CacheEvict(value = "category",key = "'getCatalogJson'")
//    })
    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????(?????????????????????)???
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ????????????
     *      ???????????????????????????????????????
     *      key??????????????????:???????????????::SimpleKey::[](????????????key???)
     *      ?????????value??????????????????jdk?????????????????????????????????????????????redis???
     *      ??????????????? -1???
     *
     *   ??????????????????key?????????
     *      ?????????????????????key???key???????????????????????????Spel
     *      ????????????????????????????????????:?????????????????????????????????
     *      ??????????????????json??????
     *
     *
     * 4???Spring-Cache??????????????????
     *  1???????????????
     *      ???????????????????????????null???????????????????????????????????????
     *      ???????????????????????????????????????????????????????????????????????????????????????????????? ? ?????????????????????;??????sync = true?????????????????????
     *      ????????????????????????key????????????????????????????????????????????????????????????
     *  2)?????????????????????????????????????????????
     *      1?????????????????????
     *      2????????????Canal,?????????MySQL??????????????????Redis
     *      3???????????????????????????????????????????????????
     *
     *  ?????????
     *      ?????????????????????????????????????????????????????????????????????????????????????????????Spring-Cache???????????????(????????????????????????????????????????????????)
     *      ???????????????????????????
     *
     *  ?????????
     *      CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache?????????????????????
     * @return
     */
    @Cacheable(value={"category"}, key = "#root.methodName",sync = true) //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public List<CategoryEntity> getLevelOneCategory() {
        System.out.println("?????????????????????");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("?????????????????????");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> levelOneCategory = getParentCid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parentCid = levelOneCategory.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //??????????????????????????????????????????
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2item.getCatId().toString(), l2item.getName());
                    List<CategoryEntity> level3Catalog = getParentCid(selectList, l2item.getCatId());
                    if (level3Catalog != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Catalog.stream().map(l3item -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2item.getCatId().toString(), l3item.getCatId().toString(), l3item.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parentCid;
    }

    //TODO  ???????????????????????????OutOfDirectMemoryError
    //springboot 2.0??????????????????lettuce????????????redis????????????????????????netty??????????????????
    //lettuce???bug??????netty???????????????????????????product service????????????300m???netty????????????????????????????????????????????????300m
    //???????????????????????????dio.netty.maxDirectMemory????????????????????????
    //1. ??????lettuce????????????????????????jedis
    // lettuce??? jedis????????????redis?????????????????????redisTemplate???????????????????????????
    //@Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        /**
         * ????????????????????????????????????????????????
         * ??????????????????????????????????????????????????????
         * ?????????????????????????????????
         */
        //1.?????????????????????????????????????????????json?????????????????????json???????????????????????????????????????????????????????????????????????????
        //json???????????????????????????
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.hasText(catalogJSON)) {
            //???????????????
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            //??????????????????????????????????????????????????????json???????????????
            return catalogJsonFromDb;
        }
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }

    /**
     * ????????????????????????????????????????????????????????????
     * 1. ????????????
     * 2. ????????????
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //1.redisson?????????????????????????????????????????????
        //????????????????????????????????????????????????????????? 11??????????????????product-11-lock
        RLock catalogJsonLock = redissonClient.getLock("CatalogJsonLock");
        catalogJsonLock.lock();


        Map<String, List<Catelog2Vo>> catalogJsonFromDb;
        try {
            catalogJsonFromDb = getCatalogJsonFromDb();
        } finally {
            catalogJsonLock.unlock();
        }
        return catalogJsonFromDb;

    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //1.????????????????????????redis??????
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("????????????????????????");
            //????????????
            //??????????????????????????????????????? ????????????????????????????????????????????????????????????????????????
            //stringRedisTemplate.expire("lock",30, TimeUnit.MINUTES);
            Map<String, List<Catelog2Vo>> catalogJsonFromDb;
            try {
                catalogJsonFromDb = getCatalogJsonFromDb();
            } finally {

                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                Long deleteResult = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
                System.out.println("????????????????????????" + deleteResult);
            }

            //stringRedisTemplate.delete("lock");//?????????????????????????????????
            //???????????????????????????????????????????????????????????????????????????????????????????????????lua??????
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){ //????????????????????????????????????????????????????????????????????????????????????????????????????????????
//                stringRedisTemplate.delete("lock");
//            }
            return catalogJsonFromDb;
        } else {
            System.out.println("???????????????????????????????????????");
            //????????????????????????
            //??????100ms??????
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.hasText(catalogJSON)) {
            //???????????????null????????????
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("??????????????????....");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> levelOneCategory = getParentCid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parentCid = levelOneCategory.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //??????????????????????????????????????????
            List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2item.getCatId().toString(), l2item.getName());
                    List<CategoryEntity> level3Catalog = getParentCid(selectList, l2item.getCatId());
                    if (level3Catalog != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3Vos = level3Catalog.stream().map(l3item -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2item.getCatId().toString(), l3item.getCatId().toString(), l3item.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3Vos);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        String s = JSON.toJSONString(parentCid);
        stringRedisTemplate.opsForValue().set("catalogJSON", s, new Random().nextInt(10), TimeUnit.DAYS);
        return parentCid;
    }

    //????????????????????????????????????
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        /**
         * ????????????????????????????????????????????????????????????????????????????????????????????????
         */
        //??????????????????????????????????????????????????????????????????
        // synchronised(this)???springboot??????????????????????????????????????????
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        synchronized (this) {
            //?????????????????????????????????????????????????????????????????????????????????????????????
            return getCatalogJsonFromDb();
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
    }


    //??????225???25???2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        Long parentCid = categoryEntity.getParentCid();
        if (parentCid != 0) {
            findParentPath(parentCid, paths);
        }
        return paths;
    }

    //????????????????????????????????????
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1. ???????????????
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2. ????????????
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}