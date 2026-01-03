package com.vladmarica.betterpingdisplay.integ;

import com.vladmarica.betterpingdisplay.Config;
import com.vladmarica.betterpingdisplay.client.ColorUtil;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.gui.controllers.string.StringController;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.awt.*;
import java.util.function.Predicate;

import static net.minecraft.network.chat.Component.translatable;

public class YaclConfigScreenFactory implements IConfigScreenFactory {
    @Override
    public Screen createScreen(ModContainer modContainer, Screen parent) {
        ModConfigSpec.ConfigValue<String> textColorValue = getConfigValue("textColor");
        ModConfigSpec.ConfigValue<Boolean> autoColorTextValue = getConfigValue("autoColorText");
        ModConfigSpec.ConfigValue<String> textFormatStringValue = getConfigValue("textFormatString");
        ModConfigSpec.ConfigValue<Boolean> renderPingBarsValue = getConfigValue("renderPingBars");

        Option<Color> pingTextColorOption =  Option.<Color>createBuilder()
                .name(translatable("betterpingdisplay.configuration.textColor"))
                .description(OptionDescription.of(translatable("betterpingdisplay.configuration.textColor.tooltip")))
                .binding(
                        ColorUtil.parseColor(textColorValue.getDefault()),
                        () -> ColorUtil.parseColor(textColorValue.get()),
                        (c) -> textColorValue.set(ColorUtil.colorToString(c)))
                .controller(o -> ColorControllerBuilder.create(o).allowAlpha(false))
                .available(!autoColorTextValue.get())
                .build();

        Option<Boolean> autoColorPingTextOption = Option.<Boolean>createBuilder()
                .name(translatable("betterpingdisplay.configuration.autoColorText"))
                .description(OptionDescription.of(translatable("betterpingdisplay.configuration.autoColorText.tooltip")))
                .binding(
                        autoColorTextValue.getDefault(),
                        autoColorTextValue::get,
                        autoColorTextValue::set)
                .controller(o -> BooleanControllerBuilder.create(o).coloured(true))
                .addListener((option, event) -> pingTextColorOption.setAvailable(!option.pendingValue()))
                .build();


        Option<String> textFormatOption = Option.<String>createBuilder()
                .name(translatable("betterpingdisplay.configuration.textFormatString"))
                .description(OptionDescription.of(translatable("betterpingdisplay.configuration.textFormatString.tooltip")))
                .binding(
                        textFormatStringValue.getDefault(),
                        textFormatStringValue::get,
                        textFormatStringValue::set)
                .controller(StringControllerBuilder::create)
                .customController((o) -> new ValidatedStringController(o, (s) -> s.contains("%d")))
                .build();

        Option<Boolean> renderPingBarsOption = Option.<Boolean>createBuilder()
                .name(translatable("betterpingdisplay.configuration.renderPingBars"))
                .description(OptionDescription.of(translatable("betterpingdisplay.configuration.renderPingBars.tooltip")))
                .binding(
                        renderPingBarsValue.getDefault(),
                        renderPingBarsValue::get,
                        renderPingBarsValue::set)
                .controller(o -> BooleanControllerBuilder.create(o).coloured(true))
                .build();

        return YetAnotherConfigLib.createBuilder()
                .title(translatable("betterpingdisplay.configuration.title"))
                .category(ConfigCategory.createBuilder()
                        .name(translatable("betterpingdisplay.configuration.title"))
                        .option(autoColorPingTextOption)
                        .option(pingTextColorOption)
                        .option(textFormatOption)
                        .option(renderPingBarsOption)
                        .build())
                .save(Config.SPEC::save)
                .build()
                .generateScreen(parent);

    }

    private static <T> ModConfigSpec.ConfigValue<T> getConfigValue(String key) {
        return Config.SPEC.getValues().get(key);
    }

    private static class ValidatedStringController extends StringController {
        private final Predicate<String> validator;

        public ValidatedStringController(Option<String> option, Predicate<String> validator) {
            super(option);
            this.validator = validator;
        }

        @Override
        public boolean isInputValid(String input) {
            return validator.test(input);
        }
    }
}
