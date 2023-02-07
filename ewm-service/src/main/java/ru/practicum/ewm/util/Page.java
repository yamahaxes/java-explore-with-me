package ru.practicum.ewm.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Page implements Pageable {

    private final Pageable pageable;

    public Page(int from, int size) {
        pageable = PageRequest.of(from / size, size);
    }

    public Page(int from, int size, Sort sort) {
        pageable = PageRequest.of(from / size, size, sort);
    }

    @Override
    public int getPageNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getPageSize() {
        return pageable.getPageSize();
    }

    @Override
    public long getOffset() {
        return pageable.getOffset();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public Pageable next() {
        return pageable.next();
    }

    @Override
    public Pageable previousOrFirst() {
        return pageable.previousOrFirst();
    }

    @Override
    public Pageable first() {
        return pageable.first();
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return pageable.withPage(pageNumber);
    }

    @Override
    public boolean hasPrevious() {
        return pageable.hasPrevious();
    }

}
