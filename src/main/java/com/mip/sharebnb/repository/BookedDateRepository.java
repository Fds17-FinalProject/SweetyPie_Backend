package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.BookedDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookedDateRepository extends JpaRepository<BookedDate, Long> {
    
}
