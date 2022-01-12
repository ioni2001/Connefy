package com.example.finalproject.paging;

import com.example.finalproject.domain.Cerere;

public interface RequestsPgRepository {
    Page<Cerere> getReqByName(Pageable<Cerere> pageable, String email);
}
