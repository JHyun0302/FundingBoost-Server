package kcs.funding.fundingboost.domain.repository.bookmark;

import kcs.funding.fundingboost.domain.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
}
