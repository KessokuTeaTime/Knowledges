package net.krlite.knowledges.api.entrypoint;

import net.krlite.knowledges.api.contract.Contract;
import net.krlite.knowledges.api.entrypoint.base.Provider;

public interface ContractProvider<T extends Contract<?, ?>> extends Provider<T> {
    interface Global extends ContractProvider<Contract<?, ?>> {
    }
}
