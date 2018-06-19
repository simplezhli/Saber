package com.zl.weilu.saber.compiler.utils;

import javax.lang.model.element.Element;

/**
 * Created by weilu on 2017/12/14.
 */

public interface OnRegulaListener<T extends Element> {
    /**
     * @param element
     * @return return null or "" to declare that the element is correct
     */
    String onRegula(T element);
}