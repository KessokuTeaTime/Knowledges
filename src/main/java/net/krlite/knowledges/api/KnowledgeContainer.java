package net.krlite.knowledges.api;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface KnowledgeContainer {
	@NotNull ArrayList<Knowledge> register();
}
