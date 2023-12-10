package net.krlite.knowledges.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ComponentsProvider {
	/**
	 * Provides the {@link Knowledge} classes that are going to be instanced during runtime.
	 * @return	The ready to be instanced classes with interface {@link Knowledge} implemented.
	 */
	@NotNull List<Class<? extends Knowledge>> provide();
}
