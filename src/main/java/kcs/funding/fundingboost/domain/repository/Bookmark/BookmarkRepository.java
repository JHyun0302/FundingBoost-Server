package kcs.funding.fundingboost.domain.repository.Bookmark;

import kcs.funding.fundingboost.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
}
