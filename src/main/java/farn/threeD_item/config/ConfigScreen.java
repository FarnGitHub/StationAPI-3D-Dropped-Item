package farn.threeD_item.config;

import farn.threeD_item.main.ValueHolder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.TranslationStorage;

public class ConfigScreen extends Screen {
    final Screen parent;

    public ConfigScreen(Screen parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public void init() {
        buttons.add(new ButtonWidget(
                0,
                this.width / 2 - 100,
                this.height / 2 - 10,
                enabledString()
        ));
        buttons.add(new ButtonWidget(
                1,
                this.width / 2 - 100,
                this.height / 2 + 48,
                TranslationStorage.getInstance().get("gui.done")
        ));
    }

    public void render(int x, int y, float tick) {
        this.renderBackground();
        this.drawCenteredTextWithShadow(
                this.minecraft.textRenderer,
                "3D Dropped Item Config",
                this.width / 2,
                this.height / 4,
                16777215

        );
        super.render(x, y, tick);
    }

    public void buttonClicked(ButtonWidget button) {
        if(button.id == 0) {
            ValueHolder.enabled = !ValueHolder.enabled;
            button.text = enabledString();
            ValueHolder.saveConfig();
        } else {
            this.minecraft.setScreen(this.parent);
        }

    }

    private String enabledString() {
        return "Enabled: " + ValueHolder.enabled;
    }
}
