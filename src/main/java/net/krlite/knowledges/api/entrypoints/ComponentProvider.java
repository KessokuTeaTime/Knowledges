package net.krlite.knowledges.api.entrypoints;

import net.krlite.knowledges.api.Knowledge;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ComponentProvider {
	/**
	 * Provides the {@link Knowledge} classes that are going to be instanced during runtime.
	 * @return	The ready to be instanced classes with interface {@link Knowledge} implemented.
	 */
	@NotNull List<Class<? extends Knowledge>> provide();
}
