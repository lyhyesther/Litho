package com.inin.litho.custom;

import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.widget.Text;

@LayoutSpec
public class ListItemSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext context) {
        return Column.create(context)
                .child(Text.create(context)
                        .text("Hello World.")
                        .build())
                .child(Text.create(context)
                        .text("Column Component Guide.")
                        .build())
                .build();
    }
}
