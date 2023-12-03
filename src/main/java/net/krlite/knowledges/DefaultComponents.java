package net.krlite.knowledges;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.api.KnowledgeContainer;
import net.krlite.knowledges.components.ArmorDurabilityComponent;
import net.krlite.knowledges.components.CrosshairComponent;
import net.krlite.knowledges.components.info.BlockInfoComponent;
import net.krlite.knowledges.components.info.EntityInfoComponent;
import net.krlite.knowledges.components.info.FluidInfoComponent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DefaultComponents implements KnowledgeContainer {
	private static final List<Class<? extends Knowledge>> classes = List.of(
			CrosshairComponent.class,

			BlockInfoComponent.class,
			EntityInfoComponent.class,
			FluidInfoComponent.class,

			ArmorDurabilityComponent.class
	);

	@Override
	public @NotNull List<? extends Knowledge> register() {
		return classes.stream().map(c -> {
			try {
				return c.getDeclaredConstructor().newInstance();
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
        }).toList();
	}

	public static boolean contains(Knowledge knowledge) {
		return classes.contains(knowledge.getClass());
	}
}
