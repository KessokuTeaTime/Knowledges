package net.krlite.knowledges.data;

import net.krlite.knowledges.api.Data;
import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import org.jetbrains.annotations.NotNull;

public class TestData implements Data<BlockInfoComponent.BlockInfoTarget, BlockInfoComponent.BlockInfoTarget.TestEvent> {
    @Override
    public BlockInfoComponent.BlockInfoTarget.TestEvent listener() {
        return flag -> System.out.println("Test data output: " + flag);
    }

    @Override
    public BlockInfoComponent.BlockInfoTarget target() {
        return BlockInfoComponent.BlockInfoTarget.TEST;
    }

    @Override
    public Class<? extends Knowledge<?>> knowledgeClass() {
        return BlockInfoComponent.class;
    }

    @Override
    public @NotNull String path() {
        return "test";
    }
}
