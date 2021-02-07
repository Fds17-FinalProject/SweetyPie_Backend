package com.mip.sharebnb.controller;

import com.mip.sharebnb.model.Bookmark;
import com.mip.sharebnb.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark/{memberId}")
    public List<Bookmark> getBookmarks(@PathVariable long memberId) {

        return bookmarkService.findBookmarks(memberId);
    }

    @DeleteMapping("/bookmark/{id}")
    public void deleteBookmark(@PathVariable long id) {

        bookmarkService.deleteBookmarkById(id);
    }
}
