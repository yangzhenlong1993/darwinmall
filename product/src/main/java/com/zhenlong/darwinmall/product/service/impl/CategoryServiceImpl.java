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
        //1. 查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //2. 组装成父子树形结构
        //2.1 找到所有一级分类
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
        //TODO  1.检查当前删除的菜单，是否有被别的地方引用
        //应该使用逻辑删除，deleteBatchIds是物理删除
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 找到catelogId的完整路径
     * 【父/子/孙】
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        //调顺序2，25，225
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[0]);
    }

    /**
     * 级联更新所有关联的数据
     * 同时进行多种缓存操作@Caching
     * 指定删除某个分区下的所有数据@CacheEvict(value = "category", allEntries = true)
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
     * 每一个需要缓存的数据我们都来指定要放到那个名字的缓存。【缓存的分区(按照业务类型分)】
     * 代表当前方法的结果需要缓存，如果缓存中有，方法都不用调用，如果缓存中没有，会调用方法。最后将方法的结果放入缓存
     * 默认行为
     *      如果缓存中有，方法不再调用
     *      key是默认生成的:缓存的名字::SimpleKey::[](自动生成key值)
     *      缓存的value值，默认使用jdk序列化机制，将序列化的数据存到redis中
     *      默认时间是 -1：
     *
     *   自定义操作：key的生成
     *      指定生成缓存的key：key属性指定，接收一个Spel
     *      指定缓存的数据的存活时间:配置文档中修改存活时间
     *      将数据保存为json格式
     *
     *
     * 4、Spring-Cache的不足之处：
     *  1）、读模式
     *      缓存穿透：查询一个null数据。解决方案：缓存空数据
     *      缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;使用sync = true来解决击穿问题
     *      缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间
     *  2)、写模式：（缓存与数据库一致）
     *      1）、读写加锁。
     *      2）、引入Canal,感知到MySQL的更新去更新Redis
     *      3）、读多写多，直接去数据库查询就行
     *
     *  总结：
     *      常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用Spring-Cache）：写模式(只要缓存的数据有过期时间就足够了)
     *      特殊数据：特殊设计
     *
     *  原理：
     *      CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
     * @return
     */
    @Cacheable(value={"category"}, key = "#root.methodName",sync = true) //当前方法的结果需要缓存，如果缓存中有，方法不用调用。如果缓存中没有，会调用方法，最后将方法的结果放入缓存中
    @Override
    public List<CategoryEntity> getLevelOneCategory() {
        System.out.println("查询了一级菜单");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询二三级菜单");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> levelOneCategory = getParentCid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parentCid = levelOneCategory.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查到一级分类内的所有二级分类
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

    //TODO  生产堆外内存溢出，OutOfDirectMemoryError
    //springboot 2.0以后默认使用lettuce作为操作redis的客户端，它使用netty进行网络通信
    //lettuce的bug导致netty堆外内存溢出，如果product service的内存为300m，netty如果没有指定堆外内存，默认使用这300m
    //解决方案，不能使用dio.netty.maxDirectMemory只去调大堆外内存
    //1. 升级lettuce客户端，切换使用jedis
    // lettuce， jedis都是操作redis的底层客户端，redisTemplate是对他们的再次封装
    //@Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        /**
         * 加入空结果缓存，解决缓存穿透问题
         * 设置随机的过期事件，解决缓存雪崩问题
         * 加锁，解决缓存击穿问题
         */
        //1.加入缓存逻辑，缓存中存的数据是json字符串，拿出的json字符串还要能逆转为能用的对象类型，序列化和反序列化
        //json跨语言，跨平台兼容
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.hasText(catalogJSON)) {
            //缓存中没有
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedisLock();
            //查到数据库数据并放入缓存，将对象转为json放在缓存中
            return catalogJsonFromDb;
        }
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }

    /**
     * 缓存里的数据如何和数据库的数据保持一致性
     * 1. 双写模式
     * 2. 失效模式
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //1.redisson锁，锁的粒度，粒度越细粒度越快
        //锁的粒度：具体缓存的是某个数据，比如， 11号商品加锁，product-11-lock
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
        //1.抢占分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功");
            //加锁成功
            //设置该锁的过期时间以防止， 而且加锁和设置过期时间必须是同步的，要符合原子性
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
                System.out.println("删除分布式锁成功" + deleteResult);
            }

            //stringRedisTemplate.delete("lock");//拿到数据后一定要删除锁
            //获取值对比，对比之后成功删除，这两步操作也必须保证原子性，可以使用lua脚本
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){ //如果业务时间过长，必须要检测此时的锁是不是当前请求的锁，避免误删别人的锁
//                stringRedisTemplate.delete("lock");
//            }
            return catalogJsonFromDb;
        } else {
            System.out.println("获取分布式锁失败。。。等待");
            //枷锁失败之后重试
            //休眠100ms重试
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
            //缓存不为空null直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库....");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> levelOneCategory = getParentCid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parentCid = levelOneCategory.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查到一级分类内的所有二级分类
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

    //从数据查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        /**
         * 将数据库的多次查询变为一次，避免频繁与数据库交互，避免循环内查库
         */
        //只要是同一把锁，就能锁住需要这个锁的所有线程
        // synchronised(this)，springboot所有的组件在容器中都是单例的
        // 但是本地锁只能锁住当前实例，在分布式情况下，想要锁住所有，必须使用分布式锁
        synchronized (this) {
            //得到锁之后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            return getCatalogJsonFromDb();
        }
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
    }


    //比如225，25，2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        Long parentCid = categoryEntity.getParentCid();
        if (parentCid != 0) {
            findParentPath(parentCid, paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1. 找到子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2. 菜单排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}