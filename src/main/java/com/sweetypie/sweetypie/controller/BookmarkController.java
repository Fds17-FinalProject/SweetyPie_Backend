package com.sweetypie.sweetypie.controller;

import com.sweetypie.sweetypie.dto.BookmarkDto;
import com.sweetypie.sweetypie.dto.BookmarkListDto;
import com.sweetypie.sweetypie.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api")
@PreAuthorize("authenticated")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/bookmark")
    public List<BookmarkListDto> getBookmarksByMemberId(@RequestHeader("Authorization") String token) {

        return bookmarkService.findBookmarksByToken(token);
    }

    @PostMapping("/bookmark")
    public void postBookmark(@RequestHeader("Authorization") String token, @Valid @RequestBody BookmarkDto bookmarkDto) {

        bookmarkService.postBookmark(token, bookmarkDto);
    }

    @DeleteMapping("/bookmark/{accommodationId}")
    public void deleteBookmark(@RequestHeader("Authorization") String token, @PathVariable long accommodationId) {

        bookmarkService.deleteBookmark(token, accommodationId);
    }
}
