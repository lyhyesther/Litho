package com.inin.litho;


import android.os.Bundle;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.Text;
import com.inin.litho.custom.CustomComponentWithEvent;
import com.inin.litho.custom.ListItem;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentContext componentContext = new ComponentContext(this);

        Component component = Text.create(componentContext)
                .text("Hello Litho.")
                .textSizeDip(18)
                .build();
        Component listItemComponent = ListItem.create(componentContext)
                .build();

        Component customComponentWithEvent = CustomComponentWithEvent.create(componentContext)
                .build();


        setContentView(LithoView.create(componentContext, customComponentWithEvent));

    }
}
