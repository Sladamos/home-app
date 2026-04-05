package com.sladamos.book.app.items.viewmodel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookItemsActiveState {
    private BookItemsViewModel activeViewModel;

    public void register(BookItemsViewModel viewModel) {
        log.info("Registering BookItemsViewModel");
        this.activeViewModel = viewModel;
    }

    public BookItemsViewModel getActive() {
        return activeViewModel;
    }
}