package com.yio.trade.common;

public interface ScrollTopListener {

    void scrollToTop();

    default void scrollToTopRefresh() {};

}
