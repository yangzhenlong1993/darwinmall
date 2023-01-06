package com.zhenlong.darwinmall.product.vo;

/**
 * VO,通常用于业务层之间的数据传递，
 * 和PO（entity）一样也是仅仅包含数据而已。
 * 但应是抽象出的业务对象，可以和表对应，也可以不对应， 这根据业务的需求
 * 用new关键字创建，由GC回收
 *
 * 也可以理解为View Object，视图对象
 * 接受和返回给前端的数据不一定都是entity里的信息，有可能是包含其他的冗余信息，所以需要单独创建VO 对象来封装这些信息
 */