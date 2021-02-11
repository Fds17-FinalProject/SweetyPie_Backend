package com.mip.sharebnb.controller;

import com.mip.sharebnb.dto.BookmarkDto;
import com.mip.sharebnb.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark/{memberId}")
    public List<BookmarkDto> getBookmarks(@PathVariable long memberId) {

        return bookmarkService.findBookmarks(memberId);
    }

    @PostMapping("/bookmark")
    public void postBookmark(@Valid @RequestBody BookmarkDto bookmarkDto) {

        bookmarkService.postBookmark(bookmarkDto);
    }

    @DeleteMapping("/bookmark")
    public void deleteBookmark(@RequestParam long memberId, @RequestParam long accommodationId) {

        bookmarkService.deleteBookmark(memberId, accommodationId);
    }
}
