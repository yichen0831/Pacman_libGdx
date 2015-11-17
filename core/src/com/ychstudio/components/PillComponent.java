package com.ychstudio.components;

import com.badlogic.ashley.core.Component;

public class PillComponent implements Component {
    public boolean eaten;
    public boolean big;

    public PillComponent(boolean isBig) {
        big = isBig;
        eaten = false;
    }
}
