package kcs.funding.fundingboost.elasticsearch.repository;

import kcs.funding.fundingboost.domain.repository.item.ItemRepositoryCustom;
import kcs.funding.fundingboost.elasticsearch.index.ItemIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemIndexRepository extends ElasticsearchRepository<ItemIndex, Long>, ItemRepositoryCustom {
}
