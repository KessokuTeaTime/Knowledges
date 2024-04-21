package band.kessokuteatime.knowledges.api.entrypoint;

import band.kessokuteatime.knowledges.api.contract.Contract;
import band.kessokuteatime.knowledges.api.entrypoint.base.Provider;

public interface ContractProvider<T extends Contract<?, ?>> extends Provider<T> {
    interface Global extends ContractProvider<Contract<?, ?>> {
    }
}
