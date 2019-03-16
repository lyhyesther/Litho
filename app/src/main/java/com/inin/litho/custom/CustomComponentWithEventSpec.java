package com.inin.litho.custom;

import android.view.View;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.FromEvent;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Text;


@LayoutSpec
public class CustomComponentWithEventSpec {

    @OnCreateLayout
    static Component createLayout(ComponentContext context) {
        return Text.create(context)
                .text("点击我")
                .clickHandler(CustomComponentWithEvent.onClick(context))
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onClick(
            ComponentContext context,
            @FromEvent
                    View view,
            @Prop
                    String prop
    ) {
        //TODO 处理点击事件
    }
}
