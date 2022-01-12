package com.example.finalproject.paging;

public record PageableImpl<E>(int pageNumb, int size) implements Pageable<E> {

    @Override
    public int getPageNumb() {
        return pageNumb;
    }

    @Override
    public int getPageSize() {
        return size;
    }
}
